'use strict';
define(['highcharts', 'highcharts-exporting', 'highcharts-more'], function(angular, nvd3) {
    (function() {
        Highcharts.theme = {
            lang: {
                noData: "No data",
            },
            global: {
                useUTC: false,
            },
            colorTypes : {
                "Electric": [
                    '#FFD97A', '#FEEF95', '#FFE57A', '#FFD589', '#FAE16C'
                ],
                "Electric on Site Solar": [
                    '#FCFCA6', '#FEEF95', '#FFFFDE', '#FFF885', '#FEF7D8'
                ],
                'Municipally Supplied Potable Water - Indoor': [
                    '#27C3F3', '#0D95BB', '#70DDFB', '#6AC7FB', '#9BD9FD'
                ],
                'Natural Gas': [
                    '#F88342', '#E04C25', '#D07347', '#F9955D', '#EE9A73'
                ],
                'Geothermal': [
                    '#7F6FB1', '#403859', '#5F5385', '#7572B1', '#ACAAD0'
                ],
                'Other (Energy)': [
                    '#3F58CE', '#5D70D4', '#2F4598', '#1f2C5C', '#4A5DA9', '#8C9BE1'
                ],
                "Measures": ["#0a708c", "#6bd2d6", "#06a1f9", "#9bd9fd", "#8c9be1", "#64467d", "#f5b569",
                            "#eb885c", "#d4483d", "#f7e3b1", "#b3b3b3", "#3f58ce", "#1f2c5c"]
            },
            colors: ["#06A1F9", "#1F2C5C",  "#3F58CE", "#5D70D4", "#8C9BE1", "#BDBBD9",
                            "#7F6FB1", "#403859", "#0D95BB", "#6AC7FB"],

            exporting: {
                enabled: false // disabled this for printing, TODO: only disable this for phantomJS
            },

            noData: {
                style: {
                    fontWeight: 'normal',
                    fontSize: '14px',
                    color: '#cdcdcd',
                    fontStyle: 'italic'
                }
            },

            tooltip: {
                useHTML: true,
                backgroundColor: "rgba(255,255,255,1)",
            },

            maalkaColors: {
                'dark-neutral-purple': '#1F2C5C',
                'medium-neutral-purple': '#3F58CE',
                'grey-neutral-purple': '#4A5DA9',
                'light-grey-neutral-purple': '#5D70D4',
                'water-blue': '#27C3F3',
                'light-water-blue': '#70DDfB',
                'blue': "#06A1F9"
            },
            chart: {
                backgroundColor: "transparent",
                style: {
                    fontFamily: "gesta,'Helvetica Neue',Arial,Helvetica,sans-serif"
                },
                resetZoomButton: {
                    position: {
                        // align: 'right', // by default
                        // verticalAlign: 'top', // by default
                        x: 0,
                        y: 0,
                        align: 'right',
                        relativeTo: 'chart'
                    },
                    theme: {
                        fill: 'white',
                        stroke: '#06A1F9',
                        r: 0,
                        states: {
                            hover: {
                                fill: '#06A1F9',
                                style: {
                                    color: 'white'
                                }
                            }
                        }
                    }
                }
            },
            plotOptions: {
                barchart: {
                    borderWidth: 0
                },

                column: {
                    borderColor: "transparent",
                },
                series: {
                    animation: false,
                    states: {
                        hover: {
                            enabled: false
                        }
                    }
                }
            },

            Axis: {
                lineWidth: 0,
                minorGridLineWidth: 0,
                gridLineWidth: 0,
                lineColor: 'transparent',
                minorTickLength: 0,
                tickLength: 0
            },

            xAxis: {
                dateTimeLabelFormats: {
                    millisecond: '%b \ %y \%Y <br> %H:%M:%S',
                    second: '%b \ %y \%Y <br> %H:%M:%S',
                    minute: '%b \ %y \%Y <br> %H:%M:%S',
                    hour: '%b \ %y \%Y <br> %H:%M:%S',
                    week: '%b \ %y \%Y <br> %H:%M:%S',
                    month: '%b \ %y \ %Y',
                    year: '%Y'
                }
            },
            yAxis: {
                lineWidth: 0,
                minorGridLineWidth: 0,
                gridLineWidth: 0,
                lineColor: 'transparent',
                minorTickLength: 0,
                tickLength: 0
            },

            credits: {
                enabled: false
            },
            hexToRGB: function(hexColor) {
                var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hexColor);
                return result ? {
                    r: parseInt(result[1], 16),
                    g: parseInt(result[2], 16),
                    b: parseInt(result[3], 16)
                } : null;
            }
        };

        // Apply the theme
        Highcharts.setOptions(Highcharts.theme);
        return Highcharts;
    })();
});