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
<script type="text/javascript" src="js/jquery-ui.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="./style.css" />
<link type="text/css" rel="stylesheet" href="./reveal.css">
<link rel="shortcut icon" href="./favicon.ico" />
<link type="text/css" rel="stylesheet" href="./jquery-ui.css" />
<title>${appTitle }</title>
<script>
	$(function() {
		$("#from").datepicker({
			defaultDate : "+1w",
			changeMonth : true,
			changeYear : true,
			numberOfMonths : 1,
			dateFormat : "dd/mm/yy",
			onClose : function(selectedDate) {
				$("#to").datepicker("option", "minDate", selectedDate);
			}
		});
		$("#to").datepicker({
			defaultDate : "+1w",
			changeMonth : true,
			changeYear : true,
			numberOfMonths : 1,
			dateFormat : "dd/mm/yy",
			onClose : function(selectedDate) {
				$("#from").datepicker("option", "maxDate", selectedDate);
			}
		});
	});
</script>
<script type="text/javascript" src="js/analytics.js"></script>
</head>
<body>
	<fmt:setLocale value="${user.locale }" />
	<fmt:setTimeZone value="${user.timeZone }" />
	<div id="container">
		<%@ include file="menu.html"%>
		<div class="main">
			<div class="equities_container">
				<div class="floatLeft">
					<h2 style="margin-top: 5px; display: inline;">Performance</h2>
					<br> from <fmt:formatDate value="${_from}" pattern="${user.datePatternWithoutHourMin }"/> to <fmt:formatDate value="${_to}" pattern="${user.datePatternWithoutHourMin }"/> [<a href="javascript:poufpouf('formDate')">modify</a>]
					<div id="formDate" style="display: none;">
						<form action="performance">
							From <input type="text" id="from" name="from" style="width:70px;"/> to <input type="text" id="to" name="to" style="width:70px;"/><input type="submit" value="modify">
						</form>
					</div>
					<form id="pdf" method="post" action="">
						<input type="hidden" name="pdf" value="pdf">
						<a href="javascript:document.getElementById('pdf').submit();">Generate pdf</a>
					</form>
				</div>
				<table id="tableEquityTotal" border="1" class="shadow">
					<tr>
						<td>
						<table>
						<tr>
							<td align="left"><span class="bold">Gain:</span></td>
							<td align="right">
								<c:choose>
								<c:when test="${fn:startsWith(portfolio.currenShareValuesGain, '-')}">
									<span class="cQuoteDown bold"><fmt:formatNumber type="currency" maxFractionDigits="1" value="${portfolio.currenShareValuesGain}" currencySymbol="${portfolio.currency.symbol }" />
									</span>
								</c:when>
								<c:otherwise>
									<span class="cQuoteUp bold"><fmt:formatNumber type="currency" maxFractionDigits="1" value="${portfolio.currenShareValuesGain}" currencySymbol="${portfolio.currency.symbol }" />
									</span>
								</c:otherwise>
							</c:choose>
							</td>
						</tr>
						<tr><td align="left">
							<span class="bold">Performance:</span> 
							</td><td align="right">
							<c:choose>
								<c:when test="${fn:startsWith(portfolio.currenShareValuesGainPorcentage, '-')}">
									<span class="cQuoteDown bold"> <fmt:formatNumber type="number" maxFractionDigits="1" value="${portfolio.currenShareValuesGainPorcentage}" /> %
									</span>
								</c:when>
								<c:otherwise>
									<span class="cQuoteUp bold">+<fmt:formatNumber type="number" maxFractionDigits="1" value="${portfolio.currenShareValuesGainPorcentage}" /> %
									</span>
								</c:otherwise>
							</c:choose>
							</td></tr>
							<tr><td align="left">
							
							<span class="bold">Yield:</span> 
							</td><td align="right">
							
							<span class="cQuoteUp bold"> <fmt:formatNumber type="currency" maxFractionDigits="1" value="${portfolio.currentShareValuesYield}"
									currencySymbol="${portfolio.currency.symbol }" /></span>
							</td></tr>
							<tr><td align="left">
						 Taxes: 
						</td><td align="right"><span class="cQuoteDown"> <fmt:formatNumber type="currency" maxFractionDigits="1" value="${portfolio.currentShareValuesTaxes}"
									currencySymbol="${portfolio.currency.symbol }" /></span>
									</td></tr>
								<tr><td align="left">
						 Volume: </td><td align="right">
						 <fmt:formatNumber type="currency" maxFractionDigits="1" value="${portfolio.currentShareValuesVolume}" currencySymbol="${portfolio.currency.symbol }" />
						 </td></tr>
						</table>
						</td>
					</tr>
				</table>
				<div class="clear"></div>
				<br>
				<div id="graphicShareValue" class="shadow"></div>
			</div>
			<div id="footer">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2013</div>
		</div>
	</div>
	<script type="text/javascript">
	<!--
		(function basic_time(container) {
			var <c:out value="${portfolio.timeChart.data}" escapeXml="false"/>, start = new Date(
					"<c:out value="${portfolio.timeChart.date}" escapeXml="false"/>")
					.getTime(), options, graph, i, x, o;
			options = {
				xaxis : {
					mode : 'time',
					labelsAngle : 45,
					timeMode : 'local'
				},
				selection : {
					mode : 'x'
				},
				HtmlText : false,
				//colors: ['#3e933d', '#190525', '#6a0efc'],
				colors : [ <c:out value="${portfolio.timeChart.colors}" escapeXml="false"/> ],
			};

			// Draw graph with default options, overwriting with passed options

			function drawGraph(opts) {

				// Clone the options, so the 'options' variable always keeps intact.
				o = Flotr._.extend(Flotr._.clone(options), opts || {});

				// Return a new graph.
				return Flotr
						.draw(
								container,
								<c:out value="${portfolio.timeChart.draw}" escapeXml="false"/>,
								o);
			}

			graph = drawGraph();

			Flotr.EventAdapter.observe(container, 'flotr:select',
					function(area) {
						// Draw selected area
						graph = drawGraph({
							xaxis : {
								min : area.x1,
								max : area.x2,
								mode : 'time',
								labelsAngle : 45
							},
							yaxis : {
								min : area.y1,
								max : area.y2
							},
							mouse: {
							    track: false,          // => true to track the mouse, no tracking otherwise
							    trackAll: false,
							    position: 'se',        // => position of the value box (default south-east)
							    relative: false,       // => next to the mouse cursor
							    trackFormatter: Flotr.defaultTrackFormatter, // => formats the values in the value box
							    margin: 5,             // => margin in pixels of the valuebox
							    lineColor: '#FF3F19',  // => line color of points that are drawn when mouse comes near a value of a series
							    trackDecimals: 1,      // => decimals for the track values
							    sensibility: 2,        // => the lower this number, the more precise you have to aim to show a value
							    trackY: false,          // => whether or not to track the mouse in the y axis
							    radius: 3,             // => radius of the track point
							    fillColor: null,       // => color to fill our select bar with only applies to bar and similar graphs (only bars for now)
							    fillOpacity: 0.4       // => opacity of the fill color, set to 1 for a solid fill, 0 hides the fill 
							  }	
						});
					});

			// When graph is clicked, draw the graph with default area.
			Flotr.EventAdapter.observe(container, 'flotr:click', function() {
				graph = drawGraph();
			});
		})(document.getElementById("graphicShareValue"));
	//-->
	</script>
</body>
</html>