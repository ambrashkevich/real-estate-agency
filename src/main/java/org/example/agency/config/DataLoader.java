package org.example.agency.config;

import org.example.agency.model.Agent;
import org.example.agency.model.Client;
import org.example.agency.model.Deal;
import org.example.agency.model.Property;
import org.example.agency.model.User;
import org.example.agency.repository.AgentRepository;
import org.example.agency.repository.ClientRepository;
import org.example.agency.repository.DealRepository;
import org.example.agency.repository.PropertyRepository;
import org.example.agency.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final AgentRepository agentRepository;
    private final ClientRepository clientRepository;
    private final PropertyRepository propertyRepository;
    private final DealRepository dealRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(AgentRepository agentRepository,
                      ClientRepository clientRepository,
                      PropertyRepository propertyRepository,
                      DealRepository dealRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.clientRepository = clientRepository;
        this.propertyRepository = propertyRepository;
        this.dealRepository = dealRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (agentRepository.count() > 0) return;

        Agent a1 = createAgent("Иван", "Петров", "ivan@agency.ru", "+7-999-111-22-33", "LIC-001", 5.0);
        Agent a2 = createAgent("Мария", "Сидорова", "maria@agency.ru", "+7-999-222-33-44", "LIC-002", 4.5);
        Agent a3 = createAgent("Андрей", "Козлов", "andrey@agency.ru", "+7-999-555-66-77", "LIC-003", 4.8);

        Client c1 = createClient("Алексей", "Козлов", "alex@mail.ru", "+7-999-333-44-55", "Москва, ул. Ленина 1", "BUYER", "3-комн. квартира", new BigDecimal("15000000"));
        Client c2 = createClient("Елена", "Новикова", "elena@mail.ru", "+7-999-444-55-66", "СПб, Невский пр. 10", "SELLER", null, new BigDecimal("8000000"));
        Client c3 = createClient("Дмитрий", "Смирнов", "dmitry@gmail.com", "+7-999-777-88-99", "Москва, Арбат 15", "BUYER", "2-комн. квартира", new BigDecimal("12000000"));
        Client c4 = createClient("Ольга", "Волкова", "olga@yandex.ru", "+7-999-101-11-22", "Казань, Баумана 20", "SELLER", null, new BigDecimal("5000000"));
        Client c5 = createClient("Михаил", "Федоров", "mikhail@mail.ru", "+7-999-303-33-44", "Москва, Кутузовский 10", "BUYER", "пентхаус", new BigDecimal("80000000"));

        a1 = agentRepository.save(a1);
        a2 = agentRepository.save(a2);
        a3 = agentRepository.save(a3);
        c1 = clientRepository.save(c1);
        c2 = clientRepository.save(c2);
        c3 = clientRepository.save(c3);
        c4 = clientRepository.save(c4);
        c5 = clientRepository.save(c5);

        Property p1 = createProperty("Квартира в центре", "ул. Тверская 5", "Москва", "Центральный", "APARTMENT", new BigDecimal("25000000"), 85.0, 3, 2, "Просторная 3-комнатная квартира с видом на Кремль", a1);
        Property p2 = createProperty("Дом за городом", "Подмосковье, Рублевка", "Москва", "Одинцово", "HOUSE", new BigDecimal("45000000"), 250.0, 5, 4, "Элитный дом с участком 15 соток", a2);
        Property p3 = createProperty("Студия у метро", "ул. Профсоюзная 12", "Москва", "ЮЗАО", "APARTMENT", new BigDecimal("8500000"), 32.0, 1, 1, "Уютная студия, ремонт под ключ", a1);
        Property p4 = createProperty("Дуплекс на Новой Риге", "Новая Рига 45 км", "Московская обл", "Истра", "HOUSE", new BigDecimal("35000000"), 180.0, 4, 3, "Современный дуплекс с гаражом на 2 авто", a2);
        Property p5 = createProperty("Двухуровка в ЖК Солнечный", "ул. Академика Варги 8", "Москва", "ЮЗАО", "APARTMENT", new BigDecimal("18500000"), 72.0, 2, 2, "Квартира в новостройке, сдача 2025", a3);
        Property p6 = createProperty("Таунхаус", "пос. Барвиха Хиллс", "Московская обл", "Одинцово", "HOUSE", new BigDecimal("72000000"), 220.0, 4, 4, "Премиум таунхаус с террасой", a3);
        Property p7 = createProperty("Офис в бизнес-центре", "ул. Тверская-Ямская 1", "Москва", "Центральный", "COMMERCIAL", new BigDecimal("95000000"), 150.0, null, 2, "Офисное помещение класса A", a1);
        Property p8 = createProperty("Участок под ИЖС", "СНТ Родник, участок 45", "Московская обл", "Пушкинский", "LAND", new BigDecimal("4500000"), 600.0, null, null, "Участок 6 соток с коммуникациями", a2);
        Property p9 = createProperty("Пентхаус", "ул. Кутузовский проспект 25", "Москва", "ЗАО", "APARTMENT", new BigDecimal("120000000"), 280.0, 5, 4, "Эксклюзивный пентхаус с панорамным остеклением", a3);
        Property p10 = createProperty("Квартира-гостиная", "ул. Маросейка 3", "Москва", "Центральный", "APARTMENT", new BigDecimal("42000000"), 95.0, 3, 2, "Дизайнерский ремонт, исторический центр", a1);

        List<Property> props = propertyRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
        p1 = props.get(0);

        Deal d1 = new Deal();
        d1.setProperty(p1);
        d1.setClient(c1);
        d1.setAgent(a1);
        d1.setFinalPrice(new BigDecimal("24000000"));
        d1.setDealType("SALE");
        d1.setDealDate(LocalDateTime.now());
        d1.setStatus("COMPLETED");
        d1.setNotes("Оплата наличными");
        dealRepository.save(d1);
        p1.setStatus("SOLD");
        propertyRepository.save(p1);

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }
    }

    private Agent createAgent(String firstName, String lastName, String email, String phone, String license, double commission) {
        Agent a = new Agent();
        a.setFirstName(firstName);
        a.setLastName(lastName);
        a.setEmail(email);
        a.setPhone(phone);
        a.setLicenseNumber(license);
        a.setCommission(commission);
        return a;
    }

    private Client createClient(String firstName, String lastName, String email, String phone, String address, String type, String preferences, BigDecimal budget) {
        Client c = new Client();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(email);
        c.setPhone(phone);
        c.setAddress(address);
        c.setClientType(type);
        c.setPreferences(preferences);
        c.setBudget(budget);
        return c;
    }

    private Property createProperty(String title, String address, String city, String district, String type,
                                    BigDecimal price, Double area, Integer bedrooms, Integer bathrooms,
                                    String description, Agent agent) {
        Property p = new Property();
        p.setTitle(title);
        p.setAddress(address);
        p.setCity(city);
        p.setDistrict(district);
        p.setPropertyType(type);
        p.setPrice(price);
        p.setArea(area);
        p.setBedrooms(bedrooms);
        p.setBathrooms(bathrooms);
        p.setDescription(description);
        p.setStatus("AVAILABLE");
        p.setAgent(agent);
        return p;
    }
}
