/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function poufpouf(id) {
	var style = document.getElementById(id).style.display;
	if (id.indexOf(".") != -1) {
		id = id.replace(".", "\\.");
	}

	if (style == "none")
		$("#" + id).show("fast");
	else
		$("#" + id).hide("fast");
}
function pouf(id) {
	$("#" + id).show("fast");
}
function poufpoufpouf(id) {
	$("#" + id).hide("fast");
}
function addEquity() {
	document.sendEquity.action = "add";
	document.sendEquity.method = "post";
	document.sendEquity.submit();
}
function modifyEquity() {
	document.sendEquityModify.action = "modifyequity";
	document.sendEquityModify.method = "post";
	document.sendEquityModify.submit();
}
function deleteEquity() {
	document.sendEquityDelete.action = "modifyequity";
	document.sendEquityDelete.method = "post";
	document.sendEquityDelete.submit();
}
function refresh() {
	document.sendRefresh.action = "updateportfolio";
	document.sendRefresh.method = "post";
	document.sendRefresh.submit();
}
function refreshList() {
	document.sendRefresh.action = "updatelist";
	document.sendRefresh.method = "post";
	document.sendRefresh.submit();
}
function refreshCurrency(){
	document.sendRefresh.action = "currencies?update=1";
	document.sendRefresh.method = "post";
	document.sendRefresh.submit();
}
function addFollow() {
	document.sendFollow.action = "addfollow";
	document.sendFollow.method = "post";
	document.sendFollow.submit();
}
function modifyFollow() {
	document.sendModifyFollow.action = "modifyfollow";
	document.sendModifyFollow.method = "post";
	document.sendModifyFollow.submit();
}
function deleteFollow() {
	document.sendFollowDelete.action = "addfollow";
	document.sendFollowDelete.method = "post";
	document.sendFollowDelete.submit();
}
function modifyOption() {
	document.modifyOptionId.action = "options?update=1";
	document.modifyOptionId.method = "post";
	document.modifyOptionId.submit();
}
function addAcc() {
	document.addAccountName.action = "accounts?add=1";
	document.addAccountName.method = "post";
	document.addAccountName.submit();
}
function modifyAcc() {
	document.modifyAccountName.action = "accounts?mod=1";
	document.modifyAccountName.method = "post";
	document.modifyAccountName.submit();
}
function deleteAcc() {
	document.deletAccountName.action = "accounts?delete=1";
	document.deletAccountName.method = "post";
	document.deletAccountName.submit();
}
function execFunWithTimeout(func) {
	setTimeout(func, 350);
}
function updateTicker(id, ticker, namePersonal, sector, industry, marketCap, quantity, unitCostPrice, yieldYahoo,
		yieldPersonal, parity, parityPersonal, stopLoss, objective) { 
	document.getElementById('modifyTicker').innerHTML = ticker;
	document.getElementById('modifyTickerHidden').value = ticker;
	document.getElementById('deleteTicker').value = id;
	document.getElementById('modifyNamePersonal').value = namePersonal;
	document.getElementById('modifySectorPersonal').value = sector;
	document.getElementById('modifyIndustryPersonal').value = industry;
	document.getElementById('modifyMarketCapPersonal').value = marketCap;
	document.getElementById('modifyQuantity').value = quantity;
	document.getElementById('modifyUnitCostPrice').value = unitCostPrice;
	if (yieldPersonal != 0.0) {
		document.getElementById('modifyYieldPersonal').value = yieldPersonal;
	} else {
		document.getElementById('modifyYieldPersonal').value = null;
	}
	if (yieldYahoo != 0.0) {
		document.getElementById('modifyYieldYahoo').innerHTML = yieldYahoo;
	} else {
		document.getElementById('modifyYieldYahoo').value = null;
	}
	document.getElementById('modifyParity').innerHTML = parity;
	document.getElementById('modifyParityPersonal').value = parityPersonal;
	document.getElementById('modifyStopLoss').value = stopLoss;
	document.getElementById('modifyObjective').value = objective;
}

function updateFollow(id, yahooId, lowerLimit, higherLimit) {
	document.getElementById('modifyFollowYahooId').innerHTML = yahooId;
	document.getElementById('ticker').value = yahooId;
	if (lowerLimit != 0.0) {
		document.getElementById('lower').value = lowerLimit;
	} else {
		document.getElementById('lower').value = null;
	}
	if (higherLimit != 0.0) {
		document.getElementById('higher').value = higherLimit;
	} else {
		document.getElementById('higher').value = null;
	}
	document.getElementById('deleteFollowId').value = id;
}

function updateAccount(id, name, currency, liquidity, del){
	document.getElementById('id').value = id;
	document.getElementById('account').value = name;
	document.getElementById('currency').value = currency;
	document.getElementById('liquidity').value = liquidity;
	document.getElementById('idDelete').value = id;
	document.getElementById('idDeleteDelete').value = del;
}

function updateShare(id, commentary){
	document.getElementById('shareId').value = id;
	document.getElementById('commentaryShare').value = commentary;
}

function checkForm(formId, divId, buttonId, func) {
	if ($('#' + formId)[0].checkValidity()) {
		$('#' + divId).trigger('reveal:close');
		console.log("function " + func.name);
		setTimeout(func, 350);
	} else {
		$('#' + buttonId).click();
	}
}