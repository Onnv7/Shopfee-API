package com.hcmute.shopfee.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class GetBlogViewByIdResponse {
    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private Date createdAt;
}
