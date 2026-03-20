package com.korarwandasystem.korarwanda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korarwandasystem.korarwanda.model.*;
import com.korarwandasystem.korarwanda.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class ApiControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ArtisanRepository artisanRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CooperativeRepository cooperativeRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    // Test Customer Controller
    @Test
    void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testCreateCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setEmail("test.customer@test.com");
        customer.setPhone("+250788999999");
        customer.setCity("Kigali");
        customer.setProvince("Kigali");
        customer.setDistrict("Nyarugenge");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.email", is("test.customer@test.com")));
    }

    @Test
    void testGetCustomerById() throws Exception {
        // First create a customer
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@test.com");
        customer.setPhone("+250788888888");
        customer.setCity("Kigali");
        customer.setProvince("Kigali");
        customer.setDistrict("Nyarugenge");
        
        Customer savedCustomer = customerRepository.save(customer);

        mockMvc.perform(get("/api/customers/{id}", savedCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")));
    }

    // Test Artisan Controller
    @Test
    void testGetAllArtisans() throws Exception {
        mockMvc.perform(get("/api/artisans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testCreateArtisan() throws Exception {
        // First create a cooperative
        Cooperative cooperative = new Cooperative();
        cooperative.setName("Test Cooperative");
        cooperative.setProvince("Kigali");
        cooperative.setDistrict("Nyarugenge");
        cooperative.setContactPhone("+250788777777");
        cooperative = cooperativeRepository.save(cooperative);

        Artisan artisan = new Artisan();
        artisan.setFullName("Test Artisan");
        artisan.setEmail("test.artisan@test.com");
        artisan.setSpecialization("Basket Weaving");
        artisan.setProvince("Kigali");
        artisan.setDistrict("Nyarugenge");
        artisan.setVerificationStatus(VerificationStatus.PENDING);
        artisan.setCooperative(cooperative);

        mockMvc.perform(post("/api/artisans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artisan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Test Artisan")))
                .andExpect(jsonPath("$.specialization", is("Basket Weaving")));
    }

    // Test Product Controller
    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testCreateProduct() throws Exception {
        // First create an artisan
        Artisan artisan = new Artisan();
        artisan.setFullName("Product Artisan");
        artisan.setEmail("product.artisan@test.com");
        artisan.setSpecialization("Pottery");
        artisan.setProvince("Kigali");
        artisan.setDistrict("Nyarugenge");
        artisan.setVerificationStatus(VerificationStatus.APPROVED);
        artisan = artisanRepository.save(artisan);

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("A test product description");
        product.setPrice(new BigDecimal("10000.00"));
        product.setStockQuantity(5);
        product.setCategory("Test Category");
        product.setMaterials("Test Materials");
        product.setDimensions("10cm x 10cm");
        product.setWeight(0.5);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setArtisan(artisan);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(10000.00)));
    }

    // Test Order Controller
    @Test
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testCreateOrder() throws Exception {
        // First create a customer
        Customer customer = new Customer();
        customer.setFirstName("Order");
        customer.setLastName("Customer");
        customer.setEmail("order.customer@test.com");
        customer.setPhone("+250788666666");
        customer.setCity("Kigali");
        customer.setProvince("Kigali");
        customer.setDistrict("Nyarugenge");
        customer = customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(new BigDecimal("25000.00"));
        order.setOrderDate(java.time.LocalDate.now());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(25000.00)))
                .andExpect(jsonPath("$.orderStatus", is("PENDING")));
    }

    // Test Error Handling
    @Test
    void testGetNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCustomerWithInvalidEmail() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("Invalid");
        customer.setLastName("Email");
        customer.setEmail("invalid-email");
        customer.setPhone("+250788555555");
        customer.setCity("Kigali");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    // Test Repository Methods
    @Test
    void testCustomerRepositoryMethods() throws Exception {
        // Test findByEmail
        Customer customer = new Customer();
        customer.setFirstName("Email");
        customer.setLastName("Test");
        customer.setEmail("email.test@test.com");
        customer.setPhone("+250788444444");
        customer.setCity("Kigali");
        customer.setProvince("Kigali");
        customer.setDistrict("Nyarugenge");
        customerRepository.save(customer);

        mockMvc.perform(get("/api/customers/email/email.test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email.test@test.com")));
    }

    @Test
    void testArtisanRepositoryMethods() throws Exception {
        // Create cooperative first
        Cooperative cooperative = new Cooperative();
        cooperative.setName("Repo Test Cooperative");
        cooperative.setProvince("Kigali");
        cooperative.setDistrict("Nyarugenge");
        cooperative.setContactPhone("+250788333333");
        cooperative = cooperativeRepository.save(cooperative);

        // Test findByCooperativeCooperativeId
        Artisan artisan = new Artisan();
        artisan.setFullName("Repo Artisan");
        artisan.setEmail("repo.artisan@test.com");
        artisan.setSpecialization("Test Craft");
        artisan.setProvince("Kigali");
        artisan.setDistrict("Nyarugenge");
        artisan.setVerificationStatus(VerificationStatus.APPROVED);
        artisan.setCooperative(cooperative);
        artisanRepository.save(artisan);

        mockMvc.perform(get("/api/artisans/cooperative/{id}", cooperative.getCooperativeId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}
