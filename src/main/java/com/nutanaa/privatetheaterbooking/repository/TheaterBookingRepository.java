package com.nutanaa.privatetheaterbooking.repository;

import com.nutanaa.privatetheaterbooking.model.TheaterBooking;
import com.nutanaa.privatetheaterbooking.model.TheaterBooking.BookingStatus;
import com.nutanaa.privatetheaterbooking.model.TheaterBooking.PaymentStatus;
import com.nutanaa.privatetheaterbooking.model.TheaterBooking.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * TheaterBookingRepository Interface
 * 
 * This repository interface provides comprehensive data access methods for TheaterBooking entities.
 * It includes queries for booking management, analytics, payment tracking, and business intelligence.
 * 
 * Features:
 * - Booking lifecycle management
 * - Payment status tracking
 * - Availability and conflict checking
 * - Revenue and analytics reporting
 * - Admin and manager dashboard queries
 * - Customer booking history
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Repository
public interface TheaterBookingRepository extends JpaRepository<TheaterBooking, Long> {

    // ========================================
    // BASIC FINDER METHODS
    // ========================================

    /**
     * Find booking by booking reference number
     */
    Optional<TheaterBooking> findByBookingReference(String bookingReference);

    /**
     * Check if booking reference exists
     */
    boolean existsByBookingReference(String bookingReference);

    /**
     * Find bookings by user ID
     */
    List<TheaterBooking> findByUserAccountUserId(Long userId);

    /**
     * Find bookings by user ID with pagination
     */
    Page<TheaterBooking> findByUserAccountUserId(Long userId, Pageable pageable);

    /**
     * Find bookings by theater ID
     */
    List<TheaterBooking> findByPrivateTheaterTheaterId(Long theaterId);

    /**
     * Find bookings by theater ID with pagination
     */
    Page<TheaterBooking> findByPrivateTheaterTheaterId(Long theaterId, Pageable pageable);

    // ========================================
    // STATUS-BASED QUERIES
    // ========================================

    /**
     * Find bookings by booking status
     */
    List<TheaterBooking> findByBookingStatus(BookingStatus status);

    /**
     * Find bookings by booking status with pagination
     */
    Page<TheaterBooking> findByBookingStatus(BookingStatus status, Pageable pageable);

    /**
     * Find bookings by payment status
     */
    List<TheaterBooking> findByPaymentStatus(PaymentStatus status);

    /**
     * Find bookings by payment status with pagination
     */
    Page<TheaterBooking> findByPaymentStatus(PaymentStatus status, Pageable pageable);

    /**
     * Find bookings by approval status
     */
    List<TheaterBooking> findByApprovalStatus(ApprovalStatus status);

    /**
     * Find bookings by approval status with pagination
     */
    Page<TheaterBooking> findByApprovalStatus(ApprovalStatus status, Pageable pageable);

    /**
     * Find pending bookings (awaiting payment or approval)
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.bookingStatus = 'PENDING' OR b.approvalStatus = 'PENDING'")
    List<TheaterBooking> findPendingBookings();

    /**
     * Find confirmed and paid bookings
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.paymentStatus = 'COMPLETED' AND " +
           "(b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED')")
    List<TheaterBooking> findConfirmedPaidBookings();

    // ========================================
    // DATE AND TIME BASED QUERIES
    // ========================================

    /**
     * Find bookings for a specific date
     */
    List<TheaterBooking> findByBookingDate(LocalDate date);

    /**
     * Find bookings between dates
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.bookingDate BETWEEN :startDate AND :endDate")
    List<TheaterBooking> findBookingsBetweenDates(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);

    /**
     * Find bookings for today
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.bookingDate = CURRENT_DATE")
    List<TheaterBooking> findTodaysBookings();

    /**
     * Find bookings for tomorrow
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.bookingDate = CURRENT_DATE + 1")
    List<TheaterBooking> findTomorrowsBookings();

    /**
     * Find upcoming bookings (future bookings)
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.bookingDate > CURRENT_DATE OR " +
           "(b.bookingDate = CURRENT_DATE AND b.startTime > CURRENT_TIME)")
    List<TheaterBooking> findUpcomingBookings();

    /**
     * Find past bookings
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.bookingDate < CURRENT_DATE OR " +
           "(b.bookingDate = CURRENT_DATE AND b.endTime < CURRENT_TIME)")
    List<TheaterBooking> findPastBookings();

    /**
     * Find bookings created between timestamps
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.createdAt BETWEEN :startTime AND :endTime")
    List<TheaterBooking> findBookingsCreatedBetween(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    // ========================================
    // CONFLICT AND AVAILABILITY CHECKING
    // ========================================

    /**
     * Check for booking conflicts (same theater, overlapping time)
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "b.privateTheater.theaterId = :theaterId AND " +
           "b.bookingDate = :bookingDate AND " +
           "(b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED') AND " +
           "((b.startTime <= :endTime AND b.endTime >= :startTime))")
    List<TheaterBooking> findConflictingBookings(@Param("theaterId") Long theaterId,
                                               @Param("bookingDate") LocalDate bookingDate,
                                               @Param("startTime") LocalTime startTime,
                                               @Param("endTime") LocalTime endTime);

    /**
     * Find bookings for a theater on a specific date
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "b.privateTheater.theaterId = :theaterId AND " +
           "b.bookingDate = :bookingDate AND " +
           "(b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED')")
    List<TheaterBooking> findTheaterBookingsForDate(@Param("theaterId") Long theaterId, 
                                                   @Param("bookingDate") LocalDate bookingDate);

    /**
     * Check if theater is available for specific time slot
     */
    @Query("SELECT COUNT(b) = 0 FROM TheaterBooking b WHERE " +
           "b.privateTheater.theaterId = :theaterId AND " +
           "b.bookingDate = :bookingDate AND " +
           "(b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED') AND " +
           "((b.startTime < :endTime AND b.endTime > :startTime))")
    boolean isTheaterAvailable(@Param("theaterId") Long theaterId,
                              @Param("bookingDate") LocalDate bookingDate,
                              @Param("startTime") LocalTime startTime,
                              @Param("endTime") LocalTime endTime);

    // ========================================
    // ADMIN AND MANAGER DASHBOARD QUERIES
    // ========================================

    /**
     * Find bookings requiring approval
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.approvalStatus = 'PENDING' AND b.paymentStatus = 'COMPLETED'")
    List<TheaterBooking> findBookingsRequiringApproval();

    /**
     * Find bookings approved by specific admin/manager
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.approvedByUserId = :userId")
    List<TheaterBooking> findBookingsApprovedBy(@Param("userId") Long userId);

    /**
     * Find bookings for manager dashboard (today and tomorrow)
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "(b.bookingDate = CURRENT_DATE OR b.bookingDate = CURRENT_DATE + 1) AND " +
           "(b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED')")
    List<TheaterBooking> findManagerDashboardBookings();

    /**
     * Find recent bookings for admin dashboard
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.createdAt >= :since ORDER BY b.createdAt DESC")
    List<TheaterBooking> findRecentBookings(@Param("since") LocalDateTime since, Pageable pageable);

    // ========================================
    // ANALYTICS AND REPORTING QUERIES
    // ========================================

    /**
     * Get booking statistics summary
     */
    @Query("SELECT " +
           "COUNT(b) as totalBookings, " +
           "COUNT(CASE WHEN b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED' THEN 1 END) as confirmedBookings, " +
           "COUNT(CASE WHEN b.paymentStatus = 'COMPLETED' THEN 1 END) as paidBookings, " +
           "COUNT(CASE WHEN b.bookingStatus = 'CANCELLED' THEN 1 END) as cancelledBookings, " +
           "SUM(CASE WHEN b.paymentStatus = 'COMPLETED' THEN b.totalAmount ELSE 0 END) as totalRevenue, " +
           "AVG(CASE WHEN b.paymentStatus = 'COMPLETED' THEN b.totalAmount ELSE NULL END) as avgBookingValue " +
           "FROM TheaterBooking b")
    Object[] getBookingStatistics();

    /**
     * Get daily booking counts
     */
    @Query("SELECT DATE(b.bookingDate) as date, COUNT(b) as count " +
           "FROM TheaterBooking b " +
           "WHERE b.bookingDate >= :startDate " +
           "GROUP BY DATE(b.bookingDate) " +
           "ORDER BY DATE(b.bookingDate)")
    List<Object[]> getDailyBookingCounts(@Param("startDate") LocalDate startDate);

    /**
     * Get daily revenue
     */
    @Query("SELECT DATE(b.bookingDate) as date, SUM(b.totalAmount) as revenue " +
           "FROM TheaterBooking b " +
           "WHERE b.paymentStatus = 'COMPLETED' AND b.bookingDate >= :startDate " +
           "GROUP BY DATE(b.bookingDate) " +
           "ORDER BY DATE(b.bookingDate)")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDate startDate);

    /**
     * Get monthly revenue summary
     */
    @Query("SELECT YEAR(b.bookingDate) as year, MONTH(b.bookingDate) as month, " +
           "COUNT(b) as bookingCount, SUM(b.totalAmount) as totalRevenue " +
           "FROM TheaterBooking b " +
           "WHERE b.paymentStatus = 'COMPLETED' " +
           "GROUP BY YEAR(b.bookingDate), MONTH(b.bookingDate) " +
           "ORDER BY YEAR(b.bookingDate), MONTH(b.bookingDate)")
    List<Object[]> getMonthlyRevenueSummary();

    /**
     * Get theater performance analytics
     */
    @Query("SELECT t.theaterName, COUNT(b) as bookingCount, SUM(b.totalAmount) as revenue, " +
           "AVG(b.totalAmount) as avgBookingValue " +
           "FROM TheaterBooking b JOIN b.privateTheater t " +
           "WHERE b.paymentStatus = 'COMPLETED' " +
           "GROUP BY t.theaterId, t.theaterName " +
           "ORDER BY SUM(b.totalAmount) DESC")
    List<Object[]> getTheaterPerformanceAnalytics();

    /**
     * Get booking status distribution
     */
    @Query("SELECT b.bookingStatus, COUNT(b) FROM TheaterBooking b GROUP BY b.bookingStatus")
    List<Object[]> getBookingStatusDistribution();

    /**
     * Get payment status distribution
     */
    @Query("SELECT b.paymentStatus, COUNT(b) FROM TheaterBooking b GROUP BY b.paymentStatus")
    List<Object[]> getPaymentStatusDistribution();

    // ========================================
    // CUSTOMER ANALYTICS
    // ========================================

    /**
     * Find top customers by booking count
     */
    @Query("SELECT u.fullName, u.mobileNumber, COUNT(b) as bookingCount, SUM(b.totalAmount) as totalSpent " +
           "FROM TheaterBooking b JOIN b.userAccount u " +
           "WHERE b.paymentStatus = 'COMPLETED' " +
           "GROUP BY u.userId, u.fullName, u.mobileNumber " +
           "ORDER BY COUNT(b) DESC")
    List<Object[]> getTopCustomersByBookingCount(Pageable pageable);

    /**
     * Find top customers by revenue
     */
    @Query("SELECT u.fullName, u.mobileNumber, COUNT(b) as bookingCount, SUM(b.totalAmount) as totalSpent " +
           "FROM TheaterBooking b JOIN b.userAccount u " +
           "WHERE b.paymentStatus = 'COMPLETED' " +
           "GROUP BY u.userId, u.fullName, u.mobileNumber " +
           "ORDER BY SUM(b.totalAmount) DESC")
    List<Object[]> getTopCustomersByRevenue(Pageable pageable);

    /**
     * Get customer booking history
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.userAccount.userId = :userId ORDER BY b.createdAt DESC")
    Page<TheaterBooking> getCustomerBookingHistory(@Param("userId") Long userId, Pageable pageable);

    // ========================================
    // REVENUE AND FINANCIAL QUERIES
    // ========================================

    /**
     * Calculate total revenue for a date range
     */
    @Query("SELECT SUM(b.totalAmount) FROM TheaterBooking b WHERE " +
           "b.paymentStatus = 'COMPLETED' AND " +
           "b.bookingDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueForPeriod(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);

    /**
     * Calculate total revenue for a specific theater
     */
    @Query("SELECT SUM(b.totalAmount) FROM TheaterBooking b WHERE " +
           "b.privateTheater.theaterId = :theaterId AND b.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalRevenueForTheater(@Param("theaterId") Long theaterId);

    /**
     * Get revenue breakdown by payment method
     */
    @Query("SELECT b.paymentMethod, COUNT(b) as count, SUM(b.totalAmount) as revenue " +
           "FROM TheaterBooking b " +
           "WHERE b.paymentStatus = 'COMPLETED' AND b.paymentMethod IS NOT NULL " +
           "GROUP BY b.paymentMethod " +
           "ORDER BY SUM(b.totalAmount) DESC")
    List<Object[]> getRevenueByPaymentMethod();

    /**
     * Find high-value bookings
     */
    @Query("SELECT b FROM TheaterBooking b WHERE b.totalAmount >= :minAmount ORDER BY b.totalAmount DESC")
    List<TheaterBooking> findHighValueBookings(@Param("minAmount") BigDecimal minAmount, Pageable pageable);

    // ========================================
    // NOTIFICATION AND REMINDER QUERIES
    // ========================================

    /**
     * Find bookings requiring reminder notifications
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "b.bookingDate = CURRENT_DATE + 1 AND " +
           "(b.bookingStatus = 'CONFIRMED' OR b.bookingStatus = 'APPROVED') AND " +
           "b.reminderCount < 2")
    List<TheaterBooking> findBookingsRequiringReminders();

    /**
     * Find bookings with failed notifications
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "(b.emailSent = false OR b.smsSent = false OR b.whatsappSent = false) AND " +
           "b.paymentStatus = 'COMPLETED'")
    List<TheaterBooking> findBookingsWithFailedNotifications();

    // ========================================
    // SEARCH AND FILTERING
    // ========================================

    /**
     * Search bookings by customer name or mobile
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "LOWER(b.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "b.customerMobile LIKE CONCAT('%', :search, '%')")
    List<TheaterBooking> searchBookingsByCustomer(@Param("search") String search);

    /**
     * Search bookings by theater name
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "LOWER(b.privateTheater.theaterName) LIKE LOWER(CONCAT('%', :theaterName, '%'))")
    List<TheaterBooking> searchBookingsByTheaterName(@Param("theaterName") String theaterName);

    /**
     * Advanced booking search with multiple criteria
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "(:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus) AND " +
           "(:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus) AND " +
           "(:startDate IS NULL OR b.bookingDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.bookingDate <= :endDate) AND " +
           "(:theaterId IS NULL OR b.privateTheater.theaterId = :theaterId) AND " +
           "(:userId IS NULL OR b.userAccount.userId = :userId) AND " +
           "(:minAmount IS NULL OR b.totalAmount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR b.totalAmount <= :maxAmount)")
    Page<TheaterBooking> findBookingsWithFilters(
            @Param("bookingStatus") BookingStatus bookingStatus,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("theaterId") Long theaterId,
            @Param("userId") Long userId,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable);

    // ========================================
    // UPDATE OPERATIONS
    // ========================================

    /**
     * Update booking status
     */
    @Modifying
    @Query("UPDATE TheaterBooking b SET b.bookingStatus = :status WHERE b.bookingId = :bookingId")
    void updateBookingStatus(@Param("bookingId") Long bookingId, @Param("status") BookingStatus status);

    /**
     * Update payment status
     */
    @Modifying
    @Query("UPDATE TheaterBooking b SET b.paymentStatus = :status, b.paymentCompletedAt = :completedAt " +
           "WHERE b.bookingId = :bookingId")
    void updatePaymentStatus(@Param("bookingId") Long bookingId, 
                           @Param("status") PaymentStatus status, 
                           @Param("completedAt") LocalDateTime completedAt);

    /**
     * Update approval status
     */
    @Modifying
    @Query("UPDATE TheaterBooking b SET b.approvalStatus = :status, b.approvedByUserId = :approvedBy, " +
           "b.approvalTimestamp = :timestamp, b.approvalComments = :comments " +
           "WHERE b.bookingId = :bookingId")
    void updateApprovalStatus(@Param("bookingId") Long bookingId,
                            @Param("status") ApprovalStatus status,
                            @Param("approvedBy") Long approvedBy,
                            @Param("timestamp") LocalDateTime timestamp,
                            @Param("comments") String comments);

    /**
     * Update notification status
     */
    @Modifying
    @Query("UPDATE TheaterBooking b SET b.emailSent = :emailSent, b.smsSent = :smsSent, " +
           "b.whatsappSent = :whatsappSent WHERE b.bookingId = :bookingId")
    void updateNotificationStatus(@Param("bookingId") Long bookingId,
                                @Param("emailSent") Boolean emailSent,
                                @Param("smsSent") Boolean smsSent,
                                @Param("whatsappSent") Boolean whatsappSent);

    /**
     * Increment reminder count
     */
    @Modifying
    @Query("UPDATE TheaterBooking b SET b.reminderCount = b.reminderCount + 1 WHERE b.bookingId = :bookingId")
    void incrementReminderCount(@Param("bookingId") Long bookingId);

    // ========================================
    // CLEANUP AND MAINTENANCE
    // ========================================

    /**
     * Find expired pending bookings (for cleanup)
     */
    @Query("SELECT b FROM TheaterBooking b WHERE " +
           "b.bookingStatus = 'PENDING' AND " +
           "b.createdAt < :expiryThreshold")
    List<TheaterBooking> findExpiredPendingBookings(@Param("expiryThreshold") LocalDateTime expiryThreshold);

    /**
     * Delete old cancelled bookings
     */
    @Modifying
    @Query("DELETE FROM TheaterBooking b WHERE " +
           "b.bookingStatus = 'CANCELLED' AND " +
           "b.updatedAt < :deleteThreshold")
    void deleteOldCancelledBookings(@Param("deleteThreshold") LocalDateTime deleteThreshold);
}