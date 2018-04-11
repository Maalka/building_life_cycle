
/*
 * A Timeline Graph Directive
 */
define(['angular', 'moment', 'highcharts', 'highcharts-drilldown', 'highcharts-exporting', 'highcharts-boost', './main'], function(angular, moment) {
    'use strict';
    var mod = angular.module('maalka.common.directives');
    mod.directive('timelineGraph', ['$timeout',
        function ($timeout) {

        return {
            restrict: 'A',
            replace: true,
            scope: {
                timelineGraph: "=",
                measures: "="
            },

            controller: ["$scope", "$element", function ($scope, $element) {
                $scope.$watchCollection("measures", function(measures) {
                    if (measures !== undefined && measures.length > 0) {
                        refresh();
                    }
                });
                var refresh = function() {

                    function sortDescByEndDate(a,b) {
                      if (a.endDate > b.endDate)
                        return -1;
                      if (a.endDate < b.endDate)
                        return 1;
                      return 0;
                    }
                    $timeout(function () {
                        $scope.measures.sort(sortDescByEndDate);
                        $scope.last5measures = $scope.measures.slice(0,5);
                        var newMeasures = [];
                        for (var i = 0; i < $scope.last5measures.length; i++) {
                            var randomColor = colors[Math.floor(Math.random()*colors.length)];
                            var endDate = moment.utc($scope.last5measures[i].endDate);
                            newMeasures.push({
                                x: Date.UTC(
                                    endDate.year(),
                                    endDate.month(),
                                    endDate.day()
                                ),
                                y: i % 5 * 10 + 10,
                                text: endDate.format("ll"),
                                dataLabels: {
                                    backgroundColor: randomColor,
                                    borderColor: randomColor,
                                },
                                name: $scope.last5measures[i].detail
                            });
                        }
                        $scope.options.series[0].data = newMeasures;
                        console.log($scope.options);
                        angular.element($element).height(300).highcharts($scope.options);
                    }, 0);
                };
                var colors = ['#06A1F9', '#0D95BB', '#0A708C', '#2F4598', '#5D70D4'];
                $scope.last5measures = [];
                $scope.options = {

                    title: {
                        text: '',
                    },
                    dataLabels: {
                        enabled: true,
                    },
                    chart: {
                        backgroundColor: "transparent",
                        style: {
                            fontFamily: 'Gesta',
                        }
                    },
                    "xAxis": {
                        "crosshair": true,
                        "title": {
                            "text": "Date Installed",
                            "style": {
                                "color": "black"
                            }
                        },
                        "type": "datetime",

                        "labels": {
                            "format": "{value:%b - %e - %Y}"
                        },
                        "gridLineWidth": 0,
                        "lineWidth": 1,
                        "minorGridLineWidth": 0,
                        "lineColor": "#cdcdcd"
                    },
                    "yAxis": {
                        "visible": false,
                        min: 0,
                        max: 70,
                    },

                    legend: {
                        enabled: false
                    },
                    tooltip: {
                        xDateFormat: 'End Date: ' + '%b - %e - %Y',
                        pointFormat: '',
                        useHTML: true,
                        enabled: false
                    },
                    "plotOptions": {
                        "column": {
                            color: "#d3d3d3",
                            maxPointWidth: 1,
                            dataLabels: {
                                enabled: true,
                                inside: false,
                                borderWidth: 2,
                                shape: 'callout',
                                y: -18,
                                style: {
                                    fontFamily: 'Gesta, Helvetica, sans-serif',
                                    fontSize: '12px',
                                    padding: "5px",
                                    fontWeight: 'normal',
                                    textShadow: 'none',
                                    color: 'white',
                                },
                                useHTML: true,
                                format: "{point.name}"
                            }
                        }
                    },

                    series: [{
                        type: 'column',
                        name: 'Observations',
                        id: 'dataseries',
                        pointWidth: 1,
                        data: [],
                    }]
                };
                 $timeout(function () {
                }, 0);
            }]
        };
    }]);

});
