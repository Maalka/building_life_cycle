
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
            },

            controller: ["$scope", "$element", function ($scope, $element) {

                var options = {
                    title: {
                        text: "Milestones",
                    },
                    chart: {
                        backgroundColor: "transparent",
                        style: {
                            fontFamily: 'Gesta',
                        }
                    },
                    yAxis: {
                        type: "datetime",
                        tickInterval: 24 * 3600 * 1000 * 365,
                        dateTimeLabelFormats: {
                            month: '%Y',
                            year: '%Y'
                        }
                    },

                    xAxis: {
						gridLineWidth: 0,
                    },

                    legend: {
                        enabled: false
                    },

                    series: [
                        {
                            type: 'flags',
                            shape: 'squarepin',
                            color: '#06A1F9',
							data: [
								{ y: Date.UTC(2009, 1, 0), text: 'Comment and date', title: 'Duct Installation' },
								{ y: Date.UTC(2010, 100, 0), text: 'Comment and date', title: 'Hot Water Heating' },
								{ y: Date.UTC(2000, 100, 0), text: 'Comment and date', title: 'Glass Exterior Door' },
								{ y: Date.UTC(2011, 100, 0), text: 'Comment and date', title: 'Wood Ceiling Finish'},
								{ y: Date.UTC(2012, 100, 0), text: 'Comment and date', title: 'Stepped Dimming Lighting Control' },
							],
                        },
                    ]

                };
                 $timeout(function () { 
                    angular.element($element).height(500).highcharts(options);
                }, 0);                
            }]
        };
    }]);
});