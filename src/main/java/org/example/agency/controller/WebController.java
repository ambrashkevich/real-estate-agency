package org.example.agency.controller;

import org.example.agency.model.*;
import org.example.agency.repository.DistrictRepository;
import org.example.agency.repository.PropertyTypeRepository;
import org.example.agency.service.AgentService;
import org.example.agency.service.ClientService;
import org.example.agency.service.DealService;
import org.example.agency.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/web")
public class WebController {

    private final AgentService agentService;
    private final ClientService clientService;
    private final PropertyService propertyService;
    private final DealService dealService;
    
    @Autowired
    private DistrictRepository districtRepository;
    
    @Autowired
    private PropertyTypeRepository propertyTypeRepository;

    public WebController(AgentService agentService, ClientService clientService,
                         PropertyService propertyService, DealService dealService) {
        this.agentService = agentService;
        this.clientService = clientService;
        this.propertyService = propertyService;
        this.dealService = dealService;
    }

    @GetMapping
    public String index(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        List<Agent> agents = agentService.getAllAgents();
        model.addAttribute("properties", properties);
        model.addAttribute("agents", agents);
        return "index";
    }

    @GetMapping("/agents")
    public String agents(Model model) {
        model.addAttribute("agents", agentService.getAllAgents());
        return "agents";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clients";
    }

    @GetMapping("/properties")
    public String properties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "properties";
    }

    @GetMapping("/deals")
    public String deals(Model model) {
        model.addAttribute("deals", dealService.getAllDeals());
        return "deals";
    }

    @GetMapping("/properties/{id}")
    public String propertyDetail(@PathVariable Long id, Model model) {
        model.addAttribute("property", propertyService.getPropertyById(id));
        model.addAttribute("deals", dealService.getDealsByPropertyId(id));
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("clients", clientService.getAllClients());
        return "property-detail";
    }

    @GetMapping("/agents/{id}")
    public String agentDetail(@PathVariable Long id, Model model) {
        model.addAttribute("agent", agentService.getAgentById(id));
        model.addAttribute("properties", propertyService.getPropertiesByAgentId(id));
        model.addAttribute("deals", dealService.getDealsByAgent(id));
        return "agent-detail";
    }

    @GetMapping("/clients/{id}")
    public String clientDetail(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.getClientById(id));
        model.addAttribute("deals", dealService.getDealsByClientId(id));
        return "client-detail";
    }

    @GetMapping("/deals/{id}")
    public String dealDetail(@PathVariable Long id, Model model) {
        model.addAttribute("deal", dealService.getDealById(id));
        return "deal-detail";
    }

    // ========== ADMIN: Property CRUD ==========
    @GetMapping("/properties/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String propertyFormNew(Model model) {
        model.addAttribute("property", new Property());
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("districts", districtRepository.findAll());
        model.addAttribute("propertyTypes", propertyTypeRepository.findAll());
        return "property-form";
    }

    @PostMapping("/properties")
    @PreAuthorize("hasRole('ADMIN')")
    public String propertyCreate(@ModelAttribute Property property, 
                                 @RequestParam(name = "agentId", required = false) Long agentId,
                                 @RequestParam(name = "districtId", required = false) Long districtId,
                                 @RequestParam(name = "propertyTypeId", required = false) Long propertyTypeId,
                                 RedirectAttributes ra) {
        propertyService.createProperty(property, agentId, districtId, propertyTypeId);
        ra.addFlashAttribute("message", "Объект успешно добавлен");
        return "redirect:/web/properties";
    }

    @GetMapping("/properties/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String propertyFormEdit(@PathVariable Long id, Model model) {
        model.addAttribute("property", propertyService.getPropertyById(id));
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("districts", districtRepository.findAll());
        model.addAttribute("propertyTypes", propertyTypeRepository.findAll());
        return "property-form";
    }

    @PostMapping("/properties/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String propertyUpdate(@PathVariable Long id, @ModelAttribute Property property,
                                 @RequestParam(name = "agentId", required = false) Long agentId,
                                 @RequestParam(name = "districtId", required = false) Long districtId,
                                 @RequestParam(name = "propertyTypeId", required = false) Long propertyTypeId,
                                 RedirectAttributes ra) {
        propertyService.updateProperty(id, property, agentId, districtId, propertyTypeId);
        ra.addFlashAttribute("message", "Объект обновлён");
        return "redirect:/web/properties/" + id;
    }

    @PostMapping("/properties/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String propertyDelete(@PathVariable Long id, RedirectAttributes ra) {
        propertyService.deleteProperty(id);
        ra.addFlashAttribute("message", "Объект удалён");
        return "redirect:/web/properties";
    }

    // ========== USER: Schedule viewing & Purchase ==========
    @PostMapping("/properties/{id}/viewing")
    public String scheduleViewing(@PathVariable Long id,
                                  @RequestParam Long clientId,
                                  @RequestParam Long agentId,
                                  @RequestParam String scheduledDate,
                                  RedirectAttributes ra) {
        Property property = propertyService.getPropertyById(id);
        if (!"AVAILABLE".equals(property.getStatus())) {
            ra.addFlashAttribute("error", "Объект недоступен для записи на просмотр");
            return "redirect:/web/properties/" + id;
        }
        Deal deal = new Deal();
        deal.setProperty(property);
        deal.setClient(clientService.getClientById(clientId));
        deal.setAgent(agentService.getAgentById(agentId));
        deal.setFinalPrice(BigDecimal.ZERO);
        deal.setDealType("VIEWING");
        deal.setDealDate(LocalDateTime.parse(scheduledDate + "T12:00:00"));
        deal.setStatus("PENDING");
        deal.setNotes("Запись на просмотр");
        dealService.createDeal(deal);
        ra.addFlashAttribute("message", "Просмотр успешно запланирован");
        return "redirect:/web/properties/" + id;
    }

    @PostMapping("/properties/{id}/purchase")
    public String purchaseProperty(@PathVariable Long id,
                                   @RequestParam Long clientId,
                                   @RequestParam Long agentId,
                                   @RequestParam BigDecimal finalPrice,
                                   @RequestParam(required = false) String notes,
                                   RedirectAttributes ra) {
        Property property = propertyService.getPropertyById(id);
        if ("SOLD".equals(property.getStatus())) {
            ra.addFlashAttribute("error", "Объект уже продан");
            return "redirect:/web/properties/" + id;
        }
        Deal deal = new Deal();
        deal.setProperty(property);
        deal.setClient(clientService.getClientById(clientId));
        deal.setAgent(agentService.getAgentById(agentId));
        deal.setFinalPrice(finalPrice);
        deal.setDealType("SALE");
        deal.setDealDate(LocalDateTime.now());
        deal.setStatus("COMPLETED");
        deal.setNotes(notes);
        dealService.createDeal(deal);
        ra.addFlashAttribute("message", "Сделка оформлена успешно");
        return "redirect:/web/properties/" + id;
    }
}
