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
<script type="text/javascript" src="js/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="js/base.js"></script>
<script type="text/javascript">
$(document).ready(function() { 
    // call the tablesorter plugin 
    $("table").tablesorter({ 
        // define a custom text extraction function 
        sortList: [[0,0]],
        textExtraction: function(node) { 	
		var size = node.childNodes.length;
		if(size == 5){
			var percent = node.childNodes[3].innerHTML;
			return parseFloat(percent.replace('%','').replace(',','.'));
	    	}else{
			if(size == 3){
				var val = parseFloat(node.firstChild.nodeValue.replace(/\s+/g, '').replace(',', '').replace('&nbsp;', ''));
				if(!isNaN(val)){
 					return val;
				}else{
					return parseFloat(node.childNodes[1].innerHTML.replace(/\s+/g, '').replace(',', '').replace('&nbsp;', ''));
				}
			}else{
				if(size == 9){
					//console.log("GO " + node.childNodes[0].innerHTML);
					return node.childNodes[0].innerHTML.replace(/\s+/g, '').replace('&nbsp;', '');
				}else{
					//console.log(node.innerHTML + " " + size);
            				return node.innerHTML.replace(/\s+/g, '').replace('&nbsp;', '').replace(',', '.');
				}
			}
	    	}
        } 
    }); 
});  
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
	<div id="addEquity" class="reveal-modal">
		<h1>${language['PORTFOLIO_HIDDEN_ADD']}</h1>
		<form id="sendEquity" name="sendEquity" autocomplete="on" action="add">
		<table>
			<tr>
				<td>${language['PORTFOLIO_HIDDEN_YAHOOID']}: </td><td><input name="ticker" type="text" required autofocus placeholder="Ex: GOOG"></td>
			</tr><tr>
				<td>${language['PORTFOLIO_HIDDEN_QUANTITY']}: </td><td><input name="quantity" type="text" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr><tr>
				<td>${language['PORTFOLIO_HIDDEN_UNITCOSTPRICE']}: </td><td><input name="unitCostPrice" type="text" required pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
			<tr>
				<td>${language['PORTFOLIO_HIDDEN_PERSONALPARITY']}:</td>
				<td><input name="parityPersonal" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
			</tr>
			<tr>
				<td><input type="button" value="Add" onclick="javascript:execFunWithTimeout(checkForm('sendEquity', 'addEquity', 'processSendEquity', addEquity))">
				<input id="processSendEquity" type="submit" style="display: none;"></td>
				<td></td>
			</tr>
		</table>
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>
	<div id="modifyEquity" class="reveal-modal">
		<h1>${language['PORTFOLIO_HIDDEN_MODIFYEQUITY']}</h1>
		<form name="sendEquityModify" id="sendEquityModify" autocomplete="on">
			<table>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_YAHOOID']}: </td>
					<td><span id="modifyTicker"></span><input type="hidden" id="modifyTickerHidden" name="ticker" value=""></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_CUSTOMIZENAME']}:</td>
					<td><input id="modifyNamePersonal" name="namePersonal" type="text" placeholder="Custom Company Name" pattern="[\w\d _\-àèé/%\.,&\(\)]+"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_CUSTOMIZESECTOR']}:</td>
					<td><input id="modifySectorPersonal" name="sectorPersonal" type="text" placeholder="Custom Sector"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_CUSTOMIZEINDUSTRY']}:</td>
					<td><input id="modifyIndustryPersonal" name="industryPersonal" type="text" placeholder="Custom Industry"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_CUSTOMIZEMARKETCAP']}:</td>
					<td><input id="modifyMarketCapPersonal" name="marketCapPersonal" type="text" pattern="\d+(\.\d+)?[BM]" placeholder="Pattern: \d+(\.\d+)?[BM]"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_QUANTITY']}:</td>
					<td><input id="modifyQuantity" name="quantity" type="text" pattern="\d+(\.\d+)?" required autofocus></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_UNITCOSTPRICE']}:</td>
					<td><input id="modifyUnitCostPrice" name="unitCostPrice" type="text" required></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_DEFAUTPARITY']}:</td>
					<td><span id="modifyParity"></span></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_PERSONALPARITY']}:</td>
					<td><input id="modifyParityPersonal" name="modifyParityPersonal" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_YAHOOYIELD']}:</td>
					<td><span id="modifyYieldYahoo"></span></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_PERSONALYIELD']}:</td>
					<td> <input id="modifyYieldPersonal" name="yieldPersonal" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_STOPLOSS']}:</td>
					<td><input id="modifyStopLoss" name="stopLoss" type="text" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td>${language['PORTFOLIO_HIDDEN_OBJECTIVE']}:</td>
					<td><input type="text" id="modifyObjective" name="objective" pattern="\d+(\.\d+)?" placeholder="Pattern: \d+(\.\d+)?"></td>
				</tr>
				<tr>
					<td><input type="button" value="${language['PORTFOLIO_HIDDEN_MODIFY']}" onclick="javascript:execFunWithTimeout(checkForm('sendEquityModify', 'modifyEquity', 'processModifyEquity', modifyEquity))">
					<input id="processModifyEquity" type="submit" style="display: none;">
					</td><td></td>
				</tr>
			</table>
			</form>
			<form name="sendEquityDelete" id="sendEquityDelete">
			${language['PORTFOLIO_HIDDEN_OR']}&nbsp;<a href="#" id="deleteEquity" onClick="if(confirm('${language['PORTFOLIO_HIDDEN_DELETECONFIRM']}')) execFunWithTimeout(checkForm('sendEquityDelete', 'modifyEquity', 'processDeleteEquity', deleteEquity))">${language['PORTFOLIO_HIDDEN_DELETE']}</a>
			<input name="delete" type="hidden" value="true">
			<input id="processDeleteEquity" type="submit" style="display: none;">
			<input id="deleteTicker" type="hidden" name="id" value="">
			
			</form>
			<a class="close-reveal-modal">&#215;</a>
	</div>
	<div id="refresh" class="reveal-modal">
		<h1>${language['PORTFOLIO_HIDDEN_REFRESH']}</h1>
		<form id="sendRefresh" name="sendRefresh" autocomplete="on">
			<input name="currencyUpdate" type="checkbox"> ${language['PORTFOLIO_HIDDEN_UPDATECURRENCIES']}<br><br>
			<input type="button" value="Confirm" onclick="javascript:checkForm('sendRefresh', 'refresh','refreshButton',refresh)">
			<input id="refreshButton" type="hidden" name="id" value="">
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>

	<div id="container">
		<%@ include file="menu.html"%>
		<div class="main">
			<div class="equities_container">
				<div class="floatLeft">
					<h2 style="margin-top: 5px;display:inline;">
						${language['PORTFOLIO_TITLE']}&nbsp;<a href="#" data-reveal-id="refresh"><img alt="Refresh" src="image/refresh.png" style="border: 0"></a>
					</h2>
						<br>Last update: <fmt:formatDate value="${portfolio.lastCompanyUpdate}" pattern="${user.datePattern }"/>

				</div>
				<c:if test="${fn:length(portfolio.equities) != 0}">
				<table id="tableEquityTotal" border="1" class="shadow">
				<tr><td class="center">
				<span class="totalValue"><fmt:formatNumber type="currency" value="${portfolio.totalValue }" maxFractionDigits="1" currencySymbol="${portfolio.currency.symbol }"/></span>
				<span class="totalGain">
				<c:choose>
					<c:when test="${fn:startsWith(portfolio.totalGain, '-')}">
						<img width="10" height="14" style="margin-right:-2px;border:0" src="image/down_r.gif" alt="Down">
						<span class="cQuoteDown"><fmt:formatNumber type="number" maxFractionDigits="0" value="${portfolio.totalGain}"/></span>
					</c:when>
					<c:otherwise>
						<img width="10" height="14" style="margin-right:-2px;border:0;" src="image/up_g.gif" alt="Up">
						<span class="cQuoteUp"><fmt:formatNumber type="number" maxFractionDigits="0" value="${portfolio.totalGain}"/></span>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${fn:startsWith(portfolio.totalPlusMinusValue, '-')}">
						<span class="cQuoteDown">(<fmt:formatNumber type="number" maxFractionDigits="1" value="${portfolio.totalPlusMinusValueAbsolute}" />%)</span>
					</c:when>
					<c:otherwise>
						<span class="cQuoteUp">(<fmt:formatNumber type="number" maxFractionDigits="1" value="${portfolio.totalPlusMinusValue}" />%)</span>
					</c:otherwise>
				</c:choose>
				</span>
				<br>
				${language['PORTFOLIO_LIQUIDITY']}: <span class="liquidity bold"><fmt:formatNumber type="currency" value="${portfolio.liquidity }" maxFractionDigits="2" currencySymbol="${portfolio.currency.symbol }"/></span>
				| ${language['PORTFOLIO_YIELDYEAR']}: <span class="yield bold">
					<fmt:formatNumber type="currency" maxFractionDigits="0" value="${portfolio.yieldYear}" currencySymbol="${portfolio.currency.symbol }"/> 
					(<fmt:formatNumber type="number" maxFractionDigits="1" value="${portfolio.yieldYearPerc}" />%)
				</span>
				<br>
				${language['PORTFOLIO_SHAREVALUE']}:
					<c:choose>
						<c:when test="${portfolio.shareValues[0].shareValue < 100}">
							<span class="cQuoteDown shareValue"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="1" value="${portfolio.shareValues[0].shareValue }" /></span>
						</c:when>
						<c:otherwise>
							<span class="cQuoteUp shareValue"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="1" value="${portfolio.shareValues[0].shareValue }" /></span>
						</c:otherwise>
					</c:choose>
				| Today:
					<c:choose>
						<c:when test="${portfolio.totalGainToday < 0}">
							<img width="10" height="14" style="margin-right:-2px;border:0" src="image/down_r.gif" alt="Down"> 
							<span class="cQuoteDown shareValue"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="0" value="${portfolio.totalGainTodayAbsolute }" /> </span>
						</c:when>
						<c:otherwise>
							<img width="10" height="14" style="margin-right:-2px;border:0;" src="image/up_g.gif" alt="Up"> 
							<span class="cQuoteUp shareValue"><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="0" value="${portfolio.totalGainToday }" /> </span>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${portfolio.totalVariation < 0}">	
							<span class="cQuoteDown shareValue">(<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="1" value="${portfolio.totalVariation }" />%)</span>
						</c:when>
						<c:otherwise>
							<span class="cQuoteUp shareValue">(<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="1" value="${portfolio.totalVariation }" />%)</span>
						</c:otherwise>
					</c:choose>
				</td></tr>
				</table>
				</c:if>
				<div class="clear">
				<div id="clear">
				<c:if test="${!empty updateStatus}">
						${updateStatus}<br>
				</c:if>
				<h3 style="margin-top: 5px;">
					<c:if test="${!empty updated}">
						<span class="cQuoteUp">${updated}</span><br>
					</c:if>
					<c:if test="${!empty error}">
						<span class="cQuoteDown">${error}</span><br>
					</c:if>
				</h3>
				<c:if test="${!empty added}">
					<span class="cQuoteUp">${added}</span><br>
				</c:if>
				<c:if test="${!empty addError}">
					<span class="cQuoteDown">${addError}</span><br>
				</c:if>
				<c:if test="${!empty modified}">
					<span class="cQuoteUp">${modified}</span><br>
				</c:if>
				<c:if test="${!empty modifyError}">
					<span class="cQuoteDown">${modifyError}</span><br>
				</c:if>
				</div>
				</div>
				[<a href="#" data-reveal-id="addEquity">${language['PORTFOLIO_ADD']}</a>]
				<table id="tableEquity" border="1" class="shadow tablesorter">
					 <thead>
					<tr class="tBackGround" style="height:50px;">
						<th class="bold" style="min-width: 180px">${language['PORTFOLIO_COMPANY']}</th>
						<th class="bold tdCenter" style="min-width: 60px">${language['PORTFOLIO_QUANTITY']}</th>
						<th class="bold tdCenter" style="min-width: 60px">${language['PORTFOLIO_UNITCOSTPRICE']}</th>
						<c:if test="${cookie.quote.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 60px">${language['PORTFOLIO_QUOTE']}<br>Today</th>
						</c:if>
						<c:if test="${cookie.currency.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 60px">${language['PORTFOLIO_CURRENCY']}</th>
						</c:if>
						<c:if test="${cookie.parity.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 60px">${language['PORTFOLIO_PARITIES']}</th>
						</c:if>
							<th class="bold tdCenter" style="min-width: 80px">${language['PORTFOLIO_VALUE']}<br>(${portfolio.currency.code})</th>
							<th class="bold tdCenter" style="min-width: 60px">${language['PORTFOLIO_PERCENTTOTAL']}</th>
						<c:if test="${cookie.yield1.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 80px">${language['PORTFOLIO_YIELDTTM']}</th>
						</c:if>
						<c:if test="${cookie.yield2.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 90px">${language['PORTFOLIO_YIELDPERUNITCOSTPRICE']}</th>
						</c:if>
							<th class="bold tdCenter" style="min-width: 70px">${language['PORTFOLIO_VALUEGAINED']}<br>(${portfolio.currency.code})</th>
						<c:if test="${cookie.stopLoss.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 65px">${language['PORTFOLIO_STOPLOSS']}</th>
						</c:if>
						<c:if test="${cookie.objective.value == 'checked' }">
							<th class="bold tdCenter" style="min-width: 65px">${language['PORTFOLIO_OBJECTIVE']}</th>
						</c:if>
					</tr>
					</thead>
					<c:forEach var="equity" items="${portfolio.equities}">
						<tr>
							<td><span class="bold">${equity.currentName}</span><br>(${equity.company.yahooId})
								[<a href="javascript:poufpouf('${equity.company.yahooId}')">${language['PORTFOLIO_INFO']}</a>] [<a href="#" data-reveal-id="modifyEquity"
									onclick="javascript:updateTicker('${equity.id}', '${equity.company.yahooId}', '${equity.namePersonal }', '${equity.sectorPersonal }', '${equity.industryPersonal }', '${equity.marketCapPersonal }', '${equity.quantity}','${equity.unitCostPrice}','${equity.company.yield}','${equity.yieldPersonal}','${equity.parity }','${equity.parityPersonal }','${equity.stopLossLocal}','${equity.objectivLocal}');return false">${language['PORTFOLIO_MODIFY']}</a>]
									<span id="${equity.company.yahooId}" class="companyInfo" style="display: none;"> 
											<br> - 
												<c:choose>
												<c:when test="${!empty equity.currentSector}">
													${equity.currentSector}
												</c:when>
												<c:otherwise>Unknown sector</c:otherwise>
												</c:choose>
											<br> - <c:choose>
													<c:when test="${!empty equity.currentIndustry}">
														${equity.currentIndustry}
													</c:when>
													<c:otherwise>Unknown industry</c:otherwise>
												</c:choose>
											<br> - 
												<c:choose>
													<c:when test="${!empty equity.currentMarketCap}">
														${equity.currentMarketCap} ${equity.company.currency.symbol} (${equity.marketCapitalizationType.value})
													</c:when>
													<c:otherwise>Unknown market cap</c:otherwise>
												</c:choose>
											<br> - <fmt:formatDate value="${equity.company.lastUpdate}" pattern="${user.datePattern }"/>	
									</span>
							</td>
							<td class="tdRight"><fmt:formatNumber type="number" value="${equity.quantity}" maxFractionDigits="0" /></td>
							<td class="tdRight"><fmt:formatNumber type="number" maxFractionDigits="3" value="${equity.unitCostPrice}" /></td>
							<c:if test="${cookie.quote.value == 'checked' }">
								<td class="tdRight">
									<fmt:formatNumber type="number" maxFractionDigits="3" value="${equity.company.quote}" /><br>
									<c:choose>
										<c:when test="${equity.company.change < 0}">
											<span class="cQuoteDown"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="1" value="${equity.company.change}" />%</span>
										</c:when>
										<c:otherwise>
											<span class="cQuoteUp">+<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="1" value="${equity.company.change}" />%</span>
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
							<c:if test="${cookie.currency.value == 'checked' }">
								<td class="tdCenter">${equity.company.currency }</td>
							</c:if>
							<c:if test="${cookie.parity.value == 'checked' }">
								<td class="tdCenter">
									<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="5" value="${equity.parity }" />
									<c:if test="${equity.parityPersonal != 1 }">
										<br><fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="5" value="${equity.parityPersonal }" />
									</c:if>
								</td>
							</c:if>
							<td class="tdRight"><fmt:formatNumber type="number" value="${equity.value }" maxFractionDigits="0" /><br>
							<c:choose>
									<c:when test="${fn:startsWith(equity.plusMinusValue, '-')}">
										<span class="cQuoteDown"><fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.plusMinusValue }" />%</span>
									</c:when>
									<c:otherwise>
										<span class="cQuoteUp">+<fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.plusMinusValue }" />%</span>
									</c:otherwise>
								</c:choose></td>
							<td class="tdRight">
								<fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.value / (portfolio.totalValue-portfolio.liquidity) * 100}" />%
							</td>
								<c:if test="${cookie.yield1.value == 'checked' }">
							<td class="tdRight"><c:if test="${equity.currentYield != 0}">
									<fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.currentYield}" />% 
									<c:if test="${!empty equity.yieldPersonal}">
										*
									</c:if>
									
								</c:if></td>
								</c:if>
							<c:if test="${cookie.yield2.value == 'checked' }">
							<td class="tdRight"><c:if test="${equity.yieldUnitCostPrice != 0}">
									<fmt:formatNumber type="number" maxFractionDigits="0" value="${equity.yieldYear}" /><br>
									<fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.yieldUnitCostPrice}" />% 
								</c:if></td>
								</c:if>
							<td class="tdRight">
									<c:choose>
										<c:when test="${fn:startsWith(equity.plusMinusUnitCostPriceValue, '-')}">
											<span class="cQuoteDown"><fmt:formatNumber type="number" maxFractionDigits="0" value="${equity.plusMinusUnitCostPriceValue}" /></span>
										</c:when>
										<c:otherwise>
											<span class="cQuoteUp"><fmt:formatNumber type="number" maxFractionDigits="0" value="${equity.plusMinusUnitCostPriceValue}" /></span>
											<c:set var="classType" value="cQuoteUp" />
										</c:otherwise>
									</c:choose>
								</td>
								<c:if test="${cookie.stopLoss.value == 'checked' }">
							<td class="tdRight"><c:if test="${!empty equity.gapStopLossLocal}">
									<fmt:formatNumber type="number" minFractionDigits="3" value="${equity.stopLossLocal}" />
									<br>
									<c:choose>
										<c:when test="${equity.gapStopLossLocal < 0 }">
											<span class="cQuoteDown"><fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.gapStopLossLocal}" />%</span>
										</c:when>
										<c:when test="${equity.gapStopLossLocal < 5 }">
											<span class="cQuoteOrange"><fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.gapStopLossLocal}" />%</span>
										</c:when>
										<c:otherwise>
											<fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.gapStopLossLocal}" />%
										</c:otherwise>
									</c:choose>
								</c:if></td>
								</c:if>
							<c:if test="${cookie.objective.value == 'checked' }">
							<td class="tdRight"><c:if test="${!empty equity.gapObjectivLocal}">
									<fmt:formatNumber type="number" minFractionDigits="3" value="${equity.objectivLocal}" />
									<br>
									<c:choose>
										<c:when test="${equity.gapObjectivLocal < 0 }">
											<span class="cQuoteUp"><fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.gapObjectivLocal}" />%</span>
										</c:when>
										<c:when test="${equity.gapObjectivLocal < 5 }">
											<span class="cQuoteOrange"><fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.gapObjectivLocal}" />%</span>
										</c:when>
										<c:otherwise>
											<fmt:formatNumber type="number" maxFractionDigits="1" value="${equity.gapObjectivLocal}" />%
										</c:otherwise>
									</c:choose>
							</c:if></td>
							</c:if>
						</tr>
					</c:forEach>
				</table>
				<br>
				<br>
				<br>
				<div id="shareValue">
					<c:if test="${!empty portfolio.shareValues }">
						<h2 style="margin-top: 5px;">
							${language['PORTFOLIO_CHARTTITLE']}
						</h2>
						
						<span id="graphTop">[ <a href="home#shareValue">${language['PORTFOLIO_ALL']}</a> - <a href="home?days=1825#shareValue">${language['PORTFOLIO_FIVEYEARS']}</a> - <a href="home?days=730#shareValue">${language['PORTFOLIO_TWOYEARS']}</a> - <a href="home?days=365#shareValue">${language['PORTFOLIO_ONEYEAR']}</a> - <a href="home?days=183#shareValue">${language['PORTFOLIO_SIXMONTHS']}</a> - <a href="home?days=90#shareValue">${language['PORTFOLIO_THREEMONTHS']}</a> - <a href="home?days=30#shareValue">${language['PORTFOLIO_ONEMONTH']}</a> - <a href="home?days=7#shareValue">${language['PORTFOLIO_ONEWEEK']}</a> ]</span>
						<div id="graphicShareValue" class="shadow"></div>
						
						<br><br>
						
						<h2 style="margin-top: 5px;">
							${language['PORTFOLIO_CHARTTITLEVALUE']}
						</h2>
						
						<span id="graphTop2">[ <a href="home#shareValue">${language['PORTFOLIO_ALL']}</a> - <a href="home?days=1825#shareValue">${language['PORTFOLIO_FIVEYEARS']}</a> - <a href="home?days=730#shareValue">${language['PORTFOLIO_TWOYEARS']}</a> - <a href="home?days=365#shareValue">${language['PORTFOLIO_ONEYEAR']}</a> - <a href="home?days=183#shareValue">${language['PORTFOLIO_SIXMONTHS']}</a> - <a href="home?days=90#shareValue">${language['PORTFOLIO_THREEMONTHS']}</a> - <a href="home?days=30#shareValue">${language['PORTFOLIO_ONEMONTH']}</a> - <a href="home?days=7#shareValue">${language['PORTFOLIO_ONEWEEK']}</a> ]</span>
						<div id="graphicShareValue2" class="shadow"></div>
					</c:if>
				</div>
			</div>
		</div>
		<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2013</div>
	</div>

<script type="text/javascript">
<!--
(function basic_time(container) {
    var
    <c:out value="${portfolio.timeChart.data}" escapeXml="false"/>,
       start = new Date("<c:out value="${portfolio.timeChart.date}" escapeXml="false"/>").getTime(),
        options, graph, i, x, o;
    options = {
        xaxis: {
            mode: 'time',
            labelsAngle: 45,
            timeMode:'local'
        },
        selection: {
            mode: 'x'
        },
        HtmlText: false,
        //colors: ['#3e933d', '#190525', '#6a0efc'],
        colors: [<c:out value="${portfolio.timeChart.colors}" escapeXml="false"/>],
    };

    // Draw graph with default options, overwriting with passed options


    function drawGraph(opts) {

        // Clone the options, so the 'options' variable always keeps intact.
        o = Flotr._.extend(Flotr._.clone(options), opts || {});

        // Return a new graph.
        return Flotr.draw(
        container, <c:out value="${portfolio.timeChart.draw}" escapeXml="false"/>, o);
    }

    graph = drawGraph();

    Flotr.EventAdapter.observe(container, 'flotr:select', function(area) {
        // Draw selected area
        graph = drawGraph({
            xaxis: {
              	min: area.x1,
              	max: area.x2,
                mode: 'time',
                labelsAngle: 45
            },
            yaxis: {
                min: area.y1,
                max: area.y2
            }
        });
    });

    // When graph is clicked, draw the graph with default area.
    Flotr.EventAdapter.observe(container, 'flotr:click', function() {
        graph = drawGraph();
    });
})(document.getElementById("graphicShareValue"));
//-->
<!--
(function basic_time(container) {
    var
    <c:out value="${portfolio.timeValueChart.data}" escapeXml="false"/>,
       start = new Date("<c:out value="${portfolio.timeChart.date}" escapeXml="false"/>").getTime(),
        options, graph, i, x, o;
    options = {
        xaxis: {
            mode: 'time',
            labelsAngle: 45
        },
        selection: {
            mode: 'x'
        },
        HtmlText: false,
        //colors: ['#3e933d', '#190525', '#6a0efc'],
        colors: [<c:out value="${portfolio.timeValueChart.colors}" escapeXml="false"/>],
    };

    // Draw graph with default options, overwriting with passed options


    function drawGraph(opts) {

        // Clone the options, so the 'options' variable always keeps intact.
        o = Flotr._.extend(Flotr._.clone(options), opts || {});

        // Return a new graph.
        return Flotr.draw(
        container, <c:out value="${portfolio.timeValueChart.draw}" escapeXml="false"/>, o);
    }

    graph = drawGraph();

    Flotr.EventAdapter.observe(container, 'flotr:select', function(area) {
        // Draw selected area
        graph = drawGraph({
            xaxis: {
                min: area.x1,
                max: area.x2,
                mode: 'time',
                labelsAngle: 45
            },
            yaxis: {
                min: area.y1,
                max: area.y2
            }
        });
    });

    // When graph is clicked, draw the graph with default area.
    Flotr.EventAdapter.observe(container, 'flotr:click', function() {
        graph = drawGraph();
    });
})(document.getElementById("graphicShareValue2"));
//-->
</script>
</body>
</html>