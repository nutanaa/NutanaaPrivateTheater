package com.nutanaa.privatetheaterbooking.repository;

import com.nutanaa.privatetheaterbooking.model.PrivateTheater;
import com.nutanaa.privatetheaterbooking.model.PrivateTheater.AvailabilityStatus;
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
 * PrivateTheaterRepository Interface
 * 
 * This repository interface provides data access methods for PrivateTheater entities.
 * It includes custom queries for theater search, filtering, availability checking,
 * and analytics operations.
 * 
 * Features:
 * - Theater search and filtering operations
 * - Availability checking and booking validation
 * - Location-based theater discovery
 * - Pricing and capacity queries
 * - Analytics and reporting queries
 * - Revenue and booking statistics
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Repository
public interface PrivateTheaterRepository extends JpaRepository<PrivateTheater, Long> {

    // ========================================
    // BASIC FINDER METHODS
    // ========================================

    /**
     * Find theater by name (exact match)
     */
    Optional<PrivateTheater> findByTheaterName(String theaterName);

    /**
     * Find theaters by availability status
     */
    List<PrivateTheater> findByAvailabilityStatus(AvailabilityStatus status);

    /**
     * Find theaters by availability status with pagination
     */
    Page<PrivateTheater> findByAvailabilityStatus(AvailabilityStatus status, Pageable pageable);

    /**
     * Check if theater name already exists
     */
    boolean existsByTheaterName(String theaterName);

    // ========================================
    // LOCATION-BASED QUERIES
    // ========================================

    /**
     * Find theaters by city
     */
    List<PrivateTheater> findByCity(String city);

    /**
     * Find theaters by city with pagination
     */
    Page<PrivateTheater> findByCity(String city, Pageable pageable);

    /**
     * Find theaters by state
     */
    List<PrivateTheater> findByState(String state);

    /**
     * Find theaters by city and state
     */
    List<PrivateTheater> findByCityAndState(String city, String state);

    /**
     * Find theaters by postal code
     */
    List<PrivateTheater> findByPostalCode(String postalCode);

    /**
     * Search theaters by location (city, state, or postal code)
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "LOWER(t.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(t.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "t.postalCode LIKE CONCAT('%', :location, '%')")
    List<PrivateTheater> findByLocationContaining(@Param("location") String location);

    // ========================================
    // SEARCH AND FILTERING
    // ========================================

    /**
     * Search theaters by name (partial match, case-insensitive)
     */
    @Query("SELECT t FROM PrivateTheater t WHERE LOWER(t.theaterName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PrivateTheater> findByTheaterNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search theaters by name with pagination
     */
    @Query("SELECT t FROM PrivateTheater t WHERE LOWER(t.theaterName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<PrivateTheater> findByTheaterNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "(:name IS NULL OR LOWER(t.theaterName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:city IS NULL OR LOWER(t.city) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(t.state) = LOWER(:state)) AND " +
           "(:status IS NULL OR t.availabilityStatus = :status) AND " +
           "(:minCapacity IS NULL OR t.seatingCapacity >= :minCapacity) AND " +
           "(:maxCapacity IS NULL OR t.seatingCapacity <= :maxCapacity) AND " +
           "(:minRate IS NULL OR t.hourlyRate >= :minRate) AND " +
           "(:maxRate IS NULL OR t.hourlyRate <= :maxRate)")
    Page<PrivateTheater> findWithFilters(
            @Param("name") String name,
            @Param("city") String city,
            @Param("state") String state,
            @Param("status") AvailabilityStatus status,
            @Param("minCapacity") Integer minCapacity,
            @Param("maxCapacity") Integer maxCapacity,
            @Param("minRate") BigDecimal minRate,
            @Param("maxRate") BigDecimal maxRate,
            Pageable pageable);

    // ========================================
    // AVAILABILITY AND BOOKING QUERIES
    // ========================================

    /**
     * Find available theaters for booking
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.availabilityStatus = 'AVAILABLE'")
    List<PrivateTheater> findAvailableTheaters();

    /**
     * Find available theaters with pagination
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.availabilityStatus = 'AVAILABLE'")
    Page<PrivateTheater> findAvailableTheaters(Pageable pageable);

    /**
     * Find theaters by capacity range
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.seatingCapacity BETWEEN :minCapacity AND :maxCapacity")
    List<PrivateTheater> findByCapacityRange(@Param("minCapacity") Integer minCapacity, 
                                           @Param("maxCapacity") Integer maxCapacity);

    /**
     * Find theaters by hourly rate range
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.hourlyRate BETWEEN :minRate AND :maxRate")
    List<PrivateTheater> findByHourlyRateRange(@Param("minRate") BigDecimal minRate, 
                                             @Param("maxRate") BigDecimal maxRate);

    /**
     * Find theaters that can accommodate specific number of guests
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.seatingCapacity >= :requiredCapacity AND t.availabilityStatus = 'AVAILABLE'")
    List<PrivateTheater> findTheatersByMinimumCapacity(@Param("requiredCapacity") Integer requiredCapacity);

    // ========================================
    // GEOGRAPHIC AND DISTANCE QUERIES
    // ========================================

    /**
     * Find theaters within a distance radius (using Haversine formula)
     * Note: This is a simplified version. For production, consider using PostGIS or similar
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "t.latitude IS NOT NULL AND t.longitude IS NOT NULL AND " +
           "(6371 * ACOS(COS(RADIANS(:userLat)) * COS(RADIANS(t.latitude)) * " +
           "COS(RADIANS(t.longitude) - RADIANS(:userLng)) + " +
           "SIN(RADIANS(:userLat)) * SIN(RADIANS(t.latitude)))) <= :radiusKm")
    List<PrivateTheater> findTheatersWithinRadius(
            @Param("userLat") BigDecimal userLatitude,
            @Param("userLng") BigDecimal userLongitude,
            @Param("radiusKm") Double radiusKm);

    /**
     * Find theaters by landmark or nearby reference
     */
    @Query("SELECT t FROM PrivateTheater t WHERE LOWER(t.landmark) LIKE LOWER(CONCAT('%', :landmark, '%'))")
    List<PrivateTheater> findByLandmarkContaining(@Param("landmark") String landmark);

    // ========================================
    // ANALYTICS AND REPORTING QUERIES
    // ========================================

    /**
     * Get theater statistics summary
     */
    @Query("SELECT " +
           "COUNT(t) as totalTheaters, " +
           "COUNT(CASE WHEN t.availabilityStatus = 'AVAILABLE' THEN 1 END) as availableTheaters, " +
           "COUNT(CASE WHEN t.availabilityStatus = 'BOOKED' THEN 1 END) as bookedTheaters, " +
           "COUNT(CASE WHEN t.availabilityStatus = 'MAINTENANCE' THEN 1 END) as maintenanceTheaters, " +
           "AVG(t.hourlyRate) as avgHourlyRate, " +
           "AVG(t.seatingCapacity) as avgCapacity, " +
           "SUM(t.totalRevenue) as totalRevenue " +
           "FROM PrivateTheater t")
    Object[] getTheaterStatistics();

    /**
     * Count theaters by city
     */
    @Query("SELECT t.city, COUNT(t) FROM PrivateTheater t GROUP BY t.city ORDER BY COUNT(t) DESC")
    List<Object[]> countTheatersByCity();

    /**
     * Count theaters by state
     */
    @Query("SELECT t.state, COUNT(t) FROM PrivateTheater t GROUP BY t.state ORDER BY COUNT(t) DESC")
    List<Object[]> countTheatersByState();

    /**
     * Count theaters by availability status
     */
    @Query("SELECT t.availabilityStatus, COUNT(t) FROM PrivateTheater t GROUP BY t.availabilityStatus")
    List<Object[]> countTheatersByAvailabilityStatus();

    /**
     * Get top theaters by revenue
     */
    @Query("SELECT t FROM PrivateTheater t ORDER BY t.totalRevenue DESC")
    List<PrivateTheater> findTopTheatersByRevenue(Pageable pageable);

    /**
     * Get top theaters by booking count
     */
    @Query("SELECT t FROM PrivateTheater t ORDER BY t.totalBookings DESC")
    List<PrivateTheater> findTopTheatersByBookings(Pageable pageable);

    /**
     * Get top theaters by rating
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.averageRating > 0 ORDER BY t.averageRating DESC, t.ratingCount DESC")
    List<PrivateTheater> findTopTheatersByRating(Pageable pageable);

    /**
     * Get theaters with low performance (for improvement analysis)
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "t.totalBookings < :minBookings OR " +
           "(t.averageRating < :minRating AND t.ratingCount > :minRatingCount)")
    List<PrivateTheater> findLowPerformingTheaters(
            @Param("minBookings") Long minBookings,
            @Param("minRating") BigDecimal minRating,
            @Param("minRatingCount") Long minRatingCount);

    // ========================================
    // PRICING ANALYSIS QUERIES
    // ========================================

    /**
     * Get price range statistics for theaters
     */
    @Query("SELECT " +
           "MIN(t.hourlyRate) as minPrice, " +
           "MAX(t.hourlyRate) as maxPrice, " +
           "AVG(t.hourlyRate) as avgPrice " +
           "FROM PrivateTheater t WHERE t.availabilityStatus = 'AVAILABLE'")
    Object[] getPriceRangeStatistics();

    /**
     * Find theaters by price category
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "(:category = 'BUDGET' AND t.hourlyRate <= :budgetMax) OR " +
           "(:category = 'PREMIUM' AND t.hourlyRate >= :premiumMin) OR " +
           "(:category = 'LUXURY' AND t.hourlyRate >= :luxuryMin)")
    List<PrivateTheater> findTheatersByPriceCategory(
            @Param("category") String category,
            @Param("budgetMax") BigDecimal budgetMax,
            @Param("premiumMin") BigDecimal premiumMin,
            @Param("luxuryMin") BigDecimal luxuryMin);

    // ========================================
    // MAINTENANCE AND MANAGEMENT QUERIES
    // ========================================

    /**
     * Find theaters that need attention (low rating or no recent bookings)
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "(t.averageRating < 3.0 AND t.ratingCount >= 5) OR " +
           "t.totalBookings = 0 OR " +
           "(t.updatedAt < :oldDate)")
    List<PrivateTheater> findTheatersNeedingAttention(@Param("oldDate") LocalDateTime oldDate);

    /**
     * Find theaters created by specific user
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.createdByUserId = :userId")
    List<PrivateTheater> findTheatersByCreatedBy(@Param("userId") Long userId);

    /**
     * Find theaters updated by specific user
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.updatedByUserId = :userId")
    List<PrivateTheater> findTheatersByUpdatedBy(@Param("userId") Long userId);

    // ========================================
    // UPDATE OPERATIONS
    // ========================================

    /**
     * Update theater availability status
     */
    @Modifying
    @Query("UPDATE PrivateTheater t SET t.availabilityStatus = :status WHERE t.theaterId = :theaterId")
    void updateAvailabilityStatus(@Param("theaterId") Long theaterId, @Param("status") AvailabilityStatus status);

    /**
     * Update theater revenue and booking count
     */
    @Modifying
    @Query("UPDATE PrivateTheater t SET " +
           "t.totalRevenue = t.totalRevenue + :amount, " +
           "t.totalBookings = t.totalBookings + 1 " +
           "WHERE t.theaterId = :theaterId")
    void updateRevenueAndBookingCount(@Param("theaterId") Long theaterId, @Param("amount") BigDecimal amount);

    /**
     * Update theater rating
     */
    @Modifying
    @Query("UPDATE PrivateTheater t SET " +
           "t.averageRating = :newRating, " +
           "t.ratingCount = t.ratingCount + 1 " +
           "WHERE t.theaterId = :theaterId")
    void updateTheaterRating(@Param("theaterId") Long theaterId, @Param("newRating") BigDecimal newRating);

    /**
     * Update theater popularity score
     */
    @Modifying
    @Query("UPDATE PrivateTheater t SET t.popularityScore = :score WHERE t.theaterId = :theaterId")
    void updatePopularityScore(@Param("theaterId") Long theaterId, @Param("score") BigDecimal score);

    // ========================================
    // AMENITIES AND FEATURES QUERIES
    // ========================================

    /**
     * Find theaters with specific amenities
     */
    @Query("SELECT t FROM PrivateTheater t WHERE LOWER(t.amenities) LIKE LOWER(CONCAT('%', :amenity, '%'))")
    List<PrivateTheater> findTheatersByAmenity(@Param("amenity") String amenity);

    /**
     * Find theaters with specific special features
     */
    @Query("SELECT t FROM PrivateTheater t WHERE LOWER(t.specialFeatures) LIKE LOWER(CONCAT('%', :feature, '%'))")
    List<PrivateTheater> findTheatersBySpecialFeature(@Param("feature") String feature);

    /**
     * Find theaters with accessibility features
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.accessibilityFeatures IS NOT NULL AND t.accessibilityFeatures != ''")
    List<PrivateTheater> findTheatersWithAccessibilityFeatures();

    /**
     * Find theaters by sound system rating
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.soundSystemRating >= :minRating")
    List<PrivateTheater> findTheatersBySoundRating(@Param("minRating") Integer minRating);

    /**
     * Find theaters by video quality rating
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.videoQualityRating >= :minRating")
    List<PrivateTheater> findTheatersByVideoRating(@Param("minRating") Integer minRating);

    // ========================================
    // OPERATIONAL QUERIES
    // ========================================

    /**
     * Find theaters with specific operating hours
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "t.operatingHoursStart <= :requestedStart AND t.operatingHoursEnd >= :requestedEnd")
    List<PrivateTheater> findTheatersByOperatingHours(
            @Param("requestedStart") String requestedStart,
            @Param("requestedEnd") String requestedEnd);

    /**
     * Find theaters with minimum booking duration compatibility
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.minimumBookingHours <= :requestedHours")
    List<PrivateTheater> findTheatersByMinBookingHours(@Param("requestedHours") Integer requestedHours);

    /**
     * Find theaters with maximum booking duration compatibility
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.maximumBookingHours >= :requestedHours")
    List<PrivateTheater> findTheatersByMaxBookingHours(@Param("requestedHours") Integer requestedHours);

    /**
     * Find theaters with flexible cancellation policy
     */
    @Query("SELECT t FROM PrivateTheater t WHERE t.cancellationPolicyHours <= :maxCancellationHours")
    List<PrivateTheater> findTheatersWithFlexibleCancellation(@Param("maxCancellationHours") Integer maxCancellationHours);

    // ========================================
    // RECOMMENDATION QUERIES
    // ========================================

    /**
     * Find recommended theaters based on multiple factors
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "t.availabilityStatus = 'AVAILABLE' AND " +
           "t.averageRating >= 4.0 AND " +
           "t.seatingCapacity >= :minCapacity " +
           "ORDER BY t.popularityScore DESC, t.averageRating DESC")
    List<PrivateTheater> findRecommendedTheaters(@Param("minCapacity") Integer minCapacity, Pageable pageable);

    /**
     * Find similar theaters (same city, similar capacity and price range)
     */
    @Query("SELECT t FROM PrivateTheater t WHERE " +
           "t.theaterId != :theaterId AND " +
           "t.city = :city AND " +
           "t.availabilityStatus = 'AVAILABLE' AND " +
           "ABS(t.seatingCapacity - :capacity) <= 10 AND " +
           "ABS(t.hourlyRate - :hourlyRate) <= :priceRange " +
           "ORDER BY t.averageRating DESC")
    List<PrivateTheater> findSimilarTheaters(
            @Param("theaterId") Long theaterId,
            @Param("city") String city,
            @Param("capacity") Integer capacity,
            @Param("hourlyRate") BigDecimal hourlyRate,
            @Param("priceRange") BigDecimal priceRange,
            Pageable pageable);
}