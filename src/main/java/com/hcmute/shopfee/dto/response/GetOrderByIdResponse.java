package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.order.*;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import com.hcmute.shopfee.enums.*;
import com.hcmute.shopfee.utils.DateUtils;
import lombok.Builder;
import lombok.Data;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.ShopfeeConstant.HOURS_REQUEST_REFUND;

@Data
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetOrderByIdResponse {
    private String id;
    private String note;
    private List<Product> itemList;

    private Long totalItemPrice;

    private Long shippingFee;

    private Long coin;

    private Long totalPayment;
    private RewardInformation rewardInformation;

    private OrderType orderType;
    private ReceiverInformation receiverInformation;
    private Date createdAt;

    private Transaction transaction;

    private Branch branch;
    private Boolean needReview;
    private RefundStatus refundStatus;
    private ImplementationStaff employee;
    @Data
    private static class ImplementationStaff {
        private String id;
        private String fullName;

        public ImplementationStaff(String id, String fullName) {
            this.id = id;
            this.fullName = fullName;
        }
    }
    private enum RefundStatus {
        CAN_REFUND,
        REFUNDED,
        NOT_REFUND
    }

    public static GetOrderByIdResponse fromOrderBillEntityAndEmployee(OrderBillEntity entity, EmployeeEntity employeeEntity) {
        GetOrderByIdResponse order = new GetOrderByIdResponse();
        order.setId(entity.getId());
        order.setNote(entity.getNote());
        order.setCoin(entity.getCoin());
        order.setTotalPayment(entity.getTotalPayment());
        order.setShippingFee(entity.getShippingFee());
        order.setTotalItemPrice(entity.getTotalItemPrice());
        order.setReceiverInformation(entity.getReceiverInformation() != null ? ReceiverInformation.fromReceiverInformationEntity(entity.getReceiverInformation()) : null);

        order.setOrderType(entity.getOrderType());
        order.setCreatedAt(entity.getCreatedAt());
        if(entity.getOrderRefundRequest() != null) {
            order.setRefundStatus(RefundStatus.REFUNDED);
        } else if(DateUtils.nowIsAfterPeriodFromTimeOriginal(entity.getOrderEventList().get(0).getCreatedAt().toInstant(), HOURS_REQUEST_REFUND, ChronoUnit.HOURS)) {
            order.setRefundStatus(RefundStatus.NOT_REFUND);
        } else {
            order.setRefundStatus(RefundStatus.CAN_REFUND);
        }

        List<Product> itemList = new ArrayList<>();

        for (OrderItemEntity item : entity.getOrderItemList()) {
            Product product = Product.fromOrderItemEntity(item);
            itemList.add(product);

            if (item.getProductReview() == null) {
                order.setNeedReview(true);
            }
        }
        if (order.getNeedReview() == null) {
            order.setNeedReview(false);
        }
        order.setItemList(itemList);
        order.setTransaction(Transaction.fromTransactionEntity(entity.getTransaction()));

        order.setShippingFee(entity.getShippingFee());
        order.setBranch(Branch.builder()
                .address(entity.getBranch().getFullAddress())
                .id(entity.getBranch().getId())
                .build());
        if(employeeEntity != null) {
            ImplementationStaff employee = new ImplementationStaff(employeeEntity.getId(), employeeEntity.getFullName());
            order.setEmployee(employee);
        }

        RewardInformation rewardInformation = new RewardInformation(entity.getOrderDiscount(), entity.getShippingDiscount());
        CouponUsedEntity productCoupon = entity.getCouponUsedList().stream().filter(it -> it.getType() == CouponType.PRODUCT).findFirst().orElse(null);
        if (productCoupon != null) {
            if (productCoupon.getCouponRewardReceived().getType() == CouponRewardType.PRODUCT_GIFT) {
                List<ProductRewardReceivedEntity> productRewardReceivedEntityList = productCoupon.getCouponRewardReceived().getProductRewardReceivedList();
                List<GetOrderByIdResponse.ProductGift> productGiftList = new ArrayList<>();
                for (ProductRewardReceivedEntity productGiftEntity: productRewardReceivedEntityList) {
                    productGiftList.add(new ProductGift(productGiftEntity.getProductId(), productGiftEntity.getProductName(), productGiftEntity.getProductSize(), productGiftEntity.getQuantity()));
                }
                rewardInformation.setProductGiftList(productGiftList);
            }
        }
        order.setRewardInformation(rewardInformation);
        return order;
    }

    @Data
    private static class RewardInformation {
        private Long orderDiscount;
        private Long shippingDiscount;
        private List<ProductGift> productGiftList;

        public RewardInformation(Long orderDiscount, Long shippingDiscount) {
            this.orderDiscount = orderDiscount;
            this.shippingDiscount = shippingDiscount;
        }
    }

    @Data
    private static class ProductGift {
        private String productId;
        private String name;
        private String size;
        private Short quantity;

        public ProductGift(String productId, String name, String size, Short quantity) {
            this.productId = productId;
            this.name = name;
            this.size = size;
            this.quantity = quantity;
        }
    }

    @Data
    @Builder
    static class Branch {
        private String id;
        private String address;
    }

    @Data
    private static class Transaction {
        private String id;
        private TransactionStatus status;
        private PaymentType paymentType;
        private String paymentUrl;

        public static Transaction fromTransactionEntity(TransactionEntity entity) {
            Transaction transaction = new Transaction();
            transaction.setId(entity.getId());
            transaction.setStatus(entity.getStatus());
            transaction.setPaymentType(entity.getPaymentType());
            String paymentUrl = transaction.getPaymentType() == PaymentType.ZALOPAY ? entity.getZaloPay().getPaymentUrl() :
                    transaction.getPaymentType() == PaymentType.VNPAY ? entity.getVnPay().getPaymentUrl() : null;
            transaction.setPaymentUrl(paymentUrl);
            return transaction;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ReceiverInformation {
        private String userId;
        private String detail;
        private Double longitude;
        private Double latitude;
        private String note;
        private String recipientName;
        private String phoneNumber;
        private Date receiveTime;

        public static ReceiverInformation fromReceiverInformationEntity(ReceiverInformationEntity entity) {
            ReceiverInformation shipping = new ReceiverInformation();
            shipping.setUserId(entity.getOrderBill().getUser().getId());
            shipping.setDetail(entity.getAddress());
            shipping.setLatitude(entity.getLatitude());
            shipping.setLongitude(entity.getLongitude());
            shipping.setNote(entity.getNote());
            shipping.setRecipientName(entity.getRecipientName());
            shipping.setPhoneNumber(entity.getPhoneNumber());
            shipping.setReceiveTime(entity.getReceiveTime());
            return shipping;
        }
    }

    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public static class Product {
        private String productId;
        private String name;
        private List<ItemDetail> itemDetailList;


        @Data
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        public static class ItemDetail {
            private int quantity;
            private List<Topping> toppingList;
            private ProductSize size;
            private Long price;
            private Long productDiscount;
            private String note;

            public static ItemDetail fromItemDetailEntity(ItemDetailEntity entity) {
                ItemDetail data = new ItemDetail();
                data.setPrice(entity.getPrice());
                data.setProductDiscount(entity.getProductDiscount());
                data.setNote(entity.getNote());
                data.setSize(entity.getSize());
                data.setQuantity(entity.getQuantity());
                List<Topping> toppingList = new ArrayList<>();
                for (ItemToppingEntity topping : entity.getItemToppingList()) {
                    Topping toppingData = new Topping();
                    toppingData.setName(topping.getName());
                    toppingData.setPrice(topping.getPrice());
                    toppingList.add(toppingData);
                }
                data.setToppingList(toppingList);
                return data;
            }

            public static List<ItemDetail> fromItemDetailEntityList(List<ItemDetailEntity> entityList) {
                List<ItemDetail> data = new ArrayList<>();
                for (ItemDetailEntity entity : entityList) {
                    data.add(fromItemDetailEntity(entity));
                }
                return data;
            }
        }

        public static Product fromOrderItemEntity(OrderItemEntity orderItemEntity) {
            Product product = new Product();
            List<ItemDetail> itemDetailList = ItemDetail.fromItemDetailEntityList(orderItemEntity.getItemDetailList());
            product.setItemDetailList(itemDetailList);
            product.setProductId(orderItemEntity.getProduct().getId());
            product.setName(orderItemEntity.getName());
            return product;
        }

        @Data
        static class ProductGift {
            private String productName;
            private String size;
            private Integer quantity;
        }

        @Data
        public static class Topping {
            private String name;
            private double price;
        }

    }


}

