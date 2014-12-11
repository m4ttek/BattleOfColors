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
			dataMoveRanges.push([resultNumber, parseInt(json[result].slowestMove), parseInt(json[result].fastestMove)]);
			dataMeanMove.push([resultNumber, parseInt(json[result].meanMoveTime)]);
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
		drawHighcharts();
		$('#overall').html(dataOverall.player1wins + " " + dataOverall.player2wins + " " + dataOverall.wholeMeanMoveTime + " " + dataOverall.wholeGameTime);
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
});



function drawHighcharts() {

    var ranges = dataMoveRanges,
        averages = dataMeanMove;

    $('#container').highcharts({

        title: {
            text: 'Czas wykonywanego ruchu'
        },

        xAxis: {
        	title: {
                text: 'numer gry'
            },
        	type: 'number',
            min: 0
        },

        yAxis: {
            title: {
                text: 'czas'
            },
            min: 0
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
            name: 'Przedział (min - max)',
            data: ranges,
            type: 'arearange',
            lineWidth: 0,
            linkedTo: ':previous',
            color: Highcharts.getOptions().colors[0],
            fillOpacity: 0.3,
            zIndex: 0
        }]
    });
    
    Highcharts.createElement('link', {
    	   href: 'http://fonts.googleapis.com/css?family=Signika:400,700',
    	   rel: 'stylesheet',
    	   type: 'text/css'
    	}, null, document.getElementsByTagName('head')[0]);

    	// Add the background image to the container
    Highcharts.wrap(Highcharts.Chart.prototype, 'getContainer', function (proceed) {
    	proceed.call(this);
    	this.container.style.background = 'url(http://www.highcharts.com/samples/graphics/sand.png)';
    });

    Highcharts.theme = {
    		colors: ["#f45b5b", "#8085e9", "#8d4654", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
    		         "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
    		         chart: {	
    		        	 backgroundColor: null,
    		        	 style: {
    		        		 fontFamily: "Signika, serif"
    		        	 }
    		         },
    	   title: {
    	      style: {
    	         color: 'black',
    	         fontSize: '16px',
    	         fontWeight: 'bold'
    	      }
    	   },
    	   subtitle: {
    	      style: {
    	         color: 'black'
    	      }
    	   },
    	   tooltip: {
    	      borderWidth: 0
    	   },
    	   legend: {
    	      itemStyle: {
    	         fontWeight: 'bold',
    	         fontSize: '13px'
    	      }
    	   },
    	   xAxis: {
    	      labels: {
    	         style: {
    	            color: '#6e6e70'
    	         }
    	      }
    	   },
    	   yAxis: {
    	      labels: {
    	         style: {
    	            color: '#6e6e70'
    	         }
    	      }
    	   },
    	   plotOptions: {
    	      series: {
    	         shadow: true
    	      },
    	      candlestick: {
    	         lineColor: '#404048'
    	      },
    	      map: {
    	         shadow: false
    	      }
    	   },

    	   // Highstock specific
    	   navigator: {
    	      xAxis: {
    	         gridLineColor: '#D0D0D8'
    	      }
    	   },
    	   rangeSelector: {
    	      buttonTheme: {
    	         fill: 'white',
    	         stroke: '#C0C0C8',
    	         'stroke-width': 1,
    	         states: {
    	            select: {
    	               fill: '#D0D0D8'
    	            }
    	         }
    	      }
    	   },
    	   scrollbar: {
    	      trackBorderColor: '#C0C0C8'
    	   },

    	   // General
    	   background2: '#E0E0E8'
    	   
    	};

    	// Apply the theme
    	Highcharts.setOptions(Highcharts.theme);
}