/**
 * NUTANAA PRIVATE THEATER BOOKING PLATFORM
 * Authentication JavaScript File
 * Handles: Login Modal, OTP Verification, User Authentication
 */

// Authentication State
let authState = {
    isLoggedIn: false,
    currentUser: null,
    otpSent: false,
    otpVerified: false,
    loginAttempts: 0,
    maxAttempts: 3
};

// DOM Elements
let authElements = {};

// Initialize Authentication System
document.addEventListener('DOMContentLoaded', function() {
    initializeAuthElements();
    initializeAuthEvents();
    checkAuthStatus();
    console.log('🔐 Authentication system initialized');
});

/**
 * Initialize DOM Elements
 */
function initializeAuthElements() {
    authElements = {
        loginModal: document.getElementById('loginModal'),
        loginForm: document.getElementById('loginForm'),
        otpForm: document.getElementById('otpForm'),
        closeModal: document.querySelector('.close-modal'),
        mobileInput: document.getElementById('mobileNumber'),
        heardAboutSelect: document.querySelector('select[name="heardAboutUs"]'),
        otpInput: document.getElementById('otpCode'),
        loginBtn: document.querySelector('.login-btn'),
        sendOtpBtn: document.querySelector('#loginForm button[type="submit"]'),
        verifyOtpBtn: document.querySelector('#otpForm button[type="submit"]')
    };
    
    console.log('Auth elements initialized:', Object.keys(authElements));
}

/**
 * Initialize Event Listeners
 */
function initializeAuthEvents() {
    // Listen for custom login modal open event
    document.addEventListener('openLoginModal', function() {
        openLoginModal();
    });
    
    // Login button click
    if (authElements.loginBtn) {
        authElements.loginBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openLoginModal();
        });
    }
    
    // Close modal events
    if (authElements.closeModal) {
        authElements.closeModal.addEventListener('click', closeLoginModal);
    }
    
    // Click outside modal to close
    if (authElements.loginModal) {
        authElements.loginModal.addEventListener('click', function(e) {
            if (e.target === authElements.loginModal) {
                closeLoginModal();
            }
        });
    }
    
    // Login form submission
    if (authElements.loginForm) {
        authElements.loginForm.addEventListener('submit', handleLoginSubmit);
    }
    
    // OTP form submission
    if (authElements.otpForm) {
        authElements.otpForm.addEventListener('submit', handleOtpSubmit);
    }
    
    // Mobile number input formatting
    if (authElements.mobileInput) {
        authElements.mobileInput.addEventListener('input', formatMobileNumber);
        authElements.mobileInput.addEventListener('keypress', function(e) {
            // Only allow numbers, +, and space
            if (!/[\d\+\s]/.test(e.key) && !['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
                e.preventDefault();
            }
        });
    }
    
    // OTP input formatting
    if (authElements.otpInput) {
        authElements.otpInput.addEventListener('input', formatOtpInput);
        authElements.otpInput.addEventListener('keypress', function(e) {
            // Only allow numbers
            if (!/\d/.test(e.key) && !['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
                e.preventDefault();
            }
        });
    }
    
    console.log('Auth event listeners initialized');
}

/**
 * Open Login Modal
 */
function openLoginModal() {
    if (authElements.loginModal) {
        authElements.loginModal.classList.add('show');
        authElements.loginModal.style.display = 'flex';
        
        // Reset forms
        resetAuthForms();
        
        // Focus on mobile input
        setTimeout(() => {
            if (authElements.mobileInput) {
                authElements.mobileInput.focus();
            }
        }, 300);
        
        console.log('Login modal opened');
    }
}

/**
 * Close Login Modal
 */
function closeLoginModal() {
    if (authElements.loginModal) {
        authElements.loginModal.classList.remove('show');
        
        setTimeout(() => {
            authElements.loginModal.style.display = 'none';
            resetAuthForms();
        }, 300);
        
        console.log('Login modal closed');
    }
}

/**
 * Reset Authentication Forms
 */
function resetAuthForms() {
    // Show login form, hide OTP form
    if (authElements.loginForm) {
        authElements.loginForm.classList.remove('hidden');
    }
    if (authElements.otpForm) {
        authElements.otpForm.classList.add('hidden');
    }
    
    // Clear inputs
    if (authElements.mobileInput) {
        authElements.mobileInput.value = '';
    }
    if (authElements.otpInput) {
        authElements.otpInput.value = '';
    }
    if (authElements.heardAboutSelect) {
        authElements.heardAboutSelect.value = '';
    }
    
    // Reset button states
    resetButtonStates();
    
    // Reset auth state
    authState.otpSent = false;
    authState.otpVerified = false;
    
    console.log('Auth forms reset');
}

/**
 * Handle Login Form Submission
 */
async function handleLoginSubmit(e) {
    e.preventDefault();
    
    const mobileNumber = authElements.mobileInput.value;
    const heardAboutUs = authElements.heardAboutSelect.value;
    
    // Validate inputs
    if (!validateMobileNumber(mobileNumber)) {
        showErrorMessage('Please enter a valid mobile number');
        return;
    }
    
    if (!heardAboutUs) {
        showErrorMessage('Please select where you heard about us');
        return;
    }
    
    // Disable button and show loading
    setButtonLoading(authElements.sendOtpBtn, 'Sending OTP...');
    
    try {
        // Send OTP request
        const response = await sendOtpRequest(mobileNumber, heardAboutUs);
        
        if (response.success) {
            authState.otpSent = true;
            showOtpForm();
            showSuccessMessage('OTP sent successfully! Check your SMS.');
            startOtpTimer();
        } else {
            showErrorMessage(response.message || 'Failed to send OTP. Please try again.');
        }
    } catch (error) {
        console.error('OTP send error:', error);
        showErrorMessage('Network error. Please check your connection and try again.');
    }
    
    resetButtonStates();
}

/**
 * Handle OTP Form Submission
 */
async function handleOtpSubmit(e) {
    e.preventDefault();
    
    const otpCode = authElements.otpInput.value;
    const mobileNumber = authElements.mobileInput.value;
    
    // Validate OTP
    if (!validateOtpCode(otpCode)) {
        showErrorMessage('Please enter a valid 6-digit OTP');
        return;
    }
    
    // Disable button and show loading
    setButtonLoading(authElements.verifyOtpBtn, 'Verifying...');
    
    try {
        // Verify OTP request
        const response = await verifyOtpRequest(mobileNumber, otpCode);
        
        if (response.success) {
            authState.otpVerified = true;
            authState.isLoggedIn = true;
            authState.currentUser = response.user;
            
            showSuccessMessage('Login successful! Welcome to Nutanaa.');
            
            // Store authentication
            storeAuthData(response);
            
            // Close modal and update UI
            setTimeout(() => {
                closeLoginModal();
                updateAuthUI();
                
                // Redirect based on user role
                handleUserRedirect(response.user);
            }, 1500);
            
        } else {
            authState.loginAttempts++;
            
            if (authState.loginAttempts >= authState.maxAttempts) {
                showErrorMessage('Too many failed attempts. Please try again later.');
                setTimeout(closeLoginModal, 2000);
            } else {
                showErrorMessage(response.message || 'Invalid OTP. Please try again.');
            }
        }
    } catch (error) {
        console.error('OTP verify error:', error);
        showErrorMessage('Network error. Please check your connection and try again.');
    }
    
    resetButtonStates();
}

/**
 * Show OTP Form
 */
function showOtpForm() {
    if (authElements.loginForm && authElements.otpForm) {
        authElements.loginForm.classList.add('hidden');
        authElements.otpForm.classList.remove('hidden');
        
        // Focus on OTP input
        setTimeout(() => {
            if (authElements.otpInput) {
                authElements.otpInput.focus();
            }
        }, 300);
        
        console.log('OTP form displayed');
    }
}

/**
 * Send OTP Request
 */
async function sendOtpRequest(mobileNumber, heardAboutUs) {
    // Simulate API call for demo
    return new Promise((resolve) => {
        setTimeout(() => {
            // Simulate success response
            resolve({
                success: true,
                message: 'OTP sent successfully',
                otpSent: true
            });
        }, 1500);
    });
    
    // Real API call (uncomment when backend is ready)
    /*
    const response = await fetch('/api/auth/send-otp', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({
            mobileNumber: mobileNumber,
            heardAboutUs: heardAboutUs,
            source: 'WEB_LOGIN'
        })
    });
    
    return await response.json();
    */
}

/**
 * Verify OTP Request
 */
async function verifyOtpRequest(mobileNumber, otpCode) {
    // Simulate API call for demo
    return new Promise((resolve) => {
        setTimeout(() => {
            // Simulate success response
            if (otpCode === '123456') {
                resolve({
                    success: true,
                    message: 'Login successful',
                    user: {
                        userId: 1,
                        fullName: 'Demo User',
                        mobileNumber: mobileNumber,
                        userRole: 'USER',
                        kycStatus: 'PENDING'
                    },
                    token: 'demo-jwt-token'
                });
            } else {
                resolve({
                    success: false,
                    message: 'Invalid OTP. Please try again.'
                });
            }
        }, 1000);
    });
    
    // Real API call (uncomment when backend is ready)
    /*
    const response = await fetch('/api/auth/verify-otp', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({
            mobileNumber: mobileNumber,
            otpCode: otpCode
        })
    });
    
    return await response.json();
    */
}

/**
 * Validation Functions
 */
function validateMobileNumber(mobile) {
    // Remove spaces and special characters
    const cleanMobile = mobile.replace(/[\s\-\(\)]/g, '');
    
    // Check if it's a valid Indian mobile number
    const mobileRegex = /^(\+91|91)?[6-9]\d{9}$/;
    return mobileRegex.test(cleanMobile);
}

function validateOtpCode(otp) {
    // Check if it's a 6-digit number
    const otpRegex = /^\d{6}$/;
    return otpRegex.test(otp);
}

/**
 * Format Mobile Number Input
 */
function formatMobileNumber(e) {
    let value = e.target.value.replace(/\D/g, '');
    
    if (value.startsWith('91')) {
        value = '+91 ' + value.slice(2);
    } else if (value.length > 0 && !value.startsWith('+91')) {
        value = '+91 ' + value;
    }
    
    // Format as +91 XXXXX XXXXX
    if (value.length > 8) {
        value = value.slice(0, 4) + ' ' + value.slice(4, 9) + ' ' + value.slice(9, 14);
    }
    
    e.target.value = value;
}

/**
 * Format OTP Input
 */
function formatOtpInput(e) {
    let value = e.target.value.replace(/\D/g, '');
    
    // Limit to 6 digits
    if (value.length > 6) {
        value = value.slice(0, 6);
    }
    
    e.target.value = value;
}

/**
 * Button State Management
 */
function setButtonLoading(button, text) {
    if (button) {
        button.disabled = true;
        button.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${text}`;
    }
}

function resetButtonStates() {
    if (authElements.sendOtpBtn) {
        authElements.sendOtpBtn.disabled = false;
        authElements.sendOtpBtn.innerHTML = 'Send OTP';
    }
    
    if (authElements.verifyOtpBtn) {
        authElements.verifyOtpBtn.disabled = false;
        authElements.verifyOtpBtn.innerHTML = 'Verify & Login';
    }
}

/**
 * Message Display Functions
 */
function showErrorMessage(message) {
    showMessage(message, 'error');
}

function showSuccessMessage(message) {
    showMessage(message, 'success');
}

function showMessage(message, type) {
    // Remove existing messages
    const existingMessage = document.querySelector('.auth-message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // Create message element
    const messageEl = document.createElement('div');
    messageEl.className = `auth-message ${type}`;
    messageEl.innerHTML = `
        <i class="fas fa-${type === 'error' ? 'exclamation-circle' : 'check-circle'}"></i>
        ${message}
    `;
    
    // Add styles
    messageEl.style.cssText = `
        position: fixed;
        top: 100px;
        left: 50%;
        transform: translateX(-50%);
        background: ${type === 'error' ? '#ff6b6b' : '#4ecdc4'};
        color: white;
        padding: 1rem 2rem;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 3000;
        animation: slideDown 0.3s ease;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-weight: 500;
    `;
    
    document.body.appendChild(messageEl);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (messageEl.parentNode) {
            messageEl.style.animation = 'slideUp 0.3s ease forwards';
            setTimeout(() => messageEl.remove(), 300);
        }
    }, 5000);
    
    console.log(`${type} message:`, message);
}

/**
 * OTP Timer
 */
function startOtpTimer() {
    let timeLeft = 300; // 5 minutes
    const timerEl = document.querySelector('.otp-timer');
    
    if (!timerEl) {
        // Create timer element if it doesn't exist
        const timer = document.createElement('div');
        timer.className = 'otp-timer';
        timer.style.cssText = `
            text-align: center;
            margin-top: 1rem;
            color: #b0b0b0;
            font-size: 0.9rem;
        `;
        authElements.otpForm.appendChild(timer);
    }
    
    const updateTimer = () => {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        const timerElement = document.querySelector('.otp-timer');
        
        if (timerElement) {
            if (timeLeft > 0) {
                timerElement.innerHTML = `OTP expires in ${minutes}:${seconds.toString().padStart(2, '0')}`;
                timeLeft--;
            } else {
                timerElement.innerHTML = 'OTP expired. Please request a new one.';
                // Disable verify button
                if (authElements.verifyOtpBtn) {
                    authElements.verifyOtpBtn.disabled = true;
                }
            }
        }
    };
    
    updateTimer();
    const interval = setInterval(updateTimer, 1000);
    
    // Clear interval when OTP is verified or modal is closed
    const clearTimer = () => {
        clearInterval(interval);
        const timerElement = document.querySelector('.otp-timer');
        if (timerElement) {
            timerElement.remove();
        }
    };
    
    // Store clear function for later use
    window.clearOtpTimer = clearTimer;
}

/**
 * Authentication Data Storage
 */
function storeAuthData(response) {
    try {
        const authData = {
            token: response.token,
            user: response.user,
            timestamp: Date.now()
        };
        
        localStorage.setItem('nutanaa_auth', JSON.stringify(authData));
        console.log('Auth data stored successfully');
    } catch (error) {
        console.error('Failed to store auth data:', error);
    }
}

/**
 * Check Authentication Status
 */
function checkAuthStatus() {
    try {
        const authData = JSON.parse(localStorage.getItem('nutanaa_auth'));
        
        if (authData && authData.token) {
            // Check if token is still valid (24 hours)
            const tokenAge = Date.now() - authData.timestamp;
            const tokenValid = tokenAge < 24 * 60 * 60 * 1000; // 24 hours
            
            if (tokenValid) {
                authState.isLoggedIn = true;
                authState.currentUser = authData.user;
                updateAuthUI();
                console.log('User already authenticated:', authData.user.fullName);
            } else {
                // Token expired, clear storage
                localStorage.removeItem('nutanaa_auth');
                console.log('Auth token expired');
            }
        }
    } catch (error) {
        console.error('Failed to check auth status:', error);
        localStorage.removeItem('nutanaa_auth');
    }
}

/**
 * Update Authentication UI
 */
function updateAuthUI() {
    const loginBtn = document.querySelector('.login-btn');
    const headerActions = document.querySelector('.header-actions');
    
    if (authState.isLoggedIn && authState.currentUser) {
        // Replace login button with user menu
        if (loginBtn && headerActions) {
            loginBtn.innerHTML = `
                <i class="fas fa-user"></i> ${authState.currentUser.fullName}
            `;
            loginBtn.classList.add('user-menu-btn');
            
            // Add user menu functionality
            loginBtn.addEventListener('click', function(e) {
                e.preventDefault();
                showUserMenu();
            });
        }
        
        console.log('Auth UI updated for logged-in user');
    }
}

/**
 * Show User Menu
 */
function showUserMenu() {
    console.log('User menu clicked - redirect to profile or show dropdown');
    // This would typically show a dropdown with profile, bookings, logout options
    // For now, just redirect to profile
    window.location.href = '/profile';
}

/**
 * Handle User Redirect Based on Role
 */
function handleUserRedirect(user) {
    switch (user.userRole) {
        case 'ADMIN':
            setTimeout(() => {
                window.location.href = '/admin';
            }, 2000);
            break;
        case 'MANAGER':
            setTimeout(() => {
                window.location.href = '/manager';
            }, 2000);
            break;
        default:
            // Regular user stays on current page
            console.log('Regular user logged in, staying on current page');
            break;
    }
}

/**
 * Logout Function
 */
function logout() {
    authState.isLoggedIn = false;
    authState.currentUser = null;
    
    localStorage.removeItem('nutanaa_auth');
    
    // Reset UI
    const loginBtn = document.querySelector('.login-btn');
    if (loginBtn) {
        loginBtn.innerHTML = '<i class="fas fa-sign-in-alt"></i> Login';
        loginBtn.classList.remove('user-menu-btn');
    }
    
    // Redirect to home
    window.location.href = '/';
    
    console.log('User logged out');
}

// Export functions for global use
window.NutanaaAuth = {
    openLoginModal,
    closeLoginModal,
    logout,
    checkAuthStatus,
    authState
};

console.log('🔐 Nutanaa Authentication JavaScript initialized successfully');