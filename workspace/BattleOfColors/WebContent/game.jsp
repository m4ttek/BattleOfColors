<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Game</title>
<style>
	td { cursor: pointer; }
</style>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
	$(document).ready(function() {	//call when document loaded
		<% for(int i = 0; i<2; i++) {
			for(int j = 0; j<2; j++) { %>
				$('#td<%=i%><%=j%>').click(function() {		//set onclick callback
					$.ajax({
						url: "PlayerMoveServlet",
						type: 'GET',
						contentType: 'application/json',
						dataType: 'json',
						data: { x: "dupa dupa" },
						success: function(data) {		//call after successful call
							document.getElementById("td00").style.backgroundColor=data[0];
							document.getElementById("td01").style.backgroundColor=data[1];
							document.getElementById("td10").style.backgroundColor=data[2];
							document.getElementById("td11").style.backgroundColor=data[3];
						},
						error: function() {
							alert("ERROR!!!");
						}
					})
				});
			<%
			}
			
		} %>
	});
</script>
</head>
<body>
	<table border="1">
	<% for(int i = 0; i<2; i++) { %>
		<tr>
			<td id="td<%=i%>0"><%=(i==0 ? "Zielony" : "Błękitny") %></td>
			<td id="td<%=i%>1"><%= (i==0 ? "Żółty" : "Niebieski") %></td>
		</tr>
	<% } %>
	</table>
</body>
</html>