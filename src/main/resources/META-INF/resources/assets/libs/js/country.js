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
            loadChartEvents();
        }
    });


    $("#input-country").change(function () {
        buildChart();
        loadChartEvents();
    });


    var plotLineChart = function (type, title, color, data) {
        // draw chart
        $('#' + type).highcharts({
            chart: {
                type: "area",
                marginLeft: 40, // Keep all charts left aligned
                spacingTop: 30,
                spacingBottom: 30
            },
            tooltip: {
                backgroundColor: 'none',
                xDateFormat: '%Y-%m-%d',
                shadow: false
            },
            title: {
                text: title,
                align: 'left',
                margin: 0,
                x: 30
            },
            credits: {
                enabled: false
            },
            legend: {
                enabled: false
            },
            xAxis: {
                type: 'datetime',
                crosshair: true,
                events: {
                    setExtremes: syncExtremes
                },
            },
            yAxis: {
                title: {
                    text: null
                }
            },
            series: [{
                name: title,
                data: data,
                color: color,
                fillOpacity: 0.3
            }]
        });
    };


    var loadChartEvents = function () {

        ['mousemove', 'touchmove', 'touchstart'].forEach(function (eventType) {
            document.getElementById('container').addEventListener(
                eventType,
                function (e) {
                    var chart,
                        point,
                        i,
                        event;

                    for (i = 0; i < Highcharts.charts.length; i = i + 1) {
                        chart = Highcharts.charts[i];
                        // Find coordinates within the chart
                        event = chart.pointer.normalize(e);
                        // Get the hovered point
                        point = chart.series[0].searchPoint(event, true);

                        if (point) {
                            point.highlight(e);
                        }
                    }
                }
            );
        });

        /**
         * Override the reset function, we don't need to hide the tooltips and
         * crosshairs.
         */
        Highcharts.Pointer.prototype.reset = function () {
            return undefined;
        };

        /**
         * Highlight a point by showing tooltip, setting hover state and draw crosshair
         */
        Highcharts.Point.prototype.highlight = function (event) {
            event = this.series.chart.pointer.normalize(event);
            this.onMouseOver(); // Show the hover marker
            //this.series.chart.tooltip.refresh(this); // Show the tooltip
            this.series.chart.xAxis[0].drawCrosshair(event, this); // Show the crosshair
        };


    }

    /**
     * Synchronize zooming through the setExtremes event handler.
     */
    function syncExtremes(e) {
        var thisChart = this.chart;

        if (e.trigger !== 'syncExtremes') { // Prevent feedback loop
            Highcharts.each(Highcharts.charts, function (chart) {
                if (chart !== thisChart) {
                    if (chart.xAxis[0].setExtremes) { // It is null while updating
                        chart.xAxis[0].setExtremes(
                            e.min,
                            e.max,
                            undefined,
                            false,
                            {trigger: 'syncExtremes'}
                        );
                    }
                }
            });
        }
    }


    var buildChart = function () {
        var country = encodeURIComponent($('#input-country').val());
        console.log(country);
        $.getJSON('/rest/country/' + country, function (data) {
            plotLineChart('confirmed', 'Confirmed Cases', '#58508d', data.map((data) => [data.date, data.confirmed]));
            //plotLineChart('recovered', 'Recovered Cases','#bc5090',data.map((data) => [data.date, data.recovered]));
            plotLineChart('death', 'Deaths', '#ff6361', data.map((data) => [data.date, data.death]));
            plotLineChart('active', 'Active Cases', '#003f5c', data.map((data) => [data.date, (data.confirmed - data.recovered - data.death)]));
        });
    }



});

