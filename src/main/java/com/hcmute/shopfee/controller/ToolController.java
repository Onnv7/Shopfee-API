package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.entity.sql.database.*;
import com.hcmute.shopfee.entity.sql.database.order.*;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.SizeEntity;
import com.hcmute.shopfee.entity.sql.database.product.ToppingEntity;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.vnpay.transaction.dto.PreTransactionInfo;
import com.hcmute.shopfee.module.vnpay.querydr.response.TransactionInfoQuery;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CreateOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.request.GetOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.response.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.module.zalopay.order.dto.response.GetOrderZaloPayResponse;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.repository.elasticsearch.OrderSearchRepository;
import com.hcmute.shopfee.repository.elasticsearch.ProductSearchRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import com.hcmute.shopfee.service.elasticsearch.OrderSearchService;
import com.hcmute.shopfee.service.elasticsearch.ProductSearchService;
import com.hcmute.shopfee.service.core.impl.OrderService;
import com.hcmute.shopfee.service.redis.EmployeeTokenRedisService;
import com.hcmute.shopfee.utils.HandleFileUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static com.hcmute.shopfee.constant.ErrorConstant.NOT_FOUND;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@RestController
@RequestMapping("tool")
@Tag(name = "tool")
@RequiredArgsConstructor
public class ToolController {
    private final ProductSearchRepository productSearchRepository;
    private final OrderSearchService orderSearchService;
    private final ProductRepository productRepository;
    private final ProductSearchService productSearchService;
    private final OrderSearchRepository orderSearchRepository;
    private final OrderBillRepository orderBillRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final OrderService orderService;
    private final EmployeeTokenRedisService employeeTokenRedisService;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductReviewRepository productReviewRepository;
    private final RoleRepository roleRepository;
    private final VNPayService vnPayService;
    private final ZaloPayService zaloPayService;

    @Autowired
    private Environment environment;
    private final EmployeeRepository employeeRepository;

    @DeleteMapping(value = "/deleteOrderElastisearch")
    public ResponseEntity<String> deleteOrderElastisearch() {
//        orderService.checkOrderCoupon(code);
        orderSearchRepository.deleteAll();
        return ResponseEntity.status(200).body("Ok fine");
    }

    @DeleteMapping(value = "/deleteProductElastisearch")
    public ResponseEntity<String> deleteProductElastisearch() {
//        orderService.checkOrderCoupon(code);
        productSearchRepository.deleteAll();
        return ResponseEntity.status(200).body("Ok fine");
    }

    @GetMapping(value = "/creatingToTest")
    @Transactional
    public ResponseEntity<String> createMore() {
        BranchEntity branchEntity = BranchEntity.builder()
                .detail(ADDRESS_DETAILS_EX)
                .province(PROVINCE_EX)
                .district(DISTRICT_EX)
                .ward(WARD_EX)
                .longitude(Double.parseDouble(LONGITUDE_EX))
                .latitude(Double.parseDouble(LATITUDE_EX))
                .createdAt(new Date())
                .openTime(Time.valueOf("07:00:00"))
                .closeTime(Time.valueOf("20:00:00"))
                .status(BranchStatus.ACTIVE)
                .imageId("imageId")
                .imageUrl("imageUrl")
                .name("HCM Vo Van Ngan")
                .phoneNumber(PHONE_NUMBER_EX)
                .build();
        branchRepository.save(branchEntity);
        AlbumEntity image = AlbumEntity.builder()
                .type(AlbumType.CATEGORY)
                .cloudinaryImageId("fileUploaded.get(CloudinaryConstant.PUBLIC_ID)")
                .imageUrl("fileUploaded.get(CloudinaryConstant.URL_PROPERTY)")
                .build();

        CategoryEntity category = CategoryEntity.builder()
                .image(image)
                .status(CategoryStatus.VISIBLE)
                .name("category1")
                .build();
        categoryRepository.save(category);
        List<SizeEntity> sizeEntityList = new ArrayList<>();
        ProductEntity product = ProductEntity.builder()
                .category(category)
                .createdAt(new Date())
                .description("description")
                .price(50000L)
                .image(AlbumEntity.builder()
                        .type(AlbumType.PRODUCT)
                        .cloudinaryImageId("cloudinaryImageId")
                        .imageUrl("imageUrl")
                        .thumbnailUrl("thumbnailUrl")
                        .build())
                .status(ProductStatus.AVAILABLE)
                .name("Product")
                .build();
        sizeEntityList.add(SizeEntity.builder()
                .size(ProductSize.SMALL)
                .price(50000L)
                .product(product)
                .build());

        sizeEntityList.add(SizeEntity.builder()
                .size(ProductSize.MEDIUM)
                .price(75000L)
                .product(product)
                .build());

        sizeEntityList.add(SizeEntity.builder()
                .size(ProductSize.LARGE)
                .price(100000L)
                .product(product)
                .build());
        List<ToppingEntity> toppingEntityList = new ArrayList<ToppingEntity>();
        toppingEntityList.add(ToppingEntity.builder()
                .product(product)
                .price(15000L)
                .name("Pudding")
                .build());
        toppingEntityList.add(ToppingEntity.builder()
                .product(product)
                .price(20000L)
                .name("Pudding 2")
                .build());
        product.setType(ProductType.BEVERAGE);
        product.setToppingList(toppingEntityList);
        product.setSizeList(sizeEntityList);

        productRepository.save(product);
        Set<RoleEntity> userRole = new HashSet<>();
        RoleEntity role = roleRepository.findByRoleName(Role.ROLE_USER)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "Role with name"));
        userRole.add(role);


        UserEntity userEntity = UserEntity.builder()
                .email("nva611@gmail.com")
                .password(passwordEncoder.encode("112233"))
                .firstName("an")
                .lastName("nguyen")
                .coin(0L)
                .birthDate(java.sql.Date.valueOf("2002-06-11"))
                .status(UserStatus.ACTIVE)
                .roleList(userRole)
                .build();
        userRepository.save(userEntity);
        UserEntity userEntity2 = UserEntity.builder()
                .email("nva@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .firstName("an")
                .lastName("nguyen")
                .coin(0L)
                .birthDate(java.sql.Date.valueOf("2002-06-12"))
                .status(UserStatus.ACTIVE)
                .roleList(userRole)
                .build();
        userRepository.save(userEntity2);
        AddressEntity addressEntity = AddressEntity.builder()
                .detail("detail address")
                .phoneNumber("0123456789") //20.981971,105.864323
                .note("nothing")
                .isDefault(true)
                .latitude(Double.valueOf(LATITUDE_EX))
                .longitude(Double.valueOf(LONGITUDE_EX))
                .recipientName("NVA")
                .user(userEntity)
                .build();
        addressRepository.save(addressEntity);

        Set<RoleEntity> employeeRoleList = new HashSet<>();
        RoleEntity employeeRole = roleRepository.findByRoleName(Role.ROLE_EMPLOYEE)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "Role with name"));
        employeeRoleList.add(employeeRole);
        EmployeeEntity employee = EmployeeEntity.builder()
                .username("employee")
                .password(passwordEncoder.encode("123456"))
                .firstName("an")
                .lastName("nguyen")
                .roleList(employeeRoleList)
                .status(EmployeeStatus.ACTIVE)
                .branch(branchEntity)
                .isDeleted(false)
                .build();
        employeeRepository.save(employee);

        OrderBillEntity orderBill = OrderBillEntity.builder()
                .createdAt(new Date())
                .branch(branchEntity)
                .note("order bill note")
                .orderType(OrderType.SHIPPING)
                .totalItemPrice(100000L)
                .shippingFee(15000L)
                .totalPayment(115000L)
                .updatedAt(new Date())
                .user(userEntity)
                .build();

        ReceiverInformationEntity receiverInformation = ReceiverInformationEntity.builder()
                .address("sadasdasd")
                .latitude(2312423.4)
                .longitude(2312423.3)
                .phoneNumber("0432342343")
                .recipientName("An nguyen")
                .orderBill(orderBill)
                .build();
        orderBill.setReceiverInformation(receiverInformation);

        List<OrderEventEntity> orderEventEntityList = new ArrayList<OrderEventEntity>();
        orderEventEntityList.add(OrderEventEntity.builder()
                .createdAt(new Date())
                .isEmployee(false)
                .description("Create order successfully")
                .orderBill(orderBill)
                .orderStatus(OrderStatus.CREATED)
                .createdBy("U00000001")
                .build());
        orderBill.setOrderEventList(orderEventEntityList);

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .createdAt(new Date())
                .status(PaymentStatus.UNPAID)
                .totalPaid(0L)
                .paymentType(PaymentType.VNPAY)
                .orderBill(orderBill)
                .build();
        orderBill.setTransaction(transactionEntity);

        List<OrderItemEntity> orderItemEntityList = new ArrayList<OrderItemEntity>();
        OrderItemEntity orderItemEntity = OrderItemEntity.builder()
                .orderBill(orderBill)
                .name(product.getName())
                .product(product)

                .build();

        List<ItemDetailEntity> itemDetailEntityList = new ArrayList<ItemDetailEntity>();
        ItemDetailEntity itemDetailEntity = ItemDetailEntity.builder()
                .quantity(2)
                .size(ProductSize.SMALL)
                .price(50000L)
                .orderItem(orderItemEntity)
                .build();
        itemDetailEntityList.add(itemDetailEntity);


        orderItemEntity.setItemDetailList(itemDetailEntityList);

        orderItemEntityList.add(orderItemEntity);

        orderBill.setOrderItemList(orderItemEntityList);

        orderBillRepository.save(orderBill);


        ProductReviewEntity productReviewEntity = ProductReviewEntity.builder()
                .star(4)
                .content("Good product")
                .orderItem(orderItemEntity)
                .createdAt(new Date())
                .build();
        orderItemEntity.setProductReview(productReviewEntity);
        productReviewRepository.save(productReviewEntity);
        return ResponseEntity.status(200).body("Ok fine");
    }

    @DeleteMapping(value = "/refresh/{employeeId}")
    public ResponseEntity<String> deleteRefreshToken(@PathVariable("employeeId") String employeeId) {
//        orderService.checkOrderCoupon(code);
        employeeTokenRedisService.deleteAllTokenByEmployeeId(employeeId);
        return ResponseEntity.status(200).body("Ok fine");
    }

    @GetMapping(value = "/{code}")
    public ResponseEntity<String> ceckCoupon(@PathVariable("code") String code) {
//        orderService.checkOrderCoupon(code);
        return ResponseEntity.status(200).body("Ok fine");
    }

    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try (InputStream inputStream = file.getInputStream()) {
            HandleFileUtils.readFileToCreateProduct(inputStream);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while processing the file");
        }
    }

    @PostMapping(value = "/uploadAndStream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> uploadAndStreamFiles(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        return Flux.create((FluxSink<String> sink) -> {
            try (InputStream inputStream = file.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sink.next(line); // Send each line to Flux
                }

                sink.complete(); // When reading is complete
            } catch (IOException e) {
                sink.error(e); // In case of an error
            }
        }).delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(value = "/stream-time", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamTime() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> LocalTime.now().toString());
    }


    @GetMapping("/sync-product-elasticsearch")
    public String syncProductElasticSearch() {
        productSearchRepository.deleteAll();
        List<ProductEntity> productList = productRepository.findAll();
        for (ProductEntity item : productList) {
            productSearchService.createProduct(item);
        }
        return "okokok";
    }

    @GetMapping("/sync-order-elasticsearch")
    public String syncOrder() {
        orderSearchRepository.deleteAll();
        List<OrderBillEntity> productList = orderBillRepository.findAll();
        for (OrderBillEntity item : productList) {
            orderSearchService.createOrder(item);
        }
        return "okokok";
    }

    @GetMapping("/test-create-url-vnpay")
    public PreTransactionInfo searchProduct(

            HttpServletRequest request) throws UnsupportedEncodingException {

        return vnPayService.createUrlPayment(request, 50000, "odkasok");
    }

    @PostMapping("/test-create-url-zalopay")
    public CreateOrderZaloPayResponse createZaloPay(@RequestBody CreateOrderZaloPayRequest request) throws IOException {

        return zaloPayService.createOrderTest(request);
    }

    @PostMapping("/test-get-order-zalopay")
    public GetOrderZaloPayResponse createZaloPay(@RequestBody GetOrderZaloPayRequest request) throws IOException, URISyntaxException {

        return zaloPayService.getOrderTest(request);
    }

    @GetMapping("/test-get-info-vnpay-ip-address")
    public TransactionInfoQuery searchProduct(

            HttpServletRequest request,
            @RequestParam("txnref") String txnref,
            @RequestParam("transId") String transId,
            @RequestParam("ip") String ip
    ) throws UnsupportedEncodingException {

        return vnPayService.getTransactionInfoTest(txnref, transId, ip);
    }

    @GetMapping("/test-cloudinary-thumbnail")
    public String cloudinary(@RequestParam("id") String id) throws UnsupportedEncodingException {

        System.out.println(new Date());
        String data = cloudinaryService.getThumbnailUrl(id);
        System.out.println(new Date());
        return data;
    }

    @GetMapping("/getIpAddressServer")
    public String getIpAddressServer(HttpServletRequest request) throws UnsupportedEncodingException {

        String serverIpAddress = environment.getProperty("local.server.ip");
        return "Server IP Address: " + serverIpAddress;
    }
}
