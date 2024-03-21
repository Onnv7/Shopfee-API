package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UploadImageRequest;
import com.hcmute.shopfee.dto.response.GetAllImageResponse;
import com.hcmute.shopfee.entity.database.AlbumEntity;
import com.hcmute.shopfee.enums.AlbumSortType;
import com.hcmute.shopfee.enums.AlbumType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.AlbumRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.core.IAlbumService;
import com.hcmute.shopfee.utils.ImageUtils;
import com.hcmute.shopfee.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AlbumService implements IAlbumService {
    private final AlbumRepository albumRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public void uploadImage(UploadImageRequest body) {
        if(!ImageUtils.isValidImageFile(body.getImage())) {
            throw new CustomException(ErrorConstant.IMAGE_INVALID);
        }
        String pathCloudinary = body.getType() == AlbumType.CATEGORY ? CloudinaryConstant.CATEGORY_PATH : CloudinaryConstant.PRODUCT_PATH;
        String fileName = StringUtils.generateFileName("", "album");
        try {
            HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(pathCloudinary, fileName, body.getImage().getBytes());
            AlbumEntity album = AlbumEntity.builder()
                    .imageUrl(fileUploaded.get(CloudinaryConstant.URL_PROPERTY))
                    .cloudinaryImageId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID))
                    .thumbnailUrl(cloudinaryService.getThumbnailUrl(fileUploaded.get(CloudinaryConstant.PUBLIC_ID)))
                    .type(body.getType())
                    .build();
            albumRepository.save(album);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetAllImageResponse getAllImage(AlbumType type, int page, int size, AlbumSortType sortType) {
        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sortType == AlbumSortType.CREATED_AT_DESC ? sort.descending() : sort.ascending());
        GetAllImageResponse data = new GetAllImageResponse();
        Page<AlbumEntity> albumPage;
        if(type == null) {
            albumPage = albumRepository.findByCloudinaryImageIdIsNotNull(pageable);
        } else {
            albumPage = albumRepository.findByCloudinaryImageIdIsNotNullAndType(type, pageable);
        }
        data.setTotalPage(albumPage.getTotalPages());
        data.setImageList(GetAllImageResponse.fromAlbumEntityList(albumPage.getContent()));

        return data;
    }

    @Override
    public void deleteImageById(String imageId) {
        AlbumEntity album = albumRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND,ErrorConstant.ALBUM_ID_INVALID + imageId));

        if(album.getProduct() != null || album.getCategory() != null) {
            throw new CustomException(ErrorConstant.CANT_DELETE);
        }
        try {
            cloudinaryService.deleteImage(album.getCloudinaryImageId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        albumRepository.delete(album);
    }

}
