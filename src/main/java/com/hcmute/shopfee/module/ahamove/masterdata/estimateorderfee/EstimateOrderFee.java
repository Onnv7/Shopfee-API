package com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.ahamove.Ahamove;
import com.hcmute.shopfee.module.ahamove.masterdata.estimateorderfee.response.EstimateOrderFeeResponse;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class EstimateOrderFee {
    private static final String endpointUrl = "https://apistg.ahamove.com/v1/order/estimated_fee";
    public static EstimateOrderFeeResponse getEstimateOrderFee(Double latOrigin, Double lngOrigin, Double latDestination, Double lngDestination) {
        String token = Ahamove.getAhamoveToken();
        RestTemplate restTemplate = new RestTemplate();

        // Định dạng body data
        MultiValueMap<String, String> bodyData = new LinkedMultiValueMap<>();
        bodyData.add("token", token);
        bodyData.add("order_time", "0");
        bodyData.add("path", "[{\"lat\":"+ latOrigin + ",\"lng\":"+ lngOrigin + ",\"address\":\"725 Hẻm số 7 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Việt Nam\"," +
                "\"short_address\":\"Quận 10\",\"name\":\"Anh\",\"mobile\":\"0931245678\",\"remarks\":\"call me\"}," +
                "{\"lat\":"+ latDestination + ",\"lng\":"+ lngDestination + ",\"address\":\"Chợ Bến Thành, Bến Thành, Quận 01, Hồ Chí Minh, Việt Nam\",\"name\":\"Bao\",\"mobie\":\"09xxxxxxxx\"}]");
        bodyData.add("service_id", "SGN-BIKE");
        bodyData.add("requests", "[]");

        // Tạo HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Tạo HttpEntity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyData, headers);

        try {
            // Gửi request
            ResponseEntity<String> response = restTemplate.exchange(
                    endpointUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Xử lý response
            HttpStatusCode statusCode = response.getStatusCode();
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, EstimateOrderFeeResponse.class);
        } catch (Exception e) {
            throw new CustomException(ErrorConstant.SERVER_ERROR, "Ahamove error: " + e.getMessage());
        }
    }
}
