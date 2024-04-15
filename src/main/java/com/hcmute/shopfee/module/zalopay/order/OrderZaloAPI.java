package com.hcmute.shopfee.module.zalopay.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.module.zalopay.ZaloPayUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.*;

public class OrderZaloAPI {
    private static final String ORDER_CREATE_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";
    private static final String ORDER_STATUS_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/query";
    private final ZaloPay zaloPay;

    public OrderZaloAPI(ZaloPay zaloPay) {
        this.zaloPay = zaloPay;
    }

    public Map<String, Object> createOrder(String appUser, long amount) throws IOException {

        String apptransid = ZaloPayUtils.getCurrentTimeString("yyMMdd") + "_" + new Date().getTime();
        System.out.println("apptransid - " + apptransid);
        Map<String, Object> order = new HashMap<String, Object>() {{
            put("app_id", zaloPay.getAppId());
            put("app_trans_id", apptransid);
            // translation missing: vi.docs.shared.sample_code.comments.app_trans_id
            put("app_time", System.currentTimeMillis()); // miliseconds
            put("app_user", appUser);
            put("amount", amount);
            put("description", "Shopfee - Payment for the order");
            put("bank_code", "");
            put("item", "[]");
            put("embed_data", "{}");
            put("callback_url", zaloPay.getCallbackUrl() != null ? zaloPay.getCallbackUrl() : "");
        }};

        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|" + order.get("amount")
                + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|" + order.get("item");
        order.put("mac", ZaloPayUtils.hmacSha256(zaloPay.getKey1(), data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ORDER_CREATE_ENDPOINT);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {

            params.add(new BasicNameValuePair(e.getKey(), e.getValue() != null ? e.getValue().toString() : ""));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {

            resultJsonStr.append(line);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resData = objectMapper.readValue(resultJsonStr.toString(), Map.class);
        resData.put("invoice_code", apptransid);

        return resData;
    }

    public Map<String, Object> getOrder(String appTransId) throws URISyntaxException, IOException {

//        String appTranId = "210608_2553_1623145380738";  // Input your app_trans_id
        String data = zaloPay.getAppId() + "|" + appTransId + "|" + zaloPay.getKey1(); // appid|app_trans_id|key1
        String mac = ZaloPayUtils.hmacSha256(zaloPay.getKey1(), data);


        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", zaloPay.getAppId()));
        params.add(new BasicNameValuePair("app_trans_id", appTransId));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ORDER_STATUS_ENDPOINT);
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {

            resultJsonStr.append(line);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(resultJsonStr.toString(), Map.class);
    }


}
