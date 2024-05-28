package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.enums.BlogStatus;
import com.hcmute.shopfee.payload.request.CreateBlogRequest;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.payload.request.UpdateBlogRequest;
import com.hcmute.shopfee.payload.response.GetBlogDetailsByIdResponse;
import com.hcmute.shopfee.payload.response.GetBlogDetailsListResponse;
import com.hcmute.shopfee.payload.response.GetBlogViewByIdResponse;
import com.hcmute.shopfee.payload.response.GetBlogViewListResponse;
import com.hcmute.shopfee.service.core.IBlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = BLOG_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(BLOG_BASE_PATH)
public class BlogController {
    private final IBlogService blogService;
    @Operation(summary = BLOG_CREATE_SUM)
    @PostMapping(path = POST_BLOG_CREATE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> createBlog(@ModelAttribute @Valid CreateBlogRequest body) {
        blogService.createBlog(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = BLOG_GET_DETAILS_BY_ID_SUM)
    @GetMapping(path = GET_BLOG_DETAILS_BY_ID_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetBlogDetailsByIdResponse>> getBlogDetailsById(@PathVariable(BLOG_ID) String blogId) {
        GetBlogDetailsByIdResponse data = blogService.getBlogDetailsById(blogId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.GET)
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = BLOG_GET_DETAILS_LIST_SUM)
    @GetMapping(path = GET_BLOG_DETAILS_LIST_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetBlogDetailsListResponse>> getBlogDetailsList(
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "status", required = false)
            @RequestParam(name = "status", required = false) BlogStatus status
            ) {
        GetBlogDetailsListResponse data = blogService.getBlogDetailsList(page, size, status);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.GET)
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = BLOG_GET_VIEW_BY_ID_SUM)
    @GetMapping(path = GET_BLOG_VIEW_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetBlogViewByIdResponse>> getBlogViewById(@PathVariable(BLOG_ID) String blogId) {
        GetBlogViewByIdResponse data = blogService.getBlogViewById(blogId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.GET)
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = BLOG_GET_LIST_SUM)
    @GetMapping(path = GET_BLOG_VIEW_LIST_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetBlogViewListResponse>> getBlogViewList(
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetBlogViewListResponse data = blogService.getBlogViewList(page, size);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.GET)
                .data(data)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = BLOG_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_BLOG_BY_ID_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> deleteBlog(@PathVariable(BLOG_ID) String blogId) {
        blogService.deleteBlog(blogId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.DELETED)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
    @Operation(summary = BLOG_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_BLOG_UPDATE_BY_ID_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> updateBlog(@PathVariable(BLOG_ID) String blogId, @ModelAttribute @Valid UpdateBlogRequest body) {
        blogService.updateBlog(blogId, body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
