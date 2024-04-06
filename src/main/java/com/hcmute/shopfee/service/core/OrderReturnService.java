package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.dto.request.CreateOrderReturnRequest;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderReturnMediaEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderReturnRequestEntity;
import com.hcmute.shopfee.enums.AnswerStatus;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderReturnRequestRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.utils.DateUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReturnService implements IOrderReturnService {

    private final OrderBillRepository orderBillRepository;
    private final CloudinaryService cloudinaryService;
    private final OrderReturnRequestRepository orderReturnRequestRepository;

    @Override
    public void createOrderRefundRequest(CreateOrderReturnRequest body, String orderId) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));
        Date currentTime = new Date();
        List<OrderEventEntity> orderEventEntityList = orderBill.getOrderEventList();
        OrderEventEntity lastEvent = orderEventEntityList.get(0);

        if(lastEvent.getOrderStatus() != OrderStatus.SUCCEED) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "It is not possible to submit a refund request without a successful application");
        }

        if(Date.from(DateUtils.plus(lastEvent.getCreatedAt().toInstant(), 30, ChronoUnit.MINUTES)).after(currentTime)) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "A refund request cannot be submitted after 30 minutes from the time the order is successfully delivered");
        }

        OrderEventEntity event = OrderEventEntity.builder()
                .orderBill(orderBill)
                .orderStatus(OrderStatus.ORDER_REFUND_REQUEST)
                .build();
        orderBill.getOrderEventList().add(event);



        List<OrderReturnMediaEntity> orderReturnMediaEntityList = new ArrayList<>();
        OrderReturnRequestEntity orderReturnRequestEntity = OrderReturnRequestEntity
                .builder()
                .status(AnswerStatus.PENDING)
                .orderBill(orderBill)
                .reason(body.getReason())
                .orderReturnMediaList(orderReturnMediaEntityList)
                .build();
        orderBill.setOrderReturnRequest(orderReturnRequestEntity);
        for (MultipartFile media : body.getMediaList()) {
            try {
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.ORDER_RETURN_PATH, StringUtils.generateFileName(orderId, "order_return"), media.getBytes());
                OrderReturnMediaEntity mediaEntity = OrderReturnMediaEntity.builder()
                        .mediaUrl(fileUploaded.getUrl())
                        .cloudinaryMediaId(fileUploaded.getPublicId())
                        .thumbnailUrl(cloudinaryService.getThumbnailUrl(fileUploaded.getPublicId()))
                        .orderReturnRequest(orderReturnRequestEntity)
                        .build();
                orderReturnMediaEntityList.add(mediaEntity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        orderBillRepository.save(orderBill);
    }

    @Override
    public void processOrderRefundRequest(AnswerStatus status, String orderId) {
        OrderReturnRequestEntity orderReturnRequest = orderReturnRequestRepository.findByOrderBill_Id(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Order return request not found"));
        if(orderReturnRequest.getStatus() != AnswerStatus.PENDING) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "The request has already been processed");
        }
        OrderBillEntity orderBill = orderReturnRequest.getOrderBill();
        orderReturnRequest.setStatus(status);
        if(status == AnswerStatus.REFUSED) {
            OrderEventEntity event = OrderEventEntity.builder()
                    .orderBill(orderBill)
                    .orderStatus(OrderStatus.ORDER_REFUND_REFUSED)
                    .build();
            orderBill.getOrderEventList().add(event);

        } else if(status == AnswerStatus.ACCEPTED){
            OrderEventEntity event = OrderEventEntity.builder()
                    .orderBill(orderBill)
                    .orderStatus(OrderStatus.ORDER_REFUND_ACCEPTED)
                    .build();
            orderBill.getOrderEventList().add(event);

            UserEntity user = orderBill.getUser();
            long coinRefund = orderBill.getCoin() + orderBill.getTotalPayment();
            user.setCoin(user.getCoin() + coinRefund);
            orderBill.getTransaction().setRefunded(true);

        }
        orderBillRepository.save(orderBill);
    }
}
