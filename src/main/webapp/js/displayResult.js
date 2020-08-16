//hide the results part initially
$(document).ready(function () {
  $("#display-results").hide();
});

//on form input
$('#input-text-form').submit(function (e) {
  e.preventDefault();

  //get inputs
  var inputType = document.getElementById("input-text-options");
  var inputText = document.getElementById("input-text");

  //input type text
  var textObject = {
    "text": inputText.value
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
    },
	
    error: function (data, status, er) {
	  alert("error: " + " status: " + status + " er:" + er);
	}
  });

  inputText.value = "";

});

var score = 0;

//sets score according to response from the query
function setScore(result){
  //do stuff
  score = result.score;
  displayResults(result); 
}


function displayResults(result) {
  google.charts.load('current', {'packages': ['gauge']});
  google.charts.setOnLoadCallback(drawGaugeChart);

  drawWordCloud(result.wordcount);

  //unhide the results
  $("#display-results").show();
}


//gauge-chart
function drawGaugeChart() {
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


//wordcloud
function drawWordCloud(wordObjects){
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
    // create a tag cloud chart
    var chart = anychart.tagCloud(data);
  
    // set the chart title
    chart.title('Sentiment Analysis ')
    // set array of angles, by which words will be placed
    chart.angles([0])
    // display chart
    chart.container("wordcloud");
    chart.draw();
  });

}
