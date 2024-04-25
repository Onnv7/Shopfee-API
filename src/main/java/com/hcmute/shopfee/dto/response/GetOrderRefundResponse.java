package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.order.OrderRefundRequestEntity;
import com.hcmute.shopfee.enums.AnswerStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetOrderRefundResponse {
    private String reason;
    private String note;
    private AnswerStatus status;
    private List<Media> mediaList;
    private Date createdAt;
    @Data
    private static class Media {
        private String thumbnailUrl;
        private String mediaUrl;
    }

    public static GetOrderRefundResponse fromOrderRefundRequestEntity(OrderRefundRequestEntity entity) {
        GetOrderRefundResponse data = new GetOrderRefundResponse();
        data.setReason(entity.getReason());
        data.setNote(entity.getNote());
        data.setStatus(entity.getStatus());
        data.setCreatedAt(entity.getCreatedAt());
        data.setMediaList(entity.getOrderRefundMediaList().stream().map(it -> {
            Media media = new Media();
            media.setMediaUrl(it.getMediaUrl());
            media.setThumbnailUrl(it.getThumbnailUrl());
            return media;
        }).toList());
        return data;
    }
}
