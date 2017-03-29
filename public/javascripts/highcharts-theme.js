'use strict';
define(['highcharts', 'highcharts-more'], function(angular, nvd3) {
    (function() {
        Highcharts.theme = {
            chart: {
                style: {
                    fontFamily: '"Open Sans", "Helvetica Neue", Arial, Helvetica, sans-serif'
                }
            },
            credits: {
                enabled: true,
                href: 'http://www.maalka.com',
                text: 'maalka.com'
            },
            plotOptions: {
                area: {
                    showInLegend: false
                },
                column: {
                    showInLegend: false
                },
                line: {
                    showInLegend: false
                },
                flag: {
                    showInLegend: false
                }
            },
            colors: [
                '#06A1F9'
            ],
        };

        // Apply the theme
        Highcharts.setOptions(Highcharts.theme);

        //set the line legond symbol to be the area symbol (we want a box)
        Highcharts.seriesTypes.line.prototype.drawLegendSymbol = 
            Highcharts.seriesTypes.area.prototype.drawLegendSymbol;


        // add new area symbols
        Highcharts.Renderer.prototype.symbols['arrow-right'] = function (x, y, w, h, options) {
            return [
                "M", x - w / 2, y,
                "L", x + w / 2, y + h /2,
                x - w / 2, y + h,
                'Z'
            ];
        };
         Highcharts.Renderer.prototype.symbols['arrow-left'] = function (x, y, w, h, options) {
            return [
                "M", x, y,
                "L", x + w, y + h /2,
                    x + w, y - h / 2,
                'Z'
            ];
        };
        return Highcharts;
    })();
});