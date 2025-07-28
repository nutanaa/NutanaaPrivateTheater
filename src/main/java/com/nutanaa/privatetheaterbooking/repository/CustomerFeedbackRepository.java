package com.nutanaa.privatetheaterbooking.repository;

import com.nutanaa.privatetheaterbooking.model.CustomerFeedback;
import com.nutanaa.privatetheaterbooking.model.CustomerFeedback.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CustomerFeedbackRepository Interface
 * 
 * This repository interface provides data access methods for CustomerFeedback entities.
 * It supports feedback management, moderation, analytics, and public display operations.
 * 
 * Features:
 * - Feedback CRUD operations
 * - Public feedback display queries
 * - Admin moderation and management
 * - Analytics and reporting
 * - Rating calculations and statistics
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {

    // ========================================
    // BASIC FINDER METHODS
    // ========================================

    /**
     * Find feedback by user ID
     */
    List<CustomerFeedback> findByUserAccountUserId(Long userId);

    /**
     * Find feedback by theater ID
     */
    List<CustomerFeedback> findByPrivateTheaterTheaterId(Long theaterId);

    /**
     * Find feedback by theater ID with pagination
     */
    Page<CustomerFeedback> findByPrivateTheaterTheaterId(Long theaterId, Pageable pageable);

    /**
     * Find feedback by booking ID
     */
    List<CustomerFeedback> findByTheaterBookingBookingId(Long bookingId);

    /**
     * Find feedback by status
     */
    List<CustomerFeedback> findByFeedbackStatus(FeedbackStatus status);

    /**
     * Find feedback by status with pagination
     */
    Page<CustomerFeedback> findByFeedbackStatus(FeedbackStatus status, Pageable pageable);

    // ========================================
    // PUBLIC DISPLAY QUERIES
    // ========================================

    /**
     * Find public visible feedback for a theater
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') " +
           "ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findPublicFeedbackForTheater(@Param("theaterId") Long theaterId);

    /**
     * Find public visible feedback for a theater with pagination
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') " +
           "ORDER BY f.createdAt DESC")
    Page<CustomerFeedback> findPublicFeedbackForTheater(@Param("theaterId") Long theaterId, Pageable pageable);

    /**
     * Find verified reviews for a theater
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isVerifiedReview = true AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') " +
           "ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findVerifiedFeedbackForTheater(@Param("theaterId") Long theaterId, Pageable pageable);

    /**
     * Find top-rated feedback for a theater
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') AND " +
           "f.overallRating >= :minRating " +
           "ORDER BY f.overallRating DESC, f.helpfulVotes DESC")
    List<CustomerFeedback> findTopRatedFeedbackForTheater(@Param("theaterId") Long theaterId, 
                                                         @Param("minRating") Integer minRating, 
                                                         Pageable pageable);

    /**
     * Find most helpful feedback for a theater
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') " +
           "ORDER BY f.helpfulVotes DESC, f.overallRating DESC")
    List<CustomerFeedback> findMostHelpfulFeedbackForTheater(@Param("theaterId") Long theaterId, Pageable pageable);

    // ========================================
    // ADMIN MODERATION QUERIES
    // ========================================

    /**
     * Find feedback pending moderation
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.feedbackStatus = 'PENDING' ORDER BY f.createdAt ASC")
    List<CustomerFeedback> findFeedbackPendingModeration();

    /**
     * Find feedback pending moderation with pagination
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.feedbackStatus = 'PENDING' ORDER BY f.createdAt ASC")
    Page<CustomerFeedback> findFeedbackPendingModeration(Pageable pageable);

    /**
     * Find feedback requiring attention (high report count)
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.reportCount >= :threshold ORDER BY f.reportCount DESC")
    List<CustomerFeedback> findFeedbackRequiringAttention(@Param("threshold") Integer threshold);

    /**
     * Find feedback moderated by specific admin
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.moderatedByUserId = :userId ORDER BY f.moderatedAt DESC")
    List<CustomerFeedback> findFeedbackModeratedBy(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find recently reported feedback
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.reportCount > 0 ORDER BY f.updatedAt DESC")
    List<CustomerFeedback> findRecentlyReportedFeedback(Pageable pageable);

    // ========================================
    // ANALYTICS AND STATISTICS QUERIES
    // ========================================

    /**
     * Get overall rating statistics for a theater
     */
    @Query("SELECT " +
           "COUNT(f) as totalReviews, " +
           "AVG(f.overallRating) as averageRating, " +
           "COUNT(CASE WHEN f.overallRating = 5 THEN 1 END) as fiveStarCount, " +
           "COUNT(CASE WHEN f.overallRating = 4 THEN 1 END) as fourStarCount, " +
           "COUNT(CASE WHEN f.overallRating = 3 THEN 1 END) as threeStarCount, " +
           "COUNT(CASE WHEN f.overallRating = 2 THEN 1 END) as twoStarCount, " +
           "COUNT(CASE WHEN f.overallRating = 1 THEN 1 END) as oneStarCount " +
           "FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED')")
    Object[] getRatingStatisticsForTheater(@Param("theaterId") Long theaterId);

    /**
     * Get detailed rating breakdown for a theater
     */
    @Query("SELECT " +
           "AVG(f.overallRating) as avgOverallRating, " +
           "AVG(f.soundQualityRating) as avgSoundRating, " +
           "AVG(f.videoQualityRating) as avgVideoRating, " +
           "AVG(f.cleanlinessRating) as avgCleanlinessRating, " +
           "AVG(f.serviceRating) as avgServiceRating, " +
           "AVG(f.valueForMoneyRating) as avgValueRating " +
           "FROM CustomerFeedback f WHERE " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED')")
    Object[] getDetailedRatingBreakdown(@Param("theaterId") Long theaterId);

    /**
     * Get feedback statistics by status
     */
    @Query("SELECT f.feedbackStatus, COUNT(f) FROM CustomerFeedback f GROUP BY f.feedbackStatus")
    List<Object[]> getFeedbackCountByStatus();

    /**
     * Get top theaters by average rating
     */
    @Query("SELECT t.theaterName, AVG(f.overallRating) as avgRating, COUNT(f) as reviewCount " +
           "FROM CustomerFeedback f JOIN f.privateTheater t WHERE " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') " +
           "GROUP BY t.theaterId, t.theaterName " +
           "HAVING COUNT(f) >= :minReviewCount " +
           "ORDER BY AVG(f.overallRating) DESC")
    List<Object[]> getTopTheatersByRating(@Param("minReviewCount") Long minReviewCount, Pageable pageable);

    /**
     * Get monthly feedback trends
     */
    @Query("SELECT YEAR(f.createdAt) as year, MONTH(f.createdAt) as month, COUNT(f) as feedbackCount " +
           "FROM CustomerFeedback f " +
           "WHERE f.createdAt >= :startDate " +
           "GROUP BY YEAR(f.createdAt), MONTH(f.createdAt) " +
           "ORDER BY YEAR(f.createdAt), MONTH(f.createdAt)")
    List<Object[]> getMonthlyFeedbackTrends(@Param("startDate") LocalDateTime startDate);

    // ========================================
    // SEARCH AND FILTERING QUERIES
    // ========================================

    /**
     * Search feedback by review content
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "LOWER(f.reviewComments) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.reviewTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CustomerFeedback> searchFeedbackByContent(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find feedback by rating range
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.overallRating BETWEEN :minRating AND :maxRating AND " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') " +
           "ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findFeedbackByRatingRange(@Param("minRating") Integer minRating, 
                                                    @Param("maxRating") Integer maxRating, 
                                                    Pageable pageable);

    /**
     * Find feedback by date range
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findFeedbackByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate, 
                                                  Pageable pageable);

    /**
     * Advanced feedback search with multiple criteria
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "(:theaterId IS NULL OR f.privateTheater.theaterId = :theaterId) AND " +
           "(:status IS NULL OR f.feedbackStatus = :status) AND " +
           "(:minRating IS NULL OR f.overallRating >= :minRating) AND " +
           "(:maxRating IS NULL OR f.overallRating <= :maxRating) AND " +
           "(:isVerified IS NULL OR f.isVerifiedReview = :isVerified) AND " +
           "(:isPublic IS NULL OR f.isPublicVisible = :isPublic) " +
           "ORDER BY f.createdAt DESC")
    Page<CustomerFeedback> findFeedbackWithFilters(
            @Param("theaterId") Long theaterId,
            @Param("status") FeedbackStatus status,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            @Param("isVerified") Boolean isVerified,
            @Param("isPublic") Boolean isPublic,
            Pageable pageable);

    // ========================================
    // USER INTERACTION QUERIES
    // ========================================

    /**
     * Check if user has already given feedback for a theater
     */
    @Query("SELECT COUNT(f) > 0 FROM CustomerFeedback f WHERE " +
           "f.userAccount.userId = :userId AND f.privateTheater.theaterId = :theaterId")
    boolean hasUserReviewedTheater(@Param("userId") Long userId, @Param("theaterId") Long theaterId);

    /**
     * Check if user has given feedback for a specific booking
     */
    @Query("SELECT COUNT(f) > 0 FROM CustomerFeedback f WHERE " +
           "f.userAccount.userId = :userId AND f.theaterBooking.bookingId = :bookingId")
    boolean hasUserReviewedBooking(@Param("userId") Long userId, @Param("bookingId") Long bookingId);

    /**
     * Find user's feedback history
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.userAccount.userId = :userId ORDER BY f.createdAt DESC")
    Page<CustomerFeedback> findUserFeedbackHistory(@Param("userId") Long userId, Pageable pageable);

    /**
     * Count user's total feedback submissions
     */
    @Query("SELECT COUNT(f) FROM CustomerFeedback f WHERE f.userAccount.userId = :userId")
    Long countUserFeedbackSubmissions(@Param("userId") Long userId);

    // ========================================
    // HELPFUL VOTES AND ENGAGEMENT QUERIES
    // ========================================

    /**
     * Find most helpful feedback overall
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.isPublicVisible = true AND " +
           "(f.feedbackStatus = 'APPROVED' OR f.feedbackStatus = 'EDITED') AND " +
           "f.helpfulVotes > 0 " +
           "ORDER BY f.helpfulVotes DESC, f.overallRating DESC")
    List<CustomerFeedback> findMostHelpfulFeedbackOverall(Pageable pageable);

    /**
     * Find feedback with high engagement (votes and responses)
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "(f.helpfulVotes + f.notHelpfulVotes) > :minVotes OR " +
           "f.adminResponse IS NOT NULL " +
           "ORDER BY (f.helpfulVotes + f.notHelpfulVotes) DESC")
    List<CustomerFeedback> findHighEngagementFeedback(@Param("minVotes") Integer minVotes, Pageable pageable);

    // ========================================
    // ADMIN RESPONSE QUERIES
    // ========================================

    /**
     * Find feedback with admin responses
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.adminResponse IS NOT NULL ORDER BY f.adminResponseDate DESC")
    List<CustomerFeedback> findFeedbackWithAdminResponses(Pageable pageable);

    /**
     * Find feedback without admin responses (high rating or complaints)
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.adminResponse IS NULL AND " +
           "(f.overallRating <= 2 OR f.reportCount > 0) AND " +
           "f.isPublicVisible = true " +
           "ORDER BY f.overallRating ASC, f.reportCount DESC")
    List<CustomerFeedback> findFeedbackNeedingAdminResponse(Pageable pageable);

    /**
     * Find admin responses by specific admin
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE f.adminResponseByUserId = :adminId ORDER BY f.adminResponseDate DESC")
    List<CustomerFeedback> findAdminResponsesByAdmin(@Param("adminId") Long adminId, Pageable pageable);

    // ========================================
    // UPDATE OPERATIONS
    // ========================================

    /**
     * Update feedback status
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET f.feedbackStatus = :status WHERE f.feedbackId = :feedbackId")
    void updateFeedbackStatus(@Param("feedbackId") Long feedbackId, @Param("status") FeedbackStatus status);

    /**
     * Update public visibility
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET f.isPublicVisible = :isVisible WHERE f.feedbackId = :feedbackId")
    void updatePublicVisibility(@Param("feedbackId") Long feedbackId, @Param("isVisible") Boolean isVisible);

    /**
     * Increment helpful votes
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET f.helpfulVotes = f.helpfulVotes + 1 WHERE f.feedbackId = :feedbackId")
    void incrementHelpfulVotes(@Param("feedbackId") Long feedbackId);

    /**
     * Increment not helpful votes
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET f.notHelpfulVotes = f.notHelpfulVotes + 1 WHERE f.feedbackId = :feedbackId")
    void incrementNotHelpfulVotes(@Param("feedbackId") Long feedbackId);

    /**
     * Increment report count
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET f.reportCount = f.reportCount + 1 WHERE f.feedbackId = :feedbackId")
    void incrementReportCount(@Param("feedbackId") Long feedbackId);

    /**
     * Add admin response
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET " +
           "f.adminResponse = :response, " +
           "f.adminResponseDate = :responseDate, " +
           "f.adminResponseByUserId = :adminId " +
           "WHERE f.feedbackId = :feedbackId")
    void addAdminResponse(@Param("feedbackId") Long feedbackId,
                         @Param("response") String response,
                         @Param("responseDate") LocalDateTime responseDate,
                         @Param("adminId") Long adminId);

    /**
     * Update moderation details
     */
    @Modifying
    @Query("UPDATE CustomerFeedback f SET " +
           "f.feedbackStatus = :status, " +
           "f.moderatedByUserId = :moderatorId, " +
           "f.moderatedAt = :moderatedAt, " +
           "f.moderationNotes = :notes " +
           "WHERE f.feedbackId = :feedbackId")
    void updateModerationDetails(@Param("feedbackId") Long feedbackId,
                                @Param("status") FeedbackStatus status,
                                @Param("moderatorId") Long moderatorId,
                                @Param("moderatedAt") LocalDateTime moderatedAt,
                                @Param("notes") String notes);

    // ========================================
    // CLEANUP OPERATIONS
    // ========================================

    /**
     * Delete old rejected feedback
     */
    @Modifying
    @Query("DELETE FROM CustomerFeedback f WHERE " +
           "f.feedbackStatus = 'REJECTED' AND " +
           "f.moderatedAt < :deleteThreshold")
    void deleteOldRejectedFeedback(@Param("deleteThreshold") LocalDateTime deleteThreshold);

    /**
     * Find duplicate feedback (same user, same theater, similar content)
     */
    @Query("SELECT f FROM CustomerFeedback f WHERE " +
           "f.userAccount.userId = :userId AND " +
           "f.privateTheater.theaterId = :theaterId AND " +
           "f.feedbackId != :excludeId " +
           "ORDER BY f.createdAt DESC")
    List<CustomerFeedback> findPotentialDuplicateFeedback(@Param("userId") Long userId,
                                                         @Param("theaterId") Long theaterId,
                                                         @Param("excludeId") Long excludeId);
}