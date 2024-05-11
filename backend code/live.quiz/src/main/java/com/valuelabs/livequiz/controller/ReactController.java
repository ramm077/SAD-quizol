package com.valuelabs.livequiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsible for handling frontend react routes
 */
@Controller
public class ReactController {
    /**
     * Endpoint for handling react routes
     * @return the page on the route
     */
    @RequestMapping(value = {"/login","/forgot_password","/admin/**","/profile/**","/about","/user_dashboard","/user_history","/quiz_display/**",})
    public String forward() {
        return "forward:/";
    }
}