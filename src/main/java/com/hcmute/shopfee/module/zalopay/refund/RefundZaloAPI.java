package com.hcmute.shopfee.module.zalopay.refund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.shopfee.module.zalopay.ZaloPay;
import com.hcmute.shopfee.module.zalopay.ZaloPayUtils;
import com.hcmute.shopfee.utils.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RefundZaloAPI {
    private final String ORDER_REFUND_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/refund";
    public static final String REFUND_STATUS_PAYMENT_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/query_refund";
    private final ZaloPay zaloPay;
    public RefundZaloAPI(ZaloPay zaloPay) {
        this.zaloPay = zaloPay;
    }

    public Map<String, Object> sendRefund(String zpTransId, long amount, String description) throws JSONException, IOException {
        Map<String, Object> order = new HashMap<>(){{
            put("app_id", zaloPay.getAppId());
            put("zp_trans_id", zpTransId);
            put("m_refund_id", getCurrentTimeString("yyMMdd") +"_"+ zaloPay.getAppId() +"_"+
                    System.currentTimeMillis() + "" + (111 + new Random().nextInt(888)));
            put("timestamp", System.currentTimeMillis());
            put("amount", amount);
            put("description", description);
        }};

        String data = order.get("app_id") +"|"+ order.get("zp_trans_id") +"|"+ order.get("amount")
                +"|"+ order.get("description") +"|"+ order.get("timestamp");
        order.put("mac", ZaloPayUtils.hmacSha256(zaloPay.getKey1(), data));


        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ORDER_REFUND_ENDPOINT);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

//        JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
//        Map<String, Object> finalResult = new HashMap<>();
//        for (Iterator it = jsonResult.keys(); it.hasNext(); ) {
//            String key = (String) it.next();
//            finalResult.put(key, jsonResult.get(key));
//        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(resultJsonStr.toString(), Map.class);
    }
    private String getCurrentTimeString(String format) {

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(DateUtils.GMT_7));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public Map<String, Object> getStatusRefund(String refundId) throws IOException, URISyntaxException, JSONException {
        String timestamp = Long.toString(System.currentTimeMillis()); // miliseconds
        String data = zaloPay.getAppId() +"|"+ refundId  +"|"+ timestamp; // app_id|m_refund_id|timestamp
        String mac = ZaloPayUtils.hmacSha256(zaloPay.getKey1(), data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", zaloPay.getAppId()));
        params.add(new BasicNameValuePair("m_refund_id", refundId));
        params.add(new BasicNameValuePair("timestamp", timestamp));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(REFUND_STATUS_PAYMENT_ENDPOINT);
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

        JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("return_code", jsonResult.get("return_code"));
        finalResult.put("return_message", jsonResult.get("return_message"));
        finalResult.put("sub_return_code", jsonResult.get("sub_return_code"));
        finalResult.put("sub_return_message", jsonResult.get("sub_return_message"));
        return finalResult;
    }
}
