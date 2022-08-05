<%@ page import="com.example.wifiapiapp.DataBase" %><%--
  Created by IntelliJ IDEA.
  User: kimse
  Date: 2022-07-30
  Time: 오전 6:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>위치 히스토리 목록</title>
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
<h1>위치 히스토리 목록</h1>
<div class="menu mb-16">
    <a href="index.jsp">홈</a> |
    <a href="positionHistory.jsp">위치 히스토리 목록</a> |
    <a href="getWifiInfo.jsp">Open API 와이파이 정보 가져오기</a>
</div>
<%
    DataBase dataBase = new DataBase();
    String delIdx = request.getParameter("delIdx");
    if (delIdx != null) {
        dataBase.deleteHistory(delIdx);
    }
%>
<table>
    <tr class="info">
        <th>ID</th>
        <th>X좌표</th>
        <th>Y좌표</th>
        <th>조회일자</th>
        <th>비고</th>
    </tr>
    <%
        if (dataBase.getHistorySize() > 0) {
            int historySize = dataBase.getHistorySize();
            dataBase.loadHistory();
            for (int i = 0; i < historySize; i++) {
                String[] historyArray = dataBase.getHistory();
                out.println("<tr class=\"data\">");
                for (int j = 0; j < 4; j++) {
                    out.println("<th>" + historyArray[j] + "</th>");
                }
                out.println("<th align=\"center\"><button type=\"button\" " +
                        "onclick=\"location.href=\'positionHistory.jsp?delIdx="
                        + historyArray[0] + "\' \">삭제</button></th>");
                out.println("</tr>");
            }
            dataBase.closeConnection();
        } else {
            out.println("<tr><th colspan=\"5\" align=\"center\">위치 조회 히스토리가 없습니다</th></tr>");
        }

    %>
</table>
</body>
</html>
