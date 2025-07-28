package com.nutanaa.privatetheaterbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * UserSourceAnalytics Entity Model
 * 
 * This entity tracks and analyzes user acquisition sources for the Nutanaa platform.
 * It helps understand where customers are coming from and which marketing channels
 * are most effective, providing valuable insights for business decisions.
 * 
 * Database Table: user_source_analytics
 * 
 * Features:
 * - User acquisition source tracking
 * - Marketing channel effectiveness analysis
 * - Registration and conversion tracking
 * - Time-based analytics support
 * - Detailed source breakdown
 * - Admin dashboard analytics data
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "user_source_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSourceAnalytics {

    /**
     * Primary key - Auto-generated unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analytics_id")
    private Long analyticsId;

    /**
     * Main source category where user heard about Nutanaa
     * Primary classification for analytics grouping
     */
    @Column(name = "source_category", nullable = false, length = 100)
    private String sourceCategory;

    /**
     * Specific source details within the category
     * Detailed breakdown of the source
     */
    @Column(name = "source_details", length = 200)
    private String sourceDetails;

    /**
     * Complete source information as provided by user
     * Raw data as entered during registration
     */
    @Column(name = "raw_source_data", length = 500)
    private String rawSourceData;

    /**
     * Number of users from this source
     * Count of registrations from this specific source
     */
    @Column(name = "user_count")
    private Long userCount = 0L;

    /**
     * Number of users who completed registration
     * Users who successfully verified and activated accounts
     */
    @Column(name = "completed_registrations")
    private Long completedRegistrations = 0L;

    /**
     * Number of users who made at least one booking
     * Conversion tracking for actual customers
     */
    @Column(name = "booking_conversions")
    private Long bookingConversions = 0L;

    /**
     * Total revenue generated from this source
     * Sum of all payments from users of this source
     */
    @Column(name = "total_revenue", precision = 15, scale = 2)
    private java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;

    /**
     * Number of active users from this source in last 30 days
     * Recent activity tracking
     */
    @Column(name = "active_users_30_days")
    private Long activeUsers30Days = 0L;

    /**
     * Average customer lifetime value for this source
     * Calculated based on total revenue and user count
     */
    @Column(name = "avg_customer_value", precision = 10, scale = 2)
    private java.math.BigDecimal avgCustomerValue = java.math.BigDecimal.ZERO;

    /**
     * Conversion rate from registration to booking
     * Percentage of users who made bookings
     */
    @Column(name = "conversion_rate", precision = 5, scale = 2)
    private java.math.BigDecimal conversionRate = java.math.BigDecimal.ZERO;

    /**
     * Quality score of this source (1-10)
     * Based on conversion rate, revenue, and user engagement
     */
    @Column(name = "source_quality_score", precision = 3, scale = 1)
    private java.math.BigDecimal sourceQualityScore = java.math.BigDecimal.ZERO;

    /**
     * Marketing cost associated with this source (if applicable)
     * For paid marketing channels
     */
    @Column(name = "marketing_cost", precision = 10, scale = 2)
    private java.math.BigDecimal marketingCost = java.math.BigDecimal.ZERO;

    /**
     * Return on Investment for this source
     * (Total Revenue - Marketing Cost) / Marketing Cost * 100
     */
    @Column(name = "roi_percentage", precision = 7, scale = 2)
    private java.math.BigDecimal roiPercentage = java.math.BigDecimal.ZERO;

    /**
     * First registration date from this source
     * When we first received a user from this source
     */
    @Column(name = "first_registration_date")
    private LocalDateTime firstRegistrationDate;

    /**
     * Most recent registration date from this source
     * Latest user acquisition from this source
     */
    @Column(name = "last_registration_date")
    private LocalDateTime lastRegistrationDate;

    /**
     * Growth trend for this source
     * - GROWING: Increasing user acquisition
     * - STABLE: Consistent user acquisition
     * - DECLINING: Decreasing user acquisition
     * - INACTIVE: No recent acquisitions
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "growth_trend")
    private GrowthTrend growthTrend = GrowthTrend.STABLE;

    /**
     * Priority level for this source
     * - HIGH: High-performing, focus marketing efforts
     * - MEDIUM: Average performance, monitor
     * - LOW: Poor performance, reduce investment
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level")
    private PriorityLevel priorityLevel = PriorityLevel.MEDIUM;

    /**
     * Geographic region associated with this source
     * For location-based source analysis
     */
    @Column(name = "geographic_region", length = 100)
    private String geographicRegion;

    /**
     * Age group that primarily comes from this source
     * Demographic analysis
     */
    @Column(name = "primary_age_group", length = 50)
    private String primaryAgeGroup;

    /**
     * Device type commonly used by users from this source
     * Mobile, Desktop, Tablet analysis
     */
    @Column(name = "primary_device_type", length = 50)
    private String primaryDeviceType;

    /**
     * Peak activity hours for this source
     * When users from this source are most active
     */
    @Column(name = "peak_activity_hours", length = 100)
    private String peakActivityHours;

    /**
     * Notes and observations about this source
     * Manual notes from marketing team
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Whether this source is currently being tracked
     * Active/Inactive source monitoring
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Timestamp when this analytics record was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this analytics record was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========================================
    // ENUMERATIONS
    // ========================================

    /**
     * Growth Trend Enumeration
     */
    public enum GrowthTrend {
        GROWING("Growing"),
        STABLE("Stable"),
        DECLINING("Declining"),
        INACTIVE("Inactive");

        private final String displayName;

        GrowthTrend(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Priority Level Enumeration
     */
    public enum PriorityLevel {
        HIGH("High Priority"),
        MEDIUM("Medium Priority"),
        LOW("Low Priority");

        private final String displayName;

        PriorityLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Calculate and update conversion rate
     */
    public void calculateConversionRate() {
        if (userCount == null || userCount == 0) {
            conversionRate = java.math.BigDecimal.ZERO;
            return;
        }

        long conversions = bookingConversions != null ? bookingConversions : 0;
        double rate = (double) conversions / userCount * 100;
        conversionRate = java.math.BigDecimal.valueOf(rate).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate and update average customer value
     */
    public void calculateAvgCustomerValue() {
        if (userCount == null || userCount == 0 || totalRevenue == null) {
            avgCustomerValue = java.math.BigDecimal.ZERO;
            return;
        }

        avgCustomerValue = totalRevenue.divide(
            java.math.BigDecimal.valueOf(userCount), 
            2, 
            java.math.RoundingMode.HALF_UP
        );
    }

    /**
     * Calculate and update ROI percentage
     */
    public void calculateROI() {
        if (marketingCost == null || marketingCost.compareTo(java.math.BigDecimal.ZERO) == 0) {
            roiPercentage = java.math.BigDecimal.ZERO;
            return;
        }

        java.math.BigDecimal profit = totalRevenue.subtract(marketingCost);
        roiPercentage = profit.divide(marketingCost, 4, java.math.RoundingMode.HALF_UP)
                             .multiply(java.math.BigDecimal.valueOf(100))
                             .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate and update quality score based on multiple factors
     */
    public void calculateQualityScore() {
        double score = 0.0;

        // Conversion rate weight (40%)
        if (conversionRate != null) {
            score += (conversionRate.doubleValue() / 100) * 4.0;
        }

        // Revenue weight (30%)
        if (totalRevenue != null && totalRevenue.doubleValue() > 0) {
            score += Math.min(totalRevenue.doubleValue() / 10000, 3.0);
        }

        // User count weight (20%)
        if (userCount != null && userCount > 0) {
            score += Math.min(userCount.doubleValue() / 100, 2.0);
        }

        // Activity weight (10%)
        if (activeUsers30Days != null && userCount != null && userCount > 0) {
            double activityRatio = activeUsers30Days.doubleValue() / userCount.doubleValue();
            score += activityRatio * 1.0;
        }

        sourceQualityScore = java.math.BigDecimal.valueOf(Math.min(score, 10.0))
                                                  .setScale(1, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Update priority level based on quality score
     */
    public void updatePriorityLevel() {
        if (sourceQualityScore == null) {
            priorityLevel = PriorityLevel.LOW;
            return;
        }

        double score = sourceQualityScore.doubleValue();
        if (score >= 7.0) {
            priorityLevel = PriorityLevel.HIGH;
        } else if (score >= 4.0) {
            priorityLevel = PriorityLevel.MEDIUM;
        } else {
            priorityLevel = PriorityLevel.LOW;
        }
    }

    /**
     * Determine growth trend based on recent activity
     */
    public void updateGrowthTrend() {
        if (lastRegistrationDate == null) {
            growthTrend = GrowthTrend.INACTIVE;
            return;
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        if (lastRegistrationDate.isBefore(thirtyDaysAgo)) {
            growthTrend = GrowthTrend.INACTIVE;
        } else if (activeUsers30Days != null && userCount != null && userCount > 0) {
            double activityRatio = activeUsers30Days.doubleValue() / userCount.doubleValue();
            if (activityRatio > 0.5 && lastRegistrationDate.isAfter(sevenDaysAgo)) {
                growthTrend = GrowthTrend.GROWING;
            } else if (activityRatio > 0.2) {
                growthTrend = GrowthTrend.STABLE;
            } else {
                growthTrend = GrowthTrend.DECLINING;
            }
        } else {
            growthTrend = GrowthTrend.STABLE;
        }
    }

    /**
     * Add a new user to this source
     */
    public void addUser() {
        if (userCount == null) userCount = 0L;
        userCount++;

        LocalDateTime now = LocalDateTime.now();
        if (firstRegistrationDate == null) {
            firstRegistrationDate = now;
        }
        lastRegistrationDate = now;

        // Recalculate metrics
        calculateConversionRate();
        calculateAvgCustomerValue();
        calculateQualityScore();
        updatePriorityLevel();
        updateGrowthTrend();
    }

    /**
     * Record a completed registration
     */
    public void addCompletedRegistration() {
        if (completedRegistrations == null) completedRegistrations = 0L;
        completedRegistrations++;
    }

    /**
     * Record a booking conversion
     */
    public void addBookingConversion(java.math.BigDecimal bookingAmount) {
        if (bookingConversions == null) bookingConversions = 0L;
        if (totalRevenue == null) totalRevenue = java.math.BigDecimal.ZERO;

        bookingConversions++;
        if (bookingAmount != null) {
            totalRevenue = totalRevenue.add(bookingAmount);
        }

        // Recalculate metrics
        calculateConversionRate();
        calculateAvgCustomerValue();
        calculateROI();
        calculateQualityScore();
        updatePriorityLevel();
    }

    /**
     * Get performance summary
     */
    public String getPerformanceSummary() {
        return String.format("Users: %d, Conversions: %d (%.1f%%), Revenue: ₹%.2f, Quality: %.1f/10",
                userCount != null ? userCount : 0,
                bookingConversions != null ? bookingConversions : 0,
                conversionRate != null ? conversionRate.doubleValue() : 0.0,
                totalRevenue != null ? totalRevenue.doubleValue() : 0.0,
                sourceQualityScore != null ? sourceQualityScore.doubleValue() : 0.0);
    }

    /**
     * Check if source is performing well
     */
    public boolean isHighPerforming() {
        return priorityLevel == PriorityLevel.HIGH && 
               growthTrend != GrowthTrend.DECLINING && 
               growthTrend != GrowthTrend.INACTIVE;
    }

    /**
     * Get cost per acquisition (if marketing cost is available)
     */
    public java.math.BigDecimal getCostPerAcquisition() {
        if (marketingCost == null || userCount == null || userCount == 0) {
            return java.math.BigDecimal.ZERO;
        }
        return marketingCost.divide(java.math.BigDecimal.valueOf(userCount), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Get formatted source display name
     */
    public String getDisplayName() {
        if (sourceDetails != null && !sourceDetails.trim().isEmpty()) {
            return sourceCategory + " - " + sourceDetails;
        }
        return sourceCategory;
    }
}