$(function () {
    "use strict";


    // ==============================================================
    // Graphs of 4 countries
    // ==============================================================
    //INDIA
    $.ajax({
        url: '/rest/country/India',
        success: function (data) {
            $("#sparkline-india").sparkline(data.map((data) => data.numberOfPeople), {
                type: 'line',
                width: '99.5%',
                height: '100',
                lineColor: '#5969ff',
                fillColor: '#dbdeff',
                lineWidth: 2,
                spotColor: undefined,
                minSpotColor: undefined,
                maxSpotColor: undefined,
                highlightSpotColor: undefined,
                highlightLineColor: undefined,
                resize: true
            });
        }
    });


    //China
    $.ajax({
        url: '/rest/country/China',
        success: function (data) {
            $("#sparkline-china").sparkline(data.map((data) => data.numberOfPeople), {
                type: 'line',
                width: '99.5%',
                height: '100',
                lineColor: '#ff407b',
                fillColor: '#ffdbe6',
                lineWidth: 2,
                spotColor: undefined,
                minSpotColor: undefined,
                maxSpotColor: undefined,
                highlightSpotColor: undefined,
                highlightLineColor: undefined,
                resize: true
            });
        }
    });


    //Italy
    $.ajax({
        url: '/rest/country/Italy',
        success: function (data) {
            $("#sparkline-italy").sparkline(data.map((data) => data.numberOfPeople), {
                type: 'line',
                width: '99.5%',
                height: '100',
                lineColor: '#25d5f2',
                fillColor: '#dffaff',
                lineWidth: 2,
                spotColor: undefined,
                minSpotColor: undefined,
                maxSpotColor: undefined,
                highlightSpotColor: undefined,
                highlightLineColor: undefined,
                resize: true
            });
        }
    });


    //Germany
    $.ajax({
        url: '/rest/country/Germany',
        success: function (data) {
            $("#sparkline-germany").sparkline(data.map((data) => data.numberOfPeople), {
                type: 'line',
                width: '99.5%',
                height: '100',
                lineColor: '#fec957',
                fillColor: '#fff2d5',
                lineWidth: 2,
                spotColor: undefined,
                minSpotColor: undefined,
                maxSpotColor: undefined,
                highlightSpotColor: undefined,
                highlightLineColor: undefined,
                resize: true,
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
                    data: dataSet,
                    columns: [
                        {title: "Country"},
                        {title: "Total Cases TIll Now"},
                        {title: "New cases today"}
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

