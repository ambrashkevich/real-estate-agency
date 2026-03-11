package org.example.agency.controller;

import org.example.agency.service.AgentService;
import org.example.agency.service.ClientService;
import org.example.agency.service.PropertyService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PropertyService propertyService;
    private final AgentService agentService;
    private final ClientService clientService;

    public HomeController(PropertyService propertyService, AgentService agentService,
                          ClientService clientService) {
        this.propertyService = propertyService;
        this.agentService = agentService;
        this.clientService = clientService;
    }

    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        model.addAttribute("propertiesCount", propertyService.getAllProperties().size());
        model.addAttribute("agentsCount", agentService.getAllAgents().size());
        model.addAttribute("clientsCount", clientService.getAllClients().size());
        return "home";
    }
}
