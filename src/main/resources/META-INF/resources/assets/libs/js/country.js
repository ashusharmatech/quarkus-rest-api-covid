$(function () {
    "use strict";


    $.ajax({
        type: "GET",
        url: "/rest/country/stats",
        dataType: "json",
        success: function (data) {
            $.each(data, function (i, obj) {
                var div_data = "<option value='" + obj.country + "'>" + obj.country + "[ Confirmed Cases: " + obj.latestConfirmedCases + "]</option>";
                $(div_data).appendTo('#input-country');
            });
            $('#input-country').val('India');
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

            // draw chart
            $('#complete').highcharts({
                chart: {
                    type: "line"
                },
                tooltip: {
                    xDateFormat: '%Y-%m-%d',
                    shared: true
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
                    name: "Confirmed Cases",
                    data: data.map((data) => [data.date, data.confirmed])
                },
                    {
                        name: "Recovered Cases",
                        data: data.map((data) => [data.date, data.recovered])

                    },
                    {
                        type: "column",
                        color: "#ff5b5d",
                        name: "Death Cases",
                        data: data.map((data) => [data.date, data.death])
                    }
                ]
            });


            // draw chart
            $('#active').highcharts({
                chart: {
                    type: "line"
                },
                tooltip: {
                    xDateFormat: '%Y-%m-%d',
                    shared: true
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
                    name: "Active",
                    data: data.map((data) => [data.date, (data.confirmed - data.recovered - data.death)])
                }
                ]
            });

        });
    }


});

