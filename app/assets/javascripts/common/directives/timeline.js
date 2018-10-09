
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

                    function sortAscByX(a,b) {
                      return a.x - b.x;
                    }

                    var groupBy = function(xs, key) {
                        return xs.reduce(function(rv, x) {
                            (rv[x[key]] = rv[x[key]] || []).push(x);
                            return rv;
                        }, {});
                    };

                    $timeout(function () {
                        // first we sort in desc order, because we need to find 5 newest measures
                        $scope.measures.sort(sortDescByEndDate);
                        $scope.last5measures = $scope.measures.slice(0,5);
                        $scope.newMeasures = [];
                        for (var i = 0; i < $scope.last5measures.length; i++) {
                            var randomColor = colors[Math.floor(Math.random()*colors.length)];
                            var endDate = moment.utc($scope.last5measures[i].endDate);
                            $scope.newMeasures.push({
                                x: moment.utc($scope.last5measures[i].endDate).valueOf(),
                                y: i % 5 * 10 + 10,
                                text: endDate.format("ll"),
                                name: $scope.last5measures[i].detail,
                                implementationStatus: $scope.last5measures[i].implementationStatus
                            });
                        }
                        // now sort in asc order, because highcharts requires data be sorted on x axis
                        $scope.newMeasures.sort(sortAscByX);
                        $scope.grouped = groupBy($scope.newMeasures, 'implementationStatus');

                        $scope.groups = [];
                        for (var t = 0; t < Object.keys($scope.grouped).length; t++) {
                            var newObject = {};
                            newObject.name = Object.keys($scope.grouped)[t];
                            newObject.type = 'column';
                            newObject.data = $scope.grouped[Object.keys($scope.grouped)[t]];
                            newObject.dataLabels = {
                                backgroundColor: colors[t],
                                borderColor: colors[t],
                            };
                            $scope.groups.push(newObject);
                        }
                        $scope.options.series = $scope.groups;

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
                            color: "transparent",
                            maxPointWidth: 1,
                            dataLabels: { 
                                align: 'center',
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
