package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.enums.BlogStatus;
import com.hcmute.shopfee.payload.request.CreateBlogRequest;
import com.hcmute.shopfee.payload.request.UpdateBlogRequest;
import com.hcmute.shopfee.payload.response.GetBlogDetailsByIdResponse;
import com.hcmute.shopfee.payload.response.GetBlogDetailsListResponse;
import com.hcmute.shopfee.payload.response.GetBlogViewByIdResponse;
import com.hcmute.shopfee.payload.response.GetBlogViewListResponse;

public interface IBlogService {
    void createBlog(CreateBlogRequest body);
    void deleteBlog(String blogId);
    void updateBlog(String blogId, UpdateBlogRequest body);
    GetBlogViewByIdResponse getBlogViewById(String blogId);
    GetBlogViewListResponse getBlogViewList(int page, int size);
    GetBlogDetailsByIdResponse getBlogDetailsById(String blogId);
    GetBlogDetailsListResponse getBlogDetailsList(int page, int size, BlogStatus status);
}
