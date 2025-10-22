package com.nutanaa.privatetheaterbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application Class for Nutanaa Private Theater Booking Platform
 * 
 * This class serves as the entry point for the entire application, enabling:
 * - Spring Boot Auto Configuration
 * - Asynchronous processing for SMS/Email/WhatsApp services
 * - Transaction management for database operations
 * - Configuration properties binding
 * 
 * Features Enabled:
 * - Comprehensive theater booking system
 * - Mobile-based OTP authentication
 * - KYC document management
 * - Admin/Manager dashboards with analytics
 * - AI chatbot integration
 * - Real-time booking availability
 * - Multi-channel notifications (SMS/Email/WhatsApp)
 * 
 * @author Nutanaa Development Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableConfigurationProperties
public class NutanaaPrivateTheaterBookingApplication {

    /**
     * Main method to start the Nutanaa Private Theater Booking Platform
     * 
     * This method initializes the Spring Boot application context and starts
     * the embedded Tomcat server to serve both REST APIs and static web content.
     * 
     * The application will be available at:
     * - Frontend: http://localhost:8080
     * - API Endpoints: http://localhost:8080/api/**
     * - Admin Dashboard: http://localhost:8080/admin/**
     * - Manager Dashboard: http://localhost:8080/manager/**
     * 
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Start the Spring Boot application
        SpringApplication.run(NutanaaPrivateTheaterBookingApplication.class, args);
        
        // Log application startup message
        System.out.println("========================================");
        System.out.println("🎬 NUTANAA PRIVATE THEATER BOOKING PLATFORM");
        System.out.println("========================================");
        System.out.println("✅ Application Started Successfully!");
        System.out.println("🌐 Frontend: http://localhost:8080");
        System.out.println("🔧 Admin Panel: http://localhost:8080/admin");
        System.out.println("👨‍💼 Manager Panel: http://localhost:8080/manager");
        System.out.println("📱 API Docs: http://localhost:8080/actuator");
        System.out.println("========================================");
    }
}