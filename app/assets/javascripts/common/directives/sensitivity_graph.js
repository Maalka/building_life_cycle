// Consumption by End Use Energy Signature

/*
 * A discussions app directive.
 */
define(['angular', 'highcharts', 'highcharts-drilldown', 'highcharts-boost', './main'], function(angular) {
    'use strict';
    var mod = angular.module('maalka.common.directives');
    mod.directive('sensitivityGraph', ['$timeout',
    function ($timeout) {
        return {
            restrict: 'A',
            replace: true,
            scope: {
                model: "="
            },
            link: function (scope, element) {
                var model;

                function nFormatter(num, digits) {
                    var si = [
                        { value: 1E18, symbol: "E" },
                        { value: 1E15, symbol: "P" },
                        { value: 1E12, symbol: "T" },
                        { value: 1E9,  symbol: "G" },
                        { value: 1E6,  symbol: "M" },
                        { value: 1E3,  symbol: "k" }
                    ], rx = /\.0+$|(\.[0-9]*[1-9])0+$/, i;
                    for (i = 0; i < si.length; i += 1) {
                        if (num >= si[i].value) {
                            return (num / si[i].value).toFixed(digits).replace(rx, "$1") + si[i].symbol;
                        }
                    }
                    return num.toFixed(digits).replace(rx, "$1");
                }
                var derivePoint = function(data, x) {
                    var point1, point2;
                    var a, b, c, y, i;
                    var l = data.length;
                    if (data.length === 0) {
                        return undefined;
                    }
                    if (x < data[0][0]) {
                        point1 = data[0];
                        point2 = data[1];
                    } else if (x > data[l - 1][0]) {
                        point1 = data[l - 2];
                        point2 = data[l - 1];
                    } else {
                        for (i = 0; i < l; i += 1 ){
                            if (x === data[i][0]) {
                                return {y: data[i][1]};
                            }
                            if (x > data[i][0] && x < data[i + 1][0]) {
                                point1 = data[i];
                                point2 = data[i + 1];
                                break;
                            }
                        }
                    }
                    if (point1 === undefined || point2 === undefined) {
                        return undefined;
                    }

                    a = point1[1] - point2[1];
                    b = point2[0] - point1[0];
                    c = (point1[0] - point2[0]) * point1[1] + (point2[1] - point1[1])* point1[0];
                    if (b === 0 || b === undefined) {
                        return undefined;
                    }
                    y = (-1 * a * x - c) / b;
                    y = Math.max(0, y);
                    return {y: y, a: a, b: b, c: c};
                };

                /**
                    Watch the model scope variable for new data.
                    If new data is detected, compile the new data points
                    and generate the new plotdata
                **/
                scope.$watch('model', function (_model) {
                    var i, result;
                    var data = [];
                    if (_model !== undefined) {
                        result = _model.result;
                        if (result.length > 0) {
                            var r = result[0];
                            if (r.x_p !== null && r.x_p.length > 0) {
                                for(i = 0; i < r.x_p.length; i += 1) {
                                    data.push([r.x_p[i], r.y_p[i]]);
                                }
                            } else {
                                var b = r.b;
                                var m = r.m1;
                                for (i = 0 ; i < 3; i += 1) {
                                    data.push([i, m * i + b]);
                                }
                            }
                            generatePlotData(data);
                        }
                    }
                });

                /**
                  * GeneratePlotData: generate the plot data for the given model.  the default extends are -30 to 110;
                  */
                var generatePlotData = function(model) {
                    var i0 = -30;
                    var i1 = 110;
                    var i, dp;
                    var data = [];
                    for (i = i0; i < i1; i += 1) {
                        dp = derivePoint(model, i);
                        if (dp !== undefined) {
                            data.push([i, dp.y]);
                        }
                    }
                    loadChart({
                        series: [{
                            name: 'Temperature Sensitivity',
                            data: data
                        }]
                    });
                };

                var loadChart = function(data) {
                    var options = {
                        chart: {
                            type: 'area'
                        },
                        title: {
                            text: "Temperature Sensitivity"
                        },
                        tooltip: {
                            crosshairs: [true, true],
                            shared: true,
                            valueDecimals: 1,
                            valueSuffix: '',
                            headerFormat: '<tspan style="font-size: 12px;">{point.key} °</tspan><br/>',
                            formatter: function(tooltip) {
                                var items = this.points || splat(this),
                                    s;

                                // build the header
                                s = [tooltip.tooltipFooterHeaderFormatter(items[0])]; //#3397: abstraction to enable formatting of footer and header
                                // build the values
                                s = s.concat(tooltip.bodyFormatter(items));

                                // footer
                                s.push(tooltip.tooltipFooterHeaderFormatter(items[0], true)); //#3397: abstraction to enable formatting of footer and header

                                return s.join('');
                            }
                        },
                        xAxis: {
                            title: {
                                text: 'Temperature',
                                style: {
                                    color: Highcharts.getOptions().colors[0]
                                }
                            },
                            labels: {
                                format: '{value} °',
                                style: {
                                    color: Highcharts.getOptions().colors[0]
                                }
                            },
                            min: 0,
                            max: 100
                        },
                        yAxis: { // Primary yAxis,
                            labels: {
                                formatter: function () {
                                    return nFormatter(this.value, 4);
                                }
                            },
                            title: {
                                text: 'Value',
                            },
                            min: 0
                        },
                        legend: {
                            enabled: true
                        },
                        plotOptions: {
                            area: {
                                marker: {
                                    radius: 1
                                },
                                lineWidth: 0.5,
                                stacking: 'normal',
                                states: {
                                    hover: {
                                        lineWidth: 1
                                    }
                                },
                                threshold: null
                            },
                        },
                        series: data.series
                    };
                    $timeout(function () {
                        angular.element(element).highcharts(options);
                    });
                };
            }
        };
    }]);
    return mod;
});
