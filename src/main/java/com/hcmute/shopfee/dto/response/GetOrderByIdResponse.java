package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.TransactionEntity;
import com.hcmute.shopfee.entity.order.*;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.enums.ProductSize;
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
    private OrderType orderType;
    private ShippingInformation shippingInformation;

    private Date createdAt;
    private List<Product> itemList;

    private Transaction transaction;
    private Review review;
    private Date receiveTime;
    private Long shippingDiscount;
    private Long orderDiscount;

//    private Branch branch;
    private String branchAddress;

    public static GetOrderByIdResponse fromOrderBillEntity(OrderBillEntity entity) {
        // TODO: viết set cho private Review review;
        GetOrderByIdResponse order = new GetOrderByIdResponse();
        order.setId(entity.getId());
        order.setNote(entity.getNote());

        order.setTotalPayment(entity.getTotalPayment());
        order.setShippingFee(entity.getShippingFee());
        order.setTotalItemPrice(entity.getTotalItemPrice());

        order.setOrderType(entity.getOrderType());
        order.setShippingInformation(entity.getShippingInformation() != null ? ShippingInformation.fromShippingInformationEntity(entity.getShippingInformation()) : null);
        order.setCreatedAt(entity.getCreatedAt());
        List<Product> itemList = new ArrayList<>();
        entity.getOrderItemList().forEach(it -> {
            Product product = Product.fromOrderItemEntity(it);
            itemList.add(product);
        });
        order.setItemList(itemList);
        order.setTransaction(Transaction.fromTransactionEntity(entity.getTransaction()));
        order.setReceiveTime(entity.getReceiveTime());
        order.setShippingFee(entity.getShippingFee());
        order.setBranchAddress(entity.getBranch().getFullAddress());
        return order;
    }
    @Data
    static class Branch {
        private String address;
    }
    @Data
    private static class Review {
        private int rating;
        private String content;
    }
    @Data
    private static class Transaction {
        private String id;
        private PaymentStatus status;
        private PaymentType paymentType;
        private Long totalPaid;

        public static Transaction fromTransactionEntity(TransactionEntity entity) {
            Transaction transaction = new Transaction();
            transaction.setId(entity.getId());
            transaction.setStatus(entity.getStatus());
            transaction.setPaymentType(entity.getPaymentType());
            transaction.setTotalPaid(entity.getTotalPaid());
            return transaction;
        }
    }

    @Data
    private static class ShippingInformation {
        private String detail;
        private Double longitude;
        private Double latitude;
        private String note;
        private String recipientName;
        private String phoneNumber;

        public static ShippingInformation fromShippingInformationEntity(ShippingInformationEntity entity){
            ShippingInformation shipping = new ShippingInformation();
            shipping.setDetail(entity.getDetail());
            shipping.setLatitude(entity.getLatitude());
            shipping.setLongitude(entity.getLongitude());
            shipping.setNote(entity.getNote());
            shipping.setRecipientName(entity.getRecipientName());
            shipping.setPhoneNumber(entity.getPhoneNumber());
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
                ItemDetail data= new ItemDetail();
                data.setPrice(entity.getPrice());
                data.setNote(entity.getNote());
                data.setSize(entity.getSize());
                data.setQuantity(entity.getQuantity());
                List<Topping> toppingList = new ArrayList<>();
                for (ItemToppingEntity topping: entity.getItemToppingList()) {
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
                for(ItemDetailEntity entity: entityList) {
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

