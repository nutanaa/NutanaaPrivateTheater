package com.nutanaa.privatetheaterbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserAccount Entity Model
 * 
 * This entity represents all users in the Nutanaa Private Theater Booking Platform
 * including regular users, managers, and administrators. It contains all necessary
 * fields for authentication, profile management, KYC verification, and user tracking.
 * 
 * Database Table: user_accounts
 * 
 * Features:
 * - Mobile number-based authentication
 * - Role-based access control (USER, MANAGER, ADMIN)
 * - KYC document management
 * - Profile picture upload
 * - User source tracking (where they heard about us)
 * - Account status management
 * - Audit fields for creation and modification tracking
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "user_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    /**
     * Primary key - Auto-generated unique identifier for each user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * User's full name as provided during registration
     * Required field, minimum 2 characters, maximum 100 characters
     */
    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /**
     * Mobile number - Primary authentication identifier
     * Must be unique across the system, used for OTP login
     * Format: Country code + 10 digits (e.g., +919876543210)
     */
    @Column(name = "mobile_number", nullable = false, unique = true, length = 15)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;

    /**
     * Email address for booking confirmations and notifications
     * Optional field but recommended for better communication
     */
    @Column(name = "email_address", length = 150)
    @Email(message = "Invalid email format")
    private String emailAddress;

    /**
     * Aadhaar number for KYC verification
     * 12-digit unique identification number issued by UIDAI
     * Required for booking validation and government compliance
     */
    @Column(name = "aadhaar_number", length = 12)
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")
    private String aadhaarNumber;

    /**
     * PAN number for additional KYC verification
     * Optional but recommended for premium services
     * Format: AAAAA9999A (5 letters + 4 digits + 1 letter)
     */
    @Column(name = "pan_number", length = 10)
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;

    /**
     * User role in the system
     * - USER: Regular customers who can make bookings
     * - MANAGER: Theater managers with limited admin access
     * - ADMIN: Full system administrators
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.USER;

    /**
     * Account status to control user access
     * - ACTIVE: User can login and make bookings
     * - INACTIVE: Temporarily disabled account
     * - SUSPENDED: Account suspended due to violations
     * - PENDING: Awaiting verification or approval
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    /**
     * Path to user's profile picture
     * Stored as relative path to upload directory
     */
    @Column(name = "profile_picture_path")
    private String profilePicturePath;

    /**
     * Path to uploaded Aadhaar document (PDF or image)
     * Encrypted storage path for security compliance
     */
    @Column(name = "aadhaar_document_path")
    private String aadhaarDocumentPath;

    /**
     * Path to uploaded PAN document (PDF or image)
     * Encrypted storage path for security compliance
     */
    @Column(name = "pan_document_path")
    private String panDocumentPath;

    /**
     * KYC verification status
     * - PENDING: Documents uploaded, awaiting verification
     * - VERIFIED: Documents verified and approved
     * - REJECTED: Documents rejected, re-upload required
     * - NOT_SUBMITTED: No documents uploaded yet
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    private KycStatus kycStatus = KycStatus.NOT_SUBMITTED;

    /**
     * Source information - Where the user heard about Nutanaa
     * Used for marketing analytics and understanding customer acquisition
     * Examples: "Social Media", "Friends", "Google Search", "Advertisement"
     */
    @Column(name = "heard_about_us", length = 100)
    private String heardAboutUs;

    /**
     * Additional details about the source
     * For example, if source is "Social Media", this could be "Instagram", "Facebook"
     */
    @Column(name = "source_details", length = 200)
    private String sourceDetails;

    /**
     * Last login timestamp for security tracking
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * Number of failed login attempts (for security)
     */
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    /**
     * Account lockout time (if temporarily locked due to failed attempts)
     */
    @Column(name = "lockout_time")
    private LocalDateTime lockoutTime;

    /**
     * Device information for last login (for security)
     */
    @Column(name = "last_login_device", length = 200)
    private String lastLoginDevice;

    /**
     * IP address of last login (for security tracking)
     */
    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    /**
     * Timestamp when the account was created
     * Automatically set by Hibernate on entity creation
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the account was last updated
     * Automatically updated by Hibernate on entity modification
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User's booking history
     * One-to-Many relationship with TheaterBooking entity
     */
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TheaterBooking> bookingHistory;

    /**
     * User Role Enumeration
     * Defines the different types of users in the system
     */
    public enum UserRole {
        USER("Regular User"),
        MANAGER("Theater Manager"),
        ADMIN("System Administrator");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Account Status Enumeration
     * Defines the different states of user accounts
     */
    public enum AccountStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended"),
        PENDING("Pending Verification");

        private final String displayName;

        AccountStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * KYC Status Enumeration
     * Defines the different states of KYC verification
     */
    public enum KycStatus {
        NOT_SUBMITTED("Not Submitted"),
        PENDING("Pending Verification"),
        VERIFIED("Verified"),
        REJECTED("Rejected");

        private final String displayName;

        KycStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Helper method to check if user can make bookings
     * User must be active and have verified KYC
     */
    public boolean canMakeBookings() {
        return accountStatus == AccountStatus.ACTIVE && kycStatus == KycStatus.VERIFIED;
    }

    /**
     * Helper method to check if account is locked
     */
    public boolean isAccountLocked() {
        return lockoutTime != null && lockoutTime.isAfter(LocalDateTime.now());
    }

    /**
     * Helper method to get display name with role
     */
    public String getDisplayNameWithRole() {
        return fullName + " (" + userRole.getDisplayName() + ")";
    }
}