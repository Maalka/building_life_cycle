
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
                        text: "",
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
                        data: [ { x: Date.UTC(2000, 5, 26), y: (0), color: '#06A1F9'},
                                { x: Date.UTC(2005, 4, 20), y: (40), color: '#0D95BB'},
                                { x: Date.UTC(2006, 3, 14), y: (80), color: '#0A708C'},
                                { x: Date.UTC(2011, 5, 17), y: (40), color: '#2F4598'},
                                { x: Date.UTC(2012, 11, 23), y: (0), color: '#5D70D4'}
                                ],
                        marker: {
                            radius: 4
                        }
                    },{
                        type: 'flags',
                        allowOverlapX: false,
                        shape: 'squarepin',
                        useHTML: true,
                        onSeries: 'dataseries',
						data: [
							{ x: Date.UTC(2000, 5, 26), text: 'Comment and date', title: '<span style="margin: 15px;">Duct Installation </span>', color: '#06A1F9', fillColor: '#06A1F9'},
							{ x: Date.UTC(2005, 4, 20), text: 'Comment and date', title: '<span style="margin: 15px">Hot Water Heating</span>', color: '#0D95BB', fillColor: '#0D95BB'},
							{ x: Date.UTC(2006, 3, 14), text: 'Comment and date', title: '<span style="margin: 15px">Glass Exterior Door</span>', color: '#0A708C', fillColor: '#0A708C'},
							{ x: Date.UTC(2011, 5, 17), text: 'Comment and date', title: '<span style="margin: 15px">Wood Ceiling Finish</span>', color: '#2F4598', fillColor: '#2F4598'},
							{ x: Date.UTC(2012, 11, 23), text: 'Comment and date', title: '<span style="margin: 15px">Stepped Dimming Lighting Control </span>', color: '#5D70D4', fillColor: '#5D70D4'},
							],
                        },
                    ]
                };
                 $timeout(function () { 
                    angular.element($element).height(300).highcharts(options);
                }, 0);                
            }]
        };
    }]);

});
