package com.hcmute.shopfee.controller;


import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateProductRequest;
import com.hcmute.shopfee.dto.request.CreateReviewRequest;
import com.hcmute.shopfee.enums.ProductType;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = REVIEW_CONTROLLER_TITLE)
@RestController
@RequestMapping(REVIEW_BASE_PATH)
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @Operation(summary = REVIEW_CREATE_SUM)
    @PostMapping(path = POST_REVIEW_CREATE_SUB_PATH)
    public ResponseEntity<ResponseAPI> createReview(@RequestBody @Valid CreateReviewRequest body) {
        reviewService.createReview(body);

        ResponseAPI res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

}
