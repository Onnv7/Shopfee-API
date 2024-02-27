package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.entity.database.*;
import com.hcmute.shopfee.entity.database.order.ItemDetailEntity;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.database.order.OrderItemEntity;
import com.hcmute.shopfee.entity.database.product.ProductEntity;
import com.hcmute.shopfee.entity.database.product.SizeEntity;
import com.hcmute.shopfee.entity.database.product.ToppingEntity;
import com.hcmute.shopfee.entity.database.review.ProductReviewEntity;
import com.hcmute.shopfee.entity.elasticsearch.ProductIndex;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.repository.database.AddressRepository;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.CategoryRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.repository.elasticsearch.OrderSearchRepository;
import com.hcmute.shopfee.repository.elasticsearch.ProductSearchRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.elasticsearch.OrderSearchService;
import com.hcmute.shopfee.service.elasticsearch.ProductSearchService;
import com.hcmute.shopfee.service.core.impl.OrderService;
import com.hcmute.shopfee.service.redis.EmployeeTokenRedisService;
import com.hcmute.shopfee.utils.HandleFileUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.TransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @DeleteMapping(value = "/deleteOrderElastisearch")
    public ResponseEntity<String> deleteOrderElastisearch() {
//        orderService.checkOrderCoupon(code);
        orderSearchRepository.deleteAll();
        return ResponseEntity.status(200).body("Ok fine");
    }
    @DeleteMapping(value = "/deleteProductElastisearch")
    public ResponseEntity<String> deleteProductElastisearch( ) {
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
                .phoneNumber(PHONE_NUMBER_EX)
                .build();
        branchRepository.save(branchEntity);

        CategoryEntity category = CategoryEntity.builder()
                .imageId("123")
                .imageUrl("url")
                .isDeleted(false)
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
                .imageId("imageId")
                .imageUrl("imageUrl")
                .status(ProductStatus.AVAILABLE)
                .isDeleted(false)
                .name("Product")
                .thumbnailUrl("thumbnailUrl")
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

        UserEntity userEntity = UserEntity.builder()
                .email("nva611@gmail.com")
                .password(passwordEncoder.encode("112233"))
                .firstName("an")
                .lastName("nguyen")
                .birthDate(java.sql.Date.valueOf("2002-06-11"))
                .enabled(true)
                .build();
        userRepository.save(userEntity);
        UserEntity userEntity2 = UserEntity.builder()
                .email("nva@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .firstName("an")
                .lastName("nguyen")
                .birthDate(java.sql.Date.valueOf("2002-06-12"))
                .enabled(true)
                .build();
        userRepository.save(userEntity2);
        AddressEntity addressEntity = AddressEntity.builder()
                .detail("detail address")
                .phoneNumber("0123456789")
                .note("nothing")
                .isDefault(true)
                .latitude(Double.valueOf(LATITUDE_EX))
                .longitude(Double.valueOf(LONGITUDE_EX))
                .recipientName("NVA")
                .user(userEntity)
                .build();

        addressRepository.save(addressEntity);

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
                .paymentType(PaymentType.BANKING_VNPAY)
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
    public String addElasticSearch() {
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
            orderBillRepository.save(item);
        }
        return "okokok";
    }

    @GetMapping("/product")
    public List<ProductIndex> searchProduct(
            @Parameter(name = "key", description = "Key is order's id, customer name or phone number", required = false, example = "65439a55e9818f43f8b8e02c")
            @RequestParam("key") String key) {
        Pageable page = PageRequest.of(0, 3);
//        List<ProductIndex> lst = productSearchRepository.searchVisibleProduct(key, page).getContent();
//        return lst;
        return null;
    }
}
