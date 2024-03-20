package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.UploadImageRequest;
import com.hcmute.shopfee.dto.response.GetAllImageResponse;
import com.hcmute.shopfee.enums.AlbumSortType;
import com.hcmute.shopfee.enums.AlbumType;

public interface IAlbumService {
    void uploadImage(UploadImageRequest body);
    GetAllImageResponse getAllImage(AlbumType type, int page, int size, AlbumSortType sortType);
    void deleteImageById(String imageId);
}
