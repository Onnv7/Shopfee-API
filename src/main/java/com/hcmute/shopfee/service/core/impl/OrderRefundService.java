package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.dto.request.CreateOrderReturnRequest;
import com.hcmute.shopfee.dto.response.GetOrderRefundResponse;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderRefundMediaEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderRefundRequestEntity;
import com.hcmute.shopfee.enums.AnswerStatus;
import com.hcmute.shopfee.enums.MediaType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderReturnRequestRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.core.IOrderRefundService;
import com.hcmute.shopfee.utils.DateUtils;
import com.hcmute.shopfee.utils.MediaUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.ShopfeeConstant.HOURS_REQUEST_REFUND;

@Service
@RequiredArgsConstructor
public class OrderRefundService implements IOrderRefundService {

    private final OrderBillRepository orderBillRepository;
    private final CloudinaryService cloudinaryService;
    private final OrderReturnRequestRepository orderReturnRequestRepository;

    @Transactional
    @Override
    public void createOrderRefundRequest(CreateOrderReturnRequest body, String orderId) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new ShopfeeException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

        Date currentTime = new Date();
        List<OrderEventEntity> orderEventEntityList = orderBill.getOrderEventList();
        OrderEventEntity lastEvent = orderEventEntityList.get(0);

        if (lastEvent.getOrderStatus() != OrderStatus.SUCCEED) {
            throw new ShopfeeException(ErrorConstant.ACTING_INCORRECTLY, "It is not possible to submit a refund request without a successful application");
        }

        if (DateUtils.nowIsAfterPeriodFromTimeOriginal(lastEvent.getCreatedAt().toInstant(), HOURS_REQUEST_REFUND, ChronoUnit.HOURS)) {
            throw new ShopfeeException(ErrorConstant.ACTING_INCORRECTLY, "A refund request cannot be submitted after 30 minutes from the time the order is successfully delivered");
        }

        List<OrderRefundMediaEntity> orderRefundMediaEntityList = new ArrayList<>();
        OrderRefundRequestEntity orderRefundRequestEntity = OrderRefundRequestEntity
                .builder()
                .status(AnswerStatus.PENDING)
                .orderBill(orderBill)
                .reason(body.getReason())
                .note(body.getNote())
                .build();
        orderBill.setOrderRefundRequest(orderRefundRequestEntity);

        for (MultipartFile media : body.getMediaList()) {
            try {
                String thumbnailUrl = "";
                MediaType mediaType = MediaUtils.getMediaType(media);

                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.ORDER_RETURN_PATH, StringUtils.generateFileName(orderId, "order_return"), media.getBytes());
                if(mediaType == MediaType.IMAGE) {
                    thumbnailUrl = cloudinaryService.getThumbnailUrlOfImage(fileUploaded.getPublicId());
                } else if(mediaType == MediaType.VIDEO) {
                    thumbnailUrl = cloudinaryService.getThumbnailUrlOfVideo(fileUploaded.getPublicId());
                }

                OrderRefundMediaEntity mediaEntity = OrderRefundMediaEntity.builder()
                        .mediaUrl(fileUploaded.getUrl())
                        .cloudinaryMediaId(fileUploaded.getPublicId())
                        .thumbnailUrl(thumbnailUrl)
                        .orderRefundRequest(orderRefundRequestEntity)
                        .build();
                orderRefundMediaEntityList.add(mediaEntity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        orderRefundRequestEntity.setOrderRefundMediaList(orderRefundMediaEntityList);
        orderBillRepository.save(orderBill);
    }

    @Override
    public void processOrderRefundRequest(AnswerStatus status, String orderId) {
        OrderRefundRequestEntity orderReturnRequest = orderReturnRequestRepository.findByOrderBill_Id(orderId)
                .orElseThrow(() -> new ShopfeeException(ErrorConstant.NOT_FOUND, "Order return request not found"));
        if (orderReturnRequest.getStatus() != AnswerStatus.PENDING) {
            throw new ShopfeeException(ErrorConstant.ACTING_INCORRECTLY, "The request has already been processed");
        }
        OrderBillEntity orderBill = orderReturnRequest.getOrderBill();
        orderReturnRequest.setStatus(status);
        if (status == AnswerStatus.ACCEPTED) {

            UserEntity user = orderBill.getUser();
            long coinRefunded = orderBill.getCoin() + orderBill.getTotalPayment();
            user.setCoin(user.getCoin() + coinRefunded);
            orderBill.getTransaction().setRefunded(true);

        }
        orderBillRepository.save(orderBill);
    }

    @Override
    public GetOrderRefundResponse getOrderRefundRequest(String orderId) {
        OrderRefundRequestEntity orderReturnRequest = orderReturnRequestRepository.findByOrderBill_Id(orderId)
                .orElseThrow(() -> new ShopfeeException(ErrorConstant.NOT_FOUND, "Order return request not found"));
        return GetOrderRefundResponse.fromOrderRefundRequestEntity(orderReturnRequest);
    }
}
