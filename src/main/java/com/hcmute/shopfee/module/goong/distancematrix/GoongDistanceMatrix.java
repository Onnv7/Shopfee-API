package com.hcmute.shopfee.module.goong.distancematrix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.module.goong.Goong;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class GoongDistanceMatrix {
    private final String endpointUrl = "https://rsapi.goong.io/DistanceMatrix?origins=%s&destinations=%s&vehicle=%s&api_key=%s";

    private final Goong goong;

    public GoongDistanceMatrix(Goong goong) {
        this.goong = goong;
    }

    public DistanceMatrixResponse getDistanceMatrix(String origins, String destinations, String vehicle) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            String url = String.format(endpointUrl, origins, destinations, vehicle, goong.getAPI_KEY());
            String result = restTemplate.getForObject(url, String.class); //"{\"rows\":[{\"elements\":[{\"distance\":{\"text\":\"12.26 km\",\"value\":12258},\"duration\":{\"text\":\"42 phút\",\"value\":2490},\"status\":\"OK\"},{\"distance\":{\"text\":\"11.03 km\",\"value\":11028},\"duration\":{\"text\":\"37 phút\",\"value\":2212},\"status\":\"OK\"},{\"distance\":{\"text\":\"10.92 km\",\"value\":10923},\"duration\":{\"text\":\"37 phút\",\"value\":2223},\"status\":\"OK\"}]}]}";
            // restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(result, DistanceMatrixResponse.class);
        }
        catch (JsonMappingException e) {
            throw new CustomException(ErrorConstant.SERVER_ERROR, e.getMessage());
        }
        catch (HttpClientErrorException e) {
            throw new CustomException(ErrorConstant.NOT_FOUND, "Goong could not find the location with the posted coordinates");
        }
        catch (Exception e) {
            throw new CustomException(ErrorConstant.SERVER_ERROR, e.getMessage());
        }
    }
}
