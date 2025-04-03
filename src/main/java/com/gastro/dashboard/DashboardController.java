package com.gastro.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController() {
        // Tworzenie instancji serwisu bezpośrednio
        this.dashboardService = new DashboardService();
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        // Pobierz dane dashboardu z serwisu
        DashboardDTO dashboardData = dashboardService.getDashboardData();

        // Dodaj dane do modelu
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("dashboard", dashboardData);

        return "dashboard/dashboard";
    }
}