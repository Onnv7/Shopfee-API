package com.hcmute.shopfee.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.dto.common.vnpay.VnpayCallbackResponse;
import com.hcmute.shopfee.dto.common.zalopay.CallBackDto;
import com.hcmute.shopfee.dto.common.zalopay.ZaloCallbackResponse;
import com.hcmute.shopfee.service.common.VNPayService;
import com.hcmute.shopfee.service.common.ZaloPayService;
import com.hcmute.shopfee.service.core.ICallbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.hcmute.shopfee.constant.RouterConstant.ALBUM_BASE_PATH;
import static com.hcmute.shopfee.constant.RouterConstant.CALLBACK_BASE_PATH;
import static com.hcmute.shopfee.constant.SwaggerConstant.ALBUM_CONTROLLER_TITLE;


@RestController
@RequestMapping(CALLBACK_BASE_PATH)
@RequiredArgsConstructor
public class CallbackController {
    private final ICallbackService callbackService;

    @GetMapping("/vnpay")
    public ResponseEntity<VnpayCallbackResponse> doCallBackVNPay(@RequestParam Map<String, Object> callBackInfo, HttpServletRequest request) throws UnsupportedEncodingException, JsonProcessingException {
        VnpayCallbackResponse res = callbackService.processCallback(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/zalopay")
    public ZaloCallbackResponse doCallBackZaloPay(@RequestBody CallBackDto body) throws IOException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        return callbackService.processCallback(body);
    }
}
