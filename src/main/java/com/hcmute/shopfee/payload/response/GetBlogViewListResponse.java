package com.hcmute.shopfee.payload.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetBlogViewListResponse {

    private Integer totalPage;
    private List<Blog> blogList;

    @Data
    public static class Blog {
        private String id;
        private String title;
        private String summary;
        private String thumbnailUrl;
        private Date createdAt;
    }
}
