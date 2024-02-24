package com.hcmute.shopfee.controller;


import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateReviewRequest;
import com.hcmute.shopfee.dto.request.InteractProductReviewRequest;
import com.hcmute.shopfee.dto.response.GetProductReviewListResponse;
import com.hcmute.shopfee.enums.ReviewInteraction;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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
    public ResponseEntity<ResponseAPI<?>> createReview(@RequestBody @Valid CreateReviewRequest body) {
        reviewService.createProductReview(body);

        ResponseAPI<?> res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = REVIEW_CREATE_INTERACTION_FOR_PRODUCT_SUM)
    @PostMapping(path = POST_REVIEW_INTERACT_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> interactProductReview(@PathVariable(PRODUCT_REVIEW_ID) String productReviewId,
                                                             @RequestBody @Valid InteractProductReviewRequest body) {
        reviewService.createProductReviewInteraction(productReviewId, body);

        ResponseAPI<?> res = ResponseAPI.builder()
                .message(SuccessConstant.CREATED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = REVIEW_GET_PRODUCT_TYPE_SUM)
    @GetMapping(path = GET_PRODUCT_REVIEW_LIST_BY_PRODUCT_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetProductReviewListResponse>> getProductReviewListByProductId(
            @PathVariable(PRODUCT_ID) String productId,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
            ) {
        GetProductReviewListResponse resData = reviewService.getProductReviewListByProductId(productId, page, size);

        ResponseAPI<GetProductReviewListResponse> res = ResponseAPI.<GetProductReviewListResponse>builder()
                .message(SuccessConstant.GET)
                .data(resData)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }
}
