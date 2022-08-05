<%@ page import="com.example.wifiapiapp.DataBase" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>와이파이 정보 구하기</title>
    <style>
        .mb-16 {
            margin-bottom: 16px;
        }
        table {
            width: 100%;
            border-spacing: 0;
            /*border: 1px solid #ccc;*/
        }

        .info {
            background-color: #04AA6D;
            color: white;
            line-height: 2;
        }

        .data {
            /*height: 50px;*/
            line-height: 1.5;
        }

        .data:nth-child(2n) {
            background-color: #fff
        }

        .data:nth-child(2n+1) {
            background-color: #E7E9EB
        }

        th {
            border: 1px solid #e1e1e1;
        }

    </style>
</head>
<body>
<h1>와이파이 정보 구하기</h1>
<div class="menu mb-16">
    <a href="index.jsp">홈</a> |
    <a href="positionHistory.jsp">위치 히스토리 목록</a> |
    <a href="getWifiInfo.jsp">Open API 와이파이 정보 가져오기</a>
</div>
<%
    String lat = request.getParameter("lat");
    String lnt = request.getParameter("lnt");
    String wifiSearch = request.getParameter("wifiSearch");
    boolean isSearched = wifiSearch != null ? (wifiSearch.equals("true") ? true : false) : false;

    if (lat == null) {
        lat = "0.0";
        lnt = "0.0";
    }
%>
<div class="mb-16">
    <label for="lat">LAT:</label>
    <input id="lat" type="text" value=<%= lat %>>
    <label for="lnt">, LNT:</label>
    <input id="lnt" type="text" value=<%= lnt %>>
    <button type="button" onclick=getLocation()>내 위치 가져오기</button>
    <button type="button" onclick=searchWifiInfo()>근처 Wifi 정보 가져오기</button>
</div>
<table>
    <tr class="info">
        <th>거리(Km)</th>
        <th>관리번호</th>
        <th>자치구</th>
        <th>와이파이명</th>
        <th>도로명주소</th>
        <th>상세주소</th>
        <th>설치위치(층)</th>
        <th>설치유형</th>
        <th>설치기관</th>
        <th>서비스구분</th>
        <th>망종류</th>
        <th>설치년도</th>
        <th>실내외구분</th>
        <th>WIFI접속환경</th>
        <th>X좌표</th>
        <th>Y좌표</th>
        <th>작업일자</th>
    </tr>
    <%
        if (isSearched) {
            DataBase dataBase = new DataBase();
            dataBase.loadWifiInfo(lat, lnt);
            for (int i = 0; i < 20; i++) {
                String[] wifiArray = dataBase.getLoadedWifiInfo();
                out.println("<tr class=\"data\">");
                for (int j = 0; j < 17; j++) {
                    out.println("<th>" + wifiArray[j] + "</th>");
                }
                out.println("</tr>");
            }
            dataBase.insertHistory(lat, lnt);
            //sqlManager.appendHistory(yData,xData);
            dataBase.closeConnection();
        } else {
            out.println("<tr class=\"data\"><th colspan=\"17\" align=\"center\">위치 정보를 입력한 후에 조회해 주세요</th></tr>");
        }
    %>
</table>

<script>
    function searchWifiInfo() {
        const lat = document.getElementById("lat").value;
        const lnt = document.getElementById("lnt").value;
        if (lat === "" || lnt === "") {
            alert("위치정보를 입력하세요");
            return;
        }
        location.href = "index.jsp?lat=" + lat + "&lnt=" + lnt + "&wifiSearch=true";
    }

    function success({coords, timestamp}) {
        const lat = coords.latitude;
        const lnt = coords.longitude;
        location.href = "index.jsp?lat=" + lat + "&lnt=" + lnt + "&wifiSearch=false";
    }

    function getLocation() {

        if (!navigator.geolocation) {
            alert("위치 정보가 조회되지 않습니다.");
        }
        navigator.geolocation.getCurrentPosition(success);
    }
</script>
</body>
</html>