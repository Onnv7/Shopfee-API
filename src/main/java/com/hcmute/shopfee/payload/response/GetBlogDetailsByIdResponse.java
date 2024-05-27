package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.enums.BlogStatus;
import lombok.Data;

import java.util.Date;

@Data
public class GetBlogDetailsByIdResponse {
    private BlogStatus status;
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private Date createdAt;
}
