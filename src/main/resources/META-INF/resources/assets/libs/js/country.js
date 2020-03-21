$(function () {
    "use strict";


    $.ajax({
        type: "GET",
        url: "/rest/country/stats",
        dataType: "json",
        success: function (data) {
            $.each(data, function (i, obj) {
                var div_data = "<option value='" + obj.country + "'>" + obj.country + "(" + obj.latestTotalCases + ")</option>";
                $(div_data).appendTo('#input-country');
            });
            buildChart();
        }
    });


    $("#input-country").change(function () {
       buildChart();
    });


    var newdata;


    var buildChart = function () {
        var country = encodeURIComponent($('#input-country').val());
        console.log(country);
        $.getJSON('/rest/country/' + country, function (data) {
            newdata = [];
            data.map(function (value) {
                newdata.push([value.date, value.numberOfPeople]);
            });

            // draw chart
            $('#container').highcharts({
                chart: {
                    type: "line"
                },
                tooltip: {
                    xDateFormat: '%Y-%m-%d'
                },
                title: {
                    text: $('#input-country').val(),
                    align: 'center'
                },
                xAxis: {
                    type: 'datetime',
                },
                yAxis: {
                    title: {
                        text: "Number of people"
                    }
                },
                series: [{
                    name:$('#input-country').val(),
                    data: newdata
                }]
            });
        });
    }


});

