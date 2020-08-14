google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(drawChart);


function drawChart() {
  var data = google.visualization.arrayToDataTable([
    ['Sentiment', 'Relative score'],
    ['Negative', 11],
    ['Postive', 2]
  ]);

  var options = {
    title: 'Sentiment Scores',
    is3D: true,
  };

  var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));
  chart.draw(data, options);
}





google.charts.load('current', { 'packages': ['gauge'] });
google.charts.setOnLoadCallback(drawGaugeChart);

function drawGaugeChart() {

  var data = google.visualization.arrayToDataTable([
    ['Sentiment', 'Value'],
    ['Postive', 80],
    ['Negative', 55]
  ]);

  var options = {
    width: 500, height: 200,
    redFrom: 90, redTo: 100,
    yellowFrom: 75, yellowTo: 90,
    minorTicks: 5
  };

  var chart = new google.visualization.Gauge(document.getElementById('chart_div'));

  chart.draw(data, options);

  setInterval(function () {
    data.setValue(0, 1, 40 + Math.round(60 * Math.random()));
    chart.draw(data, options);
  }, 13000);
  setInterval(function () {
    data.setValue(1, 1, 40 + Math.round(60 * Math.random()));
    chart.draw(data, options);
  }, 5000);
  setInterval(function () {
    data.setValue(2, 1, 60 + Math.round(20 * Math.random()));
    chart.draw(data, options);
  }, 26000);

}

//wordcloud

anychart.onDocumentReady(function () {

  //dummy data
  var data = [
    { "x": "Mandarin chinese", "value": 0.9 },
    { "x": "English", "value": -0.2 },
    { "x": "Hindustani", "value": 0.1 },
    { "x": "Spanish", "value": 0.08 },
    { "x": "Arabic", "value": -0.3 },
    { "x": "Malay", "value": -0.87 },
    { "x": "Russian", "value": 0.5 },
    { "x": "Bengali", "value": 0.261 },
    { "x": "Portuguese", "value": 0.229 },
    { "x": "French", "value": -0.229 },
    { "x": "Hausa", "value": 0.150 },
    { "x": "Punjabi", "value": -0.148 },
    { "x": "Japanese", "value": 0.29 },
    { "x": "German", "value": -0.9 },
    { "x": "Persian", "value": 0.121 }
  ];

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
