//hide the results part initially
$(document).ready(function () {
  $("#display-results").hide();
  google.charts.load('current', {'packages': ['gauge', 'corechart', 'bar']});
});

//on form input
$('#input-text-form').submit(function (e) {
  e.preventDefault();

  //get inputs
  var inputType = $('#input-text-options').val();
  
  switch(inputType) {
    case "text":
      var inputTextElement = document.getElementById("input-text");
      analyzeText(inputTextElement.value);
      inputTextElement.value = "";
      break;

    case "file":
      console.log('Reading file');
      var fileSelectorElement = document.getElementById("file-selector");
      if (fileSelectorElement.files.length == 0) {
        alert("No file selected");
      } else {
        readText(fileSelectorElement.files[0]);
        fileSelectorElement.value = "";
      }
      break;
  }

});

function readText(file) {
  //Check for .txt file
  if (file.name.split('.').pop().toLowerCase() != "txt") {
    console.log('Not a text file.', file.type, file);
    alert("Error: Invalid file");
    return;
  }

  const reader = new FileReader();
  reader.addEventListener('load', (event) => {
    var contents = reader.result;
    console.log(contents);
    analyzeText(contents);
  });
  reader.readAsText(file);
}

//display loading gif till response is recieved
$(document).ajaxStart(function () {
  $('#loading').show();  
});

$(document).ajaxStop(function() {
  $('#loading').hide();  
});


function analyzeText(inputText) {
  if (inputText.length == 0) {
      alert("Enter text to be analyzed");
      return;
  }
        
  var textObject = {
    "text": inputText
  }
  
  $.ajax({
    url: "/analyze/text",
	type: 'POST',
	dataType: 'json',
	data: JSON.stringify(textObject),
	contentType: 'application/json',
	mimeType: 'application/json',

	success: function (response) {
      setScore(response);
      document.title = "Your Results are here!";
			document.getElementById("input_text_div").textContent = inputText;
    },
	
    error: function (xhr, status, er) {
	  alert("Error: " + xhr.responseText);
	}
  });
}

//sets score according to response from the query
function setScore(result){
  //do stuff
  displayResults(result); 
}


function displayResults(result) {
	drawGaugeChart(result.score);
	drawEntitiesBarChart(result.entities);

  drawWordCloud(result.wordcount, "wordcloud");
  drawWordCloud(result.saliences, "salience_wordcloud");

  //unhide the results
  $("#display-results").show();
}

//gauge-chart
function drawGaugeChart(score) {
  var data = google.visualization.arrayToDataTable([
    ['Sentiment', 'Value'],
		['Score', score]
  ]);

  var options = {
    min: -1,
		max: 1,
		redFrom: -1,
		redTo: 0,
		minorTicks: 5
  };

  var chart = new google.visualization.Gauge(document.getElementById('chart_div'));
  chart.draw(data, options);
}

function drawEntitiesBarChart(entity_sentiments) {

	var entities = Object.keys(entity_sentiments);

	if(entities.length > 0) {
		var entity_sentiments_bar = [['Entity', 'Sentiment']];
		entities.forEach(entity => {
			entity_sentiments_bar.push([entity, entity_sentiments[entity]]);
		});

		var data = google.visualization.arrayToDataTable(entity_sentiments_bar);

		var options = {
			title: 'Entity - Sentiments',
			chartArea: {width: '50%'},
			hAxis: {
				title: 'Sentiment',
				minValue: -1,
				textStyle: {
					bold: true,
					fontSize: 12,
				},
				titleTextStyle: {
					bold: true,
					fontSize: 18,
				}
			},
			vAxis: {
				title: 'Entity',
				textStyle: {
					fontSize: 14,
					bold: true,
				},
				titleTextStyle: {
					fontSize: 14,
					bold: true,
				}
			}
		};
		var chart = new google.visualization.BarChart(document.getElementById('entities_div'));
		chart.draw(data, options);
	} else {
		document.getElementById('entities_div').textContent = "No entities found."
	}
}

//wordcloud
function drawWordCloud(wordObjects, divname){
  anychart.onDocumentReady(function () {
    var data =[];
    console.log(wordObjects);

    for(word in wordObjects){
      wordObject = {
        "x":word,
        "value":wordObjects[word]
      }
      data.push(wordObject);
    }

    console.log(data);

    var chartContainer = document.getElementById(divname);
    chartContainer.innerHTML = '';

    // create a tag cloud chart
    var chart = anychart.tagCloud(data);
  
    // set the chart title
    chart.title('Sentiment Analysis ')
    // set array of angles, by which words will be placed
    chart.angles([0])
    // display chart
    chart.container(divname);
    chart.draw();
  });

}
