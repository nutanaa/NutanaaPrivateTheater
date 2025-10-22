package com.nutanaa.privatetheaterbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * TheaterBooking Entity Model
 * 
 * This entity represents all theater booking transactions in the Nutanaa platform.
 * It contains complete booking information including customer details, theater information,
 * payment tracking, and booking status management.
 * 
 * Database Table: theater_bookings
 * 
 * Features:
 * - Complete booking lifecycle management
 * - Payment tracking and status updates
 * - Customer and theater relationship mapping
 * - Booking validation and conflict prevention
 * - Automatic booking confirmation workflow
 * - Revenue tracking and analytics support
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "theater_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheaterBooking {

    /**
     * Primary key - Auto-generated unique identifier for each booking
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    /**
     * Unique booking reference number for customer tracking
     * Format: NUT-YYYYMMDD-HHMMSS-XXX (e.g., NUT-20241215-143022-001)
     */
    @Column(name = "booking_reference", nullable = false, unique = true, length = 25)
    @NotBlank(message = "Booking reference is required")
    private String bookingReference;

    /**
     * Reference to the user who made the booking
     * Foreign key relationship with UserAccount entity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User account is required")
    private UserAccount userAccount;

    /**
     * Reference to the theater being booked
     * Foreign key relationship with PrivateTheater entity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theater_id", nullable = false)
    @NotNull(message = "Theater selection is required")
    private PrivateTheater privateTheater;

    // ========================================
    // BOOKING DETAILS
    // ========================================

    /**
     * Date of the theater booking
     * Must be a future date (validated in service layer)
     */
    @Column(name = "booking_date", nullable = false)
    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private LocalDate bookingDate;

    /**
     * Start time of the booking session
     * Combined with booking_date to create full start datetime
     */
    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    /**
     * End time of the booking session
     * Must be after start_time (validated in service layer)
     */
    @Column(name = "end_time", nullable = false)
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    /**
     * Duration of booking in hours
     * Calculated field: (end_time - start_time) in hours
     */
    @Column(name = "duration_hours", nullable = false, precision = 3, scale = 1)
    @NotNull(message = "Duration is required")
    @DecimalMin(value = "0.5", message = "Minimum booking duration is 30 minutes")
    private BigDecimal durationHours;

    /**
     * Number of people attending the session
     * Must not exceed theater capacity
     */
    @Column(name = "number_of_guests", nullable = false)
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    private Integer numberOfGuests;

    // ========================================
    // CUSTOMER INFORMATION (for booking confirmation)
    // ========================================

    /**
     * Customer name for this specific booking
     * May differ from UserAccount name for group bookings
     */
    @Column(name = "customer_name", nullable = false, length = 100)
    @NotBlank(message = "Customer name is required")
    private String customerName;

    /**
     * Customer mobile number for booking confirmation
     * Usually same as UserAccount mobile, but can be different
     */
    @Column(name = "customer_mobile", nullable = false, length = 15)
    @NotBlank(message = "Customer mobile is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String customerMobile;

    /**
     * Customer email for booking confirmation
     * Optional field for email notifications
     */
    @Column(name = "customer_email", length = 150)
    @Email(message = "Invalid email format")
    private String customerEmail;

    /**
     * Aadhaar number for identity verification
     * Required for compliance and security
     */
    @Column(name = "customer_aadhaar", length = 12)
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")
    private String customerAadhaar;

    // ========================================
    // PRICING AND PAYMENT
    // ========================================

    /**
     * Base amount (hourly_rate * duration_hours)
     * Before taxes and additional charges
     */
    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Base amount is required")
    @DecimalMin(value = "0.01", message = "Base amount must be greater than 0")
    private BigDecimal baseAmount;

    /**
     * Weekend/Holiday surcharge amount
     * Applied if booking is on weekend or holiday
     */
    @Column(name = "surcharge_amount", precision = 10, scale = 2)
    private BigDecimal surchargeAmount = BigDecimal.ZERO;

    /**
     * Tax amount (GST/VAT)
     * Calculated based on local tax regulations
     */
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /**
     * Additional charges (cleaning, security deposit, etc.)
     * Optional charges as per theater policy
     */
    @Column(name = "additional_charges", precision = 10, scale = 2)
    private BigDecimal additionalCharges = BigDecimal.ZERO;

    /**
     * Discount amount (promotional offers, coupons)
     * Reduces the total amount
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Final total amount to be paid
     * Calculated: base_amount + surcharge_amount + tax_amount + additional_charges - discount_amount
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    /**
     * Amount actually paid by customer
     * Usually same as total_amount, but may differ in case of partial payments
     */
    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Remaining amount to be paid
     * Calculated: total_amount - paid_amount
     */
    @Column(name = "remaining_amount", precision = 10, scale = 2)
    private BigDecimal remainingAmount = BigDecimal.ZERO;

    // ========================================
    // PAYMENT TRACKING
    // ========================================

    /**
     * Payment method used for the booking
     * Examples: "Credit Card", "Debit Card", "UPI", "Net Banking", "Wallet"
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Payment gateway transaction ID
     * Reference from payment processor (Razorpay, PayU, etc.)
     */
    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;

    /**
     * Payment gateway response/status
     * Stores additional payment information
     */
    @Column(name = "payment_response", columnDefinition = "TEXT")
    private String paymentResponse;

    /**
     * Payment status tracking
     * - PENDING: Payment not yet initiated
     * - PROCESSING: Payment in progress
     * - COMPLETED: Payment successful
     * - FAILED: Payment failed
     * - REFUNDED: Payment refunded
     * - PARTIAL: Partial payment received
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * Payment completion timestamp
     * Set when payment is successfully processed
     */
    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;

    // ========================================
    // BOOKING STATUS AND WORKFLOW
    // ========================================

    /**
     * Current status of the booking
     * - PENDING: Booking created, awaiting payment
     * - CONFIRMED: Payment completed, booking confirmed
     * - APPROVED: Admin/Manager approved the booking
     * - REJECTED: Booking rejected by admin/manager
     * - CANCELLED: Booking cancelled by customer
     * - COMPLETED: Booking session completed
     * - NO_SHOW: Customer didn't show up
     * - REFUNDED: Booking cancelled and refunded
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    /**
     * Admin/Manager approval status
     * - PENDING: Awaiting admin/manager review
     * - APPROVED: Approved by admin/manager
     * - REJECTED: Rejected by admin/manager
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /**
     * User ID of admin/manager who approved/rejected
     */
    @Column(name = "approved_by_user_id")
    private Long approvedByUserId;

    /**
     * Timestamp when booking was approved/rejected
     */
    @Column(name = "approval_timestamp")
    private LocalDateTime approvalTimestamp;

    /**
     * Approval/Rejection comments from admin/manager
     */
    @Column(name = "approval_comments", columnDefinition = "TEXT")
    private String approvalComments;

    // ========================================
    // SPECIAL REQUESTS AND NOTES
    // ========================================

    /**
     * Special requests from customer
     * Examples: "Birthday decoration", "Specific movie setup", "Catering"
     */
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    /**
     * Internal notes for staff
     * Admin/Manager notes for this booking
     */
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    /**
     * Cancellation reason (if booking is cancelled)
     */
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    /**
     * Cancellation timestamp
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * User who cancelled the booking
     */
    @Column(name = "cancelled_by_user_id")
    private Long cancelledByUserId;

    // ========================================
    // NOTIFICATION TRACKING
    // ========================================

    /**
     * Email confirmation sent status
     */
    @Column(name = "email_sent")
    private Boolean emailSent = false;

    /**
     * SMS confirmation sent status
     */
    @Column(name = "sms_sent")
    private Boolean smsSent = false;

    /**
     * WhatsApp message sent status
     */
    @Column(name = "whatsapp_sent")
    private Boolean whatsappSent = false;

    /**
     * Reminder notifications sent count
     */
    @Column(name = "reminder_count")
    private Integer reminderCount = 0;

    // ========================================
    // AUDIT FIELDS
    // ========================================

    /**
     * Timestamp when booking was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when booking was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * IP address from which booking was made
     */
    @Column(name = "booking_ip_address", length = 45)
    private String bookingIpAddress;

    /**
     * Device information used for booking
     */
    @Column(name = "booking_device_info", length = 200)
    private String bookingDeviceInfo;

    // ========================================
    // ENUMERATIONS
    // ========================================

    /**
     * Payment Status Enumeration
     */
    public enum PaymentStatus {
        PENDING("Payment Pending"),
        PROCESSING("Payment Processing"),
        COMPLETED("Payment Completed"),
        FAILED("Payment Failed"),
        REFUNDED("Payment Refunded"),
        PARTIAL("Partial Payment");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Booking Status Enumeration
     */
    public enum BookingStatus {
        PENDING("Pending Confirmation"),
        CONFIRMED("Booking Confirmed"),
        APPROVED("Approved by Manager"),
        REJECTED("Booking Rejected"),
        CANCELLED("Booking Cancelled"),
        COMPLETED("Session Completed"),
        NO_SHOW("Customer No-Show"),
        REFUNDED("Booking Refunded");

        private final String displayName;

        BookingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Approval Status Enumeration
     */
    public enum ApprovalStatus {
        PENDING("Pending Approval"),
        APPROVED("Approved"),
        REJECTED("Rejected");

        private final String displayName;

        ApprovalStatus(String displayName) {
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
     * Get full booking datetime (start)
     */
    public LocalDateTime getBookingStartDateTime() {
        return LocalDateTime.of(bookingDate, startTime);
    }

    /**
     * Get full booking datetime (end)
     */
    public LocalDateTime getBookingEndDateTime() {
        return LocalDateTime.of(bookingDate, endTime);
    }

    /**
     * Check if booking is confirmed and paid
     */
    public boolean isConfirmedAndPaid() {
        return bookingStatus == BookingStatus.CONFIRMED || bookingStatus == BookingStatus.APPROVED;
    }

    /**
     * Check if booking can be cancelled
     */
    public boolean isCancellable() {
        return bookingStatus == BookingStatus.PENDING || 
               bookingStatus == BookingStatus.CONFIRMED || 
               bookingStatus == BookingStatus.APPROVED;
    }

    /**
     * Check if booking is in the past
     */
    public boolean isPastBooking() {
        return getBookingEndDateTime().isBefore(LocalDateTime.now());
    }

    /**
     * Get booking status with payment info
     */
    public String getStatusWithPayment() {
        return bookingStatus.getDisplayName() + " (" + paymentStatus.getDisplayName() + ")";
    }

    /**
     * Calculate remaining payment amount
     */
    public void calculateRemainingAmount() {
        if (totalAmount != null && paidAmount != null) {
            remainingAmount = totalAmount.subtract(paidAmount);
        }
    }

    /**
     * Check if payment is completed
     */
    public boolean isPaymentCompleted() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

    /**
     * Get formatted booking duration
     */
    public String getFormattedDuration() {
        if (durationHours == null) return "0 hours";
        
        int hours = durationHours.intValue();
        int minutes = (int) ((durationHours.doubleValue() - hours) * 60);
        
        if (minutes == 0) {
            return hours + (hours == 1 ? " hour" : " hours");
        } else {
            return hours + (hours == 1 ? " hour " : " hours ") + minutes + " minutes";
        }
    }

    /**
     * Generate booking reference number
     */
    public static String generateBookingReference() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = String.format("%04d%02d%02d-%02d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond());
        return "NUT-" + timestamp + "-" + String.format("%03d", (int) (Math.random() * 1000));
    }
}