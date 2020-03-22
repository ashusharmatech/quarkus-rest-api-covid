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
            var confirmed = [];
            var recovered = [];
            var death = [];
            var active = [];


            data.map(function (value) {
                confirmed.push([value.date, value.confirmed]);
                recovered.push([value.date, value.recovered]);
                death.push([value.date, value.death]);
                active.push([value.date, value.confirmed - value.recovered - value.death]);
            });

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
                    data: confirmed
                },
                    {
                        name: "Recovered Cases",
                        data: recovered
                    },
                    {
                        type: "column",
                        color: "#ff5b5d",
                        name: "Death Cases",
                        data: death
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
                    data: active
                }
                ]
            });

        });
    }


});

