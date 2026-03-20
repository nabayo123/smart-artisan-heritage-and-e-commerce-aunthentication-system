package com.korarwandasystem.korarwanda.config;

import com.korarwandasystem.korarwanda.model.*;
import com.korarwandasystem.korarwanda.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            CooperativeRepository cooperativeRepository,
            ArtisanRepository artisanRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            CertificateRepository certificateRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            System.out.println("=== Initializing Sample Data ===");
            
            // Create Users
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@korarwanda.rw");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setUserType(UserType.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            
            User artisanUser = new User();
            artisanUser.setFirstName("Jean");
            artisanUser.setLastName("Mukiza");
            artisanUser.setEmail("jean.mukiza@artisan.rw");
            artisanUser.setPassword(passwordEncoder.encode("artisan123"));
            artisanUser.setUserType(UserType.ARTISAN);
            artisanUser.setEnabled(true);
            artisanUser.setBusinessName("Mukiza Crafts");
            artisanUser.setDescription("Traditional Rwandan crafts maker");
            userRepository.save(artisanUser);
            
            // Create Cooperatives
            Cooperative cooperative1 = new Cooperative();
            cooperative1.setName("Kigali Artisans Cooperative");
            cooperative1.setProvince("Kigali");
            cooperative1.setDistrict("Nyarugenge");
            cooperative1.setContactPhone("+250788111111");
            cooperative1.setCreatedAt(LocalDateTime.now());
            cooperativeRepository.save(cooperative1);
            
            // Create Artisans
            Artisan artisan1 = new Artisan();
            artisan1.setFullName("Jean Mukiza");
            artisan1.setEmail("jean.mukiza@artisan.rw");
            artisan1.setSpecialization("Traditional Baskets");
            artisan1.setProvince("Kigali");
            artisan1.setDistrict("Nyarugenge");
            artisan1.setVerificationStatus(VerificationStatus.APPROVED);
            artisan1.setCooperative(cooperative1);
            artisanRepository.save(artisan1);
            
            Artisan artisan2 = new Artisan();
            artisan2.setFullName("Grace Uwimana");
            artisan2.setEmail("grace.uwimana@artisan.rw");
            artisan2.setSpecialization("Pottery");
            artisan2.setProvince("Northern");
            artisan2.setDistrict("Musanze");
            artisan2.setVerificationStatus(VerificationStatus.APPROVED);
            artisan2.setCooperative(cooperative1);
            artisanRepository.save(artisan2);
            
            // Create Customers
            Customer customer1 = new Customer();
            customer1.setFirstName("John");
            customer1.setLastName("Doe");
            customer1.setEmail("john.doe@email.com");
            customer1.setPhone("+250788222222");
            customer1.setAddress("KG 123 St");
            customer1.setCity("Kigali");
            customer1.setProvince("Kigali");
            customer1.setDistrict("Nyarugenge");
            customerRepository.save(customer1);
            
            Customer customer2 = new Customer();
            customer2.setFirstName("Sarah");
            customer2.setLastName("Smith");
            customer2.setEmail("sarah.smith@email.com");
            customer2.setPhone("+250788333333");
            customer2.setAddress("KN 456 Ave");
            customer2.setCity("Kigali");
            customer2.setProvince("Kigali");
            customer2.setDistrict("Kicukiro");
            customerRepository.save(customer2);
            
            // Create Products
            Product product1 = new Product();
            product1.setName("Traditional Agaseke Basket");
            product1.setDescription("Handwoven traditional Rwandan basket with intricate patterns");
            product1.setPrice(new BigDecimal("15000.00"));
            product1.setStockQuantity(10);
            product1.setCategory("Baskets");
            product1.setMaterials("Sisal fibers, natural dyes");
            product1.setDimensions("30cm x 20cm");
            product1.setWeight(0.5);
            product1.setImageUrl("/images/basket1.jpg");
            product1.setStatus(ProductStatus.AVAILABLE);
            product1.setArtisan(artisan1);
            productRepository.save(product1);
            
            Product product2 = new Product();
            product2.setName("Ceramic Vase");
            product2.setDescription("Traditional Rwandan ceramic vase with geometric patterns");
            product2.setPrice(new BigDecimal("25000.00"));
            product2.setStockQuantity(5);
            product2.setCategory("Pottery");
            product2.setMaterials("Clay, natural pigments");
            product2.setDimensions("25cm x 15cm");
            product2.setWeight(1.2);
            product2.setImageUrl("/images/vase1.jpg");
            product2.setStatus(ProductStatus.AVAILABLE);
            product2.setArtisan(artisan2);
            productRepository.save(product2);
            
            Product product3 = new Product();
            product3.setName("Imigongo Art Piece");
            product3.setDescription("Traditional Rwandan imigongo art on wooden board");
            product3.setPrice(new BigDecimal("35000.00"));
            product3.setStockQuantity(3);
            product3.setCategory("Wall Art");
            product3.setMaterials("Wood, natural cow dung, natural dyes");
            product3.setDimensions("40cm x 30cm");
            product3.setWeight(0.8);
            product3.setImageUrl("/images/imigongo1.jpg");
            product3.setStatus(ProductStatus.AVAILABLE);
            product3.setArtisan(artisan1);
            productRepository.save(product3);
            
            // Create Orders
            Order order1 = new Order();
            order1.setCustomer(customer1);
            order1.setOrderDate(LocalDate.now());
            order1.setTotalPrice(40000.00);
            order1.setOrderStatus(OrderStatus.PENDING);
            orderRepository.save(order1);
            
            Order order2 = new Order();
            order2.setCustomer(customer2);
            order2.setOrderDate(LocalDate.now().minusDays(1));
            order2.setTotalPrice(25000.00);
            order2.setOrderStatus(OrderStatus.PAID);
            orderRepository.save(order2);
            
            // Create Order Items
            OrderItem item1 = new OrderItem();
            item1.setOrder(order1);
            item1.setProduct(product1);
            item1.setQuantity(1);
            item1.setUnitPrice(product1.getPrice());
            item1.setHeritageHash("HASH001");
            orderItemRepository.save(item1);
            
            OrderItem item2 = new OrderItem();
            item2.setOrder(order1);
            item2.setProduct(product3);
            item2.setQuantity(1);
            item2.setUnitPrice(product3.getPrice());
            item2.setHeritageHash("HASH002");
            orderItemRepository.save(item2);
            
            OrderItem item3 = new OrderItem();
            item3.setOrder(order2);
            item3.setProduct(product2);
            item3.setQuantity(1);
            item3.setUnitPrice(product2.getPrice());
            item3.setHeritageHash("HASH003");
            orderItemRepository.save(item3);
            
            // Create Payments
            Payment payment1 = new Payment();
            payment1.setOrder(order2);
            payment1.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
            payment1.setTransactionRef("TXN123456789");
            payment1.setPaymentStatus(PaymentStatus.SUCCESS);
            payment1.setPaymentDate(LocalDateTime.now().minusDays(1));
            payment1.setAmount(new BigDecimal("25000.00"));
            paymentRepository.save(payment1);
            
            // Create Certificates
            Certificate cert1 = new Certificate();
            cert1.setCertificateNumber("CERT2024001");
            cert1.setIssuingAuthority("Rwanda Development Board");
            cert1.setIssueDate(LocalDate.now().minusMonths(6));
            cert1.setExpiryDate(LocalDate.now().plusYears(2));
            cert1.setHeritageDescription("Master craftsman in traditional basket weaving techniques");
            cert1.setStatus(CertificateStatus.ACTIVE);
            cert1.setArtisan(artisan1);
            certificateRepository.save(cert1);
            
            Certificate cert2 = new Certificate();
            cert2.setCertificateNumber("CERT2024002");
            cert2.setIssuingAuthority("Rwanda Artisans Association");
            cert2.setIssueDate(LocalDate.now().minusMonths(3));
            cert2.setExpiryDate(LocalDate.now().plusYears(2));
            cert2.setHeritageDescription("Expert in traditional Rwandan pottery and ceramic arts");
            cert2.setStatus(CertificateStatus.ACTIVE);
            cert2.setArtisan(artisan2);
            certificateRepository.save(cert2);
            
            System.out.println("=== Sample Data Initialized Successfully ===");
            System.out.println("Users: " + userRepository.count());
            System.out.println("Cooperatives: " + cooperativeRepository.count());
            System.out.println("Artisans: " + artisanRepository.count());
            System.out.println("Customers: " + customerRepository.count());
            System.out.println("Products: " + productRepository.count());
            System.out.println("Orders: " + orderRepository.count());
            System.out.println("Order Items: " + orderItemRepository.count());
            System.out.println("Payments: " + paymentRepository.count());
            System.out.println("Certificates: " + certificateRepository.count());
        };
    }
}
