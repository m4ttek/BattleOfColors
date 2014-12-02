<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Statystyki</title>
<link rel="stylesheet" href="game.css" media="screen" type="text/css"  />
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<link href='http://fonts.googleapis.com/css?family=Shadows+Into+Light' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Voltaire' rel='stylesheet' type='text/css'>
<script>
	function statisticsHandler() {
		console.log("as" + this);
		$('.options').hide();
	}
	
	$(document).ready(function() {
		$('.start').click(statisticsHandler); 
	});
</script>
</head>
<body>
<div id="content">
	<div class="container">
	<div class="options">
			<h1>Ustawienia statystyk</h1>
			<form>
				<h1>Gracz 1</h1>
				<div class="sel">
					<select name="player1">
						<option value="AI_MIN_MAX">AI MINI-MAX</option>
					</select>
				</div>
				<h1>Gracz 2</h1>
				<div class="sel">
					<select name="player2">
						<option value="AI_MIN_MAX">AI MINI-MAX</option>
					</select>
				</div>
				<h1>Poziom AI 1</h1>
				<div class="sel">
					<select name="level1">
						<option value="1">Bardzo Mały</option>
						<option value="2">Mały</option>
						<option value="3">Średni</option>
						<option value="4">Wysoki</option>
					</select>
				</div>
				<h1>Poziom AI 2</h1>
				<div class="sel">
					<select name="level2">
						<option value="1">Bardzo Mały</option>
						<option value="2">Mały</option>
						<option value="3">Średni</option>
						<option value="4">Wysoki</option>
					</select>
				</div>
				<h1>Rozmiar mapy</h1>
				<div class="sel">
					<select name="size">
						<option value="10">Mała</option>
						<option value="20">Średnia</option>
						<option value="30">Duża</option>
					</select>
				</div>
				<div>
					<input class="start" type="submit" value="Rozpocznij" />
				</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>