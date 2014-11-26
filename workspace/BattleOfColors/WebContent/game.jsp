<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="pl" />
<title>Battle of Colors - gra</title>
<link rel="stylesheet" href="game.css" media="screen" type="text/css"  />
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<link href='http://fonts.googleapis.com/css?family=Shadows+Into+Light' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Voltaire' rel='stylesheet' type='text/css'>
<script>
	$(document).ready(function() {	//call when document loaded
		var handle;
		$('.map').hide();
		$('.panel').hide();
		$('.prev').hide();
		$('.next').hide();
		
		//Akcja przycisku dalej
		$('a.next').click(function(e) {		//set onclick callback
			e.preventDefault();
			if($(this).hasClass('disabled'))
				return;
			$.ajax({
				url: "PlayerMoveServlet",
				type: 'GET',
				contentType: 'application/json',
				dataType: 'json',
				data: {x:"x"},
				success: function(data) {		//call after successful call
					if(data=="empty")
						return;
					var temp=data.split("*");
					
					var array=temp[1].split("+");
					var playerid=parseInt(array[0]);
					var nrfields=parseInt(array[1]);
					var turn=parseInt(array[2]);
					var playerType=array[3];
				
					$('.panel .stats p.player'+(playerid)).html('gracz#'+(playerid+1)+': '+nrfields);
					$('.panel .stats p.turn').html('ruch: '+turn);
					
					var availableColors=array[4].split("|");
					
					$('.panel ul').html('');
					for(var i=0;i<availableColors.length-1;i++) {	
						$('.panel ul').append('<li class="'+availableColors[i].toLowerCase()+'">'+availableColors[i].toUpperCase()+'</li>');
					}
					
					var colors=array[5].split("#");
					
					var width=parseInt($('.game #size').html());
				    var height=width;
				    for(var i=0;i<height;i++){
				    	colors[i]=colors[i].split("|");
				    }
				 
				    for(var i=0;i<height;i++){
				    	for(var j=0;j<width;j++){
				    		if(!($('.map div#'+(i*width+j)).hasClass(colors[i][j].toLowerCase()))){
				    			$('.map div#'+(i*width+j)).remove();
				    			if(i==0 && j==0)
				    				$('.map').prepend('<div id="'+(i*width+j)+'" class="square '+(colors[i][j].toLowerCase())+'" style="left:0px;top:0px;"></div>');
				    			else
				    				$('.map div#'+(i*width+j-1)).after('<div id="'+(i*width+j)+'" class="square '+(colors[i][j].toLowerCase())+'" style="left:'+((500*j)/width)+'px;top:'+((500*i)/width)+'px;"></div>');
				    		}
				    	}
				    }
				    $('.square').css('width',(500/width)+'px');
				    $('.square').css('height',(500/height)+'px');
					
				    if(playerType=="HUMAN"){
						$('.menu a.next').css("background-color","#A3A2A0");
						$('.menu a.next').addClass('disabled');
					}
				    else if(playerType=="AI_MIN_MAX"){
						$('.panel li').css("background-color","#A3A2A0");
						$('.panel li').addClass('disabled');
					}
				    
					 //koniec gry
				    if(temp[0]!=""){
						temp=temp[0].split("+");
						var winner=parseInt(temp[0]);
						var p1Score=temp[1];
						var p2Score=temp[2];
						$('.panel').fadeOut();
						$('.menu a.prev').fadeOut();
						$('.menu a.next').fadeOut();
						var text;
						if(winner==-1)
							text="Remis!";
						else
							text="Wygrywa gracz "+(winner+1);
						$('.game').prepend('<div class="scores"><h1>'+text+
								'</h1><h1 style="color:#48add8;font-size:19px;">Gracz 1 uzyskał '+(p1Score)+' pól</h1>'+
								'</h1><h1 style="color:#d75eb6;font-size:19px;">Gracz 2 uzyskał '+(p2Score)+' pól</h1></div>');
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
				 	alert(xhr.status);
				    alert(thrownError);
			        alert("error");
			    }
			})
		});	
		//Akcja przycisku wyjscie
		$('a.quit').click(function(e) {		//set onclick callback
			e.preventDefault();
			$.ajax({
				url: "PlayerMoveServlet",
				type: 'POST',
				contentType: 'application/json',
				dataType: 'json',
				data: {x:"x"},
				success: function(data) {		//call after successful call
					window.location.href = "index.html";
				},
				error: function (xhr, ajaxOptions, thrownError) {
				 	alert(xhr.status);
				    alert(thrownError);
			        alert("error");
			    }
			})
		});	
		//Akcja przycisku rozpocznij formularza
		$('#content .options form .start').click(function(e) {		//set onclick callback
			e.preventDefault();
	
			var p1=$( "#content form select[name='player1']").val();	
			var p2=$( "#content form select[name='player2']").val();
			var lvl1=$( "#content form select[name='level1']").val();	
			var lvl2=$( "#content form select[name='level2']").val();	
			var s=$( "#content form select[name='size']").val();
			$.ajax({
				url: "PlayerMoveServlet",
				type: 'GET',
				contentType: 'application/json',
				dataType: 'json',
				data: {player1: p1, player2: p2, level1: lvl1, level2: lvl2, size: s},
				success: function(data) {		//call after successful call
					var array=data.split("+");
					var playerType=array[0];
					var width=array[1];
					var height=array[2];
					
					var availableColors=array[3].split("|");
					
					
					
					$('.game').prepend('<div id="size" style="display:none;">'+width+'</div>');
					
					$('.panel ul').html('');
					for(var i=0;i<availableColors.length-1;i++) {	
						$('.panel ul').append('<li class="'+availableColors[i].toLowerCase()+'">'+availableColors[i].toUpperCase()+'</li>');
					}
					
					var temp=array[4];
					
					var colors=temp.split("#");
					
				    //var colors=new Array(height);
				    for(var i=0;i<height;i++){
				    	colors[i]=colors[i].split("|");
				    }
				    
				    for(var i=0;i<height;i++){
				    	for(var j=0;j<width;j++){
				    		 $('.map').append('<div id="'+(i*width+j)+'" class="square '+(colors[i][j].toLowerCase())+'" style="left:'+((500*j)/width)+'px;top:'+((500*i)/height)+'px;"></div>');
					    }
				    }
				    if(playerType=="AI_MIN_MAX"){
						$('.panel ul li').css("background-color","#A3A2A0");
						$('.panel li').addClass('disabled');
					}
				    else if(playerType=="HUMAN"){
				    	$('.menu a.next').css("background-color","#A3A2A0");
						$('.menu a.next').addClass('disabled');
				    }
				    
				    
				    $('.square').css('width',(500/width)+'px');
				    $('.square').css('height',(500/height)+'px');
				    $('.options').fadeOut();
				    $('.map').fadeIn();
					$('.panel').fadeIn();
					$('.prev').fadeIn();
					$('.next').fadeIn();
					
				   
				},
				error: function (xhr, ajaxOptions, thrownError) {
				 	alert(xhr.status);
				    alert(thrownError);
			        alert("error");
			    }
			})
		});		
		//Akcja przycisku z kolorem
		$('.panel').on( 'click','li',function(e) {		//set onclick callback
				e.preventDefault();
				if($(this).hasClass('disabled'))
					return;
				
				var chosenColor = $(this).attr('class').toUpperCase();
					$.ajax({
						url: "PlayerMoveServlet",
						type: 'GET',
						contentType: 'application/json',
						dataType: 'json',
						data: { color: chosenColor },
						success: function(data) {		//call after successful call
							if(data=="empty")
								return;
						
							var temp=data.split("*");

							var array=temp[1].split("+");
							var playerid=parseInt(array[0]);
							var nrfields=parseInt(array[1]);
							var turn=parseInt(array[2]);
							var playerType=array[3];
						
							$('.panel .stats p.player'+(playerid)).html('gracz#'+(playerid+1)+': '+nrfields);
							$('.panel .stats p.turn').html('ruch: '+turn);
							
							var availableColors=array[4].split("|");
							
							$('.panel ul').html('');
							for(var i=0;i<availableColors.length-1;i++) {	
								$('.panel ul').append('<li class="'+availableColors[i].toLowerCase()+'">'+availableColors[i].toUpperCase()+'</li>');
							}
							
							var colors=array[5].split("#");
							
							var width=parseInt($('.game #size').html());
						    var height=width;
						    for(var i=0;i<height;i++){
						    	colors[i]=colors[i].split("|");
						    }
						 
						    for(var i=0;i<height;i++){
						    	for(var j=0;j<width;j++){
						    		if(!($('.map div#'+(i*width+j)).hasClass(colors[i][j].toLowerCase()))){
						    			$('.map div#'+(i*width+j)).remove();
						    			if(i==0 && j==0)
						    				$('.map').prepend('<div id="'+(i*width+j)+'" class="square '+(colors[i][j].toLowerCase())+'" style="left:0px;top:0px;"></div>');
						    			else
						    				$('.map div#'+(i*width+j-1)).after('<div id="'+(i*width+j)+'" class="square '+(colors[i][j].toLowerCase())+'" style="left:'+((500*j)/width)+'px;top:'+((500*i)/width)+'px;"></div>');
						    		}
						    	}
						    }
						    $('.square').css('width',(500/width)+'px');
						    $('.square').css('height',(500/height)+'px');
						    
						    if(playerType=="AI_MIN_MAX"){
								$('.panel li').css("background-color","#A3A2A0");
								$('.panel li').addClass('disabled');
								$('.menu a.next').removeClass('disabled');
								$('.menu a.next').css("background-color","#ec313e");
							}
						    
						    //koniec gry
						    if(temp[0]!=""){
								temp=temp[0].split("+");
								var winner=parseInt(temp[0]);
								var p1Score=temp[1];
								var p2Score=temp[2];
								$('.panel').fadeOut();
								$('.menu a.prev').fadeOut();
								$('.menu a.next').fadeOut();
								var text;
								if(winner==-1)
									text="Remis!";
								else
									text="Wygrywa gracz "+(winner+1);
								$('.game').prepend('<div class="scores"><h1>'+text+
										'</h1><h1 style="color:#48add8;font-size:19px;">Gracz 1 uzyskał '+(p1Score)+' pól</h1>'+
										'</h1><h1 style="color:#d75eb6;font-size:19px;">Gracz 2 uzyskał '+(p2Score)+' pól</h1></div>');
							}
						},
						error: function (xhr, ajaxOptions, thrownError) {
						 	alert(xhr.status);
						    alert(thrownError);
					        alert("error");
					    }
					})
				});
	});
</script>
</head>
<body>
<img id="bg" src="images/bg.jpg" />
<div id="header">
	<div class="container">
		<div id="title">Battle of Colors</div>
	</div>
</div>
<div id="content">
	<div class="container">
		<div class="menu">
			<ul>
				<li><a class="quit" href="index.html">Wyjście z gry</a></li>
				<li><a class="prev">Wstecz</a></li>
				<li><a class="next">Dalej</a></li>
			</ul>
		</div>
		
		<div class="options">
			<h1>Ustawienia gry</h1>
			<form>
				<h1>Gracz 1</h1>
				<div class="sel">
					<select name="player1">
						<option value="HUMAN">Człowiek</option>
						<option value="AI_MIN_MAX">AI MINI-MAX</option>
					</select>
				</div>
				<h1>Gracz 2</h1>
				<div class="sel">
					<select name="player2">
						<option value="HUMAN">Człowiek</option>
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
		
			<div class="game">
				<div class="map" style="width:500px;height:500px;">
				</div>
				<div class="panel" style="width:600px">
					<div class="stats">
						<p class="player0">gracz#1: 0</p>
						<p class="turn">ruch: 1</p>
						<p class="player1">gracz#2: 0</p>
						<br />
					</div>
					<ul>
						<li class="red">RED</li>
						<li class="green">GREEN</li>
						<li class="blue">BLUE</li>
						<li class="pink">PINK</li>
						<li class="yellow">YELLOW</li>
						<li class="brown">BROWN</li>
					</ul>
				</div>
			</div>
			
	</div>
</div>

</body>
</html>