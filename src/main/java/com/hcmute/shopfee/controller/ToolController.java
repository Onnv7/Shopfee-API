package com.hcmute.shopfee.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.NotificationMessageDto;
import com.hcmute.shopfee.dto.common.OrderNotificationDto;
import com.hcmute.shopfee.dto.kafka.BranchNotificationDto;
import com.hcmute.shopfee.entity.sql.database.*;
import com.hcmute.shopfee.entity.sql.database.order.*;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.SizeEntity;
import com.hcmute.shopfee.entity.sql.database.product.ToppingEntity;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.kafka.publisher.EmployeeOrderNotificationKafkaPublisher;
import com.hcmute.shopfee.kafka.publisher.UserOrderNotificationKafkaPublisher;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.module.vnpay.VNPay;
import com.hcmute.shopfee.module.vnpay.VNPayUtils;
import com.hcmute.shopfee.dto.common.vnpay.VNPayPaymentUrl;
import com.hcmute.shopfee.dto.common.vnpay.TransactionInfoQuery;
import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.dto.common.zalopay.CallBackDto;
import com.hcmute.shopfee.dto.common.zalopay.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.dto.common.zalopay.GetOrderZaloPayResponse;
import com.hcmute.shopfee.dto.common.zalopay.ZaloCallbackResponse;
import com.hcmute.shopfee.dto.common.zalopay.RefundRequestDTO;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.product.ProductRepository;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.repository.elasticsearch.OrderSearchRepository;
import com.hcmute.shopfee.repository.elasticsearch.ProductSearchRepository;
import com.hcmute.shopfee.schedule.job.RefuseOrderJob;
import com.hcmute.shopfee.service.common.*;
import com.hcmute.shopfee.service.core.impl.CallbackService;
import com.hcmute.shopfee.service.elasticsearch.OrderSearchService;
import com.hcmute.shopfee.service.elasticsearch.ProductSearchService;
import com.hcmute.shopfee.service.core.impl.OrderService;
import com.hcmute.shopfee.service.redis.EmployeeTokenRedisService;
import com.hcmute.shopfee.service.redis.ProductRedisService;
import com.hcmute.shopfee.statemachine.OrderEvent;
import com.hcmute.shopfee.statemachine.OrderStateService;
import com.hcmute.shopfee.utils.DateUtils;
import com.hcmute.shopfee.utils.ExcelUtils;
import com.hcmute.shopfee.utils.HandleFileUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Calendar;

import static com.hcmute.shopfee.constant.ErrorConstant.NOT_FOUND;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;
import static com.hcmute.shopfee.module.vnpay.VNPayConstant.VNP_TRANSACTION_DATE_KEY;
import static com.hcmute.shopfee.module.vnpay.VNPayConstant.VNP_TXN_REF_KEY;

@RestController
@RequestMapping("tool")
@Tag(name = "tool")
@RequiredArgsConstructor
//@Slf4j
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
    private final ProductRedisService productRedisService;
    private final Scheduler scheduler;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductReviewRepository productReviewRepository;
    private final RoleRepository roleRepository;
    private final VNPayService vnPayService;
    private final CallbackService callbackService;
    private final ZaloPayService zaloPayService;
    private final ZaloPay zaloPay;
    private final FirebaseMessagingService firebaseMessagingService;
    private final UserOrderNotificationKafkaPublisher userOrderNotificationKafkaPublisher;
    private final EmployeeOrderNotificationKafkaPublisher employeeOrderNotificationKafkaPublisher;
    private final OrderStateService orderStateService;
    @Autowired
    private Environment environment;
    private final EmployeeRepository employeeRepository;


    private final VNPay vnPay;

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
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ROLE_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + "Role with name"));
        userRole.add(role);


        UserEntity userEntity = UserEntity.builder()
                .email("nva611@gmail.com")
                .password(passwordEncoder.encode("112233"))
                .firstName("an")
                .lastName("nguyen")
//                .coin(0L)
                .birthDate(java.sql.Date.valueOf("2002-06-11"))
                .status(UserStatus.ACTIVE)
                .roleList(userRole)
                .build();
        userRepository.save(userEntity);
        UserEntity userEntity2 = UserEntity.builder()
                .email("nva@gmail.com")
                .password(passwordEncoder.encode("112233"))
                .firstName("an")
                .lastName("nguyen")
//                .coin(0L)
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
        RoleEntity employeeRole = roleRepository.findByRoleName(Role.ROLE_WAITER)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ROLE_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + "Role with name"));
        employeeRoleList.add(employeeRole);
        EmployeeEntity employee = EmployeeEntity.builder()
                .username("nva6112002")
                .password(passwordEncoder.encode("112233"))
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
                .actor(ActorType.USER)
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
                .imageUrl(product.getImage().getImageUrl())
                .thumbnailUrl(product.getImage().getThumbnailUrl())
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

    @DeleteMapping(value = "/deleteRefreshToken/{employeeId}")
    public ResponseEntity<String> deleteRefreshToken(@PathVariable("employeeId") String employeeId) {
//        orderService.checkOrderCoupon(code);
        employeeTokenRedisService.deleteAllTokenOfEmployee(employeeId);
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

    @GetMapping("/VNPAY-test-create-url")
    public VNPayPaymentUrl searchProduct(
            HttpServletRequest request) throws UnsupportedEncodingException {
        return vnPayService.createUrlPayment(request, 50000, "odkasok");
    }

    @GetMapping("/VNPAY-test-refund")
    public Map<String, Object> refundZalo(HttpServletRequest req, HttpServletResponse resp,
                                          @RequestParam(VNP_TRANSACTION_DATE_KEY) String transId,
                                          @RequestParam("amount") String amount1,
                                          @RequestParam(VNP_TXN_REF_KEY) String txnref,
                                          @RequestParam("refund_type") String type)
            throws IOException {
        //Command: refund
        String vnp_RequestId = VNPayUtils.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = vnPay.getTmnCode();
        String vnp_TransactionType = type;
        String vnp_TxnRef = txnref;
        long amount = Integer.parseInt(amount1) * 100L;
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = ""; //Assuming value of the parameter "vnp_TransactionNo" does not exist on your system.
        String vnp_TransactionDate = transId;
        String vnp_CreateBy = "name";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = VNPayUtils.getIpAddress(req);

        JsonObject vnp_Params = new JsonObject();

        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

        if (vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty()) {
            vnp_Params.addProperty("vnp_TransactionNo", "{get value of vnp_TransactionNo}");
        }

        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = VNPayUtils.hmacSHA512(vnPay.getSecretKey(), hash_Data.toString());

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(vnPay.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());

        return null;
    }


    @GetMapping("/VNPAY-test-callback")
    public ResponseEntity<Map<String, Object>> doCallBack(@RequestParam Map<String, Object> callBackInfo, HttpServletRequest request) throws UnsupportedEncodingException, JsonProcessingException {

        callbackService.processCallback(request);
        System.out.println(callBackInfo);
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
    }

    @GetMapping("/VNPAY-test-get-order")
    public TransactionInfoQuery getTransactionInfo(HttpServletRequest request, @RequestParam("txnref") String txnref, @RequestParam("transId") String transId) throws UnsupportedEncodingException {
        return vnPayService.getTransactionInfo(txnref, transId, request);
    }

    @PostMapping("/ZALOPAY-test-create-url")
    public CreateOrderZaloPayResponse createZaloPay(@RequestParam("amount") long amount) throws IOException {

        return zaloPayService.createOrderTransaction(amount);
    }

    @PostMapping("/ZALOPAY-test-get-order")
    public GetOrderZaloPayResponse getOrderTransactionInformation(@RequestParam("appTransId") String appTransId) throws IOException, URISyntaxException {
        return zaloPayService.getOrderTransactionInformation(appTransId);
    }

    @PostMapping("/ZALOPAY-test-callback")
    public ZaloCallbackResponse createZaloPaycallback(@RequestBody CallBackDto body) throws IOException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        return callbackService.processCallback(body);
    }

    @PostMapping("/ZALOPAY-test-refund")
    public Map sendRefundZalo(@RequestBody RefundRequestDTO request) throws IOException, URISyntaxException {
        return zaloPayService.sendRefund(request);
    }

    @GetMapping("/ZALOPAY-test-get-refund")
    public Map sendRefundZalo(@RequestParam("refundId") String refundId) throws IOException, URISyntaxException {
        return zaloPayService.getStatusRefund(refundId);
    }


    @GetMapping("/test-cloudinary-thumbnail")
    public String cloudinary(@RequestParam("id") String id) throws UnsupportedEncodingException {

        System.out.println(new Date());
        String data = cloudinaryService.getThumbnailUrlOfImage(id);
        System.out.println(new Date());
        return data;
    }

    @GetMapping("/getIpAddressServer")
    public String getIpAddressServer(HttpServletRequest request) throws UnsupportedEncodingException {

        String serverIpAddress = environment.getProperty("local.server.ip");
        return "Server IP Address: " + serverIpAddress;
    }


    @PostMapping("/sendNotificationTopic")
    public String sendNotificationTopic(@RequestBody NotificationMessageDto body) {
        firebaseMessagingService.sendOrderNotificationToBranch(body.getRecipientToken(), body.getTitle(), body.getBody());
        return "okoko";
    }

    @PostMapping("/kafka-kafkaSendToBranch")
    public String kafkaSendToBranch(@RequestBody BranchNotificationDto body) {
        userOrderNotificationKafkaPublisher.sendNotificationToBranch(body);
        return "okoko";
    }

    @PostMapping("/kafka-kafkaSendToClient")
    public String kafkaSendToClient(@RequestBody OrderNotificationDto body) {
        employeeOrderNotificationKafkaPublisher.sendNotificationToUserId(body);
        return "okoko";
    }

    @GetMapping("/tst-state-machine")
    public String machine(@RequestParam("orderId") String orderId, @RequestParam("orderEvent") OrderEvent orderEvent) {

        Mono<OrderStatus> rs = orderStateService.sendEventMono(orderId, "Test", orderEvent);

//                .subscribe(rs1 -> {
//                    System.out.println(rs1);
//                }, err -> {
//
//                    System.out.println(err);
//                }, () -> {
//
//                    System.out.println("err");
//                });
        return "okoko";
    }


    @GetMapping("/create-excel")
    public String createExcelt() throws IOException {

        Workbook workbook = new XSSFWorkbook();

        // Create Categories sheet
        Sheet categoriesSheet = workbook.createSheet("Categories");

        // Create some data for demonstration
        String[] categoryIds = {"101", "102", "103"};
        String[] categoryNames = {"Category 1", "Category 2", "Category 3"};

        // Write data to Categories sheet
        for (int i = 0; i < categoryIds.length; i++) {
            Row row = categoriesSheet.createRow(i);
            row.createCell(0).setCellValue(categoryIds[i]);
            row.createCell(1).setCellValue(categoryNames[i]);
        }

        // Create Product sheet
        Sheet productSheet = workbook.createSheet("Products");

        // Create a named range for the first column (Category ID)
        String rangeName = "CategoryID";
        String reference = "Categories!$A$1:$A$" + (categoryIds.length); // Assuming data starts from row 2
        Name namedRange = workbook.createName();
        namedRange.setNameName(rangeName);
        namedRange.setRefersToFormula(reference);

        // Set data validation with drop-down list in Product sheet
        DataValidationHelper dvHelper = productSheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(rangeName);
        CellRangeAddressList addressList = new CellRangeAddressList(0, 0, 0, 0); // Assuming the drop-down list is in the first column (A) of the first row
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);

        // Set error message for invalid data
        validation.createErrorBox("Invalid Data", "Please select a value from the drop-down list.");
        // Set error style
        validation.setShowErrorBox(true);
        validation.setShowPromptBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setSuppressDropDownArrow(true);

        productSheet.addValidationData(validation);


        Cell cell = productSheet.createRow(2).createCell(3); // Hàng 3, Cột D (index 3 là cột D)
        cell.setCellValue("1234"); // Gán giá trị cho ô 3D
        DataValidationHelper dvHelper2 = productSheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint2 = dvHelper2.createNumericConstraint(
                DataValidationConstraint.ValidationType.INTEGER,
                DataValidationConstraint.OperatorType.GREATER_THAN,
                "1000", "999999"); // Minimum value

        CellRangeAddressList addressList2 = new CellRangeAddressList(2, 2, 3, 3); // Hàng 3, Cột D
        DataValidation validation2 = dvHelper2.createValidation(dvConstraint2, addressList2);

        // Set error message for invalid data
        validation2.createErrorBox("Invalid Data", "Price must be greater than 1000.");

        // Set error style
        validation2.setShowErrorBox(true);
        validation2.setShowPromptBox(true);
        validation2.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation2.setSuppressDropDownArrow(true);

        productSheet.addValidationData(validation2);

        // Close the workbook to release resources

        try (FileOutputStream fileOut = new FileOutputStream("categories.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();


        return "ok";
    }

    @GetMapping("/create-beverage-excel")
    public String createBeverageExcel() throws IOException {
        int rowEffected = 100;
        Workbook workbook = new XSSFWorkbook();
        Sheet dataSheet = workbook.createSheet("data");
        Sheet productSheet = workbook.createSheet("product");

        Row headerRow = dataSheet.createRow(0);
        String[] firstRow = {"Product name", "Category", "Status", "Description", "Image", "Size name", "Size price", "Topping name", "Topping price"};
        String[] firstRowData1 = {"Milk", "Milk tea", "AVAILABLE", "Delicious milk tea", "https://www.facebook.com/", "SMALL", "15000", "Flan", "2000"};
        String[] firstRowData2 = {null, null, null, null, "https://www.facebook.com/", "MEDIUM", "20000", null, null};
        String[] sizeNameArray = {ProductSize.SMALL.name(), ProductSize.MEDIUM.name(), ProductSize.LARGE.name()};
        String[] statusArray = {ProductStatus.AVAILABLE.name(), ProductStatus.HIDDEN.name(), ProductStatus.OUT_OF_STOCK.name()};
        for (int i = 0; i < firstRow.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(firstRow[i]);
        }

        // category value drop list
        List<String> categoryNameList = categoryRepository.getCategoryNameList();
        ExcelUtils.setDropList(categoryNameList.toArray(new String[0]), dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 1, 1);


        // size value drop list
        ExcelUtils.setDropList(sizeNameArray, dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 5, 5);

        // size status drop list
        ExcelUtils.setDropList(statusArray, dataSheet, "Invalid Data", "Please select a value from the drop-down list.", 1, rowEffected, 2, 2);


        // validate price > 1000
        ExcelUtils.setIntegerConstraint(dataSheet, 1000, 9999999, "Invalid Data", "Price must be greater than 1000.", 1, rowEffected, 6, 6);
        ExcelUtils.setIntegerConstraint(dataSheet, 1000, 9999999, "Invalid Data", "Price must be greater than 1000.", 1, rowEffected, 8, 8);


        // validate product name
        List<String> productNameList = productRepository.getProductNameList();
        for (int i = 0; i < productNameList.size(); i++) {
            Row row = productSheet.createRow(i);
            row.createCell(0).setCellValue(productNameList.get(i));
        }

        String rangeName = "productName";
        String reference = "product!$A$1:$A$" + (productNameList.size());
        ExcelUtils.setFormulas(workbook, rangeName, reference);
        ExcelUtils.setCustomConstraint(dataSheet, "COUNTIF(productName, A2)=0", "Invalid Data", "The product name is already in the database", 1, rowEffected, 0, 0);


        // Apply the validation to the sheet
        Row r1 = dataSheet.createRow(1);
        Row r2 = dataSheet.createRow(2);
        for (int i = 0; i < firstRowData1.length; i++) {
            Cell cell1 = r1.createCell(i);
            Cell cell2 = r2.createCell(i);
            cell1.setCellValue(firstRowData1[i]);
            cell2.setCellValue(firstRowData2[i]);
        }
        for (int i = 0; i <= 4; i++) {
            dataSheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
        }
        for (Row row : dataSheet) {
            row.setHeight((short) -1);
            for (Cell cell : row) {
                dataSheet.autoSizeColumn(cell.getColumnIndex());
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream("beverage.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
        return "nice";
    }

    @PostMapping(value = "/test-upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFileToFolder(@ModelAttribute(name = "file") MultipartFile file) throws IOException {
        cloudinaryService.uploadFileToFolder(CloudinaryConstant.ORDER_RETURN_PATH, "test-video", file.getBytes());
        return "uploaded";
    }

    @DeleteMapping(value = "/test-delete-redis-key")
    public String deleteKeysWithPattern(@RequestParam("key") String key) throws IOException {
        productRedisService.deleteKeysWithPattern(key);
        return "deleted";
    }

    @GetMapping(value = "/getProductById")
    public List<ProductEntity> getProductById(@RequestParam("productId") String productId) throws IOException {
        return productRepository.getProductById(productId);
    }

    @GetMapping(value = "/getEmployeeTokenValue")
    public String getProductById(@RequestParam("emplId") String emplId, @RequestParam("token") String token) throws IOException {
        Boolean rs = employeeTokenRedisService.getEmployeeTokenValue(emplId, token);
        if (rs == null)
            return "null";
        return String.valueOf(rs.booleanValue());
    }

    Logger log = LoggerFactory.getLogger(ToolController.class);

    @GetMapping(value = "/testlog")
    public String testlog(@RequestParam("emplId") String emplId) throws IOException {

        log.error(emplId);
        log.warn(emplId);
        log.info(emplId);
        log.debug(emplId);
        log.trace(emplId);
        int b = 14 / 0;
        log.debug("debug");
        System.out.println(14 / 0);
//        try {
//            int a = 4 / 0;
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
        return "ok";
    }

    @GetMapping(value = "/testSchedule")
    public String testSchedule(@RequestParam("timeSecond") int timeSecond) throws IOException, SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(HelloWorldJob.class)
                .withIdentity(UUID.randomUUID().toString(), "group-job-test")
                .withDescription("Job details description test")
//                .usingJobData(jobDataMap)
                .requestRecovery()
//                .storeDurably()
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(Date.from(DateUtils.plus(new Date().toInstant(), timeSecond, ChronoUnit.SECONDS))).
                withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withRepeatCount(0)
                                .withIntervalInSeconds(10)
//                                .withMisfireHandlingInstructionFireNow()
                ).
                build();
        scheduler.scheduleJob(jobDetail, trigger);
        return "ok";
    }

    public class HelloWorldJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Hello World");
        }
    }
}
