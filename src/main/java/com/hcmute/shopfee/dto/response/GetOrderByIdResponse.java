package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.sql.database.order.*;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.enums.ProductSize;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetOrderByIdResponse {
    private String id;
    //    private String code;
    private String note;
    private Long totalPayment;
    private Long shippingFee;
    private Long totalItemPrice;
    private Long coin;
    private OrderType orderType;
    private ReceiverInformation receiverInformation;
    private Date createdAt;
    private List<Product> itemList;

    private Transaction transaction;

    private Long shippingDiscount;
    private Long orderDiscount;
    private Branch branch;
    private Boolean needReview;
//    private String branchAddress;

    public static GetOrderByIdResponse fromOrderBillEntity(OrderBillEntity entity) {
        // TODO: viết set cho private Review review;
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

        List<Product> itemList = new ArrayList<>();

        for (OrderItemEntity item : entity.getOrderItemList()) {
            Product product = Product.fromOrderItemEntity(item);
            itemList.add(product);

            if(item.getProductReview() == null) {
                order.setNeedReview(true);
            }
        }
        if(order.getNeedReview() == null) {
            order.setNeedReview(false);
        }
        order.setItemList(itemList);
        order.setTransaction(Transaction.fromTransactionEntity(entity.getTransaction()));

        order.setShippingFee(entity.getShippingFee());
        order.setBranch(Branch.builder()
                .address(entity.getBranch().getFullAddress())
                .id(entity.getBranch().getId())
                .build());
        return order;
    }

    @Data
    @Builder
    private static class ReceiverInfo {
        private String receiverName;
        private Date receiveTime;
        private String phoneNumber;
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
        private PaymentStatus status;
        private PaymentType paymentType;
        private Long totalPaid;
        private String paymentUrl;

        public static Transaction fromTransactionEntity(TransactionEntity entity) {
            Transaction transaction = new Transaction();
            transaction.setId(entity.getId());
            transaction.setStatus(entity.getStatus());
            transaction.setPaymentType(entity.getPaymentType());
            transaction.setTotalPaid(entity.getTotalPaid());
            transaction.setPaymentUrl(entity.getPaymentUrl());
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
            private String note;

            public static ItemDetail fromItemDetailEntity(ItemDetailEntity entity) {
                ItemDetail data = new ItemDetail();
                data.setPrice(entity.getPrice());
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

