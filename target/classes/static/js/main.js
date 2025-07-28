/**
 * NUTANAA PRIVATE THEATER BOOKING PLATFORM
 * Main JavaScript File
 * Handles: Loading, Navigation, Smooth Scrolling, Stats Animation
 */

// DOM Content Loaded Event
document.addEventListener('DOMContentLoaded', function() {
    console.log('🎬 Nutanaa Theater Booking Platform - JavaScript Loaded');
    
    // Initialize all components
    initializeLoadingScreen();
    initializeNavigation();
    initializeScrollEffects();
    initializeStatsAnimation();
    initializeSkeletonLoading();
    initializeScrollToTop();
    
    // Hide loading screen after 2 seconds
    setTimeout(() => {
        hideLoadingScreen();
    }, 2000);
});

/**
 * Loading Screen Management
 */
function initializeLoadingScreen() {
    const loadingScreen = document.getElementById('loading-screen');
    
    if (loadingScreen) {
        console.log('Loading screen initialized');
        // Loading screen is visible by default
        // Will be hidden by setTimeout in DOMContentLoaded
    }
}

function hideLoadingScreen() {
    const loadingScreen = document.getElementById('loading-screen');
    
    if (loadingScreen) {
        loadingScreen.classList.add('hidden');
        console.log('Loading screen hidden');
        
        // Enable scroll after loading
        document.body.style.overflow = 'auto';
        
        // Trigger entrance animations
        triggerEntranceAnimations();
    }
}

/**
 * Navigation Management
 */
function initializeNavigation() {
    const header = document.querySelector('.main-header');
    const navLinks = document.querySelectorAll('.nav-link');
    const loginBtn = document.querySelector('.login-btn');
    
    // Header scroll effect
    window.addEventListener('scroll', function() {
        if (window.scrollY > 100) {
            header.style.background = 'rgba(26, 26, 46, 0.98)';
            header.style.backdropFilter = 'blur(25px)';
        } else {
            header.style.background = 'rgba(26, 26, 46, 0.95)';
            header.style.backdropFilter = 'blur(20px)';
        }
    });
    
    // Smooth scrolling for navigation links
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            
            if (targetId.startsWith('#')) {
                const targetSection = document.querySelector(targetId);
                if (targetSection) {
                    const headerHeight = header.offsetHeight;
                    const targetPosition = targetSection.offsetTop - headerHeight;
                    
                    window.scrollTo({
                        top: targetPosition,
                        behavior: 'smooth'
                    });
                    
                    // Update active nav link
                    updateActiveNavLink(targetId);
                }
            }
        });
    });
    
    // Login button click event
    if (loginBtn) {
        loginBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openLoginModal();
        });
    }
    
    console.log('Navigation initialized');
}

/**
 * Update Active Navigation Link
 */
function updateActiveNavLink(targetId) {
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === targetId) {
            link.classList.add('active');
        }
    });
}

/**
 * Scroll Effects and Animations
 */
function initializeScrollEffects() {
    // Intersection Observer for scroll animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
                
                // Special handling for different sections
                if (entry.target.classList.contains('feature-card')) {
                    animateFeatureCard(entry.target);
                } else if (entry.target.classList.contains('theater-card')) {
                    animateTheaterCard(entry.target);
                } else if (entry.target.classList.contains('process-step')) {
                    animateProcessStep(entry.target);
                }
            }
        });
    }, observerOptions);
    
    // Observe elements for animation
    const animatedElements = document.querySelectorAll('.feature-card, .theater-card, .process-step, .testimonial-card');
    animatedElements.forEach(element => {
        observer.observe(element);
    });
    
    console.log('Scroll effects initialized');
}

/**
 * Stats Counter Animation
 */
function initializeStatsAnimation() {
    const statsSection = document.querySelector('.hero-stats');
    let statsAnimated = false;
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting && !statsAnimated) {
                animateStats();
                statsAnimated = true;
            }
        });
    }, { threshold: 0.5 });
    
    if (statsSection) {
        observer.observe(statsSection);
    }
}

function animateStats() {
    const statNumbers = document.querySelectorAll('.stat-number');
    
    statNumbers.forEach(statNumber => {
        const finalNumber = parseInt(statNumber.textContent.replace(/[^\d]/g, ''));
        const suffix = statNumber.textContent.replace(/[\d]/g, '');
        let currentNumber = 0;
        const increment = finalNumber / 50; // 50 steps
        
        const timer = setInterval(() => {
            currentNumber += increment;
            if (currentNumber >= finalNumber) {
                statNumber.textContent = finalNumber + suffix;
                clearInterval(timer);
            } else {
                statNumber.textContent = Math.floor(currentNumber) + suffix;
            }
        }, 40); // Update every 40ms
    });
    
    console.log('Stats animation triggered');
}

/**
 * Skeleton Loading Management
 */
function initializeSkeletonLoading() {
    setTimeout(() => {
        const skeletonCards = document.querySelectorAll('.skeleton-theater');
        
        skeletonCards.forEach((skeleton, index) => {
            setTimeout(() => {
                // Replace skeleton with actual content
                skeleton.classList.remove('skeleton');
                skeleton.innerHTML = `
                    <div class="theater-image">
                        <div class="theater-badge">Premium</div>
                    </div>
                    <div class="theater-content">
                        <h3 class="theater-title">Luxury Theater ${index + 1}</h3>
                        <p class="theater-location"><i class="fas fa-map-marker-alt"></i> Downtown Location</p>
                        <div class="theater-amenities">
                            <span class="amenity"><i class="fas fa-couch"></i> Recliners</span>
                            <span class="amenity"><i class="fas fa-volume-up"></i> Dolby Atmos</span>
                        </div>
                        <div class="theater-footer">
                            <div class="theater-price">₹999/hour</div>
                            <button class="btn-primary book-btn">Book Now</button>
                        </div>
                    </div>
                `;
                
                // Add click event to book button
                const bookBtn = skeleton.querySelector('.book-btn');
                if (bookBtn) {
                    bookBtn.addEventListener('click', function() {
                        openBookingModal();
                    });
                }
            }, index * 300); // Stagger the loading
        });
    }, 3000); // Start after 3 seconds
    
    console.log('Skeleton loading initialized');
}

/**
 * Scroll to Top Button
 */
function initializeScrollToTop() {
    const scrollTopBtn = document.querySelector('.scroll-top');
    
    if (!scrollTopBtn) {
        // Create scroll to top button if it doesn't exist
        const button = document.createElement('button');
        button.className = 'scroll-top';
        button.innerHTML = '<i class="fas fa-arrow-up"></i>';
        document.body.appendChild(button);
        
        button.addEventListener('click', function() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }
    
    // Show/hide scroll to top button
    window.addEventListener('scroll', function() {
        const scrollTopBtn = document.querySelector('.scroll-top');
        if (window.scrollY > 500) {
            scrollTopBtn.classList.add('show');
        } else {
            scrollTopBtn.classList.remove('show');
        }
    });
    
    console.log('Scroll to top initialized');
}

/**
 * Animation Functions
 */
function triggerEntranceAnimations() {
    const heroContent = document.querySelector('.hero-content');
    if (heroContent) {
        heroContent.style.animation = 'slideInFromBottom 1s ease forwards';
    }
}

function animateFeatureCard(card) {
    card.style.animation = 'fadeInUp 0.8s ease forwards';
}

function animateTheaterCard(card) {
    card.style.animation = 'fadeInUp 0.8s ease forwards';
}

function animateProcessStep(step) {
    step.style.animation = 'fadeInUp 0.8s ease forwards';
}

/**
 * Modal Management Functions
 */
function openLoginModal() {
    console.log('Opening login modal');
    // This will be handled by auth.js
    const event = new CustomEvent('openLoginModal');
    document.dispatchEvent(event);
}

function openBookingModal() {
    console.log('Opening booking modal');
    // This will be handled by booking.js
    const event = new CustomEvent('openBookingModal');
    document.dispatchEvent(event);
}

/**
 * Utility Functions
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Export functions for use in other files
window.NutanaaMain = {
    openLoginModal,
    openBookingModal,
    hideLoadingScreen,
    updateActiveNavLink
};

console.log('🎬 Nutanaa Main JavaScript initialized successfully');