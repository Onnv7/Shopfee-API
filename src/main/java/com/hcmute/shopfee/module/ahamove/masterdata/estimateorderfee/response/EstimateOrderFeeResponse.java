package com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.shopfee.module.ahamove.Ahamove;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Data
public class EstimateOrderFeeResponse {
    private double distance;
    private int duration;
    @JsonProperty("user_main_account")
    private int userMainAccount;
    @JsonProperty("user_bonus_account")
    private int userBonusAccount;
    private String currency;
    @JsonProperty("total_pay")
    private int totalPay;
    @JsonProperty("polyline_points")
    private String polylinePoints;
    private String polylines;
    @JsonProperty("total_balance")
    private int totalBalance;
    @JsonProperty("total_business_account_balance")
    private int totalBusinessAccountBalance;
    @JsonProperty("is_postpaid")
    private boolean isPostpaid;
    @JsonProperty("is_child_account")
    private boolean isChildAccount;
    @JsonProperty("online_pay")
    private int onlinePay;
    @JsonProperty("payment_method")
    private String paymentMethod;
    private String[] requests;
    @JsonProperty("distance_fee")
    private int distanceFee;
    @JsonProperty("request_fee")
    private int requestFee;
    @JsonProperty("stop_fee")
    private int stopFee;
    @JsonProperty("vat_fee")
    private int vatFee;
    private int discount;
    @JsonProperty("total_fee")
    private int totalFee;
    @JsonProperty("cod_commission_fee")
    private int codCommissionFee;
    @JsonProperty("premium_service_fee")
    private int premiumServiceFee;
    @JsonProperty("weight_fee")
    private int weightFee;
    private double surcharge;
    @JsonProperty("partner_surcharge")
    private double partnerSurcharge;
    @JsonProperty("payment_error_message")
    private String paymentErrorMessage;
    @JsonProperty("stoppoint_price")
    private int stoppointPrice;
    @JsonProperty("special_request_price")
    private int specialRequestPrice;
    private int vat;
    @JsonProperty("cod_commission_price")
    private int codCommissionPrice;
    @JsonProperty("weight_price")
    private int weightPrice;
    @JsonProperty("distance_price")
    private int distancePrice;
    @JsonProperty("voucher_discount")
    private int voucherDiscount;
    @JsonProperty("subtotal_price")
    private int subtotalPrice;
    @JsonProperty("total_price")
    private int totalPrice;
    @JsonProperty("surge_rate")
    private double surgeRate;
}
