package com.nutanaa.privatetheaterbooking.repository;

import com.nutanaa.privatetheaterbooking.model.UserSourceAnalytics;
import com.nutanaa.privatetheaterbooking.model.UserSourceAnalytics.GrowthTrend;
import com.nutanaa.privatetheaterbooking.model.UserSourceAnalytics.PriorityLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * UserSourceAnalyticsRepository Interface
 * 
 * This repository interface provides data access methods for UserSourceAnalytics entities.
 * It supports marketing analytics, user acquisition tracking, and source performance monitoring.
 * 
 * Features:
 * - User source tracking and analytics
 * - Marketing channel performance analysis
 * - ROI and conversion rate calculations
 * - Growth trend analysis
 * - Admin dashboard analytics
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Repository
public interface UserSourceAnalyticsRepository extends JpaRepository<UserSourceAnalytics, Long> {

    // ========================================
    // BASIC FINDER METHODS
    // ========================================

    /**
     * Find analytics by source category
     */
    Optional<UserSourceAnalytics> findBySourceCategory(String sourceCategory);

    /**
     * Find analytics by source category and details
     */
    Optional<UserSourceAnalytics> findBySourceCategoryAndSourceDetails(String sourceCategory, String sourceDetails);

    /**
     * Find all active analytics records
     */
    List<UserSourceAnalytics> findByIsActiveTrue();

    /**
     * Find analytics by priority level
     */
    List<UserSourceAnalytics> findByPriorityLevel(PriorityLevel priorityLevel);

    /**
     * Find analytics by growth trend
     */
    List<UserSourceAnalytics> findByGrowthTrend(GrowthTrend growthTrend);

    // ========================================
    // PERFORMANCE RANKING QUERIES
    // ========================================

    /**
     * Get top performing sources by user count
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.isActive = true ORDER BY s.userCount DESC")
    List<UserSourceAnalytics> findTopSourcesByUserCount(Pageable pageable);

    /**
     * Get top performing sources by revenue
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.isActive = true ORDER BY s.totalRevenue DESC")
    List<UserSourceAnalytics> findTopSourcesByRevenue(Pageable pageable);

    /**
     * Get top performing sources by conversion rate
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.isActive = true AND s.userCount >= :minUsers ORDER BY s.conversionRate DESC")
    List<UserSourceAnalytics> findTopSourcesByConversionRate(@Param("minUsers") Long minUsers, Pageable pageable);

    /**
     * Get top performing sources by ROI
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.isActive = true AND s.marketingCost > 0 ORDER BY s.roiPercentage DESC")
    List<UserSourceAnalytics> findTopSourcesByROI(Pageable pageable);

    /**
     * Get top performing sources by quality score
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.isActive = true ORDER BY s.sourceQualityScore DESC")
    List<UserSourceAnalytics> findTopSourcesByQualityScore(Pageable pageable);

    /**
     * Get high priority sources
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.priorityLevel = 'HIGH' AND s.isActive = true ORDER BY s.sourceQualityScore DESC")
    List<UserSourceAnalytics> findHighPrioritySources();

    // ========================================
    // ANALYTICS AND REPORTING QUERIES
    // ========================================

    /**
     * Get overall source performance summary
     */
    @Query("SELECT " +
           "COUNT(s) as totalSources, " +
           "SUM(s.userCount) as totalUsers, " +
           "SUM(s.bookingConversions) as totalConversions, " +
           "SUM(s.totalRevenue) as totalRevenue, " +
           "AVG(s.conversionRate) as avgConversionRate, " +
           "SUM(s.marketingCost) as totalMarketingCost " +
           "FROM UserSourceAnalytics s WHERE s.isActive = true")
    Object[] getOverallSourcePerformance();

    /**
     * Get source distribution by priority level
     */
    @Query("SELECT s.priorityLevel, COUNT(s), SUM(s.userCount), SUM(s.totalRevenue) " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true " +
           "GROUP BY s.priorityLevel " +
           "ORDER BY s.priorityLevel")
    List<Object[]> getSourceDistributionByPriority();

    /**
     * Get source distribution by growth trend
     */
    @Query("SELECT s.growthTrend, COUNT(s), SUM(s.userCount), SUM(s.totalRevenue) " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true " +
           "GROUP BY s.growthTrend " +
           "ORDER BY s.growthTrend")
    List<Object[]> getSourceDistributionByGrowthTrend();

    /**
     * Get monthly source performance trends
     */
    @Query("SELECT " +
           "s.sourceCategory, " +
           "YEAR(s.lastRegistrationDate) as year, " +
           "MONTH(s.lastRegistrationDate) as month, " +
           "SUM(s.activeUsers30Days) as monthlyUsers, " +
           "SUM(s.totalRevenue) as monthlyRevenue " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true AND s.lastRegistrationDate >= :startDate " +
           "GROUP BY s.sourceCategory, YEAR(s.lastRegistrationDate), MONTH(s.lastRegistrationDate) " +
           "ORDER BY year, month, SUM(s.totalRevenue) DESC")
    List<Object[]> getMonthlySourceTrends(@Param("startDate") LocalDateTime startDate);

    /**
     * Get source performance comparison
     */
    @Query("SELECT " +
           "s.sourceCategory, " +
           "s.userCount, " +
           "s.bookingConversions, " +
           "s.conversionRate, " +
           "s.totalRevenue, " +
           "s.avgCustomerValue, " +
           "s.sourceQualityScore " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true " +
           "ORDER BY s.sourceQualityScore DESC")
    List<Object[]> getSourcePerformanceComparison();

    // ========================================
    // CONVERSION AND REVENUE ANALYSIS
    // ========================================

    /**
     * Find sources with high conversion rates
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "s.conversionRate >= :minConversionRate AND " +
           "s.userCount >= :minUsers " +
           "ORDER BY s.conversionRate DESC")
    List<UserSourceAnalytics> findHighConversionSources(@Param("minConversionRate") BigDecimal minConversionRate, 
                                                        @Param("minUsers") Long minUsers);

    /**
     * Find sources with high customer value
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "s.avgCustomerValue >= :minCustomerValue " +
           "ORDER BY s.avgCustomerValue DESC")
    List<UserSourceAnalytics> findHighValueSources(@Param("minCustomerValue") BigDecimal minCustomerValue);

    /**
     * Find underperforming sources
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "(s.conversionRate < :maxConversionRate OR " +
           "s.sourceQualityScore < :maxQualityScore) AND " +
           "s.userCount >= :minUsers " +
           "ORDER BY s.sourceQualityScore ASC")
    List<UserSourceAnalytics> findUnderperformingSources(@Param("maxConversionRate") BigDecimal maxConversionRate, 
                                                         @Param("maxQualityScore") BigDecimal maxQualityScore,
                                                         @Param("minUsers") Long minUsers);

    /**
     * Calculate total ROI across all sources
     */
    @Query("SELECT " +
           "SUM(s.totalRevenue) as totalRevenue, " +
           "SUM(s.marketingCost) as totalCost, " +
           "((SUM(s.totalRevenue) - SUM(s.marketingCost)) / SUM(s.marketingCost) * 100) as overallROI " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true AND s.marketingCost > 0")
    Object[] calculateOverallROI();

    // ========================================
    // GEOGRAPHIC AND DEMOGRAPHIC ANALYSIS
    // ========================================

    /**
     * Find sources by geographic region
     */
    List<UserSourceAnalytics> findByGeographicRegion(String geographicRegion);

    /**
     * Get source distribution by geographic region
     */
    @Query("SELECT s.geographicRegion, COUNT(s), SUM(s.userCount), SUM(s.totalRevenue) " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.geographicRegion IS NOT NULL AND s.isActive = true " +
           "GROUP BY s.geographicRegion " +
           "ORDER BY SUM(s.totalRevenue) DESC")
    List<Object[]> getSourceDistributionByRegion();

    /**
     * Find sources by primary age group
     */
    List<UserSourceAnalytics> findByPrimaryAgeGroup(String primaryAgeGroup);

    /**
     * Get source distribution by age group
     */
    @Query("SELECT s.primaryAgeGroup, COUNT(s), SUM(s.userCount), AVG(s.avgCustomerValue) " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.primaryAgeGroup IS NOT NULL AND s.isActive = true " +
           "GROUP BY s.primaryAgeGroup " +
           "ORDER BY SUM(s.userCount) DESC")
    List<Object[]> getSourceDistributionByAgeGroup();

    /**
     * Find sources by primary device type
     */
    List<UserSourceAnalytics> findByPrimaryDeviceType(String primaryDeviceType);

    /**
     * Get source distribution by device type
     */
    @Query("SELECT s.primaryDeviceType, COUNT(s), SUM(s.userCount), AVG(s.conversionRate) " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.primaryDeviceType IS NOT NULL AND s.isActive = true " +
           "GROUP BY s.primaryDeviceType " +
           "ORDER BY SUM(s.userCount) DESC")
    List<Object[]> getSourceDistributionByDeviceType();

    // ========================================
    // TIME-BASED ANALYSIS
    // ========================================

    /**
     * Find sources with recent activity
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "s.lastRegistrationDate >= :recentThreshold " +
           "ORDER BY s.lastRegistrationDate DESC")
    List<UserSourceAnalytics> findSourcesWithRecentActivity(@Param("recentThreshold") LocalDateTime recentThreshold);

    /**
     * Find dormant sources (no recent activity)
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "(s.lastRegistrationDate < :dormantThreshold OR s.lastRegistrationDate IS NULL) " +
           "ORDER BY s.lastRegistrationDate ASC NULLS FIRST")
    List<UserSourceAnalytics> findDormantSources(@Param("dormantThreshold") LocalDateTime dormantThreshold);

    /**
     * Find growing sources
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "s.growthTrend = 'GROWING' " +
           "ORDER BY s.activeUsers30Days DESC")
    List<UserSourceAnalytics> findGrowingSources();

    /**
     * Find declining sources
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "s.growthTrend = 'DECLINING' " +
           "ORDER BY s.sourceQualityScore DESC")
    List<UserSourceAnalytics> findDecliningSources();

    // ========================================
    // SEARCH AND FILTERING QUERIES
    // ========================================

    /**
     * Search sources by category or details
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "(LOWER(s.sourceCategory) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.sourceDetails) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<UserSourceAnalytics> searchSources(@Param("searchTerm") String searchTerm);

    /**
     * Advanced source filtering
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "(:priorityLevel IS NULL OR s.priorityLevel = :priorityLevel) AND " +
           "(:growthTrend IS NULL OR s.growthTrend = :growthTrend) AND " +
           "(:minUsers IS NULL OR s.userCount >= :minUsers) AND " +
           "(:maxUsers IS NULL OR s.userCount <= :maxUsers) AND " +
           "(:minRevenue IS NULL OR s.totalRevenue >= :minRevenue) AND " +
           "(:maxRevenue IS NULL OR s.totalRevenue <= :maxRevenue) AND " +
           "(:minConversionRate IS NULL OR s.conversionRate >= :minConversionRate) AND " +
           "(:geographicRegion IS NULL OR s.geographicRegion = :geographicRegion)")
    Page<UserSourceAnalytics> findSourcesWithFilters(
            @Param("priorityLevel") PriorityLevel priorityLevel,
            @Param("growthTrend") GrowthTrend growthTrend,
            @Param("minUsers") Long minUsers,
            @Param("maxUsers") Long maxUsers,
            @Param("minRevenue") BigDecimal minRevenue,
            @Param("maxRevenue") BigDecimal maxRevenue,
            @Param("minConversionRate") BigDecimal minConversionRate,
            @Param("geographicRegion") String geographicRegion,
            Pageable pageable);

    // ========================================
    // MARKETING INVESTMENT ANALYSIS
    // ========================================

    /**
     * Find sources with marketing cost data
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.marketingCost > 0 ORDER BY s.roiPercentage DESC")
    List<UserSourceAnalytics> findSourcesWithMarketingCost();

    /**
     * Get cost per acquisition analysis
     */
    @Query("SELECT " +
           "s.sourceCategory, " +
           "s.marketingCost, " +
           "s.userCount, " +
           "(s.marketingCost / s.userCount) as costPerAcquisition, " +
           "s.roiPercentage " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true AND s.marketingCost > 0 AND s.userCount > 0 " +
           "ORDER BY (s.marketingCost / s.userCount) ASC")
    List<Object[]> getCostPerAcquisitionAnalysis();

    /**
     * Find most cost-effective sources
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "s.marketingCost > 0 AND " +
           "s.userCount > 0 AND " +
           "(s.marketingCost / s.userCount) <= :maxCostPerUser " +
           "ORDER BY s.roiPercentage DESC")
    List<UserSourceAnalytics> findCostEffectiveSources(@Param("maxCostPerUser") BigDecimal maxCostPerUser);

    // ========================================
    // UPDATE OPERATIONS
    // ========================================

    /**
     * Update user count for a source
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET " +
           "s.userCount = s.userCount + 1, " +
           "s.lastRegistrationDate = :registrationDate " +
           "WHERE s.analyticsId = :analyticsId")
    void incrementUserCount(@Param("analyticsId") Long analyticsId, @Param("registrationDate") LocalDateTime registrationDate);

    /**
     * Update completed registrations count
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.completedRegistrations = s.completedRegistrations + 1 WHERE s.analyticsId = :analyticsId")
    void incrementCompletedRegistrations(@Param("analyticsId") Long analyticsId);

    /**
     * Update booking conversions and revenue
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET " +
           "s.bookingConversions = s.bookingConversions + 1, " +
           "s.totalRevenue = s.totalRevenue + :bookingAmount " +
           "WHERE s.analyticsId = :analyticsId")
    void addBookingConversion(@Param("analyticsId") Long analyticsId, @Param("bookingAmount") BigDecimal bookingAmount);

    /**
     * Update active users count
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.activeUsers30Days = :activeUsers WHERE s.analyticsId = :analyticsId")
    void updateActiveUsers(@Param("analyticsId") Long analyticsId, @Param("activeUsers") Long activeUsers);

    /**
     * Update calculated metrics
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET " +
           "s.conversionRate = :conversionRate, " +
           "s.avgCustomerValue = :avgCustomerValue, " +
           "s.sourceQualityScore = :qualityScore " +
           "WHERE s.analyticsId = :analyticsId")
    void updateCalculatedMetrics(@Param("analyticsId") Long analyticsId,
                                @Param("conversionRate") BigDecimal conversionRate,
                                @Param("avgCustomerValue") BigDecimal avgCustomerValue,
                                @Param("qualityScore") BigDecimal qualityScore);

    /**
     * Update marketing cost and ROI
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET " +
           "s.marketingCost = :marketingCost, " +
           "s.roiPercentage = :roiPercentage " +
           "WHERE s.analyticsId = :analyticsId")
    void updateMarketingMetrics(@Param("analyticsId") Long analyticsId,
                               @Param("marketingCost") BigDecimal marketingCost,
                               @Param("roiPercentage") BigDecimal roiPercentage);

    /**
     * Update priority level
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.priorityLevel = :priorityLevel WHERE s.analyticsId = :analyticsId")
    void updatePriorityLevel(@Param("analyticsId") Long analyticsId, @Param("priorityLevel") PriorityLevel priorityLevel);

    /**
     * Update growth trend
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.growthTrend = :growthTrend WHERE s.analyticsId = :analyticsId")
    void updateGrowthTrend(@Param("analyticsId") Long analyticsId, @Param("growthTrend") GrowthTrend growthTrend);

    // ========================================
    // BULK OPERATIONS AND MAINTENANCE
    // ========================================

    /**
     * Deactivate inactive sources
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.isActive = false WHERE " +
           "s.lastRegistrationDate < :inactiveThreshold OR " +
           "(s.userCount = 0 AND s.createdAt < :zeroUserThreshold)")
    int deactivateInactiveSources(@Param("inactiveThreshold") LocalDateTime inactiveThreshold,
                                 @Param("zeroUserThreshold") LocalDateTime zeroUserThreshold);

    /**
     * Recalculate all conversion rates
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.conversionRate = " +
           "CASE WHEN s.userCount > 0 THEN (s.bookingConversions * 100.0 / s.userCount) ELSE 0 END")
    int recalculateAllConversionRates();

    /**
     * Recalculate all average customer values
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.avgCustomerValue = " +
           "CASE WHEN s.userCount > 0 THEN (s.totalRevenue / s.userCount) ELSE 0 END")
    int recalculateAllAvgCustomerValues();

    /**
     * Recalculate all ROI percentages
     */
    @Modifying
    @Query("UPDATE UserSourceAnalytics s SET s.roiPercentage = " +
           "CASE WHEN s.marketingCost > 0 THEN ((s.totalRevenue - s.marketingCost) / s.marketingCost * 100) ELSE 0 END")
    int recalculateAllROIPercentages();

    // ========================================
    // ADMIN DASHBOARD QUERIES
    // ========================================

    /**
     * Get source performance summary for dashboard
     */
    @Query("SELECT " +
           "s.sourceCategory, " +
           "s.sourceDetails, " +
           "s.userCount, " +
           "s.bookingConversions, " +
           "s.conversionRate, " +
           "s.totalRevenue, " +
           "s.priorityLevel, " +
           "s.growthTrend " +
           "FROM UserSourceAnalytics s " +
           "WHERE s.isActive = true " +
           "ORDER BY s.sourceQualityScore DESC, s.totalRevenue DESC")
    List<Object[]> getDashboardSourceSummary(Pageable pageable);

    /**
     * Get top 10 sources for quick view
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE s.isActive = true ORDER BY s.sourceQualityScore DESC")
    List<UserSourceAnalytics> getTop10Sources(Pageable pageable);

    /**
     * Get sources requiring attention
     */
    @Query("SELECT s FROM UserSourceAnalytics s WHERE " +
           "s.isActive = true AND " +
           "(s.growthTrend = 'DECLINING' OR " +
           "s.priorityLevel = 'LOW' OR " +
           "(s.conversionRate < 5.0 AND s.userCount > 50)) " +
           "ORDER BY s.priorityLevel DESC, s.conversionRate ASC")
    List<UserSourceAnalytics> getSourcesRequiringAttention();
}