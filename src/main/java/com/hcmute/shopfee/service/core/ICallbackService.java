package com.hcmute.shopfee.service.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.dto.common.DialogFlowRequest;
import com.hcmute.shopfee.dto.common.DialogFlowResponse;
import com.hcmute.shopfee.dto.common.vnpay.VnpayCallbackResponse;
import com.hcmute.shopfee.dto.common.zalopay.CallBackDto;
import com.hcmute.shopfee.dto.common.zalopay.ZaloCallbackResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface ICallbackService {
    ZaloCallbackResponse processCallback(CallBackDto body) throws JsonProcessingException;

    VnpayCallbackResponse processCallback(HttpServletRequest request) throws UnsupportedEncodingException;
    DialogFlowResponse processDialogFlow(String body);
}
