
/*
 * A Timeline Graph Directive
 */
define(['angular', 'moment', 'highcharts', 'highcharts-drilldown', 'highcharts-boost', './main'], function(angular, moment) { 
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
                            newMeasures.push({x: Date.UTC(
                                $scope.last5measures[i].endDate.getFullYear(),
                                $scope.last5measures[i].endDate.getUTCMonth(),
                                $scope.last5measures[i].endDate.getDate()
                                ),
                                y: Math.floor(Math.random()*100),
                                text: $scope.last5measures[i].endDate.getFullYear() + ' ' + $scope.last5measures[i].endDate.getUTCMonth() + ' ' + $scope.last5measures[i].endDate.getDate(),
                                title: '<span style="margin: 15px">'+$scope.last5measures[i].measure+'</span>',
                                color: randomColor,
                                fillColor: randomColor});
                         }
                         $scope.options.series[0].data = newMeasures;
                         $scope.options.series[1].data = newMeasures;
                         angular.element($element).height(300).highcharts($scope.options);
                   }, 0);
                };

                    var colors = ['#06A1F9', '#0D95BB', '#0A708C', '#2F4598', '#5D70D4'];
                    $scope.last5measures = [];
                    $scope.options = {

                    title: {
                        text: '',
                    },
                    chart: {
                        backgroundColor: "transparent",
                        style: {
                            fontFamily: 'Gesta',
                        },
                        marginLeft: 80,
                        marginRight: 110
                    },
                    xAxis: {
						title: {
							text: 'Year',
						},
                        type: "datetime",
                        tickInterval: 24 * 3600 * 1000 * 365,
						gridLineWidth: 0,
						lineWidth: 0,
						minorGridLineWidth: 0,
						lineColor: 'transparent'
                    },

                    yAxis: {
						max: 100,
						min: 1,
						visible: false
                    },

                    legend: {
                        enabled: false
                    },
					tooltip: {
						headerFormat: '',
					},
					plotOptions: {
						flags: {
							useHTML: true,
							lineWidth: 2,
							style: { // text style
								color: 'white',
								fontSize: 12,
							},
						}
					},

					series: [{
                        type: 'column',
                        name: 'Observations',
                        id: 'dataseries',
                        pointWidth: 2,
                        data: [],
                        marker: {
                            radius: 4
                        }
                    },{
                        type: 'flags',
                        allowOverlapX: false,
                        shape: 'squarepin',
                        useHTML: true,
                        onSeries: 'dataseries',

						data: [],
                        },
                    ]
                };
                 $timeout(function () { 
                    angular.element($element).height(300).highcharts($scope.options);
                }, 0);                
            }]
        };
    }]);

});
