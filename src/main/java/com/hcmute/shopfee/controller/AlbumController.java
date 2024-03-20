package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.UploadImageRequest;
import com.hcmute.shopfee.dto.response.GetAllImageResponse;
import com.hcmute.shopfee.enums.AlbumSortType;
import com.hcmute.shopfee.enums.AlbumType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IAlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = ALBUM_CONTROLLER_TITLE)
@RestController
@RequestMapping(ALBUM_BASE_PATH)
@RequiredArgsConstructor
public class AlbumController {
    private final IAlbumService albumService;

    @Operation(summary = ALBUM_UPLOAD_IMAGE_SUM)
    @PostMapping(path = POST_ALBUM_UPLOAD_IMAGE_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<?>> addAddressToUserByUserId(@ModelAttribute @Valid UploadImageRequest body) {
        albumService.uploadImage(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = ALBUM_GET_ALL_SUM)
    @GetMapping(path = GET_ALBUM_GET_ALL_IMAGE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetAllImageResponse>> getAllImage(
            @Parameter(name = "album_type", required = false, example = "")
            @RequestParam(name = "album_type", required = false) AlbumType type,
            @Parameter(name = "sort_type", example = "CREATED_AT_DESC")
            @RequestParam(name = "sort_type", defaultValue = "CREATED_AT_DESC") AlbumSortType sortType,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetAllImageResponse data = albumService.getAllImage(type, page, size, sortType);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = ALBUM_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_ALBUM_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> deleteImageById(@PathVariable(ALBUM_ID) String albumId) {
        albumService.deleteImageById(albumId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.DELETED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
