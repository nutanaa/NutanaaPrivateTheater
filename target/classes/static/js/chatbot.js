/**
 * NUTANAA PRIVATE THEATER BOOKING PLATFORM
 * AI Chatbot JavaScript File
 * Handles: Chatbot UI, Message Processing, AI Responses
 */

// Chatbot State
let chatbotState = {
    isOpen: false,
    messages: [],
    isTyping: false,
    currentUser: null
};

// DOM Elements
let chatbotElements = {};

// Initialize Chatbot
document.addEventListener('DOMContentLoaded', function() {
    initializeChatbotElements();
    initializeChatbotEvents();
    initializeChatbot();
    console.log('🤖 AI Chatbot initialized');
});

/**
 * Initialize Chatbot DOM Elements
 */
function initializeChatbotElements() {
    chatbotElements = {
        chatbot: document.getElementById('chatbot'),
        toggle: document.querySelector('.chatbot-toggle'),
        window: document.querySelector('.chatbot-window'),
        messages: document.querySelector('.chatbot-messages'),
        input: document.querySelector('.chatbot-input input'),
        sendBtn: document.querySelector('.chatbot-input button'),
        header: document.querySelector('.chatbot-header')
    };
    
    console.log('Chatbot elements initialized');
}

/**
 * Initialize Chatbot Event Listeners
 */
function initializeChatbotEvents() {
    // Toggle chatbot
    if (chatbotElements.toggle) {
        chatbotElements.toggle.addEventListener('click', toggleChatbot);
    }
    
    // Send message on button click
    if (chatbotElements.sendBtn) {
        chatbotElements.sendBtn.addEventListener('click', sendMessage);
    }
    
    // Send message on Enter key
    if (chatbotElements.input) {
        chatbotElements.input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                sendMessage();
            }
        });
        
        // Auto-resize input
        chatbotElements.input.addEventListener('input', function() {
            // Add typing indicator functionality if needed
        });
    }
    
    console.log('Chatbot event listeners initialized');
}

/**
 * Initialize Chatbot with Welcome Message
 */
function initializeChatbot() {
    // Add welcome message
    const welcomeMessage = {
        type: 'bot',
        text: 'Hello! I\'m your Nutanaa AI assistant. I can help you with:\n\n• Theater bookings\n• KYC assistance\n• Pricing information\n• General support\n\nHow can I help you today?',
        timestamp: new Date()
    };
    
    addMessage(welcomeMessage);
    
    // Add quick action buttons
    addQuickActions();
}

/**
 * Toggle Chatbot Window
 */
function toggleChatbot() {
    chatbotState.isOpen = !chatbotState.isOpen;
    
    if (chatbotState.isOpen) {
        openChatbot();
    } else {
        closeChatbot();
    }
}

/**
 * Open Chatbot Window
 */
function openChatbot() {
    if (chatbotElements.window) {
        chatbotElements.window.classList.add('show');
        chatbotState.isOpen = true;
        
        // Update toggle button
        if (chatbotElements.toggle) {
            chatbotElements.toggle.innerHTML = '<i class="fas fa-times"></i>';
        }
        
        // Focus on input
        setTimeout(() => {
            if (chatbotElements.input) {
                chatbotElements.input.focus();
            }
        }, 300);
        
        console.log('Chatbot opened');
    }
}

/**
 * Close Chatbot Window
 */
function closeChatbot() {
    if (chatbotElements.window) {
        chatbotElements.window.classList.remove('show');
        chatbotState.isOpen = false;
        
        // Update toggle button
        if (chatbotElements.toggle) {
            chatbotElements.toggle.innerHTML = '<i class="fas fa-comments"></i>';
        }
        
        console.log('Chatbot closed');
    }
}

/**
 * Send Message
 */
async function sendMessage() {
    const input = chatbotElements.input;
    if (!input || !input.value.trim()) return;
    
    const userMessage = {
        type: 'user',
        text: input.value.trim(),
        timestamp: new Date()
    };
    
    // Add user message
    addMessage(userMessage);
    chatbotState.messages.push(userMessage);
    
    // Clear input
    input.value = '';
    
    // Show typing indicator
    showTypingIndicator();
    
    try {
        // Process message and get AI response
        const botResponse = await processMessage(userMessage.text);
        
        // Hide typing indicator
        hideTypingIndicator();
        
        // Add bot response
        addMessage(botResponse);
        chatbotState.messages.push(botResponse);
        
    } catch (error) {
        console.error('Chatbot error:', error);
        hideTypingIndicator();
        
        const errorResponse = {
            type: 'bot',
            text: 'Sorry, I encountered an error. Please try again or contact our support team.',
            timestamp: new Date()
        };
        
        addMessage(errorResponse);
    }
}

/**
 * Process User Message and Generate AI Response
 */
async function processMessage(userText) {
    // Simulate AI processing delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const lowercaseText = userText.toLowerCase();
    let responseText = '';
    
    // Intent recognition and response generation
    if (lowercaseText.includes('book') || lowercaseText.includes('booking')) {
        responseText = `I can help you with theater bookings! 🎬\n\nTo book a theater:\n1. Browse available theaters\n2. Select date and time\n3. Complete KYC verification\n4. Make payment\n\nWould you like me to show you available theaters or help with a specific booking?`;
        
    } else if (lowercaseText.includes('kyc') || lowercaseText.includes('document')) {
        responseText = `For KYC verification, you'll need:\n\n📄 Required Documents:\n• Aadhaar Card (PDF/Image)\n• PAN Card (PDF/Image)\n\n✅ Verification Process:\n1. Upload clear photos\n2. Ensure all details are visible\n3. Wait for admin approval\n\nNeed help uploading documents?`;
        
    } else if (lowercaseText.includes('price') || lowercaseText.includes('cost') || lowercaseText.includes('rate')) {
        responseText = `💰 Our Pricing:\n\n• Standard Theater: ₹800-1200/hour\n• Premium Theater: ₹1200-2000/hour\n• Luxury Theater: ₹2000-3500/hour\n\n📅 Weekend Surcharge: 20%\n⏰ Minimum Booking: 3 hours\n💳 Payment: Online/UPI/Cards\n\nWant to check specific theater rates?`;
        
    } else if (lowercaseText.includes('location') || lowercaseText.includes('address') || lowercaseText.includes('where')) {
        responseText = `📍 Theater Locations:\n\n🏢 Downtown Hub\n• Address: MG Road, Bangalore\n• Features: 4K, Dolby Atmos\n\n🌟 City Center\n• Address: Brigade Road, Bangalore\n• Features: Recliners, Premium Sound\n\n🎪 Mall Complex\n• Address: Koramangala, Bangalore\n• Features: Gaming Zone, Cafe\n\nWhich location interests you?`;
        
    } else if (lowercaseText.includes('payment') || lowercaseText.includes('pay')) {
        responseText = `💳 Payment Options:\n\n✅ Accepted Methods:\n• Credit/Debit Cards\n• UPI (PhonePe, GPay, Paytm)\n• Net Banking\n• Digital Wallets\n\n🔒 Security:\n• SSL Encrypted\n• PCI DSS Compliant\n• Instant Confirmation\n\nNeed help with payment process?`;
        
    } else if (lowercaseText.includes('cancel') || lowercaseText.includes('refund')) {
        responseText = `🔄 Cancellation Policy:\n\n• 24+ hours: Full refund\n• 12-24 hours: 50% refund\n• <12 hours: No refund\n• Emergency: Case-by-case\n\n💰 Refund Process:\n• Processed in 3-5 business days\n• Credited to original payment method\n\nNeed to cancel a booking?`;
        
    } else if (lowercaseText.includes('contact') || lowercaseText.includes('support') || lowercaseText.includes('help')) {
        responseText = `📞 Contact Support:\n\n• Phone: +91 98765 43210\n• Email: support@nutanaa.com\n• WhatsApp: +91 98765 43210\n• Live Chat: Right here!\n\n🕒 Support Hours:\n• Mon-Sun: 9 AM - 11 PM\n• Response Time: <30 minutes\n\nWhat specific help do you need?`;
        
    } else if (lowercaseText.includes('hello') || lowercaseText.includes('hi') || lowercaseText.includes('hey')) {
        responseText = `Hello there! 👋 Welcome to Nutanaa!\n\nI'm here to make your private theater experience amazing. I can assist you with:\n\n🎬 Theater bookings\n📄 KYC verification\n💰 Pricing details\n📍 Locations\n🎯 And much more!\n\nWhat would you like to know?`;
        
    } else if (lowercaseText.includes('thank')) {
        responseText = `You're very welcome! 😊\n\nI'm always here to help make your Nutanaa experience perfect. If you have any more questions or need assistance with bookings, just ask!\n\nEnjoy your private theater experience! 🎬✨`;
        
    } else {
        // Generic response with suggestions
        responseText = `I understand you're asking about "${userText}". Let me help you with that! 🤔\n\nHere are some things I can assist with:\n\n🎬 Theater Bookings\n📄 KYC & Documents\n💰 Pricing Information\n📍 Theater Locations\n💳 Payment Options\n📞 Contact Support\n\nCould you please be more specific about what you need help with?`;
    }
    
    return {
        type: 'bot',
        text: responseText,
        timestamp: new Date()
    };
}

/**
 * Add Message to Chat
 */
function addMessage(message) {
    if (!chatbotElements.messages) return;
    
    const messageEl = document.createElement('div');
    messageEl.className = `message ${message.type}-message`;
    
    const timeStr = message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    
    messageEl.innerHTML = `
        <div class="message-content">
            <div class="message-text">${formatMessageText(message.text)}</div>
            <div class="message-time">${timeStr}</div>
        </div>
        ${message.type === 'bot' ? '<div class="message-avatar"><i class="fas fa-robot"></i></div>' : ''}
    `;
    
    // Add message styles
    messageEl.style.cssText = `
        display: flex;
        margin-bottom: 1rem;
        ${message.type === 'user' ? 'justify-content: flex-end;' : 'justify-content: flex-start;'}
    `;
    
    const messageContent = messageEl.querySelector('.message-content');
    messageContent.style.cssText = `
        max-width: 80%;
        background: ${message.type === 'user' ? '#ff6b6b' : 'rgba(255, 255, 255, 0.1)'};
        color: white;
        padding: 0.75rem 1rem;
        border-radius: ${message.type === 'user' ? '15px 15px 5px 15px' : '15px 15px 15px 5px'};
        word-wrap: break-word;
    `;
    
    const messageText = messageEl.querySelector('.message-text');
    messageText.style.cssText = `
        margin-bottom: 0.25rem;
        line-height: 1.4;
        white-space: pre-line;
    `;
    
    const messageTime = messageEl.querySelector('.message-time');
    messageTime.style.cssText = `
        font-size: 0.7rem;
        opacity: 0.7;
        text-align: ${message.type === 'user' ? 'right' : 'left'};
    `;
    
    if (message.type === 'bot') {
        const avatar = messageEl.querySelector('.message-avatar');
        avatar.style.cssText = `
            width: 30px;
            height: 30px;
            background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 0.5rem;
            font-size: 0.8rem;
        `;
    }
    
    chatbotElements.messages.appendChild(messageEl);
    
    // Scroll to bottom
    chatbotElements.messages.scrollTop = chatbotElements.messages.scrollHeight;
    
    // Animate message appearance
    messageEl.style.opacity = '0';
    messageEl.style.transform = 'translateY(10px)';
    
    setTimeout(() => {
        messageEl.style.transition = 'all 0.3s ease';
        messageEl.style.opacity = '1';
        messageEl.style.transform = 'translateY(0)';
    }, 50);
}

/**
 * Format Message Text
 */
function formatMessageText(text) {
    // Convert line breaks to HTML
    let formatted = text.replace(/\n/g, '<br>');
    
    // Make bullet points look better
    formatted = formatted.replace(/•/g, '•');
    
    // Make emojis stand out
    formatted = formatted.replace(/(🎬|📄|💰|📍|💳|🔒|📞|🕒|👋|😊|✨|🤔|🏢|🌟|🎪|✅|🔄|💰)/g, '<span style="font-size: 1.1em;">$1</span>');
    
    return formatted;
}

/**
 * Show Typing Indicator
 */
function showTypingIndicator() {
    chatbotState.isTyping = true;
    
    const typingEl = document.createElement('div');
    typingEl.className = 'typing-indicator';
    typingEl.innerHTML = `
        <div class="message-avatar"><i class="fas fa-robot"></i></div>
        <div class="typing-dots">
            <span></span>
            <span></span>
            <span></span>
        </div>
    `;
    
    // Add styles
    typingEl.style.cssText = `
        display: flex;
        align-items: center;
        margin-bottom: 1rem;
    `;
    
    const avatar = typingEl.querySelector('.message-avatar');
    avatar.style.cssText = `
        width: 30px;
        height: 30px;
        background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 0.5rem;
        font-size: 0.8rem;
        color: white;
    `;
    
    const dots = typingEl.querySelector('.typing-dots');
    dots.style.cssText = `
        background: rgba(255, 255, 255, 0.1);
        padding: 0.75rem 1rem;
        border-radius: 15px 15px 15px 5px;
        display: flex;
        gap: 0.25rem;
    `;
    
    // Animate dots
    const dotSpans = typingEl.querySelectorAll('.typing-dots span');
    dotSpans.forEach((dot, index) => {
        dot.style.cssText = `
            width: 6px;
            height: 6px;
            background: #ff6b6b;
            border-radius: 50%;
            animation: typing-bounce 1.4s infinite;
            animation-delay: ${index * 0.2}s;
        `;
    });
    
    // Add CSS animation if not exists
    if (!document.querySelector('#typing-animation-styles')) {
        const style = document.createElement('style');
        style.id = 'typing-animation-styles';
        style.textContent = `
            @keyframes typing-bounce {
                0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
                30% { transform: translateY(-10px); opacity: 1; }
            }
        `;
        document.head.appendChild(style);
    }
    
    chatbotElements.messages.appendChild(typingEl);
    chatbotElements.messages.scrollTop = chatbotElements.messages.scrollHeight;
}

/**
 * Hide Typing Indicator
 */
function hideTypingIndicator() {
    chatbotState.isTyping = false;
    
    const typingEl = document.querySelector('.typing-indicator');
    if (typingEl) {
        typingEl.remove();
    }
}

/**
 * Add Quick Action Buttons
 */
function addQuickActions() {
    const quickActionsEl = document.createElement('div');
    quickActionsEl.className = 'quick-actions';
    quickActionsEl.innerHTML = `
        <div class="quick-action-title">Quick Actions:</div>
        <div class="quick-action-buttons">
            <button class="quick-btn" data-action="booking">🎬 Book Theater</button>
            <button class="quick-btn" data-action="kyc">📄 KYC Help</button>
            <button class="quick-btn" data-action="pricing">💰 View Pricing</button>
            <button class="quick-btn" data-action="contact">📞 Contact Us</button>
        </div>
    `;
    
    // Add styles
    quickActionsEl.style.cssText = `
        margin: 1rem 0;
        padding: 1rem;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 10px;
        border: 1px solid rgba(255, 255, 255, 0.1);
    `;
    
    const title = quickActionsEl.querySelector('.quick-action-title');
    title.style.cssText = `
        color: #4ecdc4;
        font-size: 0.8rem;
        margin-bottom: 0.5rem;
        font-weight: 600;
    `;
    
    const buttonsContainer = quickActionsEl.querySelector('.quick-action-buttons');
    buttonsContainer.style.cssText = `
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.5rem;
    `;
    
    const buttons = quickActionsEl.querySelectorAll('.quick-btn');
    buttons.forEach(btn => {
        btn.style.cssText = `
            background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
            color: white;
            border: none;
            padding: 0.5rem;
            border-radius: 8px;
            font-size: 0.75rem;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 500;
        `;
        
        btn.addEventListener('click', function() {
            const action = this.getAttribute('data-action');
            handleQuickAction(action);
        });
        
        btn.addEventListener('mouseover', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 4px 15px rgba(255, 107, 107, 0.3)';
        });
        
        btn.addEventListener('mouseout', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = 'none';
        });
    });
    
    chatbotElements.messages.appendChild(quickActionsEl);
    chatbotElements.messages.scrollTop = chatbotElements.messages.scrollHeight;
}

/**
 * Handle Quick Action Button Clicks
 */
function handleQuickAction(action) {
    let message = '';
    
    switch (action) {
        case 'booking':
            message = 'I want to book a theater';
            break;
        case 'kyc':
            message = 'I need help with KYC verification';
            break;
        case 'pricing':
            message = 'Show me the pricing details';
            break;
        case 'contact':
            message = 'I need to contact support';
            break;
        default:
            return;
    }
    
    // Set input value and send message
    if (chatbotElements.input) {
        chatbotElements.input.value = message;
        sendMessage();
    }
}

// Export chatbot functions for global use
window.NutanaaChatbot = {
    toggleChatbot,
    openChatbot,
    closeChatbot,
    sendMessage,
    chatbotState
};

console.log('🤖 Nutanaa AI Chatbot JavaScript initialized successfully');