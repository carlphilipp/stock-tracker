<!--
Copyright 2016 Carl-Philipp Harmant

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/jquery.reveal.js"></script>
	<script type="text/javascript" src="js/jquery.confirm-1.3.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="./style.css"/>
	<link type="text/css" rel="stylesheet" href="./reveal.css">
	<link rel="shortcut icon" href="./favicon.ico"/>
	<title>${appTitle }</title>
	<script type="text/javascript" src="js/analytics.js"></script>
</head>
<body>
<fmt:setLocale value="${user.locale }"/>
<fmt:setTimeZone value="${user.timeZone }"/>
<div id="refresh" class="reveal-modal">
	<h1>${language['CURRENCIES_HIDDEN_REFRESH']}</h1>
	<form id="sendRefresh" name="sendRefresh" autocomplete="on">
		<input type="button" value="${language['CURRENCIES_HIDDEN_CONFIRM']}"
			   onclick="javascript:checkForm('sendRefresh', 'refresh','refreshButton',refreshCurrency)">
		<input id="refreshButton" type="hidden" name="id" value="">
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>
<div id="container">
	<%@ include file="menu.html" %>
	<div class="main">
		<div class="equities_container">
			<h2 style="margin-top: 5px;">
				${language['CURRENCIES_TITLE']}&nbsp;<a href="#" data-reveal-id="refresh"><img alt=""
																							   src="image/refresh.png"
																							   style="border: 0"></a>
			</h2>
			<c:if test="${!empty message}">
				<span class="cQuoteUp">${message}</span><br>
			</c:if>
			<c:if test="${!empty error}">
				<span class="cQuoteDown">${error}</span><br>
			</c:if>
			${language['CURRENCIES_PORTFOLIOCURRENCY']}: ${portfolio.currency }<br>
			<table id="tableCurrency" border="1" class="shadow">
				<tr class="tBackGround">
					<td class="bold" width="180px">${language['CURRENCIES_CURRENCY']}</td>
					<td class="bold" width="80px">${portfolio.currency } / X</td>
					<td class="bold" width="80px">X / ${portfolio.currency }</td>
					<td class="bold">${language['CURRENCIES_LASTUPDATE']}</td>
				</tr>
				<c:forEach var="i" begin="0" end="${fn:length(tab) - 1}">
					<tr>
						<td class=""><span class="bold">${tab[i][0] }</span> (${tab[i][1] })</td>
						<td class="">${tab[i][2] }</td>
						<td class="">${tab[i][3] }</td>
						<td class=""><fmt:formatDate value="${tab[i][4] }" pattern="${user.datePattern }"/></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2016</div>
</div>
</body>
</html>
