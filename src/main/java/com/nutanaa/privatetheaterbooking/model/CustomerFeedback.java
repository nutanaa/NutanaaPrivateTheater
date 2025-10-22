package com.nutanaa.privatetheaterbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * CustomerFeedback Entity Model
 * 
 * This entity represents customer feedback, reviews, and ratings for theaters
 * in the Nutanaa Private Theater Booking Platform. It supports admin moderation
 * and feedback management capabilities.
 * 
 * Database Table: customer_feedback
 * 
 * Features:
 * - Customer ratings and reviews management
 * - Admin moderation and editing capabilities
 * - Feedback status tracking
 * - Theater-specific feedback association
 * - User-based feedback history
 * - Analytics and rating calculation support
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "customer_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFeedback {

    /**
     * Primary key - Auto-generated unique identifier for each feedback
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    /**
     * Reference to the user who provided the feedback
     * Foreign key relationship with UserAccount entity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User account is required")
    private UserAccount userAccount;

    /**
     * Reference to the theater being reviewed
     * Foreign key relationship with PrivateTheater entity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theater_id", nullable = false)
    @NotNull(message = "Theater selection is required")
    private PrivateTheater privateTheater;

    /**
     * Reference to the booking for which feedback is provided
     * Optional - feedback can be given without specific booking reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private TheaterBooking theaterBooking;

    // ========================================
    // FEEDBACK CONTENT
    // ========================================

    /**
     * Overall rating given by customer (1-5 stars)
     * Required field for feedback submission
     */
    @Column(name = "overall_rating", nullable = false)
    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private Integer overallRating;

    /**
     * Sound quality rating (1-5 stars)
     * Optional specific rating for audio experience
     */
    @Column(name = "sound_quality_rating")
    @Min(value = 1, message = "Sound quality rating must be at least 1")
    @Max(value = 5, message = "Sound quality rating cannot exceed 5")
    private Integer soundQualityRating;

    /**
     * Video quality rating (1-5 stars)
     * Optional specific rating for visual experience
     */
    @Column(name = "video_quality_rating")
    @Min(value = 1, message = "Video quality rating must be at least 1")
    @Max(value = 5, message = "Video quality rating cannot exceed 5")
    private Integer videoQualityRating;

    /**
     * Cleanliness rating (1-5 stars)
     * Optional specific rating for theater cleanliness
     */
    @Column(name = "cleanliness_rating")
    @Min(value = 1, message = "Cleanliness rating must be at least 1")
    @Max(value = 5, message = "Cleanliness rating cannot exceed 5")
    private Integer cleanlinessRating;

    /**
     * Service quality rating (1-5 stars)
     * Optional specific rating for staff service
     */
    @Column(name = "service_rating")
    @Min(value = 1, message = "Service rating must be at least 1")
    @Max(value = 5, message = "Service rating cannot exceed 5")
    private Integer serviceRating;

    /**
     * Value for money rating (1-5 stars)
     * Optional specific rating for pricing satisfaction
     */
    @Column(name = "value_for_money_rating")
    @Min(value = 1, message = "Value for money rating must be at least 1")
    @Max(value = 5, message = "Value for money rating cannot exceed 5")
    private Integer valueForMoneyRating;

    /**
     * Customer's review title/summary
     * Brief headline for the review
     */
    @Column(name = "review_title", length = 200)
    @Size(max = 200, message = "Review title cannot exceed 200 characters")
    private String reviewTitle;

    /**
     * Detailed customer review comments
     * Main feedback content from the customer
     */
    @Column(name = "review_comments", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Review comments cannot exceed 2000 characters")
    private String reviewComments;

    /**
     * Customer's recommendations or suggestions
     * Constructive feedback for improvements
     */
    @Column(name = "recommendations", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Recommendations cannot exceed 1000 characters")
    private String recommendations;

    // ========================================
    // FEEDBACK STATUS AND MODERATION
    // ========================================

    /**
     * Current status of the feedback
     * - PENDING: Awaiting admin review
     * - APPROVED: Approved and visible to public
     * - REJECTED: Rejected by admin (inappropriate content)
     * - HIDDEN: Hidden from public view
     * - EDITED: Modified by admin
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_status", nullable = false)
    private FeedbackStatus feedbackStatus = FeedbackStatus.PENDING;

    /**
     * Whether feedback is visible to public
     * Controls display on theater pages
     */
    @Column(name = "is_public_visible", nullable = false)
    private Boolean isPublicVisible = true;

    /**
     * Whether customer wants to remain anonymous
     * If true, customer name is not displayed with review
     */
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    /**
     * Whether this is a verified review
     * Set to true if customer actually used the theater
     */
    @Column(name = "is_verified_review", nullable = false)
    private Boolean isVerifiedReview = false;

    // ========================================
    // ADMIN MODERATION
    // ========================================

    /**
     * Admin who reviewed/moderated this feedback
     */
    @Column(name = "moderated_by_user_id")
    private Long moderatedByUserId;

    /**
     * Timestamp when feedback was reviewed by admin
     */
    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    /**
     * Admin's moderation comments/notes
     * Internal notes for moderation decisions
     */
    @Column(name = "moderation_notes", columnDefinition = "TEXT")
    private String moderationNotes;

    /**
     * Original review comments before admin editing
     * Stored for audit purposes when admin edits content
     */
    @Column(name = "original_comments", columnDefinition = "TEXT")
    private String originalComments;

    /**
     * Reason for rejection or hiding
     * Explanation when feedback is rejected or hidden
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ========================================
    // HELPFUL VOTES AND INTERACTION
    // ========================================

    /**
     * Number of "helpful" votes from other users
     * Users can vote if review was helpful
     */
    @Column(name = "helpful_votes")
    private Integer helpfulVotes = 0;

    /**
     * Number of "not helpful" votes from other users
     */
    @Column(name = "not_helpful_votes")
    private Integer notHelpfulVotes = 0;

    /**
     * Number of times this review was reported as inappropriate
     */
    @Column(name = "report_count")
    private Integer reportCount = 0;

    /**
     * Admin response to customer feedback
     * Official response from theater management
     */
    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    /**
     * Timestamp when admin response was added
     */
    @Column(name = "admin_response_date")
    private LocalDateTime adminResponseDate;

    /**
     * User who provided admin response
     */
    @Column(name = "admin_response_by_user_id")
    private Long adminResponseByUserId;

    // ========================================
    // ANALYTICS AND TRACKING
    // ========================================

    /**
     * Source of feedback submission
     * Examples: "Website", "Mobile App", "Email Survey"
     */
    @Column(name = "feedback_source", length = 50)
    private String feedbackSource = "Website";

    /**
     * Device information used for feedback submission
     */
    @Column(name = "device_info", length = 200)
    private String deviceInfo;

    /**
     * IP address from which feedback was submitted
     */
    @Column(name = "submission_ip_address", length = 45)
    private String submissionIpAddress;

    /**
     * Language of the feedback
     * For multi-language support
     */
    @Column(name = "feedback_language", length = 10)
    private String feedbackLanguage = "en";

    // ========================================
    // AUDIT FIELDS
    // ========================================

    /**
     * Timestamp when feedback was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when feedback was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========================================
    // ENUMERATIONS
    // ========================================

    /**
     * Feedback Status Enumeration
     */
    public enum FeedbackStatus {
        PENDING("Pending Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        HIDDEN("Hidden"),
        EDITED("Edited by Admin");

        private final String displayName;

        FeedbackStatus(String displayName) {
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
     * Get average of all specific ratings
     */
    public Double getAverageSpecificRating() {
        int count = 0;
        int total = 0;
        
        if (soundQualityRating != null) {
            total += soundQualityRating;
            count++;
        }
        if (videoQualityRating != null) {
            total += videoQualityRating;
            count++;
        }
        if (cleanlinessRating != null) {
            total += cleanlinessRating;
            count++;
        }
        if (serviceRating != null) {
            total += serviceRating;
            count++;
        }
        if (valueForMoneyRating != null) {
            total += valueForMoneyRating;
            count++;
        }
        
        return count > 0 ? (double) total / count : 0.0;
    }

    /**
     * Get star display for overall rating
     */
    public String getStarDisplay() {
        if (overallRating == null) return "No rating";
        return "★".repeat(overallRating) + "☆".repeat(5 - overallRating);
    }

    /**
     * Check if feedback is visible to public
     */
    public boolean isVisibleToPublic() {
        return isPublicVisible && (feedbackStatus == FeedbackStatus.APPROVED || feedbackStatus == FeedbackStatus.EDITED);
    }

    /**
     * Get customer display name (considering anonymity)
     */
    public String getCustomerDisplayName() {
        if (isAnonymous || userAccount == null) {
            return "Anonymous Customer";
        }
        return userAccount.getFullName();
    }

    /**
     * Calculate helpfulness ratio
     */
    public Double getHelpfulnessRatio() {
        int totalVotes = helpfulVotes + notHelpfulVotes;
        if (totalVotes == 0) return 0.0;
        return (double) helpfulVotes / totalVotes;
    }

    /**
     * Check if feedback needs moderation
     */
    public boolean needsModeration() {
        return feedbackStatus == FeedbackStatus.PENDING || reportCount > 5;
    }

    /**
     * Get formatted creation date
     */
    public String getFormattedDate() {
        if (createdAt == null) return "";
        return createdAt.toLocalDate().toString();
    }

    /**
     * Increment helpful votes
     */
    public void addHelpfulVote() {
        if (helpfulVotes == null) helpfulVotes = 0;
        helpfulVotes++;
    }

    /**
     * Increment not helpful votes
     */
    public void addNotHelpfulVote() {
        if (notHelpfulVotes == null) notHelpfulVotes = 0;
        notHelpfulVotes++;
    }

    /**
     * Report feedback as inappropriate
     */
    public void reportFeedback() {
        if (reportCount == null) reportCount = 0;
        reportCount++;
        
        // Auto-hide if too many reports
        if (reportCount >= 10) {
            feedbackStatus = FeedbackStatus.HIDDEN;
            isPublicVisible = false;
        }
    }

    /**
     * Mark as verified review (admin function)
     */
    public void markAsVerified(Long adminUserId) {
        isVerifiedReview = true;
        moderatedByUserId = adminUserId;
        moderatedAt = LocalDateTime.now();
    }

    /**
     * Approve feedback (admin function)
     */
    public void approveFeedback(Long adminUserId, String notes) {
        feedbackStatus = FeedbackStatus.APPROVED;
        isPublicVisible = true;
        moderatedByUserId = adminUserId;
        moderatedAt = LocalDateTime.now();
        moderationNotes = notes;
    }

    /**
     * Reject feedback (admin function)
     */
    public void rejectFeedback(Long adminUserId, String reason, String notes) {
        feedbackStatus = FeedbackStatus.REJECTED;
        isPublicVisible = false;
        rejectionReason = reason;
        moderatedByUserId = adminUserId;
        moderatedAt = LocalDateTime.now();
        moderationNotes = notes;
    }

    /**
     * Edit feedback content (admin function)
     */
    public void editFeedback(Long adminUserId, String newComments, String notes) {
        if (originalComments == null) {
            originalComments = reviewComments;
        }
        reviewComments = newComments;
        feedbackStatus = FeedbackStatus.EDITED;
        moderatedByUserId = adminUserId;
        moderatedAt = LocalDateTime.now();
        moderationNotes = notes;
    }
}