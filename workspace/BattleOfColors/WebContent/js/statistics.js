/**
 * Javascript obsługujący ekran statystyk.
 * 
 * @author Mateusz Kamiński
 */

/**
 * Status przesyłany do serwera
 */
var state = "start";

var dataMoveRanges = [];
var dataMeanMove = [];
var winnersTable = [];
var dataFirstPlayerMeanMove = [];
var dataSecondPlayerMeanMove = [];
var dataOverall;
/**
 * Konfiguracja ajax'a.
 */
var ajaxConfiguration = {
	url : '/StatisticsServlet',
	type : 'GET',
	dataType : 'json'
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

		ajaxConfiguration.url = '/BattleOfColors/StatisticsServlet?' + "state=" + state
				+ "&numberOfGames=" + number + "&player1=" + player1
				+ "&player2=" + player2 + "&level1=" +
				level1 + "&level2=" + level2 + "&size=" + size;
		state = "calculating";
	} else if (state === "calculating") {
		ajaxConfiguration.url = '/BattleOfColors/StatisticsServlet?state=' + state;
	} else if (state === "complete") {
		
	} else {
		
	}
}

function prepareDataForHighcharts(json) {
	var resultNumber = 0;
	for (var result in json) {
		if (resultNumber == 0) {
			dataOverall = json[result];
			console.log(dataOverall);
		} else {
			dataMoveRanges.push([resultNumber, parseFloat(json[result].slowestMove) / 1000000, parseFloat(json[result].fastestMove)/ 1000000]);
			dataMeanMove.push([resultNumber, parseFloat(json[result].meanMoveTime)/ 1000000]);
			dataFirstPlayerMeanMove.push([resultNumber, parseFloat(json[result].firstPlayerMeanMoveTime)/ 1000000]);
			dataSecondPlayerMeanMove.push([resultNumber, parseFloat(json[result].secondPlayerMeanMoveTime)/ 1000000]);
		}
		resultNumber += 1;
	}
	console.log(dataMoveRanges);
}

function handleResponse(response) {
	if(response === "calculating"){
		document.getElementById("pasek").innerHTML = "Proszę czekać";
		changeAjaxConfiguration();
		isStarted = true;
	} else if (response === "complete") {
		document.getElementById("pasek").innerHTML = "Koniec";
		window.clearInterval(timerId);
	} else if (response != null) {
		$('#pasek').hide();
		$('.results').show();
		prepareDataForHighcharts(response);
		winnersTable.push(["gracz 1", parseFloat(dataOverall.player1wins)]);
		winnersTable.push(["gracz 2", parseFloat(dataOverall.player2wins)]);
		
		drawHighcharts();
		//$('#overall').html(dataOverall.player1wins + " " + dataOverall.player2wins + " " + dataOverall.wholeMeanMoveTime + " " + dataOverall.wholeGameTime);
		$('#whole_time').html(parseFloat(dataOverall.wholeGameTime) / 1000 + ' s');
		$('#move_mean_whole_time').html((parseFloat(dataOverall.wholeMeanMoveTime) / 1000000).toFixed(3) + ' ms');
		$('#mean_move_whole_number').html(parseFloat(dataOverall.wholeMeanNumberOfMoves).toFixed(0));
		$('#first_player_wins').html(parseInt(dataOverall.player1wins));
		$('#second_player_wins').html(parseInt(dataOverall.player2wins));
		window.clearInterval(timerId);
	} else {
		document.getElementById("pasek").innerHTML = "błąd responsa";
		window.clearInterval(timerId);
	}
}

var timerId;
var isStarted = false;

/**
 * Funkcja obsługująca błąd odpowiedzi z serwera.
 */
function handleError(error) {
	clearInterval(timerId);
	console.log("Wystapil blad");	
	console.log(error);
}

function startAjax() {
	if (isStarted) {
		isStarted = false;
		var request = $.ajax(ajaxConfiguration);
		request.done(handleResponse);
		request.fail(handleError);
	}
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
		$('#pasek').show();
		changeAjaxConfiguration();
		isStarted = true;
	}
}

$(document).ready(function() {
	$('.results').hide();
	$('#pasek').hide();
	$('.start').click(statisticsHandler);
	timerId = window.setInterval(startAjax, 500);
	$body = $("body");

	$(document).on({
	    ajaxStart: function() { $body.addClass("loading");    },
	     ajaxStop: function() { $body.removeClass("loading"); }    
	});
});



function drawHighcharts() {

    var ranges = dataMoveRanges,
        averages = dataMeanMove,
        winner = winnersTable;

    $('#move_time_container').highcharts({

        title: {
            text: 'Czas wykonywanego ruchu'
        },

        xAxis: {
        	title: {
                text: 'numer gry'
            },
        	type: 'linear',
            min: 1,
            tickInterval: 1
        },

        yAxis: {
            title: {
                text: 'czas'
            },
            min: 0,
            minPadding: 0.1
        },

        tooltip: {
            crosshairs: true,
            shared: true,
            valueSuffix: 'ms'
        },

        legend: {
        },

        series: [{
            name: 'Średni czas ruchu',
            data: averages,
            zIndex: 1,
            marker: {
                fillColor: 'white',
                lineWidth: 2,
                lineColor: Highcharts.getOptions().colors[0]
            }
        }, {
            name: 'Przedział (max - min)',
            data: ranges,
            type: 'arearange',
            lineWidth: 0,
            linkedTo: ':previous',
            color: Highcharts.getOptions().colors[0],
            fillOpacity: 0.3,
            zIndex: 0
        }, {
            name: 'Średni czas ruchu',
            data: averages,
            zIndex: 1,
            marker: {
                fillColor: 'white',
                lineWidth: 2,
                lineColor: Highcharts.getOptions().colors[0]
            }
        },{
            name: 'Średni czas ruchu pierwszego gracza',
            data: dataFirstPlayerMeanMove,
            zIndex: 1,
            marker: {
                fillColor: 'white',
                lineWidth: 2,
                lineColor: Highcharts.getOptions().colors[0]
            }
        },{
            name: 'Średni czas ruchu drugiego gracza',
            data: dataSecondPlayerMeanMove,
            zIndex: 1,
            marker: {
                fillColor: 'white',
                lineWidth: 2,
                lineColor: Highcharts.getOptions().colors[0]
            }
        },]
    });
    
    $('#wins_container').highcharts({
    	chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            }
        },
    	title: {
            text: 'Liczba zwycięstw'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 35,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}'
                }
            }
        },
        series: [{
            type: 'pie',
            name: 'procent wygranych',
            data: winnersTable
        }]
    });

}