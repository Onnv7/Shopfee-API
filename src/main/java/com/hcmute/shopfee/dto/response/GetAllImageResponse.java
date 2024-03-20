package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.AlbumEntity;
import com.hcmute.shopfee.utils.ImageUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAllImageResponse {
    private Integer totalPage;
    private List<ImageCard> imageList;
    @Data
    private static class ImageCard {
        private String id;
        private String imageUrl;
        private static ImageCard fromAlbumEntity(AlbumEntity entity) {
            ImageCard data = new ImageCard();
            data.setId(entity.getId());
            data.setImageUrl(entity.getImageUrl());
            return data;
        }
    }
    public static List<ImageCard> fromAlbumEntityList(List<AlbumEntity> entityList ) {
        List<ImageCard> data = new ArrayList<>();
        for (AlbumEntity entity : entityList) {
            data.add(ImageCard.fromAlbumEntity(entity));
        }
        return data;
    }
}
