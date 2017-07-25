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
<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="shortcut icon" href="./favicon.ico"/>
	<link type="text/css" rel="stylesheet" href="./style.css"/>
	<link type="text/css" rel="stylesheet" href="./reveal.css">
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/flotr2.min.js"></script>
	<script type="text/javascript" src="js/jquery.reveal.js"></script>
	<script type="text/javascript" src="js/jquery.confirm-1.3.js"></script>
	<script type="text/javascript" src="js/jquery.tablesorter.min.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<script>
		$(document).ready(function () {
			// call the tablesorter plugin
			$("table").tablesorter({
				// define a custom text extraction function
				sortList: [[4, 1]],
				textExtraction: function (node) {
					return node.innerHTML.replace(/\s+/g, '').replace('&nbsp;', '').replace(',', '.');
				}
			});
		});
	</script>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${appTitle }</title>
	<script type="text/javascript" src="js/analytics.js"></script>
</head>
<body>
<fmt:setLocale value="${user.locale }"/>
<fmt:setTimeZone value="${user.timeZone }"/>
<div id="addAccount" class="reveal-modal">
	<h1>${language['ACCOUNTS_HIDDEN_ADD']}</h1>
	<form id="addAccountId" name="addAccountName" autocomplete="on">
		<table>
			<tr>
				<td>${language['ACCOUNTS_HIDDEN_ACCOUNTNAME']}:</td>
				<td><input name="account" required placeholder="Name your account"></td>
			</tr>
			<tr>
				<td>${language['ACCOUNTS_HIDDEN_CURRENCY']}:</td>
				<td><select name="currency">
					<c:forEach var="cur" items="${currencies }">
						<option value="${cur}">${cur }</option>
					</c:forEach>
				</select>
				</td>
			<tr>
				<td>${language['ACCOUNTS_HIDDEN_LIQUIDITY']}:</td>
				<td><input name="liquidity" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
		</table>
		<input type="button" value="Confirm" onclick="javascript:checkForm('addAccountId', 'addAccount','refreshButton',addAcc)">
		<input id="refreshButton" type="submit" style="display: none;">
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>
<div id="modifyAccount" class="reveal-modal">
	<h1>Modify account</h1>
	<form id="modifyAccountId" name="modifyAccountName" autocomplete="on">
		<table>
			<tr>
				<td>Account Name:</td>
				<td><input name="account" id="account" required placeholder="Name your account"></td>
			</tr>
			<tr>
				<td>Currency:</td>
				<td><select name="currency" id="currency">
					<c:forEach var="cur" items="${currencies }">
						<option value="${cur}">${cur }</option>
					</c:forEach>
				</select>
				</td>
			<tr>
				<td>Liquidity:</td>
				<td><input name="liquidity" id="liquidity" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
		</table>
		<input id="accountId" name="accountId" type="hidden" value="">
		<input type="button" value="Confirm" onclick="javascript:checkForm('modifyAccountId', 'modifyAccount','refreshButton2',modifyAcc)">
		<input id="refreshButton2" type="submit" style="display: none;">
	</form>
	<form id="deteletAccountId" name="deletAccountName">
		or <a href="#" id="deleteAccount" onClick="if(confirm('Are you sure you want to delete this account?')) checkForm('deteletAccountId','modifyAccount','refreshButton3',deleteAcc)">delete</a>
		<input id="idDelete" name="accountId" type="hidden" value="">
		<input id="refreshButton3" type="submit" style="display: none;">
	</form>
</div>
<div id="container">
	<%@ include file="menu.html" %>
	<div class="main">
		<div class="equities_container">
			<h2 style="margin-top: 5px;">${language['ACCOUNTS_TITLE']}</h2>
			<c:if test="${!empty message}">
				<span class="cQuoteUp">${message}</span><br>
			</c:if>
			<c:if test="${!empty error}">
				<span class="cQuoteDown">${error}</span><br>
			</c:if>
			[<a href="#" data-reveal-id="addAccount">${language['ACCOUNTS_ADD']}</a>]
			<table id="tableAccounts" border="1" class="shadow tablesorter">
				<thead>
				<tr class="tBackGround" style="height: 50px;">
					<th class="bold" style="min-width: 150px">${language['ACCOUNTS_ACCOUNT']}</th>
					<th class="bold tdCenter" style="min-width: 90px">${language['ACCOUNTS_CURRENCY']}</th>
					<th class="bold tdCenter" style="min-width: 90px">${language['ACCOUNTS_LIQUIDITY']}</th>
					<th class="bold tdCenter" style="min-width: 90px">${language['ACCOUNTS_PARITY']}</th>
					<th class="bold tdCenter" style="min-width: 90px">${language['ACCOUNTS_VALUE']}&nbsp;${portfolio.currency }</th>
					<th class="bold tdCenter" style="min-width: 90px">${language['ACCOUNTS_OPTION']}</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="account" items="${portfolio.accounts }">
					<tr>
						<td class="">${account.name }</td>
						<td class="tdCenter">${account.currency }</td>
						<td class="tdCenter">${account.liquidity }</td>
						<td class="tdCenter"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="3" value="${account.parity }"/></td>
						<td class="tdCenter"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${account.parity * account.liquidity }"/></td>
						<td class="tdCenter">[<a href="#" data-reveal-id="modifyAccount"
												 onclick="javascript:updateAccount('${account.id}', '${account.name}', '${account.currency }', '${account.liquidity}');">${language['ACCOUNTS_MODIFY']}</a>]
						</td>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
				<tr>
					<td colspan="6">
				</tr>
				<tr class="static">
					<td class="bold">${language['ACCOUNTS_TOTAL']}</td>
					<td colspan="3"></td>
					<td class="tdCenter bold"><fmt:formatNumber type="number" value="${portfolio.liquidity }" maxFractionDigits="2" currencySymbol="${portfolio.currency.symbol }"/></td>
					<td></td>
				</tr>
				</tfoot>
			</table>
		</div>
	</div>
	<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2017</div>
</div>
</body>
</html>
