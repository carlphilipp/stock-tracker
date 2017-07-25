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
<!DOCTYPE html>
<html lang="en">
<head>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/flotr2.min.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<link rel="shortcut icon" href="./favicon.ico"/>
	<script>
		function search(tab, str) {
			var i = 0;
			for (i; i < tab.length; i++) {
				if (str == tab[i]) {
					return i;
					break;
				}
			}
		}
		function getExplode(tab) {
			if (tab.length < 5) {
				return 5;
			} else {
				return 10;
			}
		}
	</script>
	<style type="text/css">
		#graphicSector {
			width: 500px;
			height: 400px;
		}

		#graphicCap {
			width: 500px;
			height: 400px;
		}
	</style>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="./style.css"/>
	<title>${appTitle }</title>
	<script type="text/javascript" src="js/analytics.js"></script>
</head>
<body>
<div id="container">
	<%@ include file="menu.html" %>
	<div class="main">
		<div class="equities_container">
			<h2 style="margin-top: 5px;">
				Charts
			</h2>
			<c:if test="${!empty portfolio.equities }">
				<br>
				<div id="sector" class="">
					<div id="graphicSector" class=""></div>
				</div>
				<div id="graphicSectorLegendContainer">
					<div id="graphicSectorLegend" class=""></div>
				</div>
				<br>
				<br>
				<div class="clear">
					<div id="cap" class="">
						<div id="graphicCap" class=""></div>
					</div>
					<div id="graphicCapLegendContainer">
						<div id="graphicCapLegend" class="">
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</div>
	<div id="footer2">Stock Tracker © <a href="http://www.apache.org/licenses/LICENSE-2.0">Copyright</a> 2017</div>
</div>
</body>
<script type="text/javascript">
	<!--
	(function basic_pie(container) {

		<c:out value="${portfolio.pieChartSector.data}" escapeXml="false"/>

		var graph;
		var idx = -1;
		var idx2 = -1;
		<c:out value="${portfolio.pieChartSector.title}" escapeXml="false"/>
		<c:out value="${mapSector}" escapeXml="false"/>
		graph = Flotr.draw(container, <c:out value="${portfolio.pieChartSector.draw}" escapeXml="false"/>
			,

			{
				title: 'Sector Chart',
				HtmlText: true,
				shadowSize: 0,
				colors: ['#e48984', '#88b0e3', '#b0cd71', '#ffb47a', '#ddeeb7'],
				//colors: ['#edc240', '#4da74d', '#cb4b4b'],
				grid: {
					verticalLines: false,
					horizontalLines: false
				},
				xaxis: {
					showLabels: false,
				},
				yaxis: {
					showLabels: false
				},
				pie: {
					show: true,
					explode: getExplode(title),
					labelFormatter: function (total, value) {
						idx++;
						idx2--;
						return '<span class="bold"><a href="javascript:poufpouf(\'display' + idx + '\')">' + (100 * value / total).toFixed(0) + '%</a></span>';
					},
					fill: true, // => true to fill the area from the line to the x axis, false for (transparent) no fill
					fillColor: null, // => fill color
					fillOpacity: 1, // => opacity of the fill color, set to 1 for a solid fill, 0 hides the fill
					startAngle: Math.PI / 4,

					lineWidth: 1, // => in pixels
					sizeRatio: 0.7, // => the size ratio of the pie relative to the plot
					startAngle: Math.PI / 4, // => the first slice start angle
					pie3D: false, // => whether to draw the pie in 3 dimenstions or not (ineffective)
					pie3DviewAngle: (Math.PI / 2 * 0.8),
					pie3DspliceThickness: 20,
					epsilon: 0.1 // => how close do you have to get to hit empty slice
				},
				mouse: {
					track: true,
					trackFormatter: function (obj) {
						return obj.series.label;
					},
					position: 'se',        // => position of the value box (default south-east)
					relative: false,       // => next to the mouse cursor
					margin: 5,             // => margin in pixels of the valuebox
					lineColor: '#000000',  // => line color of points that are drawn when mouse comes near a value of a series
					trackDecimals: 1,      // => decimals for the track values
					sensibility: 2,        // => the lower this number, the more precise you have to aim to show a value
					trackY: true,          // => whether or not to track the mouse in the y axis
					radius: 3,             // => radius of the track point
					fillColor: null,       // => color to fill our select bar with only applies to bar and similar graphs (only bars for now)
					fillOpacity: 0.4       // => opacity of the fill color, set to 1 for a solid fill, 0 hides the fill
				},
				legend: {
					show: true,
					position: 'se',
					backgroundColor: '#D2E8FF',
					container: document.getElementById('graphicSectorLegend'),
					labelFormatter: function (str) {
						idx2++;
						return '<a href="javascript:poufpouf(\'display' + idx2 + '\')"><b>' + str + '</b></a><br><span id="display' + idx2 + '" style="display: none;">' + companies[idx2] + '</span>';
					}

				}
			});
	})(document.getElementById("graphicSector"));
	//-->
	<!--
	(function basic_pie(container) {
		<c:out value="${portfolio.pieChartCap.data}" escapeXml="false"/>
		var graph;
		;
		var idx = -1;
		var idx2 = -1;
		<c:out value="${portfolio.pieChartCap.title}" escapeXml="false"/>
		<c:out value="${mapCap}" escapeXml="false"/>
		graph = Flotr.draw(container, <c:out value="${portfolio.pieChartCap.draw}" escapeXml="false"/>
			,

			{
				title: 'Capitalization Chart',
				colors: ['#e48984', '#88b0e3', '#b0cd71', '#ffb47a'],
				//colors: ['#edc240', '#4da74d', '#cb4b4b', '#afd8f8'],
				HtmlText: true,
				shadowSize: 0,
				grid: {
					verticalLines: false,
					horizontalLines: false,
				},
				xaxis: {
					showLabels: false,
				},
				yaxis: {
					showLabels: false,
				},
				pie: {
					show: true, // => setting to true will show bars, false will hide
					explode: 5, // => the number of pixels the splices will be far from the center
					labelFormatter: function (total, value) {
						idx++;
						idx2--;
						return '<span class="bold"><a href="javascript:poufpouf(\'display2' + idx + '\')">' + (100 * value / total).toFixed(0) + '%</a></span>';
					},

					fill: true, // => true to fill the area from the line to the x axis, false for (transparent) no fill
					fillColor: null, // => fill color
					fillOpacity: 1, // => opacity of the fill color, set to 1 for a solid fill, 0 hides the fill
					lineWidth: 1, // => in pixels
					sizeRatio: 0.7, // => the size ratio of the pie relative to the plot
					startAngle: Math.PI / 4, // => the first slice start angle
					pie3D: false, // => whether to draw the pie in 3 dimenstions or not (ineffective)
					pie3DviewAngle: (Math.PI / 2 * 0.8),
					pie3DspliceThickness: 20,
					epsilon: 0.1 // => how close do you have to get to hit empty slice

				},
				mouse: {
					track: true,	// => true to track the mouse, no tracking otherwise
					trackFormatter: function (obj) {
						return obj.series.label;
					},
					position: 'se',        // => position of the value box (default south-east)
					relative: false,       // => next to the mouse cursor
					margin: 5,             // => margin in pixels of the valuebox
					lineColor: '#000000',  // => line color of points that are drawn when mouse comes near a value of a series
					trackDecimals: 1,      // => decimals for the track values
					sensibility: 2,        // => the lower this number, the more precise you have to aim to show a value
					trackY: true,          // => whether or not to track the mouse in the y axis
					radius: 3,             // => radius of the track point
					fillColor: null,       // => color to fill our select bar with only applies to bar and similar graphs (only bars for now)
					fillOpacity: 0.4       // => opacity of the fill color, set to 1 for a solid fill, 0 hides the fill

				},
				legend: {
					position: 'se',
					backgroundColor: '#D2E8FF',
					container: document.getElementById('graphicCapLegend'),
					labelFormatter: function (str) {
						idx2++;
						return '<a href="javascript:poufpouf(\'display2' + idx2 + '\')"><b>' + str + '</b></a><br><span id="display2' + idx2 + '" style="display: none;">' + companies[idx2] + '</span>';
					}
				}
			});
	})(document.getElementById("graphicCap"));
	//-->
</script>
</html>
