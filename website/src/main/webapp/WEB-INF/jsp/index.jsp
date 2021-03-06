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
	<meta charset="utf-8">
	<title>Stock Tracker</title>
	<meta name="description" content="A very cool Stock Exchange website">
	<link type="text/css" rel="stylesheet" href="../../style.css"/>
	<link type="text/css" rel="stylesheet" href="../../reveal.css">
	<link rel="shortcut icon" href="../../favicon.ico"/>
	<script type="text/javascript" src="../../js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="../../js/jquery.reveal.js"></script>
	<script type="text/javascript" src="../../js/base.js"></script>
	<script type="text/javascript" src="../../js/md5.js"></script>
	<script type="text/javascript">
		function checkRegister() {
			if (document.getElementById('password1').value != document.getElementById('password2').value) {
				document.getElementById('password2').setCustomValidity('Passwords must match.');
			} else {
				document.getElementById('password2').setCustomValidity('');
			}
			if ($('#register')[0].checkValidity()) {
				$('#registerReveal').trigger('reveal:close');
				setTimeout(addUser, 350);
			} else {
				$('#process').click();
			}
		}
		function checkLost() {
			if ($('#lostPasswordId')[0].checkValidity()) {
				$('#lostPassword').trigger('reveal:close');
				setTimeout(lost, 350);
			} else {
				$('#process2').click();
			}
		}

		function checkLogin() {
			document.getElementById('passwordForLoginHidden').value = MD5(document.getElementById('passwordForLogin').value);
			document.loginForm.submit();
		}
		function addUser() {
			document.getElementById('password').value = MD5(document.getElementById('password1').value);
			document.register.submit();
		}
		function lost() {
			document.lostPasswordFormName.submit();
		}
	</script>
	<script type="text/javascript" src="../../js/analytics.js"></script>
</head>
<body>
<header></header>
<nav></nav>
<article>

	<div id="registerReveal" class="reveal-modal">
		<h1>Register</h1>
		<form name="register" id="register" action="register" method="post">
			<table>
				<tr>
					<td>Login:</td>
					<td><input id="login" name="login" type="text" required autofocus></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input id="password1" type="password" required></td>
				</tr>
				<tr>
					<td>Password (retype):</td>
					<td><input id="password2" type="password" required></td>
				</tr>
				<tr>
					<td>Email:</td>
					<td><input name="email" type="email" required></td>
				</tr>
				<tr>
					<td colspan="2">
						<input id="submitbutton" type="button" onclick="javascript:checkRegister();" value="Register!">
						<input id="password" name="password" type="hidden">
						<input id="process" type="submit" style="display: none;"></td>
				</tr>
			</table>
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>

	<div id="lostPassword" class="reveal-modal">
		<h1>Lost your password ?</h1>
		<form name="lostPasswordFormName" id="lostPasswordId" action="lost" method="post">
			<table>
				<tr>
					<td>Email:</td>
					<td><input id="email" name="email" type="email" required autofocus></td>
				</tr>
				<tr>
					<td colspan="2">
						<input id="submitbutton2" type="button" onclick="javascript:checkLost();" value="Help!">
						<input id="process2" type="submit" style="display: none;"></td>
				</tr>
			</table>
		</form>
		<a class="close-reveal-modal">&#215;</a>
	</div>

	<form id="loginForm" name="loginForm" action="auth" method="post" autocomplete="on">
		<table>
			<c:if test="${!empty error }">
				<tr>
					<td colspan="2"><span class="cQuoteDown">${error }</span></td>
				</tr>
			</c:if>
			<c:if test="${!empty ok }">
				<tr>
					<td colspan="2"><span class="cQuoteUp">${ok }</span></td>
				</tr>
			</c:if>
			<tr>
				<td><b>Login:</b></td>
				<td><input name="login" type="text" autofocus required="required"></td>
			</tr>
			<tr>
				<td><b>Password:</b></td>
				<td><input id="passwordForLogin" type="password" required="required">
					<input id="passwordForLoginHidden" name="password" type="hidden">
				</td>
			</tr>
			<tr>
				<td colspan="2"><input value="Validate" type="button" onclick="javascript:checkLogin();"></td>
			</tr>
			<tr>
				<td colspan="2" class="center">
					[<a href="#" data-reveal-id="registerReveal">register</a>]
					[<a href="#" data-reveal-id="lostPassword">password lost</a>]
				</td>
			</tr>
		</table>
	</form>
</article>
<footer></footer>
</body>

</html>
