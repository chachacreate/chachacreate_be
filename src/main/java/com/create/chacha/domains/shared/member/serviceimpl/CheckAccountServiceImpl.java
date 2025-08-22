package com.create.chacha.domains.shared.member.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import com.create.chacha.domains.shared.member.service.CheckAccountService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckAccountServiceImpl implements CheckAccountService {
    @Value("${iamport.key}")
    private String impKey;

    @Value("${iamport.secret}")
    private String impSecret;

    String REQUEST_URL = "https://api.iamport.kr/users/getToken";
    String PAYMENT_URL = "https://api.iamport.kr/vbanks/holder";

    public HashMap getAccessToken1(String bank_code, String bank_num) {

        HashMap map = new HashMap<>();


        try {
            // url Http 연결 생성
            URL url = new URL(REQUEST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 요청
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);// outputStreamm으로 post 데이터를 넘김

            conn.setRequestProperty("content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // 파라미터 세팅
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

            JSONObject requestData = new JSONObject();
            requestData.put("imp_key", impKey);
            requestData.put("imp_secret", impSecret);

            bw.write(requestData.toString());
            bw.flush();
            bw.close();

            int resposeCode = conn.getResponseCode();

            log.info("응답코드 =============" + resposeCode);
            if (resposeCode == 200) {// 성공
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                // 토큰 값 빼기
                JsonElement jsonElement = JsonParser.parseString(sb.toString());
                String access_token = jsonElement.getAsJsonObject().getAsJsonObject("response").get("access_token").getAsString();
                log.info("Access Token: " + access_token);

                String query = String.format("?bank_code=%s&bank_num=%s", URLEncoder.encode(bank_code, "UTF-8"),
                        URLEncoder.encode(bank_num, "UTF-8"));
                URL bankurl = new URL(PAYMENT_URL + query);
                log.info(bankurl.toString());

                HttpURLConnection getConn = (HttpURLConnection) bankurl.openConnection();
                getConn.setRequestMethod("GET");
                getConn.setRequestProperty("Content-Type", "application/json");
                getConn.setRequestProperty("Authorization", "Bearer " + access_token);

                int getResponseCode = getConn.getResponseCode();
                log.info("GET 응답코드 =============" + getResponseCode);

                if (getResponseCode == 200) {
                    log.info("#########성공");

                    BufferedReader getBr = new BufferedReader(new InputStreamReader(getConn.getInputStream()));
                    StringBuilder getResponseSb = new StringBuilder();
                    String getLine;
                    while ((getLine = getBr.readLine()) != null) {
                        getResponseSb.append(getLine).append("\n");
                    }
                    getBr.close();

                    String getResponse = getResponseSb.toString();
                    log.info("GET 응답 결과: " + getResponse);

                    JsonParser parser1 = new JsonParser();
                    JsonObject phoneJson1 = parser1.parse(getResponse).getAsJsonObject();

                    // 예금주만 값 빼기
                    String bankHolderInfo = phoneJson1.getAsJsonObject("response").get("bank_holder").getAsString();
                    log.info("bankHolderInfo: " + bankHolderInfo);

                    map.put("bankHolderInfo", bankHolderInfo);
                } else {
                    map.put("error", getResponseCode);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}
