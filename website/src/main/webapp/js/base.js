/**
 * Copyright 2014 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
  displayWaitbar();
  document.sendEquity.action = "equity";
  document.sendEquity.method = "post";
  document.sendEquity.submit();
}
function addEquityManual() {
  displayWaitbar();
  document.sendEquityManual.action = "manualEquity";
  document.sendEquityManual.method = "post";
  document.sendEquityManual.submit();
}
function modifyEquity() {
  document.sendEquityModify.action = "updateEquity";
  document.sendEquityModify.method = "post";
  document.sendEquityModify.submit();
}
function modifyEquityManual() {
  document.sendEquityModify2.action = "updateManualEquity";
  document.sendEquityModify2.method = "post";
  document.sendEquityModify2.submit();
}
function deleteEquity() {
  document.sendEquityDelete.action = "deleteEquity";
  document.sendEquityDelete.method = "post";
  document.sendEquityDelete.submit();
}
function deleteEquityManual() {
  document.sendEquityDelete2.action = "deleteManualEquity";
  document.sendEquityDelete2.method = "post";
  document.sendEquityDelete2.submit();
}
function refresh() {
  displayWaitbar();
  document.sendRefresh.action = "portfolio";
  document.sendRefresh.method = "post";
  document.sendRefresh.submit();
}
function refreshCurrency() {
  displayWaitbar();
  document.sendRefresh.action = "currencies";
  document.sendRefresh.method = "post";
  document.sendRefresh.submit();
}
function addFollow() {
  displayWaitbar();
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
  document.addAccountName.action = "addaccount";
  document.addAccountName.method = "post";
  document.addAccountName.submit();
}
function modifyAcc() {
  document.modifyAccountName.action = "editaccount";
  document.modifyAccountName.method = "post";
  document.modifyAccountName.submit();
}
function deleteAcc() {
  document.deletAccountName.action = "deleteaccount";
  document.deletAccountName.method = "post";
  document.deletAccountName.submit();
}
function execFunWithTimeout(func) {
  setTimeout(func, 350);
}

/*
function updateTicker(id, ticker, namePersonal, sector, industry, marketCap,
    quantity, unitCostPrice, yieldYahoo, yieldPersonal, parity, parityPersonal,
    stopLoss, objective) {
  if(manual == 'true') {
    document.getElementById('manualForm2').style.display = 'block';
    document.getElementById('notManualForm2').style.display = 'none';
  } else {
    document.getElementById('manualForm2').style.display = 'none';
    document.getElementById('notManualForm2').style.display = 'block';
  }
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
} */

function updateTicker(id, ticker, namePersonal, sector, industry, marketCap,
    quantity, unitCostPrice, yieldYahoo, yieldPersonal, parity, parityPersonal,
    stopLoss, objective) {
  document.getElementById('equityId').value = id;
  document.getElementById('manualForm2').style.display = 'none';
  document.getElementById('notManualForm2').style.display = 'block';
  document.getElementById('sendEquityDelete').style.display = 'block';
  document.getElementById('sendEquityDelete2').style.display = 'none';

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

function updateManual(id, ticker, namePersonal, sector, industry, marketCap,
    quantity, unitCostPrice, yieldYahoo, yieldPersonal, parity, parityPersonal,
    stopLoss, objective, companyId, quote) {
  document.getElementById('manualForm2').style.display = 'block';
  document.getElementById('notManualForm2').style.display = 'none';
  document.getElementById('sendEquityDelete').style.display = 'none';
  document.getElementById('sendEquityDelete2').style.display = 'block';

  document.getElementById('modifyTickerHidden2').value = ticker;
  document.getElementById('deleteTicker2').value = id;
  document.getElementById('companyId').value = companyId;
  document.getElementById('companyId2').value = companyId;
  document.getElementById('modifyNamePersonal2').value = namePersonal;
  document.getElementById('modifyQuote2').value = quote;
  document.getElementById('modifySectorPersonal2').value = sector;
  document.getElementById('modifyIndustryPersonal2').value = industry;
  document.getElementById('modifyMarketCapPersonal2').value = marketCap;
  document.getElementById('modifyQuantity2').value = quantity;
  document.getElementById('modifyUnitCostPrice2').value = unitCostPrice;
  if (yieldPersonal != 0.0) {
    document.getElementById('modifyYieldPersonal2').value = yieldPersonal;
  } else {
    document.getElementById('modifyYieldPersonal2').value = null;
  }
  document.getElementById('modifyParity2').innerHTML = parity;
  document.getElementById('modifyParityPersonal2').value = parityPersonal;
  document.getElementById('modifyStopLoss2').value = stopLoss;
  document.getElementById('modifyObjective2').value = objective;
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

function updateAccount(accountId, name, currency, liquidity) {
  document.getElementById('accountId').value = accountId;
  document.getElementById('account').value = name;
  document.getElementById('currency').value = currency;
  document.getElementById('liquidity').value = liquidity;
  document.getElementById('idDelete').value = accountId;
}

function updateShare(id, commentary) {
  document.getElementById('shareId').value = id;
  document.getElementById('commentaryShare').value = commentary;
}

function checkForm(formId, divId, buttonId, func) {
  if ($('#' + formId)[0].checkValidity()) {
    $('#' + divId).trigger('reveal:close');
    setTimeout(func, 350);
  } else {
    $('#' + buttonId).click();
  }
}

function displayWaitbar() {
  var newdiv = document.createElement('div');
  newdiv.setAttribute('id', 'loading');
  var container = document.getElementById('container');
  container.parentNode.insertBefore(newdiv, container);
  container.style.opacity = '0.1';
}

function hideShowManual() {
  var checkBox = document.getElementById('manual');
  var elementManual = document.getElementById('manualForm');
  var notElementManual = document.getElementById('notManualForm');
  if (checkBox.checked) {
    elementManual.style.display = 'block';
    notElementManual.style.display = 'none';
  } else {
    elementManual.style.display = 'none';
    notElementManual.style.display = 'block';
  }
}
