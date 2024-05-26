package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = TRANSACTION_CONTROLLER_TITLE)
@RestController
@RequestMapping(TRANSACTION_BASE_PATH)
@RequiredArgsConstructor
public class TransactionController {
    private final ITransactionService transactionService;
    @Operation(summary = TRANSACTION_UPDATE_BY_ID_SUM)
    @PatchMapping(path = PATCH_TRANSACTION_UPDATE_BY_ID_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_USER)
    // sau khi thanh toán (thành công/thất bại) => update transaction
    public ResponseEntity<ResponseAPI<?>> updateTransaction(@PathVariable(TRANSACTION_ID) String id, HttpServletRequest request) {
        transactionService.updateTransaction(id, request);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

//    @Operation(summary = TRANSACTION_UPDATE_SUCCESS_STATUS_BY_ID_SUM)
//    @PatchMapping(path = PATCH_TRANSACTION_UPDATE_COMPLETE_SUB_PATH)
//    @PreAuthorize(SecurityConstant.ROLE_WAITER)
//    // FIXME: nên để ở order controller: shipper complete => cập nhật trạng thái order + transaction (nếu là cashing)
//    public ResponseEntity<ResponseAPI<?>> completeTransaction(@PathVariable(TRANSACTION_ID) String id) {
//        transactionService.completeTransaction(id);
//        ResponseAPI<?> res = ResponseAPI.builder()
//                .timestamp(new Date())
//                .message(SuccessConstant.UPDATED)
//                .build();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }




}
