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

<%@ page language="java" pageEncoding="UTF-8" session="false" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="./style.css"/>
	<link rel="shortcut icon" href="./favicon.ico"/>
	<title>Registered</title>
</head>
<body>
<header></header>
<nav></nav>
<article>
	<div id="error">
		<c:choose>
			<c:when test="${!empty error }">
				${error }<br>Please <a href="/">try again</a>.
			</c:when>
			<c:otherwise>
				Welcome, ${login }!<br>
				Check your email box, you need to activate your account now !<br>
				[<a href="/">index</a>]
			</c:otherwise>
		</c:choose>
	</div>
</article>
<footer></footer>
</body>
</html>
