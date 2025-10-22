package com.nutanaa.privatetheaterbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * OtpVerification Entity Model
 * 
 * This entity manages OTP (One-Time Password) verification for user authentication
 * in the Nutanaa Private Theater Booking Platform. It handles mobile number verification,
 * login OTPs, and security-related OTP operations.
 * 
 * Database Table: otp_verifications
 * 
 * Features:
 * - Mobile number verification for new users
 * - Login OTP management
 * - Password reset OTP handling
 * - Security verification for sensitive operations
 * - Automatic expiry and cleanup
 * - Attempt limit and abuse prevention
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "otp_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {

    /**
     * Primary key - Auto-generated unique identifier for each OTP
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long otpId;

    /**
     * Mobile number for which OTP is generated
     * Required field for all OTP operations
     */
    @Column(name = "mobile_number", nullable = false, length = 15)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;

    /**
     * The OTP code sent to user
     * 6-digit numeric code by default
     */
    @Column(name = "otp_code", nullable = false, length = 10)
    @NotBlank(message = "OTP code is required")
    private String otpCode;

    /**
     * Type of OTP operation
     * - LOGIN: User login verification
     * - REGISTRATION: New user registration verification
     * - PASSWORD_RESET: Password reset verification
     * - BOOKING_CONFIRMATION: Booking confirmation
     * - SECURITY_VERIFICATION: Security-sensitive operations
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false)
    private OtpType otpType;

    /**
     * Current status of the OTP
     * - SENT: OTP has been sent to user
     * - VERIFIED: OTP successfully verified
     * - EXPIRED: OTP has expired
     * - INVALID: OTP is marked as invalid (too many attempts)
     * - USED: OTP has been used successfully
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "otp_status", nullable = false)
    private OtpStatus otpStatus = OtpStatus.SENT;

    /**
     * Reference to user account (if exists)
     * For existing users, null for new registrations
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Session identifier for this OTP request
     * Used to group related OTP operations
     */
    @Column(name = "session_id", length = 100)
    private String sessionId;

    /**
     * Number of verification attempts made
     * Used to prevent brute force attacks
     */
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    /**
     * Maximum allowed attempts before OTP becomes invalid
     * Default is 3 attempts
     */
    @Column(name = "max_attempts")
    private Integer maxAttempts = 3;

    /**
     * Timestamp when OTP expires
     * Calculated as created_at + configured expiry minutes
     */
    @Column(name = "expires_at", nullable = false)
    @NotNull(message = "Expiry time is required")
    private LocalDateTime expiresAt;

    /**
     * Timestamp when OTP was successfully verified
     * Set only when OTP is successfully validated
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * IP address from which OTP was requested
     * Used for security tracking and abuse prevention
     */
    @Column(name = "request_ip_address", length = 45)
    private String requestIpAddress;

    /**
     * Device information used for OTP request
     * Helps in security monitoring
     */
    @Column(name = "request_device_info", length = 200)
    private String requestDeviceInfo;

    /**
     * User agent string from the request
     * Additional security information
     */
    @Column(name = "request_user_agent", length = 500)
    private String requestUserAgent;

    /**
     * Purpose or context of the OTP request
     * Additional information about why OTP was requested
     */
    @Column(name = "request_purpose", length = 200)
    private String requestPurpose;

    /**
     * SMS delivery status
     * Tracks if SMS was successfully sent to the mobile number
     */
    @Column(name = "sms_delivery_status", length = 50)
    private String smsDeliveryStatus;

    /**
     * SMS provider response
     * Response from SMS gateway for tracking
     */
    @Column(name = "sms_provider_response", columnDefinition = "TEXT")
    private String smsProviderResponse;

    /**
     * SMS message ID from provider
     * Reference ID from SMS service provider
     */
    @Column(name = "sms_message_id", length = 100)
    private String smsMessageId;

    /**
     * Timestamp when OTP was created and sent
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Additional data related to OTP request
     * JSON string for storing extra context information
     */
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;

    // ========================================
    // ENUMERATIONS
    // ========================================

    /**
     * OTP Type Enumeration
     * Defines different purposes for OTP generation
     */
    public enum OtpType {
        LOGIN("Login Verification"),
        REGISTRATION("Registration Verification"),
        PASSWORD_RESET("Password Reset"),
        BOOKING_CONFIRMATION("Booking Confirmation"),
        SECURITY_VERIFICATION("Security Verification"),
        PHONE_VERIFICATION("Phone Number Verification");

        private final String displayName;

        OtpType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * OTP Status Enumeration
     * Defines different states of OTP lifecycle
     */
    public enum OtpStatus {
        SENT("OTP Sent"),
        VERIFIED("OTP Verified"),
        EXPIRED("OTP Expired"),
        INVALID("Invalid OTP"),
        USED("OTP Used"),
        FAILED("OTP Failed");

        private final String displayName;

        OtpStatus(String displayName) {
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
     * Check if OTP is still valid (not expired and not exceeded max attempts)
     */
    public boolean isValid() {
        return otpStatus == OtpStatus.SENT && 
               LocalDateTime.now().isBefore(expiresAt) && 
               attemptCount < maxAttempts;
    }

    /**
     * Check if OTP has expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if maximum attempts have been reached
     */
    public boolean isMaxAttemptsReached() {
        return attemptCount >= maxAttempts;
    }

    /**
     * Increment attempt count
     */
    public void incrementAttemptCount() {
        if (attemptCount == null) {
            attemptCount = 0;
        }
        attemptCount++;
        
        // Mark as invalid if max attempts reached
        if (attemptCount >= maxAttempts) {
            otpStatus = OtpStatus.INVALID;
        }
    }

    /**
     * Mark OTP as verified
     */
    public void markAsVerified() {
        otpStatus = OtpStatus.VERIFIED;
        verifiedAt = LocalDateTime.now();
    }

    /**
     * Mark OTP as expired
     */
    public void markAsExpired() {
        otpStatus = OtpStatus.EXPIRED;
    }

    /**
     * Mark OTP as used
     */
    public void markAsUsed() {
        otpStatus = OtpStatus.USED;
    }

    /**
     * Mark OTP as failed
     */
    public void markAsFailed() {
        otpStatus = OtpStatus.FAILED;
    }

    /**
     * Get remaining attempts
     */
    public int getRemainingAttempts() {
        return Math.max(0, maxAttempts - (attemptCount != null ? attemptCount : 0));
    }

    /**
     * Get time remaining until expiry in minutes
     */
    public long getMinutesUntilExpiry() {
        if (expiresAt == null) return 0;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) return 0;
        
        return java.time.Duration.between(now, expiresAt).toMinutes();
    }

    /**
     * Get time remaining until expiry in seconds
     */
    public long getSecondsUntilExpiry() {
        if (expiresAt == null) return 0;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) return 0;
        
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }

    /**
     * Check if OTP can be verified (valid status and not expired)
     */
    public boolean canBeVerified() {
        return otpStatus == OtpStatus.SENT && 
               !isExpired() && 
               !isMaxAttemptsReached();
    }

    /**
     * Get formatted expiry time
     */
    public String getFormattedExpiryTime() {
        if (expiresAt == null) return "No expiry set";
        return expiresAt.toString();
    }

    /**
     * Get masked mobile number for display
     * Example: +91****567890 becomes +91****7890
     */
    public String getMaskedMobileNumber() {
        if (mobileNumber == null || mobileNumber.length() < 4) {
            return mobileNumber;
        }
        
        String prefix = mobileNumber.substring(0, 3);
        String suffix = mobileNumber.substring(mobileNumber.length() - 4);
        return prefix + "****" + suffix;
    }

    /**
     * Generate OTP verification message
     */
    public String getOtpMessage() {
        return String.format("Your Nutanaa verification code is: %s. Valid for %d minutes. Do not share this code with anyone.",
                otpCode, getMinutesUntilExpiry());
    }

    /**
     * Check if this is a recent OTP (created within last 2 minutes)
     * Used to prevent spam
     */
    public boolean isRecentOtp() {
        if (createdAt == null) return false;
        return LocalDateTime.now().minusMinutes(2).isBefore(createdAt);
    }

    /**
     * Update SMS delivery status
     */
    public void updateSmsStatus(String status, String response, String messageId) {
        this.smsDeliveryStatus = status;
        this.smsProviderResponse = response;
        this.smsMessageId = messageId;
    }

    /**
     * Create a new OTP with expiry time
     */
    public static OtpVerification createNewOtp(String mobileNumber, String otpCode, 
                                               OtpType otpType, int expiryMinutes) {
        OtpVerification otp = new OtpVerification();
        otp.setMobileNumber(mobileNumber);
        otp.setOtpCode(otpCode);
        otp.setOtpType(otpType);
        otp.setOtpStatus(OtpStatus.SENT);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        otp.setAttemptCount(0);
        otp.setMaxAttempts(3);
        return otp;
    }

    /**
     * Generate a random 6-digit OTP code
     */
    public static String generateOtpCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    /**
     * Generate a session ID for grouping related OTP operations
     */
    public static String generateSessionId() {
        return "OTP-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", (int) (Math.random() * 10000));
    }
}