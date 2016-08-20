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
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/md5.js"></script>
	<script type="text/javascript" src="js/analytics.js"></script>
	<script type="text/javascript">
		function checkReset() {
			console.log(document.getElementById('password').value);
			console.log(document.getElementById('password2').value);
			if (document.getElementById('password').value != document.getElementById('password2').value) {
				document.getElementById('password').setCustomValidity('Passwords must match.');
			} else {
				document.getElementById('password2').setCustomValidity('');
			}

			if ($('#pass')[0].checkValidity()) {
				setTimeout(checkLogin, 350);
			} else {
				console.log("Sub: " + $('#sub'));
				$('#sub').click();
			}
		}
		function checkLogin() {
			document.getElementById('passwordForLoginHidden').value = MD5(document.getElementById('password').value);
			document.form.action = "newpasswordconf";
			document.form.method = "post";
			console.log(document.form);
			document.forms["form"].submit();
		}
	</script>
	<link type="text/css" rel="stylesheet" href="./style.css"/>
	<link rel="shortcut icon" href="./favicon.ico"/>
	<title>Create new password</title>
</head>
<body>
<header></header>
<nav></nav>
<article>
	<div id="error">
		<c:choose>
			<c:when test="${!empty error }">
					<span class="cQuoteDown">
							${error }
					</span>
			</c:when>
			<c:otherwise>
				<form id="pass" name="form" autocomplete="on">
					<table>
						<tr>
							<td>New password:</td>
							<td><input id="password" type="password" value=""></td>
						</tr>
						<tr>
							<td>Type it again:</td>
							<td><input id="password2" type="password" value=""></td>
						</tr>
						<tr>
							<td colspan="2">
								<input name="login" type="hidden" value="${login }">
								<input id="passwordForLoginHidden" name="password" type="hidden">
								<input type="button" onclick="javascript:checkReset()" value="Submit">
								<input id="sub" type="submit" style="display: none;">
							</td>
						</tr>
					</table>
				</form>
			</c:otherwise>
		</c:choose>
	</div>
</article>
<footer></footer>
</body>
</html>
