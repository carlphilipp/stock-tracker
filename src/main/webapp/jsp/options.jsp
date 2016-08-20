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
<!DOCTYPE html>
<html lang="en">
<head>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/jquery.reveal.js"></script>
	<script type="text/javascript" src="js/jquery.confirm-1.3.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<script>
		function showNextOrNot() {
			if (document.getElementById("autoUpdate").checked) {
				pouf("updateSpan");
			} else {
				poufpoufpouf("updateSpan");
			}
		}
	</script>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="./style.css"/>
	<link type="text/css" rel="stylesheet" href="./reveal.css">
	<link rel="shortcut icon" href="./favicon.ico"/>
	<title>${appTitle }</title>
	<script type="text/javascript" src="js/analytics.js"></script>
</head>
<body>
<fmt:setLocale value="${user.locale }"/>
<div id="container">
	<%@ include file="menu.html" %>
	<div id="modify" class="reveal-modal">
		<h1>${language['OPTIONS_HIDDEN_TITLE']} </h1>
		<form id="modifyOptionId" name="modifyOptionId" autocomplete="on" action="add">
			<table>
				<tr>
					<td>${language['OPTIONS_HIDDEN_CURRENCY']}:</td>
					<td><select name="currency">
						<c:forEach var="cur" items="${currencies }">
							<c:choose>
								<c:when test="${cur == portfolio.currency}">
									<option value="${cur}" selected>${cur }</option>
								</c:when>
								<c:otherwise>
									<option value="${cur}">${cur }</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>${language['OPTIONS_HIDDEN_FORMAT']}:</td>
					<td><select name="format">
						<c:if test="${user.locale == 'fr_FR' }">
							<c:set var="frSelected" value="selected"/>
						</c:if>
						<c:if test="${user.locale == 'en_US'  }">
							<c:set var="usSelected" value="selected"/>
						</c:if>
						<option value="fr_FR" ${frSelected }>fr_FR</option>
						<option value="en_US" ${usSelected }>en_US</option>
					</select></td>

				</tr>
				<tr>
					<td>${language['OPTIONS_HIDDEN_TIMEZONE']}:</td>
					<td><select name="timeZone">
						<c:forEach var="ti" items="${timeZone }">
							<c:choose>
								<c:when test="${ti == user.timeZone}">
									<option value="${ti}" selected>${ti }</option>
								</c:when>
								<c:otherwise>
									<option value="${ti}">${ti }</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>${language['OPTIONS_HIDDEN_DATEPATTERN']}:</td>
					<td><select name="datePattern">
						<c:if test="${user.datePattern == 'dd/MM/yyyy HH:mm' }">
							<c:set var="pattern1" value="selected"/>
						</c:if>
						<c:if test="${user.datePattern == 'yyyy-MM-dd HH:mm'  }">
							<c:set var="pattern2" value="selected"/>
						</c:if>
						<option value="dd/MM/yyyy HH:mm" ${pattern1 }>dd/MM/yyyy HH:mm</option>
						<option value="yyyy-MM-dd HH:mm" ${pattern2 }>yyyy-MM-dd HH:mm</option>
					</select></td>
				</tr>
				<tr>
					<td valign="top">Auto update history everyday:</td>
					<td>
						<c:choose>
							<c:when test="${empty user.updateHourTime}">
								<c:set var="show" value="none"/>
								<c:set var="autoUpdate" value=""/>
							</c:when>
							<c:otherwise>
								<c:set var="show" value="inline"/>
								<c:set var="autoUpdate" value="checked"/>
							</c:otherwise>
						</c:choose>
						<input type="checkbox" id="autoUpdate" name="autoUpdate"
							   onclick="showNextOrNot();" ${autoUpdate }/> Activate<br>
						<span id="updateSpan" style="display: ${show};">
								At
									<select id="updateTime" name="updateTime">
										<c:forEach var="i" begin="0" end="23">
											<c:choose>
												<c:when test="${i == user.updateHourTime }">
													<c:set var="updateTimeSelect" value="SELECTED"/>
												</c:when>
												<c:otherwise>
													<c:set var="updateTimeSelect" value=""/>
												</c:otherwise>
											</c:choose>
											<option value="${i}" ${updateTimeSelect}>${i }h</option>
										</c:forEach>
									</select>
									(in your time zone)
								<br>
								<c:if test="${!empty user.updateSendMail }">
									<c:choose>
										<c:when test="${!user.updateSendMail}">
											<c:set var="autoUpdateEmail" value=""/>
										</c:when>
										<c:otherwise>
											<c:set var="autoUpdateEmail" value="checked"/>
										</c:otherwise>
									</c:choose>
								</c:if>
								<input type="checkbox" name="autoUpdateEmail" id="autoUpdateEmail" ${autoUpdateEmail }/> Send me an email if it fails
							</span>

					</td>
				</tr>
				<tr>
					<td valign="top">${language['OPTIONS_HIDDEN_COLUMNS']}:</td>
					<td>
						<table>
							<tr>
								<td><input name="quote" type="checkbox" ${quote }> ${language['OPTIONS_HIDDEN_QUOTE']}
								</td>
								<td><input name="currency2"
										   type="checkbox" ${currency }> ${language['OPTIONS_HIDDEN_CURRENCY']}</td>
							</tr>
							<tr>
								<td><input name="parity"
										   type="checkbox" ${parity}> ${language['OPTIONS_HIDDEN_PARITIES']}</td>
								<td><input name="yield1"
										   type="checkbox" ${yield1}> ${language['OPTIONS_HIDDEN_YIELDTTM']}</td>
							</tr>
							<tr>
								<td><input name="yield2"
										   type="checkbox" ${yield2 }> ${language['OPTIONS_HIDDEN_YIELDPERUNITCOSTPRICE']}
								</td>
								<td><input name="stopLoss"
										   type="checkbox" ${stopLoss }> ${language['OPTIONS_HIDDEN_STOPLOSS']}</td>
							</tr>
							<tr>
								<td><input name="objective"
										   type="checkbox" ${objective }> ${language['OPTIONS_HIDDEN_OBJECTIVE']}</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td><input type="button" value="Modify"
							   onclick="javascript:checkForm('modifyOptionId', 'modify', 'processSendOption', modifyOption);">
						<input
							id="processSendOption" type="submit" style="display: none;"></td>
				</tr>
			</table>
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>
	<div class="main">
		<div class="equities_container">
			<h2 style="margin-top: 5px;">
				${language['OPTIONS_TITLE']}&nbsp;<a href="#" data-reveal-id="modify"><img alt="Edit"
																						   src="image/edit.png"
																						   style="border: 0"></a>
			</h2>
			<c:if test="${!empty updated}">
				<span class="cQuoteUp">${updated}</span>
				<br>
			</c:if>

			<span class="bold">${language['OPTIONS_DEFAUTCURRENCY']}:</span> ${portfolio.currency }<br>
			<span class="bold">${language['OPTIONS_FORMAT']}:</span> ${user.locale }<br>
			<span class="bold">${language['OPTIONS_TIMEZONE']}:</span> ${user.timeZone }<br>
			<span class="bold">${language['OPTIONS_DATEPATTERN']}:</span> ${user.datePattern }<br>
			<span class="bold">Auto update:</span>
			<c:choose>
				<c:when test="${empty user.updateHourTime}">
					Desactivated<br>
				</c:when>
				<c:otherwise>
					Activated at ${user.updateHourTime }h everyday.
					<c:choose>
						<c:when test="${user.updateSendMail}">
							Error email will be send
						</c:when>
						<c:otherwise>
							No error email will be send.
						</c:otherwise>
					</c:choose>
					<br>
				</c:otherwise>
			</c:choose>

			<span class="bold">
					${language['OPTIONS_PORTFOLIOCOLUMN']}:</span>
			<c:if test="${quote == 'checked'}">
				Quote
			</c:if>
			<c:if test="${currency == 'checked'}">
				Currency
			</c:if>
			<c:if test="${parity == 'checked'}">
				Parities
			</c:if>
			<c:if test="${stopLoss == 'checked'}">
				Stoploss
			</c:if>
			<c:if test="${objective == 'checked'}">
				Objective
			</c:if>
			<c:if test="${yield1 == 'checked'}">
				Yield TTM/year
			</c:if>
			<c:if test="${yield2 == 'checked'}">
				Yield per unit cost price/year
			</c:if>
			<br> <br>
			<table id="tableOption" class="shadow" border="1">
				<tr>
					<td>
						<form action="createhistory" method="post" enctype="multipart/form-data">
							${language['OPTIONS_LOADHISTORY']}: <input type="file" name="file" size="chars" required>
							<br> ${language['OPTIONS_LIQUIDITY']}: <input type="text" name="liquidity" value="0.0"
																		  required>
							<br> ${language['OPTIONS_ACCOUNT']}: <select name="account">
							<c:forEach var="acc" items="${portfolio.accounts }">
								<option value="${acc.name}">${acc.name } - ${acc.currency.symbol }</option>
							</c:forEach>
						</select>
							<br> <input type="submit"
										value="${language['OPTIONS_LOAD']}">
						</form>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2016</div>
</div>
</body>
</html>
