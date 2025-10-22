package com.nutanaa.privatetheaterbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PrivateTheater Entity Model
 * 
 * This entity represents private theaters available for booking in the Nutanaa platform.
 * Each theater contains detailed information about location, capacity, amenities, pricing,
 * and availability settings.
 * 
 * Database Table: private_theaters
 * 
 * Features:
 * - Complete theater information management
 * - Pricing and capacity configuration
 * - Image gallery support
 * - Amenities and features tracking
 * - Location and contact details
 * - Availability and status management
 * - Revenue tracking capabilities
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "private_theaters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateTheater {

    /**
     * Primary key - Auto-generated unique identifier for each theater
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long theaterId;

    /**
     * Theater name - Display name for the theater
     * Must be unique and descriptive
     */
    @Column(name = "theater_name", nullable = false, length = 150)
    @NotBlank(message = "Theater name is required")
    @Size(min = 3, max = 150, message = "Theater name must be between 3 and 150 characters")
    private String theaterName;

    /**
     * Detailed description of the theater
     * Includes facilities, special features, and other relevant information
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Theater capacity - Maximum number of seats available
     * Used for booking validation and availability calculation
     */
    @Column(name = "seating_capacity", nullable = false)
    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 100, message = "Seating capacity cannot exceed 100")
    private Integer seatingCapacity;

    /**
     * Current availability status of the theater
     * - AVAILABLE: Theater is active and accepting bookings
     * - MAINTENANCE: Temporarily unavailable for maintenance
     * - BOOKED: Currently occupied (real-time status)
     * - INACTIVE: Permanently disabled
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    /**
     * Base price per hour for theater booking
     * Used as default pricing, can be overridden for special periods
     */
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.01", message = "Hourly rate must be greater than 0")
    private BigDecimal hourlyRate;

    /**
     * Weekend/Holiday surcharge percentage
     * Applied as additional cost during peak times
     */
    @Column(name = "weekend_surcharge_percent", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Surcharge cannot be negative")
    @DecimalMax(value = "100.00", message = "Surcharge cannot exceed 100%")
    private BigDecimal weekendSurchargePercent = BigDecimal.ZERO;

    /**
     * Minimum booking duration in hours
     * Ensures profitable booking slots
     */
    @Column(name = "minimum_booking_hours")
    @Min(value = 1, message = "Minimum booking hours must be at least 1")
    private Integer minimumBookingHours = 2;

    /**
     * Maximum booking duration in hours
     * Prevents monopolization of theaters
     */
    @Column(name = "maximum_booking_hours")
    @Min(value = 1, message = "Maximum booking hours must be at least 1")
    private Integer maximumBookingHours = 12;

    // ========================================
    // LOCATION INFORMATION
    // ========================================

    /**
     * Street address of the theater
     */
    @Column(name = "street_address", nullable = false, length = 255)
    @NotBlank(message = "Street address is required")
    private String streetAddress;

    /**
     * City where theater is located
     */
    @Column(name = "city", nullable = false, length = 100)
    @NotBlank(message = "City is required")
    private String city;

    /**
     * State/Province of the theater
     */
    @Column(name = "state", nullable = false, length = 100)
    @NotBlank(message = "State is required")
    private String state;

    /**
     * Postal/ZIP code
     */
    @Column(name = "postal_code", nullable = false, length = 20)
    @NotBlank(message = "Postal code is required")
    private String postalCode;

    /**
     * Country (default: India for Nutanaa)
     */
    @Column(name = "country", nullable = false, length = 100)
    @NotBlank(message = "Country is required")
    private String country = "India";

    /**
     * Landmark or nearby reference point
     * Helps customers locate the theater easily
     */
    @Column(name = "landmark", length = 200)
    private String landmark;

    /**
     * Google Maps coordinates - Latitude
     */
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    /**
     * Google Maps coordinates - Longitude
     */
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    // ========================================
    // CONTACT INFORMATION
    // ========================================

    /**
     * Primary contact number for the theater
     */
    @Column(name = "contact_number", length = 15)
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid contact number format")
    private String contactNumber;

    /**
     * Secondary/Emergency contact number
     */
    @Column(name = "emergency_contact", length = 15)
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid emergency contact format")
    private String emergencyContact;

    /**
     * Theater manager or owner email
     */
    @Column(name = "contact_email", length = 150)
    @Email(message = "Invalid email format")
    private String contactEmail;

    // ========================================
    // MEDIA AND VISUAL CONTENT
    // ========================================

    /**
     * Primary theater image - Main display photo
     */
    @Column(name = "primary_image_path")
    private String primaryImagePath;

    /**
     * JSON array of additional image paths
     * Stored as comma-separated values for gallery
     */
    @Column(name = "gallery_images", columnDefinition = "TEXT")
    private String galleryImages;

    /**
     * Virtual tour URL (if available)
     * Link to 360-degree view or video tour
     */
    @Column(name = "virtual_tour_url")
    private String virtualTourUrl;

    // ========================================
    // AMENITIES AND FEATURES
    // ========================================

    /**
     * Sound system quality rating (1-5 stars)
     */
    @Column(name = "sound_system_rating")
    @Min(value = 1, message = "Sound system rating must be at least 1")
    @Max(value = 5, message = "Sound system rating cannot exceed 5")
    private Integer soundSystemRating;

    /**
     * Video/Projection quality rating (1-5 stars)
     */
    @Column(name = "video_quality_rating")
    @Min(value = 1, message = "Video quality rating must be at least 1")
    @Max(value = 5, message = "Video quality rating cannot exceed 5")
    private Integer videoQualityRating;

    /**
     * Available amenities as comma-separated values
     * Examples: "Air Conditioning,Parking,WiFi,Snacks,Beverages"
     */
    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities;

    /**
     * Special features or unique selling points
     * Examples: "Dolby Atmos,Recliner Seats,Gaming Setup"
     */
    @Column(name = "special_features", columnDefinition = "TEXT")
    private String specialFeatures;

    /**
     * Accessibility features for disabled guests
     * Examples: "Wheelchair Access,Hearing Aid Support"
     */
    @Column(name = "accessibility_features", columnDefinition = "TEXT")
    private String accessibilityFeatures;

    // ========================================
    // OPERATIONAL SETTINGS
    // ========================================

    /**
     * Operating hours - Opening time (24-hour format)
     * Example: "09:00" for 9 AM
     */
    @Column(name = "operating_hours_start", length = 5)
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format")
    private String operatingHoursStart = "09:00";

    /**
     * Operating hours - Closing time (24-hour format)
     * Example: "23:00" for 11 PM
     */
    @Column(name = "operating_hours_end", length = 5)
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format")
    private String operatingHoursEnd = "23:00";

    /**
     * Advance booking limit in days
     * How far in advance customers can book
     */
    @Column(name = "advance_booking_days")
    @Min(value = 1, message = "Advance booking days must be at least 1")
    private Integer advanceBookingDays = 30;

    /**
     * Cancellation policy in hours
     * Minimum notice required for cancellation
     */
    @Column(name = "cancellation_policy_hours")
    @Min(value = 1, message = "Cancellation policy must be at least 1 hour")
    private Integer cancellationPolicyHours = 24;

    // ========================================
    // ANALYTICS AND TRACKING
    // ========================================

    /**
     * Total number of completed bookings
     * Updated after each successful booking completion
     */
    @Column(name = "total_bookings")
    private Long totalBookings = 0L;

    /**
     * Total revenue generated from this theater
     * Updated after each payment completion
     */
    @Column(name = "total_revenue", precision = 15, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    /**
     * Average customer rating (1-5 stars)
     * Calculated from customer feedback
     */
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    /**
     * Total number of ratings received
     * Used for rating calculation accuracy
     */
    @Column(name = "rating_count")
    private Long ratingCount = 0L;

    /**
     * Theater popularity score (calculated field)
     * Based on bookings, ratings, and revenue
     */
    @Column(name = "popularity_score", precision = 5, scale = 2)
    private BigDecimal popularityScore = BigDecimal.ZERO;

    // ========================================
    // AUDIT FIELDS
    // ========================================

    /**
     * User who created this theater record
     * References admin or manager who added the theater
     */
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    /**
     * User who last updated this theater record
     */
    @Column(name = "updated_by_user_id")
    private Long updatedByUserId;

    /**
     * Timestamp when the theater was added to the system
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the theater information was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========================================
    // RELATIONSHIPS
    // ========================================

    /**
     * All bookings for this theater
     * One-to-Many relationship with TheaterBooking entity
     */
    @OneToMany(mappedBy = "privateTheater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TheaterBooking> bookings;

    /**
     * Customer feedback for this theater
     * One-to-Many relationship with CustomerFeedback entity
     */
    @OneToMany(mappedBy = "privateTheater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerFeedback> customerFeedbacks;

    // ========================================
    // ENUMERATIONS
    // ========================================

    /**
     * Theater Availability Status Enumeration
     */
    public enum AvailabilityStatus {
        AVAILABLE("Available for Booking"),
        MAINTENANCE("Under Maintenance"),
        BOOKED("Currently Booked"),
        INACTIVE("Temporarily Inactive");

        private final String displayName;

        AvailabilityStatus(String displayName) {
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
     * Get full address as a single string
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        address.append(streetAddress);
        if (landmark != null && !landmark.trim().isEmpty()) {
            address.append(", Near ").append(landmark);
        }
        address.append(", ").append(city);
        address.append(", ").append(state);
        address.append(" - ").append(postalCode);
        address.append(", ").append(country);
        return address.toString();
    }

    /**
     * Check if theater is currently available for booking
     */
    public boolean isAvailableForBooking() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE;
    }

    /**
     * Calculate effective hourly rate with weekend surcharge
     */
    public BigDecimal getEffectiveHourlyRate(boolean isWeekendOrHoliday) {
        if (isWeekendOrHoliday && weekendSurchargePercent != null && weekendSurchargePercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal surcharge = hourlyRate.multiply(weekendSurchargePercent).divide(new BigDecimal("100"));
            return hourlyRate.add(surcharge);
        }
        return hourlyRate;
    }

    /**
     * Get amenities as a list
     */
    public String[] getAmenitiesArray() {
        if (amenities == null || amenities.trim().isEmpty()) {
            return new String[0];
        }
        return amenities.split(",");
    }

    /**
     * Get star rating display (for UI)
     */
    public String getStarRatingDisplay() {
        if (averageRating == null || averageRating.compareTo(BigDecimal.ZERO) == 0) {
            return "No ratings yet";
        }
        return averageRating + "/5.0 ⭐ (" + ratingCount + " reviews)";
    }

    /**
     * Update revenue and booking count
     */
    public void updateBookingStats(BigDecimal bookingAmount) {
        if (totalBookings == null) totalBookings = 0L;
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        totalBookings++;
        totalRevenue = totalRevenue.add(bookingAmount);
    }

    /**
     * Update average rating
     */
    public void updateRating(BigDecimal newRating) {
        if (averageRating == null) averageRating = BigDecimal.ZERO;
        if (ratingCount == null) ratingCount = 0L;
        
        BigDecimal totalRating = averageRating.multiply(new BigDecimal(ratingCount));
        totalRating = totalRating.add(newRating);
        ratingCount++;
        averageRating = totalRating.divide(new BigDecimal(ratingCount), 2, BigDecimal.ROUND_HALF_UP);
    }
}