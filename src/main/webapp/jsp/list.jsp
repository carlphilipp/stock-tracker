<!--
Copyright 2017 Carl-Philipp Harmant

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
	<script type="text/javascript" src="js/analytics.js"></script>
	<title>${appTitle }</title>
</head>
<body>
<div id="refresh" class="reveal-modal">
	<h1>${language['LIST_HIDDEN_REFRESH']}</h1>
	<form id="sendRefresh" name="sendRefresh" autocomplete="on">
		<input type="button" value="${language['LIST_HIDDEN_CONFIRM']}"
			   onclick="javascript:checkForm('sendRefresh', 'refresh','refreshButton',refreshList)">
		<input id="refreshButton" type="hidden" name="id" value="">
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>
<div id="addFollow" class="reveal-modal">
	<h1>${language['LIST_HIDDEN_ADD']}</h1>
	<form id="sendFollow" name="sendFollow" autocomplete="on">
		<table>
			<tr>
				<td>${language['LIST_HIDDEN_YAHOOID']}:</td>
				<td><input name="ticker" type="text" required="required" autofocus placeholder="Ex: GOOG"></td>
			</tr>
			<tr>
				<td>${language['LIST_HIDDEN_LOWERLIMIT']}:</td>
				<td><input name="lower" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
			<tr>
				<td>${language['LIST_HIDDEN_HIGHERLIMIT']}:</td>
				<td><input name="higher" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="button" value="Add"
									   onclick="javascript:checkForm('sendFollow','addFollow','hiddenButtonFollow',addFollow)">
					<input id="hiddenButtonFollow" type="submit" style="display: none;" value=""></td>
			</tr>
		</table>
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>
<div id="modifyFollow" class="reveal-modal">
	<h1>${language['LIST_HIDDEN_MODIFY']}</h1>
	<form id="sendModifyFollow" name="sendModifyFollow" autocomplete="on">
		<table>
			<tr>
				<td>${language['LIST_HIDDEN_YAHOOID']}:</td>
				<td><span id="modifyFollowYahooId"></span><input id="ticker" name="ticker" type="hidden" value=""></td>
			</tr>
			<tr>
				<td>${language['LIST_HIDDEN_LOWERLIMIT']}:</td>
				<td><input id="lower" name="lower" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?">
				</td>
			</tr>
			<tr>
				<td>${language['LIST_HIDDEN_HIGHERLIMIT']}:</td>
				<td><input id="higher" name="higher" type="text" pattern="\d+(\.\d+)?"
						   placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="button" value="${language['LIST_HIDDEN_CONFIRM']}"
									   onclick="javascript:checkForm('sendModifyFollow','modifyFollow','hiddenButtonModifyFollow',modifyFollow)">
					<input id="hiddenButtonModifyFollow" type="submit" style="display: none;" value=""></td>
			</tr>
		</table>
	</form>
	<form name="sendFollowDelete" id="sendFollowDelete">
		or <a href="#" id="deleteFollow"
			  onClick="if(confirm('Are you sure you want to delete this company from your list?')) execFunWithTimeout(checkForm('sendFollowDelete','deleteFollow','processDeleteFollow',deleteFollow))">delete</a>
		<input name="delete" type="hidden" value="true">
		<input id="processDeleteFollow" type="submit" style="display: none;">
		<input id="deleteFollowId" name="deleteFollowId" type="hidden" name="id" value="">
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>
<fmt:setLocale value="${user.locale }"/>
<div id="container">
	<%@ include file="menu.html" %>
	<div class="main">
		<div class="share_container">
			<h2 style="margin-top: 5px;">${language['LIST_TITLE']}&nbsp;<a href="#" data-reveal-id="refresh"><img alt=""
																												  src="image/refresh.png"
																												  style="border: 0"></a>
			</h2>
			<div id="clear" class="clear">
				<c:if test="${!empty message}">
					<span class="cQuoteUp">${message}</span><br>
				</c:if>
				<c:if test="${!empty error}">
					<span class="cQuoteDown">${error}</span><br>
				</c:if>
				<c:if test="${!empty updateStatus}">
					${updateStatus}<br>
				</c:if>
			</div>
			[<a href="#" data-reveal-id="addFollow">${language['LIST_ADD']}</a>]
			<table id="tableList" border="1" class="shadow">
				<tr class="tBackGround">
					<td class="bold">${language['LIST_DATE']}</td>
					<td class="bold">${language['LIST_COMPANY']}</td>
					<td class="bold tdCenter">${language['LIST_QUOTE']}</td>
					<td class="bold tdCenter">${language['LIST_LOWMAXYEAR']}</td>
					<td class="bold tdCenter">${language['LIST_YIELD']}</td>
					<td class="bold tdCenter">${language['LIST_LOWERLIMIT']}</td>
					<td class="bold tdCenter">${language['LIST_HIGHERLIMIT']}</td>
				</tr>
				<c:forEach var="follow" items="${follows}">
					<tr>
						<td><fmt:formatDate value="${follow.company.lastUpdate }" pattern="yyyy-MM-dd"/><br>
							<fmt:formatDate value="${follow.company.lastUpdate }"
											pattern="HH:mm"/></td>
						<td><span class="bold">${follow.company.name}</span><br>(${follow.company.yahooId}) [<a
							href="javascript:poufpouf('${follow.company.yahooId}')">${language['LIST_INFO']}</a>]
							[<a href="#" data-reveal-id="modifyFollow"
								onclick="javascript:updateFollow('${follow.id }', '${follow.company.yahooId}','${follow.lowerLimit }','${follow.higherLimit }')">${language['LIST_MODIFY']}</a>]
							<span id="${follow.company.yahooId}" class="companyInfo"
								  style="display: none;"> <br> ${follow.company.sector}<br> ${follow.company.industry}<br>
									${follow.company.marketCapitalization}
							</span></td>
						<td class="tdRight">${follow.company.quote }<br>
							<c:choose>
								<c:when test="${empty follow.company.changeInPercent}">
									<span class="cQuoteDown">-</span>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${fn:startsWith(follow.company.changeInPercent, '-')}">
											<span class="cQuoteDown">${follow.company.changeInPercent }</span>
										</c:when>
										<c:otherwise>
												<span class="cQuoteUp">${follow.company.changeInPercent }
												</span>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>

						</td>
						<td class="tdRight"><c:set var="val"
												   value="${follow.company.quote * 25 /follow.company.yearHigh }"/>
							<table id="graphQuote" style="margin: auto;">
								<tr>
									<td align="right" width="45px">${follow.company.yearLow }&nbsp;</td>
									<c:forEach var="entry" begin="1" end="25">
										<c:choose>
											<c:when test="${val < entry }">
												<td class="graph empty"></td>
											</c:when>
											<c:otherwise>
												<td class="graph full"></td>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									<td align="left">&nbsp;${follow.company.yearHigh }</td>
								</tr>
							</table>
						</td>
						<td class="tdRight">
								${follow.company.yield }
						</td>
						<td class="tdRight"><c:if test="${!empty follow.lowerLimit}">
							<fmt:formatNumber type="number" minFractionDigits="3" value="${follow.lowerLimit}"/>
							<br>
							<c:choose>
								<c:when test="${follow.gapLowerLimit < 5 }">
									<span class="cQuoteUp"><fmt:formatNumber type="number" maxFractionDigits="1"
																			 value="${follow.gapLowerLimit}"/>%</span>
								</c:when>
								<c:otherwise>
									<fmt:formatNumber type="number" maxFractionDigits="1"
													  value="${follow.gapLowerLimit }"/>%
								</c:otherwise>
							</c:choose>
						</c:if></td>
						<td class="tdRight"><c:if test="${!empty follow.higherLimit}">
							<fmt:formatNumber type="number" minFractionDigits="3" value="${follow.higherLimit}"/>
							<br>
							<c:choose>
								<c:when test="${follow.gapHigherLimit < 5 }">
									<span class="cQuoteDown"><fmt:formatNumber type="number" maxFractionDigits="1"
																			   value="${follow.gapHigherLimit }"/>%</span>
								</c:when>
								<c:otherwise>
									<fmt:formatNumber type="number" maxFractionDigits="1"
													  value="${follow.gapHigherLimit }"/>%
								</c:otherwise>
							</c:choose>
						</c:if></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2016</div>
</div>
</body>
</html>
