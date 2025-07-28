package com.nutanaa.privatetheaterbooking.repository;

import com.nutanaa.privatetheaterbooking.model.OtpVerification;
import com.nutanaa.privatetheaterbooking.model.OtpVerification.OtpStatus;
import com.nutanaa.privatetheaterbooking.model.OtpVerification.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * OtpVerificationRepository Interface
 * 
 * This repository interface provides data access methods for OtpVerification entities.
 * It handles OTP lifecycle management, verification, and security operations.
 * 
 * Features:
 * - OTP generation and verification
 * - Security and abuse prevention
 * - Cleanup and maintenance operations
 * - Analytics and monitoring
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    // ========================================
    // BASIC FINDER METHODS
    // ========================================

    /**
     * Find OTP by mobile number and OTP code
     */
    Optional<OtpVerification> findByMobileNumberAndOtpCode(String mobileNumber, String otpCode);

    /**
     * Find latest OTP for mobile number and type
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.mobileNumber = :mobileNumber AND " +
           "o.otpType = :otpType " +
           "ORDER BY o.createdAt DESC")
    List<OtpVerification> findLatestOtpByMobileAndType(@Param("mobileNumber") String mobileNumber, 
                                                      @Param("otpType") OtpType otpType);

    /**
     * Find valid OTP for verification
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.mobileNumber = :mobileNumber AND " +
           "o.otpCode = :otpCode AND " +
           "o.otpType = :otpType AND " +
           "o.otpStatus = 'SENT' AND " +
           "o.expiresAt > :currentTime AND " +
           "o.attemptCount < o.maxAttempts")
    Optional<OtpVerification> findValidOtp(@Param("mobileNumber") String mobileNumber,
                                          @Param("otpCode") String otpCode,
                                          @Param("otpType") OtpType otpType,
                                          @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find OTPs by mobile number
     */
    List<OtpVerification> findByMobileNumber(String mobileNumber);

    /**
     * Find OTPs by status
     */
    List<OtpVerification> findByOtpStatus(OtpStatus status);

    /**
     * Find OTPs by type
     */
    List<OtpVerification> findByOtpType(OtpType type);

    // ========================================
    // SECURITY AND VALIDATION QUERIES
    // ========================================

    /**
     * Check if mobile number has recent OTP request
     */
    @Query("SELECT COUNT(o) > 0 FROM OtpVerification o WHERE " +
           "o.mobileNumber = :mobileNumber AND " +
           "o.createdAt > :timeThreshold")
    boolean hasRecentOtpRequest(@Param("mobileNumber") String mobileNumber, 
                               @Param("timeThreshold") LocalDateTime timeThreshold);

    /**
     * Count OTP requests from mobile number in time period
     */
    @Query("SELECT COUNT(o) FROM OtpVerification o WHERE " +
           "o.mobileNumber = :mobileNumber AND " +
           "o.createdAt > :timeThreshold")
    Long countOtpRequestsInPeriod(@Param("mobileNumber") String mobileNumber, 
                                 @Param("timeThreshold") LocalDateTime timeThreshold);

    /**
     * Count OTP requests from IP address in time period
     */
    @Query("SELECT COUNT(o) FROM OtpVerification o WHERE " +
           "o.requestIpAddress = :ipAddress AND " +
           "o.createdAt > :timeThreshold")
    Long countOtpRequestsFromIpInPeriod(@Param("ipAddress") String ipAddress, 
                                       @Param("timeThreshold") LocalDateTime timeThreshold);

    /**
     * Find failed verification attempts for mobile number
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.mobileNumber = :mobileNumber AND " +
           "o.attemptCount >= o.maxAttempts AND " +
           "o.createdAt > :timeThreshold")
    List<OtpVerification> findFailedAttemptsForMobile(@Param("mobileNumber") String mobileNumber, 
                                                     @Param("timeThreshold") LocalDateTime timeThreshold);

    /**
     * Check if mobile number is temporarily blocked due to failed attempts
     */
    @Query("SELECT COUNT(o) >= :maxFailedOtps FROM OtpVerification o WHERE " +
           "o.mobileNumber = :mobileNumber AND " +
           "o.otpStatus = 'INVALID' AND " +
           "o.createdAt > :timeThreshold")
    boolean isMobileTemporarilyBlocked(@Param("mobileNumber") String mobileNumber, 
                                      @Param("timeThreshold") LocalDateTime timeThreshold,
                                      @Param("maxFailedOtps") Long maxFailedOtps);

    // ========================================
    // SESSION AND USER TRACKING
    // ========================================

    /**
     * Find OTPs by session ID
     */
    List<OtpVerification> findBySessionId(String sessionId);

    /**
     * Find OTPs by user ID
     */
    List<OtpVerification> findByUserId(Long userId);

    /**
     * Find latest successful verification for user
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.userId = :userId AND " +
           "o.otpStatus = 'VERIFIED' " +
           "ORDER BY o.verifiedAt DESC")
    List<OtpVerification> findLatestSuccessfulVerificationForUser(@Param("userId") Long userId);

    /**
     * Find pending verifications for user
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.userId = :userId AND " +
           "o.otpStatus = 'SENT' AND " +
           "o.expiresAt > :currentTime")
    List<OtpVerification> findPendingVerificationsForUser(@Param("userId") Long userId, 
                                                         @Param("currentTime") LocalDateTime currentTime);

    // ========================================
    // EXPIRY AND CLEANUP QUERIES
    // ========================================

    /**
     * Find expired OTPs
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.expiresAt < :currentTime AND " +
           "o.otpStatus = 'SENT'")
    List<OtpVerification> findExpiredOtps(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find OTPs to be cleaned up (old and processed)
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.createdAt < :cleanupThreshold AND " +
           "(o.otpStatus = 'VERIFIED' OR o.otpStatus = 'EXPIRED' OR o.otpStatus = 'INVALID')")
    List<OtpVerification> findOtpsForCleanup(@Param("cleanupThreshold") LocalDateTime cleanupThreshold);

    /**
     * Count active (non-expired, non-verified) OTPs
     */
    @Query("SELECT COUNT(o) FROM OtpVerification o WHERE " +
           "o.otpStatus = 'SENT' AND o.expiresAt > :currentTime")
    Long countActiveOtps(@Param("currentTime") LocalDateTime currentTime);

    // ========================================
    // ANALYTICS AND MONITORING QUERIES
    // ========================================

    /**
     * Get OTP statistics by status
     */
    @Query("SELECT o.otpStatus, COUNT(o) FROM OtpVerification o GROUP BY o.otpStatus")
    List<Object[]> getOtpStatsByStatus();

    /**
     * Get OTP statistics by type
     */
    @Query("SELECT o.otpType, COUNT(o) FROM OtpVerification o GROUP BY o.otpType")
    List<Object[]> getOtpStatsByType();

    /**
     * Get daily OTP generation counts
     */
    @Query("SELECT DATE(o.createdAt) as date, COUNT(o) as count " +
           "FROM OtpVerification o " +
           "WHERE o.createdAt >= :startDate " +
           "GROUP BY DATE(o.createdAt) " +
           "ORDER BY DATE(o.createdAt)")
    List<Object[]> getDailyOtpCounts(@Param("startDate") LocalDateTime startDate);

    /**
     * Get OTP success rate statistics
     */
    @Query("SELECT " +
           "COUNT(o) as totalOtps, " +
           "COUNT(CASE WHEN o.otpStatus = 'VERIFIED' THEN 1 END) as verifiedOtps, " +
           "COUNT(CASE WHEN o.otpStatus = 'EXPIRED' THEN 1 END) as expiredOtps, " +
           "COUNT(CASE WHEN o.otpStatus = 'INVALID' THEN 1 END) as invalidOtps, " +
           "AVG(o.attemptCount) as avgAttempts " +
           "FROM OtpVerification o " +
           "WHERE o.createdAt >= :startDate")
    Object[] getOtpSuccessRateStats(@Param("startDate") LocalDateTime startDate);

    /**
     * Find high-usage mobile numbers (potential abuse)
     */
    @Query("SELECT o.mobileNumber, COUNT(o) as otpCount " +
           "FROM OtpVerification o " +
           "WHERE o.createdAt >= :startDate " +
           "GROUP BY o.mobileNumber " +
           "HAVING COUNT(o) > :threshold " +
           "ORDER BY COUNT(o) DESC")
    List<Object[]> findHighUsageMobileNumbers(@Param("startDate") LocalDateTime startDate, 
                                             @Param("threshold") Long threshold);

    /**
     * Find high-usage IP addresses (potential abuse)
     */
    @Query("SELECT o.requestIpAddress, COUNT(o) as otpCount " +
           "FROM OtpVerification o " +
           "WHERE o.requestIpAddress IS NOT NULL AND o.createdAt >= :startDate " +
           "GROUP BY o.requestIpAddress " +
           "HAVING COUNT(o) > :threshold " +
           "ORDER BY COUNT(o) DESC")
    List<Object[]> findHighUsageIpAddresses(@Param("startDate") LocalDateTime startDate, 
                                           @Param("threshold") Long threshold);

    // ========================================
    // SMS DELIVERY TRACKING
    // ========================================

    /**
     * Find OTPs with delivery failures
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.smsDeliveryStatus IS NOT NULL AND " +
           "o.smsDeliveryStatus != 'DELIVERED' AND " +
           "o.smsDeliveryStatus != 'SUCCESS'")
    List<OtpVerification> findOtpsWithDeliveryFailures();

    /**
     * Get SMS delivery statistics
     */
    @Query("SELECT o.smsDeliveryStatus, COUNT(o) " +
           "FROM OtpVerification o " +
           "WHERE o.smsDeliveryStatus IS NOT NULL " +
           "GROUP BY o.smsDeliveryStatus")
    List<Object[]> getSmsDeliveryStats();

    /**
     * Find OTPs pending SMS delivery status update
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.smsMessageId IS NOT NULL AND " +
           "o.smsDeliveryStatus IS NULL AND " +
           "o.createdAt > :timeThreshold")
    List<OtpVerification> findOtpsPendingDeliveryStatus(@Param("timeThreshold") LocalDateTime timeThreshold);

    // ========================================
    // UPDATE OPERATIONS
    // ========================================

    /**
     * Update OTP status
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET o.otpStatus = :status WHERE o.otpId = :otpId")
    void updateOtpStatus(@Param("otpId") Long otpId, @Param("status") OtpStatus status);

    /**
     * Mark OTP as verified
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET " +
           "o.otpStatus = 'VERIFIED', " +
           "o.verifiedAt = :verifiedAt " +
           "WHERE o.otpId = :otpId")
    void markOtpAsVerified(@Param("otpId") Long otpId, @Param("verifiedAt") LocalDateTime verifiedAt);

    /**
     * Increment attempt count
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET o.attemptCount = o.attemptCount + 1 WHERE o.otpId = :otpId")
    void incrementAttemptCount(@Param("otpId") Long otpId);

    /**
     * Mark expired OTPs
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET o.otpStatus = 'EXPIRED' WHERE " +
           "o.expiresAt < :currentTime AND o.otpStatus = 'SENT'")
    int markExpiredOtps(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Mark OTPs as invalid when max attempts reached
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET o.otpStatus = 'INVALID' WHERE " +
           "o.attemptCount >= o.maxAttempts AND o.otpStatus = 'SENT'")
    int markInvalidOtpsMaxAttemptsReached();

    /**
     * Update SMS delivery status
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET " +
           "o.smsDeliveryStatus = :status, " +
           "o.smsProviderResponse = :response " +
           "WHERE o.otpId = :otpId")
    void updateSmsDeliveryStatus(@Param("otpId") Long otpId, 
                                @Param("status") String status, 
                                @Param("response") String response);

    /**
     * Update SMS message ID
     */
    @Modifying
    @Query("UPDATE OtpVerification o SET o.smsMessageId = :messageId WHERE o.otpId = :otpId")
    void updateSmsMessageId(@Param("otpId") Long otpId, @Param("messageId") String messageId);

    // ========================================
    // CLEANUP AND MAINTENANCE OPERATIONS
    // ========================================

    /**
     * Delete old OTP records
     */
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.createdAt < :deleteThreshold")
    int deleteOldOtpRecords(@Param("deleteThreshold") LocalDateTime deleteThreshold);

    /**
     * Delete verified OTPs older than threshold
     */
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE " +
           "o.otpStatus = 'VERIFIED' AND o.verifiedAt < :deleteThreshold")
    int deleteOldVerifiedOtps(@Param("deleteThreshold") LocalDateTime deleteThreshold);

    /**
     * Delete expired OTPs older than threshold
     */
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE " +
           "o.otpStatus = 'EXPIRED' AND o.expiresAt < :deleteThreshold")
    int deleteOldExpiredOtps(@Param("deleteThreshold") LocalDateTime deleteThreshold);

    /**
     * Delete invalid OTPs older than threshold
     */
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE " +
           "o.otpStatus = 'INVALID' AND o.createdAt < :deleteThreshold")
    int deleteOldInvalidOtps(@Param("deleteThreshold") LocalDateTime deleteThreshold);

    // ========================================
    // SECURITY MONITORING QUERIES
    // ========================================

    /**
     * Find suspicious OTP patterns (same IP, multiple mobiles)
     */
    @Query("SELECT o.requestIpAddress, COUNT(DISTINCT o.mobileNumber) as uniqueMobiles, COUNT(o) as totalOtps " +
           "FROM OtpVerification o " +
           "WHERE o.requestIpAddress IS NOT NULL AND o.createdAt >= :startDate " +
           "GROUP BY o.requestIpAddress " +
           "HAVING COUNT(DISTINCT o.mobileNumber) > :mobileThreshold " +
           "ORDER BY COUNT(o) DESC")
    List<Object[]> findSuspiciousIpPatterns(@Param("startDate") LocalDateTime startDate, 
                                           @Param("mobileThreshold") Integer mobileThreshold);

    /**
     * Find rapid-fire OTP requests (potential bot activity)
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.createdAt >= :timeThreshold AND " +
           "EXISTS (SELECT o2 FROM OtpVerification o2 WHERE " +
           "o2.requestIpAddress = o.requestIpAddress AND " +
           "o2.createdAt BETWEEN o.createdAt - INTERVAL 1 MINUTE AND o.createdAt + INTERVAL 1 MINUTE AND " +
           "o2.otpId != o.otpId)")
    List<OtpVerification> findRapidFireOtpRequests(@Param("timeThreshold") LocalDateTime timeThreshold);

    /**
     * Find never-used OTPs (generated but never attempted)
     */
    @Query("SELECT o FROM OtpVerification o WHERE " +
           "o.attemptCount = 0 AND " +
           "o.otpStatus = 'EXPIRED' AND " +
           "o.expiresAt < :timeThreshold")
    List<OtpVerification> findNeverUsedOtps(@Param("timeThreshold") LocalDateTime timeThreshold);
}