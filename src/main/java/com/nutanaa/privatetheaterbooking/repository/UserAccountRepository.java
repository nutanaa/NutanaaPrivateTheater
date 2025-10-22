package com.nutanaa.privatetheaterbooking.repository;

import com.nutanaa.privatetheaterbooking.model.UserAccount;
import com.nutanaa.privatetheaterbooking.model.UserAccount.AccountStatus;
import com.nutanaa.privatetheaterbooking.model.UserAccount.KycStatus;
import com.nutanaa.privatetheaterbooking.model.UserAccount.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * UserAccountRepository Interface
 * 
 * This repository interface provides data access methods for UserAccount entities.
 * It extends JpaRepository to inherit basic CRUD operations and defines custom
 * query methods for specific business requirements.
 * 
 * Features:
 * - Basic CRUD operations via JpaRepository
 * - Custom finder methods for business logic
 * - Authentication and authorization queries
 * - User management and filtering operations
 * - Analytics and reporting queries
 * - Account status and KYC management
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    // ========================================
    // AUTHENTICATION AND LOGIN METHODS
    // ========================================

    /**
     * Find user by mobile number for authentication
     * Primary method for login operations
     */
    Optional<UserAccount> findByMobileNumber(String mobileNumber);

    /**
     * Find user by mobile number and account status
     * Used for login validation with status check
     */
    Optional<UserAccount> findByMobileNumberAndAccountStatus(String mobileNumber, AccountStatus accountStatus);

    /**
     * Find user by email address
     * Alternative login method (if implemented)
     */
    Optional<UserAccount> findByEmailAddress(String emailAddress);

    /**
     * Check if mobile number exists in the system
     * Used for registration validation
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Check if email exists in the system
     * Used for registration validation
     */
    boolean existsByEmailAddress(String emailAddress);

    /**
     * Check if Aadhaar number exists in the system
     * Used for KYC validation
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    /**
     * Check if PAN number exists in the system
     * Used for KYC validation
     */
    boolean existsByPanNumber(String panNumber);

    // ========================================
    // USER ROLE AND STATUS MANAGEMENT
    // ========================================

    /**
     * Find all users with specific role
     * Used for admin management and role-based operations
     */
    List<UserAccount> findByUserRole(UserRole userRole);

    /**
     * Find users by role with pagination
     * Used for admin dashboard user listing
     */
    Page<UserAccount> findByUserRole(UserRole userRole, Pageable pageable);

    /**
     * Find users by account status
     * Used for account management operations
     */
    List<UserAccount> findByAccountStatus(AccountStatus accountStatus);

    /**
     * Find users by account status with pagination
     * Used for admin dashboard filtering
     */
    Page<UserAccount> findByAccountStatus(AccountStatus accountStatus, Pageable pageable);

    /**
     * Find users by KYC status
     * Used for KYC management and verification
     */
    List<UserAccount> findByKycStatus(KycStatus kycStatus);

    /**
     * Find users by KYC status with pagination
     * Used for admin KYC verification dashboard
     */
    Page<UserAccount> findByKycStatus(KycStatus kycStatus, Pageable pageable);

    /**
     * Find all active users who can make bookings
     * Users with ACTIVE status and VERIFIED KYC
     */
    @Query("SELECT u FROM UserAccount u WHERE u.accountStatus = 'ACTIVE' AND u.kycStatus = 'VERIFIED'")
    List<UserAccount> findActiveVerifiedUsers();

    // ========================================
    // SEARCH AND FILTERING METHODS
    // ========================================

    /**
     * Search users by name (case-insensitive partial match)
     * Used for admin search functionality
     */
    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserAccount> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search users by name with pagination
     * Used for admin dashboard search with pagination
     */
    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<UserAccount> findByFullNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find users by multiple criteria
     * Advanced search functionality for admin dashboard
     */
    @Query("SELECT u FROM UserAccount u WHERE " +
           "(:role IS NULL OR u.userRole = :role) AND " +
           "(:status IS NULL OR u.accountStatus = :status) AND " +
           "(:kycStatus IS NULL OR u.kycStatus = :kycStatus) AND " +
           "(:name IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<UserAccount> findByMultipleCriteria(
            @Param("role") UserRole role,
            @Param("status") AccountStatus status,
            @Param("kycStatus") KycStatus kycStatus,
            @Param("name") String name,
            Pageable pageable);

    // ========================================
    // TIME-BASED QUERIES
    // ========================================

    /**
     * Find users created within a date range
     * Used for registration analytics
     */
    @Query("SELECT u FROM UserAccount u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<UserAccount> findUsersCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find users who logged in recently
     * Used for active user analytics
     */
    @Query("SELECT u FROM UserAccount u WHERE u.lastLoginTime >= :since")
    List<UserAccount> findUsersLoggedInSince(@Param("since") LocalDateTime since);

    /**
     * Find users who never logged in
     * Used for user engagement analysis
     */
    @Query("SELECT u FROM UserAccount u WHERE u.lastLoginTime IS NULL")
    List<UserAccount> findUsersWhoNeverLoggedIn();

    /**
     * Find inactive users (not logged in for specified days)
     * Used for re-engagement campaigns
     */
    @Query("SELECT u FROM UserAccount u WHERE u.lastLoginTime < :thresholdDate OR u.lastLoginTime IS NULL")
    List<UserAccount> findInactiveUsers(@Param("thresholdDate") LocalDateTime thresholdDate);

    // ========================================
    // ANALYTICS AND REPORTING QUERIES
    // ========================================

    /**
     * Count users by role
     * Used for admin dashboard statistics
     */
    @Query("SELECT u.userRole, COUNT(u) FROM UserAccount u GROUP BY u.userRole")
    List<Object[]> countUsersByRole();

    /**
     * Count users by account status
     * Used for admin dashboard statistics
     */
    @Query("SELECT u.accountStatus, COUNT(u) FROM UserAccount u GROUP BY u.accountStatus")
    List<Object[]> countUsersByAccountStatus();

    /**
     * Count users by KYC status
     * Used for KYC management dashboard
     */
    @Query("SELECT u.kycStatus, COUNT(u) FROM UserAccount u GROUP BY u.kycStatus")
    List<Object[]> countUsersByKycStatus();

    /**
     * Count users by registration source
     * Used for marketing analytics
     */
    @Query("SELECT u.heardAboutUs, COUNT(u) FROM UserAccount u WHERE u.heardAboutUs IS NOT NULL GROUP BY u.heardAboutUs")
    List<Object[]> countUsersBySource();

    /**
     * Get daily registration counts for the last N days
     * Used for registration trends analysis
     */
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count " +
           "FROM UserAccount u " +
           "WHERE u.createdAt >= :startDate " +
           "GROUP BY DATE(u.createdAt) " +
           "ORDER BY DATE(u.createdAt)")
    List<Object[]> getDailyRegistrationCounts(@Param("startDate") LocalDateTime startDate);

    /**
     * Get user registration statistics for admin dashboard
     */
    @Query("SELECT " +
           "COUNT(u) as totalUsers, " +
           "COUNT(CASE WHEN u.accountStatus = 'ACTIVE' THEN 1 END) as activeUsers, " +
           "COUNT(CASE WHEN u.kycStatus = 'VERIFIED' THEN 1 END) as verifiedUsers, " +
           "COUNT(CASE WHEN u.userRole = 'USER' THEN 1 END) as regularUsers, " +
           "COUNT(CASE WHEN u.createdAt >= :lastWeek THEN 1 END) as newUsersThisWeek " +
           "FROM UserAccount u")
    Object[] getUserStatistics(@Param("lastWeek") LocalDateTime lastWeek);

    // ========================================
    // SOURCE ANALYTICS QUERIES
    // ========================================

    /**
     * Find users by source (where they heard about us)
     * Used for source-specific analytics
     */
    List<UserAccount> findByHeardAboutUs(String heardAboutUs);

    /**
     * Find users by source with pagination
     * Used for source analysis in admin dashboard
     */
    Page<UserAccount> findByHeardAboutUs(String heardAboutUs, Pageable pageable);

    /**
     * Find users by source details
     * More specific source tracking
     */
    List<UserAccount> findBySourceDetails(String sourceDetails);

    /**
     * Get detailed source analytics
     * Combined source and source details analysis
     */
    @Query("SELECT u.heardAboutUs, u.sourceDetails, COUNT(u) as userCount, " +
           "COUNT(CASE WHEN u.accountStatus = 'ACTIVE' THEN 1 END) as activeCount, " +
           "COUNT(CASE WHEN u.kycStatus = 'VERIFIED' THEN 1 END) as verifiedCount " +
           "FROM UserAccount u " +
           "WHERE u.heardAboutUs IS NOT NULL " +
           "GROUP BY u.heardAboutUs, u.sourceDetails " +
           "ORDER BY COUNT(u) DESC")
    List<Object[]> getDetailedSourceAnalytics();

    // ========================================
    // SECURITY AND ACCOUNT MANAGEMENT
    // ========================================

    /**
     * Find users with failed login attempts above threshold
     * Used for security monitoring
     */
    @Query("SELECT u FROM UserAccount u WHERE u.failedLoginAttempts >= :threshold")
    List<UserAccount> findUsersWithHighFailedAttempts(@Param("threshold") Integer threshold);

    /**
     * Find locked users
     * Users with active lockout time
     */
    @Query("SELECT u FROM UserAccount u WHERE u.lockoutTime IS NOT NULL AND u.lockoutTime > :currentTime")
    List<UserAccount> findLockedUsers(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find users pending KYC verification
     * Users who uploaded documents but not yet verified
     */
    @Query("SELECT u FROM UserAccount u WHERE u.kycStatus = 'PENDING' AND " +
           "(u.aadhaarDocumentPath IS NOT NULL OR u.panDocumentPath IS NOT NULL)")
    List<UserAccount> findUsersPendingKycVerification();

    // ========================================
    // UPDATE METHODS
    // ========================================

    /**
     * Update user's last login information
     * Called after successful login
     */
    @Modifying
    @Query("UPDATE UserAccount u SET u.lastLoginTime = :loginTime, u.lastLoginDevice = :device, " +
           "u.lastLoginIp = :ipAddress, u.failedLoginAttempts = 0, u.lockoutTime = NULL " +
           "WHERE u.userId = :userId")
    void updateLastLogin(@Param("userId") Long userId,
                        @Param("loginTime") LocalDateTime loginTime,
                        @Param("device") String device,
                        @Param("ipAddress") String ipAddress);

    /**
     * Increment failed login attempts
     * Called after failed login attempt
     */
    @Modifying
    @Query("UPDATE UserAccount u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 " +
           "WHERE u.userId = :userId")
    void incrementFailedLoginAttempts(@Param("userId") Long userId);

    /**
     * Set account lockout
     * Called when user exceeds failed login attempts
     */
    @Modifying
    @Query("UPDATE UserAccount u SET u.lockoutTime = :lockoutTime WHERE u.userId = :userId")
    void setAccountLockout(@Param("userId") Long userId, @Param("lockoutTime") LocalDateTime lockoutTime);

    /**
     * Update KYC status
     * Called during KYC verification process
     */
    @Modifying
    @Query("UPDATE UserAccount u SET u.kycStatus = :kycStatus WHERE u.userId = :userId")
    void updateKycStatus(@Param("userId") Long userId, @Param("kycStatus") KycStatus kycStatus);

    /**
     * Update account status
     * Called for account activation/deactivation
     */
    @Modifying
    @Query("UPDATE UserAccount u SET u.accountStatus = :accountStatus WHERE u.userId = :userId")
    void updateAccountStatus(@Param("userId") Long userId, @Param("accountStatus") AccountStatus accountStatus);

    /**
     * Clear account lockout
     * Called to unlock user account
     */
    @Modifying
    @Query("UPDATE UserAccount u SET u.lockoutTime = NULL, u.failedLoginAttempts = 0 WHERE u.userId = :userId")
    void clearAccountLockout(@Param("userId") Long userId);

    // ========================================
    // BULK OPERATIONS
    // ========================================

    /**
     * Delete users who never verified their account after specified days
     * Cleanup operation for unverified accounts
     */
    @Modifying
    @Query("DELETE FROM UserAccount u WHERE u.accountStatus = 'PENDING' AND u.createdAt < :thresholdDate")
    void deleteUnverifiedAccountsOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);

    /**
     * Find managers for a specific geographic region
     * Used for regional management
     */
    @Query("SELECT u FROM UserAccount u WHERE u.userRole = 'MANAGER' AND u.accountStatus = 'ACTIVE'")
    List<UserAccount> findActiveManagers();

    /**
     * Find all admins
     * Used for admin management
     */
    @Query("SELECT u FROM UserAccount u WHERE u.userRole = 'ADMIN' AND u.accountStatus = 'ACTIVE'")
    List<UserAccount> findActiveAdmins();
}