package com.hcmute.shopfee.module.ahamove.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.module.ahamove.Ahamove;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AhamoveOrder {
    private Ahamove ahamove;

    public AhamoveOrder() {
    }

    public AhamoveOrder(Ahamove ahamove) {
        this.ahamove = ahamove;
    }

    public  Map<String, Object> createOrder(StartingPoint startingPoint, DestinationPoint destinationPoint, List<OrderItem> items, PayMethod payMethod) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Chuyển đổi đối tượng thành JSON
        String destinationJson = objectMapper.writeValueAsString(destinationPoint);
        String startingJson = objectMapper.writeValueAsString(startingPoint);
        String itemsJson = objectMapper.writeValueAsString(items);

        // In ra JSON
        System.out.println(itemsJson);
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(objectMapper));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//        headers.set("accept", "*/*");
//        String token = new String(Base64.getEncoder().encode((ahamove.getAhamoveToken()).getBytes()));
//
//        headers.add("Authorization", "Basic " + token);
        // Tham số của request
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("service_id", "SGN-BIKE");
        params.add("payment_method", payMethod.name());
        params.add("promo_code", "");
        params.add("remarks", "This is note for order");
        params.add("order_time", "0");
//        params.add("requests", "[]");
        params.add("path", "[" + startingJson + "," + destinationJson + "]");
        params.add("items", itemsJson);
        params.add("token", ahamove.getAhamoveToken());

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(params, headers);
        // Gửi yêu cầu POST và nhận response
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://apistg.ahamove.com/v1/order/create",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // Kiểm tra và xử lý phản hồi
        Map<String, Object> map =new HashMap<>();

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody().toString();
            // Xử lý dữ liệu trả về
            map = objectMapper.readValue(responseBody, Map.class);
            System.out.println(responseBody);
        } else {
            System.err.println("Failed to create order. Status code: " + responseEntity.getStatusCodeValue());
        }
        return map;
    }
}
