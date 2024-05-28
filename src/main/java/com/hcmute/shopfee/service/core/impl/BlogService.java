package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.entity.sql.database.blog.BlogEntity;
import com.hcmute.shopfee.enums.BlogStatus;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.payload.request.CreateBlogRequest;
import com.hcmute.shopfee.payload.request.UpdateBlogRequest;
import com.hcmute.shopfee.payload.response.GetBlogDetailsByIdResponse;
import com.hcmute.shopfee.payload.response.GetBlogDetailsListResponse;
import com.hcmute.shopfee.payload.response.GetBlogViewByIdResponse;
import com.hcmute.shopfee.payload.response.GetBlogViewListResponse;
import com.hcmute.shopfee.repository.database.blog.BlogRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.core.IBlogService;
import com.hcmute.shopfee.utils.MediaUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService implements IBlogService {
    private final BlogRepository blogRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;

    @Transactional
    @Override
    public void createBlog(CreateBlogRequest body) {
        BlogEntity blogEntity = modelMapperService.mapClass(body, BlogEntity.class);

        try {
            if(!MediaUtils.isValidImageFile(body.getImage())) {
                throw new ShopfeeException(ShopfeeErrorCode.IMAGE_INVALID);
            }
            CloudinaryUploadResponse imageResponse = cloudinaryService.uploadFileToFolder(CloudinaryConstant.BLOG_PATH, StringUtils.generateFileNameByTime(blogEntity.getTitle(), "blog"), body.getImage().getBytes());
            blogEntity.setThumbnailUrl(cloudinaryService.getThumbnailUrlOfImage(imageResponse.getPublicId()));
            blogEntity.setImageUrl(imageResponse.getUrl());
            blogEntity.setCloudinaryImageId(imageResponse.getPublicId());
        } catch (IOException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }

        blogRepository.save(blogEntity);
    }

    @Override
    public void deleteBlog(String blogId) {
        BlogEntity blogEntity = blogRepository.findByIdAndIsDeletedFalse(blogId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BLOG_NOT_FOUND));
        blogEntity.setDeleted(true);
        blogRepository.save(blogEntity);
    }

    @Override
    public void updateBlog(String blogId, UpdateBlogRequest body) {
        BlogEntity blogEntity = blogRepository.findByIdAndIsDeletedFalse(blogId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BLOG_NOT_FOUND));

        modelMapperService.map(body, blogEntity);

        if(body.getImage() != null) {
            if(!MediaUtils.isValidImageFile(body.getImage())) {
                throw new ShopfeeException(ShopfeeErrorCode.IMAGE_INVALID);
            }
            try {
                if(!MediaUtils.isValidImageFile(body.getImage())) {
                    throw new ShopfeeException(ShopfeeErrorCode.IMAGE_INVALID);
                }
                cloudinaryService.deleteImage(blogEntity.getCloudinaryImageId());

                CloudinaryUploadResponse imageResponse = cloudinaryService.uploadFileToFolder(CloudinaryConstant.BLOG_PATH, StringUtils.generateFileNameByTime(blogEntity.getTitle(), "blog"), body.getImage().getBytes());
                blogEntity.setThumbnailUrl(cloudinaryService.getThumbnailUrlOfImage(imageResponse.getPublicId()));
                blogEntity.setImageUrl(imageResponse.getUrl());
                blogEntity.setCloudinaryImageId(imageResponse.getPublicId());
            } catch (IOException e) {
                log.error(Arrays.toString(e.getStackTrace()));
                throw new RuntimeException(e);
            }
        }

        blogRepository.save(blogEntity);
    }

    @Override
    public GetBlogViewByIdResponse getBlogViewById(String blogId) {
        BlogEntity blogEntity = blogRepository.findByIdAndStatusAndIsDeletedFalse(blogId, BlogStatus.VISIBLE)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BLOG_NOT_FOUND));
        return modelMapperService.mapClass(blogEntity, GetBlogViewByIdResponse.class);
    }

    @Override
    public GetBlogViewListResponse getBlogViewList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BlogEntity> blogPage = blogRepository.findByStatusAndIsDeletedFalse(BlogStatus.VISIBLE, pageable);
        List<GetBlogViewListResponse.Blog> blogList = modelMapperService.mapList(blogPage.getContent(), GetBlogViewListResponse.Blog.class);
        GetBlogViewListResponse data = new GetBlogViewListResponse();
        data.setTotalPage(blogPage.getTotalPages());
        data.setBlogList(blogList);
        return data;
    }

    @Override
    public GetBlogDetailsByIdResponse getBlogDetailsById(String blogId) {
        BlogEntity blogEntity = blogRepository.findByIdAndIsDeletedFalse(blogId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BLOG_NOT_FOUND));
        return modelMapperService.mapClass(blogEntity, GetBlogDetailsByIdResponse.class);
    }

    @Override
    public GetBlogDetailsListResponse getBlogDetailsList(int page, int size, BlogStatus status) {
        GetBlogDetailsListResponse data = new GetBlogDetailsListResponse();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BlogEntity> blogPage = null;
        if(status != null) {
            blogPage = blogRepository.findByStatusAndIsDeletedFalse(status, pageable);
        } else {
            blogPage = blogRepository.findByIsDeletedFalse(pageable);
        }
        data.setTotalPage(blogPage.getTotalPages());
        data.setBlogList(modelMapperService.mapList(blogPage.getContent(), GetBlogDetailsListResponse.Blog.class));
        return data;
    }

}
