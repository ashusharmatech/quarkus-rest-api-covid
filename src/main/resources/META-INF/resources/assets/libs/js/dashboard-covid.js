$(function () {
    "use strict";


    // ==============================================================
    // Graphs of 4 countries
    // ==============================================================
    //INDIA
    $.ajax({
        url: '/rest/country/India',
        success: function (data) {
            Highcharts.chart("sparkline-india", {
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
                    lineColor: '#5969ff',
                    fillColor: '#dbdeff',
                    data: data.map((data) => data.confirmed)
                }]
            });
        }
    });


    //China
    $.ajax({
        url: '/rest/country/China',
        success: function (data) {
            Highcharts.chart("sparkline-china", {
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
                    lineColor: '#ff407b',
                    fillColor: '#ffdbe6',
                    data: data.map((data) => data.confirmed)
                }]
            });
        }
    });


    //Italy
        $.ajax({
            url: '/rest/country/Italy',
            success: function (data) {
                Highcharts.chart("sparkline-italy", {
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
                        lineColor: '#25d5f2',
                        fillColor: '#dffaff',
                        data: data.map((data) => data.confirmed)
                    }]
                });
            }
        });


    //Germany
    $.ajax({
        url: '/rest/country/Germany',
        success: function (data) {
            Highcharts.chart("sparkline-germany", {
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
                    lineColor: '#fec957',
                    fillColor: '#fff2d5',
                    data: data.map((data) => data.confirmed)
                }]
            });
        }
    });



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

