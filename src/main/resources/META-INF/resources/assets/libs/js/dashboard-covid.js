$(function () {
    "use strict";


    // ==============================================================
    // Graphs of 4 sparklne
    // ==============================================================
    function loadSparkLine(country,lineColor,fillColor) {
        $.ajax({
            url: '/rest/country/'+country,
            success: function (data) {
                Highcharts.chart("sparkline-"+country.toLowerCase(), {
                    chart: {
                        backgroundColor: null,
                        borderWidth: 0,
                        type: 'area',
                        height: '100',
                        style: {
                            overflow: 'visible'
                        },

                        // small optimalization, saves 1-2 ms each sparkline
                        skipClone: true

                    },
                    title: {
                        text: ''
                    },
                    credits: {
                        enabled: false
                    },
                    xAxis: {
                        type: 'datetime',
                        labels: {
                            enabled: false
                        }
                    },
                    yAxis: {
                        maxPadding: 0,
                        minPadding: 0,
                        gridLineWidth: 0,
                        endOnTick: false,
                        labels: {
                            enabled: false
                        },
                        title: {
                            text: null
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    series: [{
                        lineColor: lineColor,
                        fillColor: fillColor,
                        data: data.map((data) => [data.date, data.confirmed])
                    }]
                });
            }
        });
    }


    loadSparkLine('India','#5969ff','#dbdeff');
    loadSparkLine('China','#ff407b','#ffdbe6');
    loadSparkLine('Italy','#25d5f2','#dffaff');
    loadSparkLine('Germany','#fec957','#fff2d5');



    // ==============================================================
    // Country Data - Data Table
    // ==============================================================
    $.ajax({
        type: "GET",
        url: '/rest/country/stats',
        dataType: 'json',

        success: function (obj, textstatus) {

            var dataSet = new Array;
            if (!('error' in obj)) {
                $.each(obj, function (index, value) {
                    var tempArray = new Array;
                    for (var o in value) {
                        tempArray.push(value[o]);
                    }
                    dataSet.push(tempArray);
                })

                $('#countryDataTable').DataTable({
                    lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
                    order: [[1, "desc"]],
                    data: dataSet,
                    columns: [
                        {title: "Country"},
                        {title: "Total Cases TIll Now"},
                        {title: "New Cases Today"},
                        {title: "Deaths"},
                        {title: "New Deaths Today"},
                        {title: "Recovered Cases"},
                        {title: "New Recoveries Today"},
                    ]
                });
            } else {
                alert(obj.error);
            }
        },
        error: function (obj, textstatus) {
            alert(obj.msg);
        }
    });


});

