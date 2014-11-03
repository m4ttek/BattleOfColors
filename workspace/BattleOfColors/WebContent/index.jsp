<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>Battle of Colors</title>
</head>
<body>
<form action="game">
	<% for(int i = 0; i<2; i++) { %>
		<div style="margin-bottom: 10px;">
			<a>Player <%= i+1 %></a>
			<select name="<%= "player" + (i+1) + "Type" %>">
				<option value="AI">AI</option>
				<br>
				<option value="HUMAN">Human</option>
			</select>
		</div>
	<% } %>
	<div>
		<input type="submit"/>
	</div>
</form>
</body>
</html>