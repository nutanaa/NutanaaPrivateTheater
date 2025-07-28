package com.nutanaa.privatetheaterbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * NUTANAA PRIVATE THEATER BOOKING PLATFORM
 * Home Controller - Serves static pages
 * Handles: Homepage, Static Content Routing
 */
@Controller
public class HomeController {

    /**
     * Homepage route - serves index.html
     * @return the index page template name
     */
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    /**
     * Health check endpoint
     * @return a simple status message
     */
    @GetMapping("/health")
    public String health() {
        return "Application is running!";
    }
}