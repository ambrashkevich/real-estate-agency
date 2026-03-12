package org.example.agency.config;

import org.example.agency.model.*;
import org.example.agency.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final AgentRepository agentRepository;
    private final ClientRepository clientRepository;
    private final PropertyRepository propertyRepository;
    private final DealRepository dealRepository;
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final FeatureRepository featureRepository;
    private final ViewingRepository viewingRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataLoader(AgentRepository agentRepository,
                      ClientRepository clientRepository,
                      PropertyRepository propertyRepository,
                      DealRepository dealRepository,
                      UserRepository userRepository,
                      DistrictRepository districtRepository,
                      PropertyTypeRepository propertyTypeRepository,
                      FeatureRepository featureRepository,
                      ViewingRepository viewingRepository,
                      ReviewRepository reviewRepository,
                      PaymentRepository paymentRepository,
                      DocumentRepository documentRepository,
                      AuditLogRepository auditLogRepository,
                      PasswordEncoder passwordEncoder,
                      JdbcTemplate jdbcTemplate) {
        this.agentRepository = agentRepository;
        this.clientRepository = clientRepository;
        this.propertyRepository = propertyRepository;
        this.dealRepository = dealRepository;
        this.userRepository = userRepository;
        this.districtRepository = districtRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        this.featureRepository = featureRepository;
        this.viewingRepository = viewingRepository;
        this.reviewRepository = reviewRepository;
        this.paymentRepository = paymentRepository;
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (agentRepository.count() > 0) {
            System.out.println("База данных уже содержит данные. Пропуск автоматического заполнения.");
            return;
        }

        // Create Districts
        Map<String, District> districts = new HashMap<>();
        for (String name : List.of("Центральный", "ЮЗАО", "ЗАО", "Одинцово", "Истра", "Пушкинский")) {
            District d = districtRepository.saveAndFlush(new District(null, name, "Описание района " + name));
            districts.put(name, d);
        }

        // Create Property Types
        Map<String, PropertyType> types = new HashMap<>();
        for (String name : List.of("APARTMENT", "HOUSE", "COMMERCIAL", "LAND")) {
            PropertyType t = propertyTypeRepository.saveAndFlush(new PropertyType(null, name, "Тип недвижимости: " + name));
            types.put(name, t);
        }

        // Create Features
        Feature pool = featureRepository.save(new Feature(null, "Бассейн", null));
        Feature garage = featureRepository.save(new Feature(null, "Гараж", null));
        Feature security = featureRepository.save(new Feature(null, "Охрана", null));
        Feature elevator = featureRepository.save(new Feature(null, "Лифт", null));
        Feature parking = featureRepository.save(new Feature(null, "Парковка", null));
        Feature gym = featureRepository.save(new Feature(null, "Спортзал", null));
        Feature terrace = featureRepository.save(new Feature(null, "Терраса", null));

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            User agent = new User();
            agent.setUsername("agent");
            agent.setPassword(passwordEncoder.encode("agent"));
            agent.setRole("ROLE_AGENT");
            userRepository.save(agent);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole("ROLE_CLIENT");
            userRepository.save(user);
        }

        Agent a1 = createAgent("Иван", "Петров", "ivan@agency.ru", "+7-999-111-22-33", "LIC-001", 5.0);
        Agent a2 = createAgent("Мария", "Сидорова", "maria@agency.ru", "+7-999-222-33-44", "LIC-002", 4.5);
        Agent a3 = createAgent("Андрей", "Козлов", "andrey@agency.ru", "+7-999-555-66-77", "LIC-003", 4.8);
        Agent a4 = createAgent("Елена", "Смирнова", "elena@agency.ru", "+7-999-888-99-00", "LIC-004", 4.2);
        Agent a5 = createAgent("Николай", "Морозов", "nikolay@agency.ru", "+7-999-444-11-22", "LIC-005", 4.6);
        Agent a6 = createAgent("Светлана", "Иванова", "svetlana@agency.ru", "+7-999-333-55-66", "LIC-006", 4.9);

        Client c1 = createClient("Алексей", "Козлов", "alex@mail.ru", "+7-999-333-44-55", "Москва, ул. Ленина 1", "BUYER", "3-комн. квартира", new BigDecimal("15000000"));
        Client c2 = createClient("Елена", "Новикова", "elena@mail.ru", "+7-999-444-55-66", "СПб, Невский пр. 10", "SELLER", null, new BigDecimal("8000000"));
        Client c3 = createClient("Дмитрий", "Смирнов", "dmitry@gmail.com", "+7-999-777-88-99", "Москва, Арбат 15", "BUYER", "2-комн. квартира", new BigDecimal("12000000"));
        Client c4 = createClient("Ольга", "Волкова", "olga@yandex.ru", "+7-999-101-11-22", "Казань, Баумана 20", "SELLER", null, new BigDecimal("5000000"));
        Client c5 = createClient("Михаил", "Федоров", "mikhail@mail.ru", "+7-999-303-33-44", "Москва, Кутузовский 10", "BUYER", "пентхаус", new BigDecimal("80000000"));
        Client c6 = createClient("Анна", "Белова", "anna@mail.ru", "+7-999-404-44-55", "Москва, Тверская 20", "BUYER", "офис", new BigDecimal("50000000"));
        Client c7 = createClient("Сергей", "Павлов", "sergey@mail.ru", "+7-999-505-55-66", "Москва, Новый Арбат 10", "BUYER", "квартира", new BigDecimal("30000000"));
        Client c8 = createClient("Татьяна", "Кузнецова", "tanya@mail.ru", "+7-999-606-66-77", "Сочи, Курортный пр. 5", "SELLER", null, new BigDecimal("12000000"));

        a1 = agentRepository.save(a1);
        a2 = agentRepository.save(a2);
        a3 = agentRepository.save(a3);
        a4 = agentRepository.save(a4);
        a5 = agentRepository.save(a5);
        a6 = agentRepository.save(a6);
        c1 = clientRepository.save(c1);
        c2 = clientRepository.save(c2);
        c3 = clientRepository.save(c3);
        c4 = clientRepository.save(c4);
        c5 = clientRepository.save(c5);
        c6 = clientRepository.save(c6);
        c7 = clientRepository.save(c7);
        c8 = clientRepository.save(c8);

        Property p1 = createProperty("Квартира в центре", "ул. Тверская 5", "Москва", districts.get("Центральный"), types.get("APARTMENT"), new BigDecimal("25000000"), 85.0, 3, 2, "Просторная 3-комнатная квартира с видом на Кремль", a1);
        Property p2 = createProperty("Дом за городом", "Подмосковье, Рублевка", "Москва", districts.get("Одинцово"), types.get("HOUSE"), new BigDecimal("45000000"), 250.0, 5, 4, "Элитный дом с участком 15 соток", a2);
        Property p3 = createProperty("Студия у метро", "ул. Профсоюзная 12", "Москва", districts.get("ЮЗАО"), types.get("APARTMENT"), new BigDecimal("8500000"), 32.0, 1, 1, "Уютная студия, ремонт под ключ", a1);
        Property p4 = createProperty("Дуплекс на Новой Риге", "Новая Рига 45 км", "Московская обл", districts.get("Истра"), types.get("HOUSE"), new BigDecimal("35000000"), 180.0, 4, 3, "Современный дуплекс с гаражом на 2 авто", a2);
        Property p5 = createProperty("Двухуровневая в ЖК Солнечный", "ул. Академика Варги 8", "Москва", districts.get("ЮЗАО"), types.get("APARTMENT"), new BigDecimal("18500000"), 72.0, 2, 2, "Квартира в новостройке, сдача 2025", a3);
        Property p6 = createProperty("Таунхаус", "пос. Барвиха Хиллс", "Московская обл", districts.get("Одинцово"), types.get("HOUSE"), new BigDecimal("72000000"), 220.0, 4, 4, "Премиум таунхаус с террасой", a3);
        Property p7 = createProperty("Офис в бизнес-центре", "ул. Тверская-Ямская 1", "Москва", districts.get("Центральный"), types.get("COMMERCIAL"), new BigDecimal("95000000"), 150.0, null, 2, "Офисное помещение класса A", a1);
        Property p8 = createProperty("Участок под ИЖС", "СНТ Родник, участок 45", "Московская обл", districts.get("Пушкинский"), types.get("LAND"), new BigDecimal("4500000"), 600.0, null, null, "Участок 6 соток с коммуникациями", a2);
        Property p9 = createProperty("Пентхаус", "ул. Кутузовский проспект 25", "Москва", districts.get("ЗАО"), types.get("APARTMENT"), new BigDecimal("120000000"), 280.0, 5, 4, "Эксклюзивный пентхаус с панорамным остеклением", a3);
        Property p10 = createProperty("Квартира-гостиная", "ул. Маросейка 3", "Москва", districts.get("Центральный"), types.get("APARTMENT"), new BigDecimal("42000000"), 95.0, 3, 2, "Дизайнерский ремонт, исторический центр", a1);
        Property p11 = createProperty("Складское помещение", "Промзона Юг", "Подольск", districts.get("Пушкинский"), types.get("COMMERCIAL"), new BigDecimal("15000000"), 500.0, null, 1, "Большой склад с удобным подъездом", a4);
        Property p12 = createProperty("Коттедж в Истре", "пос. Светлый", "Истра", districts.get("Истра"), types.get("HOUSE"), new BigDecimal("28000000"), 160.0, 3, 2, "Уютный коттедж в лесу", a4);
        Property p13 = createProperty("Апартаменты в Сити", "Пресненская наб. 12", "Москва", districts.get("Центральный"), types.get("APARTMENT"), new BigDecimal("55000000"), 110.0, 2, 2, "Видовые апартаменты в башне Федерация", a5);
        Property p14 = createProperty("Земельный массив", "дер. Жуковка", "Московская обл", districts.get("Одинцово"), types.get("LAND"), new BigDecimal("150000000"), 5000.0, null, null, "Участок под застройку поселка", a6);
        Property p15 = createProperty("Магазин", "ул. Профсоюзная 56", "Москва", districts.get("ЮЗАО"), types.get("COMMERCIAL"), new BigDecimal("38000000"), 120.0, null, 1, "Торговое помещение с витринами", a5);
        Property p16 = createProperty("Коттедж в Барвихе", "Рублевское ш.", "Москва", districts.get("Одинцово"), types.get("HOUSE"), new BigDecimal("120000000"), 450.0, 6, 5, "Элитный коттедж с бассейном", a2);
        Property p17 = createProperty("Студия в ЗАО", "ул. Минская 10", "Москва", districts.get("ЗАО"), types.get("APARTMENT"), new BigDecimal("12500000"), 40.0, 1, 1, "Современная студия", a3);
        Property p18 = createProperty("Офис на Арбате", "ул. Арбат 10", "Москва", districts.get("Центральный"), types.get("COMMERCIAL"), new BigDecimal("85000000"), 200.0, null, 3, "Офис в историческом здании", a1);
        Property p19 = createProperty("Участок в Истре", "дер. Ленино", "Московская обл", districts.get("Истра"), types.get("LAND"), new BigDecimal("6500000"), 1200.0, null, null, "Участок у реки", a4);
        Property p20 = createProperty("Квартира в Пушкино", "ул. Чехова 5", "Пушкино", districts.get("Пушкинский"), types.get("APARTMENT"), new BigDecimal("7500000"), 55.0, 2, 1, "Тихий район, рядом парк", a6);

        p1.getFeatures().add(elevator);
        p1.getFeatures().add(security);
        p2.getFeatures().add(pool);
        p2.getFeatures().add(garage);
        p2.getFeatures().add(security);
        p5.getFeatures().add(parking);
        p6.getFeatures().add(terrace);
        p9.getFeatures().add(gym);
        p9.getFeatures().add(terrace);
        p9.getFeatures().add(pool);
        p13.getFeatures().add(security);
        p13.getFeatures().add(parking);
        p13.getFeatures().add(gym);
        p16.getFeatures().add(pool);
        p16.getFeatures().add(garage);
        p16.getFeatures().add(security);
        p18.getFeatures().add(security);
        p18.getFeatures().add(parking);

        List<Property> props = propertyRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20));
        p1 = props.get(0);
        p2 = props.get(1);
        Property p13_saved = props.get(12);
        Property p16_saved = props.get(15);
        Property p20_saved = props.get(19);

        Deal d1 = new Deal();
        d1.setProperty(p1);
        d1.setClient(c1);
        d1.setAgent(a1);
        d1.setFinalPrice(new BigDecimal("24000000"));
        d1.setDealType("SALE");
        d1.setDealDate(LocalDateTime.now().minusDays(10));
        d1.setStatus("COMPLETED");
        d1.setNotes("Оплата наличными");
        dealRepository.save(d1);
        p1.setStatus("SOLD");
        propertyRepository.save(p1);

        Deal d2 = new Deal();
        d2.setProperty(p10);
        d2.setClient(c5);
        d2.setAgent(a1);
        d2.setFinalPrice(new BigDecimal("41000000"));
        d2.setDealType("SALE");
        d2.setDealDate(LocalDateTime.now().minusDays(5));
        d2.setStatus("COMPLETED");
        dealRepository.save(d2);
        p10.setStatus("SOLD");
        propertyRepository.save(p10);

        Deal d3 = new Deal();
        d3.setProperty(p13_saved);
        d3.setClient(c7);
        d3.setAgent(a5);
        d3.setFinalPrice(new BigDecimal("53000000"));
        d3.setDealType("SALE");
        d3.setDealDate(LocalDateTime.now().minusDays(2));
        d3.setStatus("COMPLETED");
        dealRepository.save(d3);
        p13_saved.setStatus("SOLD");
        propertyRepository.save(p13_saved);

        Deal d4 = new Deal();
        d4.setProperty(p20_saved);
        d4.setClient(c8);
        d4.setAgent(a6);
        d4.setFinalPrice(new BigDecimal("7200000"));
        d4.setDealType("SALE");
        d4.setDealDate(LocalDateTime.now().minusDays(1));
        d4.setStatus("COMPLETED");
        dealRepository.save(d4);
        p20_saved.setStatus("SOLD");
        propertyRepository.save(p20_saved);

        // Add Viewings
        viewingRepository.save(new Viewing(null, p2, c3, a2, LocalDateTime.now().plusDays(1), "Заинтересован в покупке", "SCHEDULED"));
        viewingRepository.save(new Viewing(null, p3, c1, a1, LocalDateTime.now().minusDays(2), "Слишком маленькая площадь", "COMPLETED"));
        viewingRepository.save(new Viewing(null, p5, c5, a3, LocalDateTime.now().plusDays(2), "Повторный осмотр", "SCHEDULED"));
        viewingRepository.save(new Viewing(null, p7, c6, a1, LocalDateTime.now().plusDays(3), "Просмотр офиса", "SCHEDULED"));
        viewingRepository.save(new Viewing(null, p16_saved, c7, a2, LocalDateTime.now().plusDays(4), "Элитный осмотр", "SCHEDULED"));
        viewingRepository.save(new Viewing(null, p17, c1, a3, LocalDateTime.now().plusDays(5), "Осмотр студии", "SCHEDULED"));

        // Add Reviews
        reviewRepository.save(new Review(null, p1, a1, c1, 5, "Отличная сделка!", LocalDateTime.now().minusDays(9)));
        reviewRepository.save(new Review(null, null, a2, c3, 4, "Хороший агент, но долго искали", LocalDateTime.now().minusDays(1)));
        reviewRepository.save(new Review(null, p13_saved, a5, c7, 5, "Прекрасный вид из окна, спасибо агенту", LocalDateTime.now().minusDays(1)));
        reviewRepository.save(new Review(null, p20_saved, a6, c8, 4, "Все прошло гладко", LocalDateTime.now().minusHours(5)));

        // Add Payments
        paymentRepository.save(new Payment(null, d1, new BigDecimal("24000000"), LocalDateTime.now().minusDays(10), "BANK_TRANSFER", "COMPLETED", "TXN-12345", LocalDateTime.now()));
        paymentRepository.save(new Payment(null, d2, new BigDecimal("41000000"), LocalDateTime.now().minusDays(5), "CASH", "COMPLETED", "TXN-67890", LocalDateTime.now()));
        paymentRepository.save(new Payment(null, d3, new BigDecimal("53000000"), LocalDateTime.now().minusDays(2), "BANK_TRANSFER", "COMPLETED", "TXN-11223", LocalDateTime.now()));
        paymentRepository.save(new Payment(null, d4, new BigDecimal("7200000"), LocalDateTime.now().minusDays(1), "MORTGAGE", "COMPLETED", "TXN-44556", LocalDateTime.now()));

        // Add Documents
        documentRepository.save(new Document(null, "Договор купли-продажи", "contract_p1.pdf", "/uploads/docs/contract_p1.pdf", "PDF", p1, d1, LocalDateTime.now().minusDays(10)));
        documentRepository.save(new Document(null, "Технический паспорт", "tech_pass_p2.pdf", "/uploads/docs/tech_pass_p2.pdf", "PDF", p2, null, LocalDateTime.now().minusDays(20)));
        documentRepository.save(new Document(null, "Выписка из ЕГРН", "egrn_p13.pdf", "/uploads/docs/egrn_p13.pdf", "PDF", p13_saved, d3, LocalDateTime.now().minusDays(2)));
        documentRepository.save(new Document(null, "Договор ипотеки", "mortgage_p20.pdf", "/uploads/docs/mortgage_p20.pdf", "PDF", p20_saved, d4, LocalDateTime.now().minusDays(1)));

        // Add Audit Logs
        auditLogRepository.save(new AuditLog(null, "Property", p1.getId(), "CREATE", "admin", "Initial creation", LocalDateTime.now().minusDays(30)));
        auditLogRepository.save(new AuditLog(null, "Deal", d1.getId(), "COMPLETED", "admin", "Deal finalized", LocalDateTime.now().minusDays(10)));
        auditLogRepository.save(new AuditLog(null, "User", 1L, "LOGIN", "admin", "Admin logged in", LocalDateTime.now().minusMinutes(10)));
        auditLogRepository.save(new AuditLog(null, "Property", p13_saved.getId(), "UPDATE", "agent", "Price updated", LocalDateTime.now().minusDays(5)));
        auditLogRepository.save(new AuditLog(null, "Deal", d4.getId(), "CREATE", "agent", "Mortgage deal initiated", LocalDateTime.now().minusDays(2)));
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

    private Property createProperty(String title, String address, String city, District district, PropertyType type,
                                    BigDecimal price, Double area, Integer bedrooms, Integer bathrooms,
                                    String description, Agent agent) {
        if (district == null || type == null) {
            throw new RuntimeException("ОШИБКА: Район или Тип недвижимости не найдены для: " + title);
        }
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
