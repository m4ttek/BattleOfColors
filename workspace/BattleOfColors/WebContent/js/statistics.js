/**
 * Javascript obsługujący ekran statystyk.
 * 
 * @author Mateusz Kamiński
 */

/**
 * Informacja przesyłana do serwera
 */
var information = '';
var state = "start";
var timerId;
/**
 * Konfiguracja ajax'a.
 */
var ajaxConfiguration = {
	url : '/StatisticsServlet',
	type : 'GET',
	dataType : 'json',
	data : information
}

/**
 * Funkcja obsługująca odpowiedź z serwera.
 */
function changeAjaxConfiguration() {
	if (state === "start") {
		var number = document.getElementsByName('numberOfGames')[0].value;
		var player1 = document.getElementsByName('player1')[0].value;
		var player2 = document.getElementsByName('player2')[0].value;
		var level1 = document.getElementsByName('level1')[0].value;
		var level2 = document.getElementsByName('level2')[0].value;
		var size = document.getElementsByName('size')[0].value;

		ajaxConfiguration["url"] = '/BattleOfColors/StatisticsServlet?' + "state=" + state
				+ "&numberOfGames=" + number + "&player1=" + player1
				+ "&player2=" + player2 + "&level1=" +
				level1 + "&level2=" + level2 + "&size=" + size;
		console.log(ajaxConfiguration.url);
		state = "calculating";
	} else if (state === "calculating") {
		ajaxConfiguration["url"] = '/BattleOfColors/StatisticsServlet?state=' + state;
	} else if (state === "complete") {
		
	} else {
		
	}

}
function handleResponse(response) {
	if(response === "calculating"){
		document.getElementById("pasek").innerHTML = "Proszę czekać";
		startAjax();
	} else if (response === "complete") {
		document.getElementById("pasek").innerHTML = "Koniec";
	} else {
		document.getElementById("pasek").innerHTML = "błąd responsa";
	}
	console.log(response);
	console.log(state);
}

/**
 * Funkcja obsługująca błąd odpowiedzi z serwera.
 */
function handleError() {
	clearInterval(timerId);
	console.log("Wystapil blad");	
}
function startAjax() {
	var request = $.ajax(ajaxConfiguration);
	request.done(handleResponse);
	request.fail(handleError);
	changeAjaxConfiguration();
}

function statisticsHandler(event) {
	event.preventDefault();

	var inputField = document.getElementsByName('numberOfGames')[0].value;
	if (inputField === '' || isNaN(parseInt(inputField)) || inputField <= 0
			|| inputField > 10000) {
		var message = 'Podaj poprawną liczbę(1-10000)';
		document.getElementById("mess").innerHTML = message;
	} else {
		$('.options').hide();
		changeAjaxConfiguration();
		var request = $.ajax(ajaxConfiguration);
		request.done(handleResponse);
		request.fail(handleError);
		changeAjaxConfiguration();
	}
}

$(document).ready(function() {
	$('.start').click(statisticsHandler);
});