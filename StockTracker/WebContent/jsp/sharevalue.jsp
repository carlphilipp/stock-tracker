<!--  
  Copyright 2013 Carl-Philipp Harmant
 
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
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/flotr2.min.js"></script>
<script type="text/javascript" src="js/jquery.reveal.js"></script>
<script type="text/javascript" src="js/jquery.confirm-1.3.js"></script>
<script type="text/javascript" src="js/base.js"></script>
<script type="text/javascript">
	function refresh() {
		document.sendRefreshShare.action = "updatesharevalue";
		document.sendRefreshShare.method = "post";
		document.sendRefreshShare.submit();
	}
	function update() {
		document.updateShareFormName.action = "updatesharevalue?commentary=true";
		document.updateShareFormName.method = "post";
		document.updateShareFormName.submit();
	}
	function execFunWithTimeout(func) {
		setTimeout(func, 350);
	}

	function formDelete(formz) {
		document.getElementById(formz).submit();
	}
	function deleteConfirm(shareId) {
		var retVal = confirm("Are you sure ?");
		if (retVal == true) {
			formDelete(shareId);
		}
	}
	function checkForm(formId, divId, buttonId, func) {
		if ($('#' + formId)[0].checkValidity()) {
			$('#' + divId).trigger('reveal:close');
			setTimeout(func, 350);
		} else {
			$('#' + buttonId).click();
		}
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="./style.css" />
<link type="text/css" rel="stylesheet" href="./reveal.css">
<link rel="shortcut icon" href="./favicon.ico" />
<title>${appTitle }</title>
<script type="text/javascript" src="js/analytics.js"></script>
</head>
<body>
	<fmt:setLocale value="${user.locale }" />
	<fmt:setTimeZone value="${user.timeZone }"/>
	<div id="refreshShare" class="reveal-modal">
		<h1>${language['HISTORY_HIDDEN_REFRESH']}</h1>
		<form id="sendRefreshShare" name="sendRefreshShare" autocomplete="on">
			<table>
				<tr>
					<td>${language['HISTORY_HIDDEN_ACCOUNT']}:</td>
					<td>
						<select name="account">
								<c:forEach var="acc" items="${portfolio.accounts }">
									<option value="${acc.id}">${acc.name } - ${acc.currency.symbol }</option>
								</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td>${language['HISTORY_HIDDEN_MOVEMENT']}:</td>
					<td><input type="text" name="movement" value="0.0" required pattern="\-?\d+(\.\d+)?" placeholder="Pattern: \-?\d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['HISTORY_HIDDEN_YIELD']}:</td>
					<td><input type="text" name="yield" value="0.0" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['HISTORY_HIDDEN_BUY']}:</td>
					<td><input type="text" name="buy" value="0.0" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['HISTORY_HIDDEN_SELL']}:</td>
					<td><input type="text" name="sell" value="0.0" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['HISTORY_HIDDEN_TAXE']}:</td>
					<td><input type="text" name="taxe" value="0.0" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['HISTORY_HIDDEN_COMMENTARY']}:</td>
					<td><input type="text" name="commentary"></td>
				</tr>
			</table>
			<input type="button" value="${language['HISTORY_HIDDEN_UPDATE']}" onclick="javascript:execFunWithTimeout(checkForm('sendRefreshShare','refreshShare','processRefreshShare',refresh))"> <input
				id="processRefreshShare" type="submit" style="display: none;">
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>
	
	<div id="updateShare" class="reveal-modal">
		<h1>Update commentary</h1>
		<form id="updateShareFormId" name="updateShareFormName" autocomplete="on">
			<table>
				<tr>
					<td>Commentary:</td>
					<td><input type="text" name="commentaryUpdated" id="commentaryShare"></td>
				</tr>
			</table>
			<input id="shareId" type="hidden" name="shareId" value="">
			<input type="button" value="Update" onclick="javascript:execFunWithTimeout(checkForm('updateShareFormId','updateShare','processRefreshShare2',update))"> <input
				id="processRefreshShare2" type="submit" style="display: none;">
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>

	<div id="container">
		<%@ include file="menu.html"%>
		<div class="main">
			<div class="share_container">
				<div class="floatLeft">	
					<h2 style="margin-top: 5px;">${language['HISTORY_TITLE']}</h2>
				</div>
				
				<table id="tableHistoryTotal" border="1" class="shadow">
					<tr><td align="center">
						<span class="bold liquidity">Best performance</span><br>
						<span class="bold cQuoteUp"><fmt:formatNumber type="number" maxFractionDigits="2" value="${portfolio.maxShareValue }" /></span> 
						- <fmt:formatDate value="${portfolio.maxShareValueDate }" pattern="${user.datePatternWithoutHourMin }" />
					</td></tr>
				</table>
				
				<div class="clear">
					<c:if test="${!empty message}">
						<span class="cQuoteUp">${message }</span>
						<br>
					</c:if>
					<c:if test="${!empty error}">
						<span class="cQuoteDown">${error }</span>
						<br>
					</c:if>
					<c:if test="${!empty warn}">
						<span class="cQuoteOrange">${warn }</span>
						<br>
					</c:if>
					[<a href="#" data-reveal-id="refreshShare">${language['HISTORY_UPDATE']}</a>]
				</div>

				<table id="shareValueTable" border="1" class="shadow">
					<tr class="tBackGround">
						<td class="bold" style="max-width: 140px; min-width: 140px">${language['HISTORY_DATE']}</td>
						<td class="bold tdCenter" style="max-width: 60px; min-width: 60px">${language['HISTORY_ACCOUNT']}</td>
						<td class="bold tdCenter" style="max-width: 80px; min-width: 80px">${language['HISTORY_LIQUIDITYMOVEMENT']}</td>
						<td class="bold tdCenter" style="max-width: 70px; min-width: 70px">${language['HISTORY_DIVIDENDS']}</td>
						<td class="bold tdCenter" style="max-width: 70px; min-width: 70px">${language['HISTORY_BUY']}</td>
						<td class="bold tdCenter" style="max-width: 70px; min-width: 70px">${language['HISTORY_SELL']}</td>
						<td class="bold tdCenter" style="max-width: 50px; min-width: 50px">${language['HISTORY_TAXE']}</td>
						<td class="bold tdCenter" style="max-width: 90px; min-width: 90px">${language['HISTORY_PORTFOLIOVALUE']}</td>
						<td class="bold tdCenter" style="max-width: 60px; min-width: 60px">${language['HISTORY_SHAREQUANTITY']}</td>
						<td class="bold tdCenter" style="max-width: 70px; min-width: 70px">${language['HISTORY_SHAREVALUE']}</td>
						<td class="bold tdCenter" style="max-width: 60px; min-width: 60px">${language['HISTORY_MONTHLYYIELD']}</td>
						<td class="bold tdCenter" style="max-width: 140px; min-width: 140px">${language['HISTORY_COMMENTARY']}</td>
						<td class="bold tdCenter" style="max-width: 45px; min-width: 45px">${language['HISTORY_OPTION']}</td>
					</tr>
					<c:if test="${fn:length(portfolio.shareValues) > 0 }">
						<c:forEach var="i" begin="${begin }" end="${end }">
							<c:set var="share" value="${portfolio.shareValues[i] }" />
							<tr>
								<td class=""><fmt:formatDate value="${share.date }" pattern="${user.datePattern }" /> [<a href="javascript:poufpouf('shareId${share.id}')">${language['HISTORY_DETAILS']}</a>]</td>
								<td class="tdCenter">
									<c:if test="${!empty share.account.name }">
										${share.account.name }
									</c:if>
								</td>
								<td class="tdCenter"><c:if test="${share.liquidityMovement != 0}">
										<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${share.liquidityMovement }" />
									</c:if></td>
								<td class="tdCenter"><c:if test="${share.yield != 0}">
										<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${share.yield }" />
									</c:if></td>
								<td class="tdCenter"><c:if test="${share.buy != 0}">
										<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${share.buy }" />
									</c:if></td>
								<td class="tdCenter"><c:if test="${share.sell != 0}">
										<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${share.sell }" />
									</c:if></td>
								<td class="tdCenter" >
									<c:if test="${share.taxe != 0}">
										<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${share.taxe }" />
									</c:if>
								</td>
								<td class="tdCenter"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="3" value="${share.portfolioValue} " /></td>
								<td class="tdCenter"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="3" value="${share.shareQuantity }" /></td>
								<td class="tdCenter"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="3" value="${share.shareValue }" /></td>
								<td class="tdCenter"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="3" value="${share.monthlyYield }" /></td>
								<td class="tdCenter">
									<span id="commentary${share.id }" class="shareCommentary">${share.commentary }
									<a href="#" data-reveal-id="updateShare" onclick="javascript:updateShare('${share.id}', '${share.commentary }')"> <img width="10px" alt="" src="image/edit.png" style="border: 0"></a>
									</span>
								</td>
								<td class="tdCenter">
									<form action="deletesharevalue" method="post" id="share${share.id }">
										<input type="hidden" name="shareId" value="${share.id }" /> <input type="hidden" name="liquidityMovement" value="${share.liquidityMovement }" /> <input
											type="hidden" name="yield" value="${share.yield }" /> <input type="hidden" name="buy" value="${share.buy }" /> <input type="hidden" name="sell"
											value="${share.sell }" /> <input type="hidden" name="taxe" value="${share.taxe }" /> <input type="hidden" name="account" value="${share.account.name }" /><a href="javascript:deleteConfirm('share${share.id }')">${language['HISTORY_DELETE']}</a>
									</form>
								</td>
							</tr>
							<tr id="shareId${share.id}" style="display: none;">
								<td colspan="13">${share.details }</td>
							</tr>
						</c:forEach>
					</c:if>
				</table>
				<p align="center">
					<c:forEach var="j" begin="1" end="${nbPage }">
						<c:choose>
							<c:when test="${j==page}">${j }&nbsp;</c:when>
							<c:otherwise>
								<a href="sharevalue?page=${j }">${j }</a>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					[<a href="sharevalue?page=0">${language['HISTORY_ALL']}</a>]
				</p>
			</div>
		</div>
		<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2013</div>
	</div>
</body>
</html>
