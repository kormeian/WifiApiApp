
package com.example.wifiapiapp;

import com.example.wifiapiapp.data.WifiInfoData;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class ApiExplorer {
    private static final String API_URL = "http://openapi.seoul.go.kr:8088/596e504f47686f6f31303148586d5163/json/TbPublicWifiInfo";
    public int DATA_SIZE;

    public ApiExplorer(){
        try {
            JsonObject object = this.init();
            this.DATA_SIZE = object.getAsJsonPrimitive("list_total_count").getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    JsonObject init() throws IOException {
        URL url = new URL(API_URL + "/1/1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }
        String str = rd.readLine();
        rd.close();
        conn.disconnect();

        return JsonParser.parseString(str).getAsJsonObject().getAsJsonObject("TbPublicWifiInfo");
    }

    public void saveApiData() throws IOException {
        ApiExplorer apiExplorer = new ApiExplorer();
        ArrayList<WifiInfoData> wifiInfoDataList = new ArrayList<WifiInfoData>();
        for (int i = 1; i <= apiExplorer.DATA_SIZE/1000+1; i++) {
            URL url = new URL(API_URL + "/" + Integer.toString(i*1000-999) + "/" + Integer.toString(i*1000));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            //System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;

            // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }
            String str = rd.readLine();
            rd.close();
            conn.disconnect();
            JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject().getAsJsonObject("TbPublicWifiInfo");
            JsonArray rows = jsonObject.getAsJsonArray("row");
            for (int j = 0; j < rows.size(); j++) {
                Gson gson = new Gson();
                Map<String, String> row = gson.fromJson(rows.get(j), Map.class);
                WifiInfoData wifiInfoData = new WifiInfoData();
                wifiInfoData.setMGR_NO(row.get("X_SWIFI_MGR_NO"));
                wifiInfoData.setWRDOFC(row.get("X_SWIFI_WRDOFC"));
                wifiInfoData.setMAIN_NM(row.get("X_SWIFI_MAIN_NM"));
                wifiInfoData.setADRES1(row.get("X_SWIFI_ADRES1"));
                wifiInfoData.setADRES2(row.get("X_SWIFI_ADRES2"));
                wifiInfoData.setINSTL_FLOOR(row.get("X_SWIFI_INSTL_FLOOR"));
                wifiInfoData.setINSTL_TY(row.get("X_SWIFI_INSTL_TY"));
                wifiInfoData.setINSTL_MBY(row.get("X_SWIFI_INSTL_MBY"));
                wifiInfoData.setSVC_SE(row.get("X_SWIFI_SVC_SE"));
                wifiInfoData.setCMCWR(row.get("X_SWIFI_CMCWR"));
                wifiInfoData.setCNSTC_YEAR(row.get("X_SWIFI_CNSTC_YEAR"));
                wifiInfoData.setINOUT_DOOR(row.get("X_SWIFI_INOUT_DOOR"));
                wifiInfoData.setREMARS3(row.get("X_SWIFI_REMARS3"));
                wifiInfoData.setLAT(row.get("LNT"));
                wifiInfoData.setLNT(row.get("LAT"));
                wifiInfoData.setWORK_DTTM(row.get("WORK_DTTM"));

                wifiInfoDataList.add(wifiInfoData);
            }
        }
        DataBase dataBase = new DataBase();
        dataBase.createWifiInfoTable();
        dataBase.saveWifiInfo(wifiInfoDataList);
        dataBase.closeConnection();
    }
}