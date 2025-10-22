/**
 * NUTANAA PRIVATE THEATER BOOKING PLATFORM
 * Booking JavaScript File
 * Handles: Theater Booking Modal, Form Validation, Booking Process
 */

// Booking State
let bookingState = {
    selectedTheater: null,
    selectedDate: null,
    selectedTime: null,
    isBookingOpen: false
};

// Initialize Booking System
document.addEventListener('DOMContentLoaded', function() {
    initializeBookingEvents();
    console.log('📅 Booking system initialized');
});

/**
 * Initialize Booking Event Listeners
 */
function initializeBookingEvents() {
    // Listen for custom booking modal open event
    document.addEventListener('openBookingModal', function() {
        openBookingModal();
    });
    
    // Book Now buttons
    const bookButtons = document.querySelectorAll('.book-btn');
    bookButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            openBookingModal();
        });
    });
    
    console.log('Booking event listeners initialized');
}

/**
 * Open Booking Modal
 */
function openBookingModal() {
    // For now, show a simple alert
    // In a full implementation, this would open a booking modal
    alert('🎬 Booking feature coming soon!\n\nFor now, please use the chatbot or contact us directly to make a booking.\n\nDemo OTP for login: 123456');
    
    console.log('Booking modal opened');
}

// Export booking functions for global use
window.NutanaaBooking = {
    openBookingModal,
    bookingState
};

console.log('📅 Nutanaa Booking JavaScript initialized successfully');