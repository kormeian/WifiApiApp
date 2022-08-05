<%@ page import="com.example.wifiapiapp.ApiExplorer" %><%--
  Created by IntelliJ IDEA.
  User: kimse
  Date: 2022-07-28
  Time: 오전 2:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>와이파이 정보 구하기</title>
</head>
<body>
<%
    ApiExplorer apiExplorer = new ApiExplorer();
    apiExplorer.saveApiData();
    out.println("<h1 align=\"center\">" + Integer.toString(apiExplorer.DATA_SIZE) + "개의 WIFI 정보를 정상적으로 저장하였습니다.</h1>");
%>
<div align="center">
    <a href="index.jsp">홈으로 가기</a>
</div>

</body>
</html>
