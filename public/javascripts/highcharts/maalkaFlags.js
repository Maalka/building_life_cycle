define(['highcharts'], function() {
    'use strict';

    var symbols = Highcharts.SVGRenderer.prototype.symbols;
    var seriesTypes = Highcharts.seriesTypes;
    var noop = function () {};

    var defaultPlotOptions = Highcharts.getOptions().plotOptions,
        pInt = Highcharts.pInt,
        pick = Highcharts.pick,
        each = Highcharts.each,
        merge = Highcharts.merge,
        isNumber = Highcharts.isNumber,
        TrackerMixin = Highcharts.TrackerMixin,
        addEvent = Highcharts.addEvent,
        colorAxisMethods,
        UNDEFINED;

    // 1 - set default options

    defaultPlotOptions.maalkaFlags = merge(defaultPlotOptions.column, {
        fillColor: 'white',
        lineWidth: 1,
        pointRange: 0, // #673
        //radius: 2,
        shape: 'flag',
        stackDistance: 12,
        states: {
            hover: {
                lineColor: 'black',
                fillColor: '#FCFFC5'
            }
        },
        style: {
            fontSize: '11px',
            fontWeight: 'bold',
            textAlign: 'center'
        },
        tooltip: {
            pointFormat: '{point.text}<br/>'
        },
        threshold: null,
        y: -30
    });


    // 2 - Create the CandlestickSeries object
    seriesTypes.maalkaFlags = Highcharts.extendClass(seriesTypes.column, {
        type: 'maalkaFlags',
        sorted: false,
        noSharedTooltip: true,
        allowDG: false,
        takeOrdinalPosition: false, // #1074
        trackerGroups: ['markerGroup'],
        forceCrop: true,
        /**
         * Inherit the initialization from base Series
         */
        init: Highcharts.Series.prototype.init,

        /**
         * One-to-one mapping from options to SVG attributes
         */
        pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
            fill: 'fillColor',
            stroke: 'color',
            'stroke-width': 'lineWidth',
            r: 'radius'
        },

        /**
         * Extend the translate method by placing the point on the related series
         */
        translate: function () {

            seriesTypes.column.prototype.translate.apply(this);

            var series = this,
                options = series.options,
                chart = series.chart,
                points = series.points,
                cursor = points.length - 1,
                point,
                lastPoint,
                optionsOnSeries = options.onSeries,
                onSeries = optionsOnSeries && chart.get(optionsOnSeries),
                onKey = options.onKey || 'y',
                step = onSeries && onSeries.options.step,
                onData = onSeries && onSeries.points,
                i = onData && onData.length,
                xAxis = series.xAxis,
                xAxisExt = xAxis.getExtremes(),
                leftPoint,
                lastX,
                rightPoint,
                currentDataGrouping;

            // relate to a master series
            if (onSeries && onSeries.visible && i) {
                currentDataGrouping = onSeries.currentDataGrouping;
                lastX = onData[i - 1].x + (currentDataGrouping ? currentDataGrouping.totalRange : 0); // #2374

                // sort the data points
                points.sort(function (a, b) {
                    return (a.x - b.x);
                });

                onKey = 'plot' + onKey[0].toUpperCase() + onKey.substr(1);
                while (i-- && points[cursor]) {
                    point = points[cursor];
                    leftPoint = onData[i];
                    if (leftPoint.x <= point.x && leftPoint[onKey] !== undefined) {
                        if (point.x <= lastX) { // #803

                            point.plotY = leftPoint[onKey];

                            // interpolate between points, #666
                            if (leftPoint.x < point.x && !step) {
                                rightPoint = onData[i + 1];
                                if (rightPoint && rightPoint[onKey] !== UNDEFINED) {
                                    point.plotY +=
                                        ((point.x - leftPoint.x) / (rightPoint.x - leftPoint.x)) * // the distance ratio, between 0 and 1
                                        (rightPoint[onKey] - leftPoint[onKey]); // the y distance
                                }
                            }
                        }
                        cursor--;
                        i++; // check again for points in the same x position
                        if (cursor < 0) {
                            break;
                        }
                    }
                }
            }

            // Add plotY position and handle stacking
            each(points, function (point, i) {

                var stackIndex;

                // Undefined plotY means the point is either on axis, outside series range or hidden series.
                // If the series is outside the range of the x axis it should fall through with
                // an undefined plotY, but then we must remove the shapeArgs (#847).
                if (point.plotY === UNDEFINED) {
                    if (point.x >= xAxisExt.min && point.x <= xAxisExt.max) { // we're inside xAxis range
                        point.plotY = chart.chartHeight - xAxis.bottom - (xAxis.opposite ? xAxis.height : 0) + xAxis.offset - chart.plotTop;
                    } else {
                        point.shapeArgs = {}; // 847
                    }
                }
                // if multiple maalkaFlags appear at the same x, order them into a stack
                lastPoint = points[i - 1];
                if (lastPoint && lastPoint.plotX === point.plotX) {
                    if (lastPoint.stackIndex === UNDEFINED) {
                        lastPoint.stackIndex = 0;
                    }
                    stackIndex = lastPoint.stackIndex + 1;
                }
                point.stackIndex = stackIndex; // #3639
            });


        },

        /**
         * Draw the markers
         */
        drawPoints: function () {
            var series = this,
                pointAttr,
                seriesPointAttr = series.pointAttr[''],
                points = series.points,
                chart = series.chart,
                renderer = chart.renderer,
                plotX,
                plotY,
                options = series.options,
                optionsY = options.y,
                shape,
                i,
                point,
                graphic,
                stackIndex,
                anchorX,
                anchorY,
                outsideRight,
                yAxis = series.yAxis;

            i = points.length;
            while (i--) {
                point = points[i];
                outsideRight = point.plotX > series.xAxis.len;
                plotX = point.plotX;
                if (plotX > 0) { // #3119
                    plotX -= pick(point.lineWidth, options.lineWidth) % 2; // #4285
                }
                stackIndex = point.stackIndex;
                shape = point.options.shape || options.shape;
                plotY = point.plotY;
                if (plotY !== UNDEFINED) {
                    plotY = point.plotY + optionsY - (stackIndex !== UNDEFINED && stackIndex * options.stackDistance);
                }
                anchorX = stackIndex ? UNDEFINED : point.plotX; // skip connectors for higher level stacked points
                anchorY = stackIndex ? UNDEFINED : point.plotY;

                graphic = point.graphic;

                // only draw the point if y is defined and the flag is within the visible area
                if (plotY !== UNDEFINED && plotX >= 0 && !outsideRight) {
                    pointAttr = point.pointAttr[point.selected ? 'select' : ''] || seriesPointAttr;
                    if (graphic) { // update
                        graphic.attr({
                            x: plotX - (options.shape === "maalkaFlagLeftBottom" || options.shape === "maalkaLongFlagLeftBottom" ? 6 : 0),
                            y: plotY,
                            r: pointAttr.r,
                            anchorX: anchorX,
                            anchorY: anchorY
                        });
                    } else {
                        graphic = point.graphic = renderer.label(
                            point.options.title || options.title || 'A',
                            plotX,
                            plotY,
                            shape,
                            anchorX,
                            anchorY,
                            options.useHTML, 
                            true
                        )
                        .css(merge(options.style, point.style))
                        .attr(pointAttr)
                        .attr({
                            "text-anchor": options.shape === "maalkaFlagLeftBottom" || options.shape === "maalkaLongFlagLeftBottom" ? "end": "inherit",
                            width: options.width,
                            height: options.height
                        })
                        .add(series.markerGroup)
                        .shadow(options.shadow);

                    }

                    // Set the tooltip anchor position
                    point.tooltipPos = chart.inverted ? [yAxis.len + yAxis.pos - chart.plotLeft - plotY, series.xAxis.len - plotX] : [plotX, plotY];

                } else if (graphic) {
                    point.graphic = graphic.destroy();
                }

            }

        },

        /**
         * Extend the column trackers with listeners to expand and contract stacks
         */
        drawTracker: function () {
            var series = this,
                points = series.points;

            TrackerMixin.drawTrackerPoint.apply(this);

            // Bring each stacked flag up on mouse over, this allows readability of vertically
            // stacked elements as well as tight points on the x axis. #1924.
            each(points, function (point) {
                var graphic = point.graphic;
                if (graphic) {
                    addEvent(graphic.element, 'mouseover', function () {

                        // Raise this point
                        if (point.stackIndex > 0 && !point.raised) {
                            point._y = graphic.y;
                            graphic.attr({
                                y: point._y - 8
                            });
                            point.raised = true;
                        }

                        // Revert other raised points
                        each(points, function (otherPoint) {
                            if (otherPoint !== point && otherPoint.raised && otherPoint.graphic) {
                                otherPoint.graphic.attr({
                                    y: otherPoint._y
                                });
                                otherPoint.raised = false;
                            }
                        });
                    });
                }
            });
        },

        /**
         * Disable animation
         */
        animate: noop,
        buildKDTree: noop,
        setClip: noop

    });

    // create the flag icon with anchor
    symbols.maalkaFlag = function (x, y, w, h, options) {
        var anchorX = (options && options.anchorX) || x,
            anchorY = (options &&  options.anchorY) || y;

        return [
            'M', anchorX, anchorY - 3,
            'L', x, y + 6,
            'Z'
        ];
    };

    symbols.maalkaFlagBottom = function (x, y, w, h, options) {
        var anchorX = (options && options.anchorX) || x,
            anchorY = (options &&  options.anchorY) || y;

        return [
            'M', anchorX, anchorY + 3,
            'L', x, y + h,
            'Z'
        ];
    };

    symbols.maalkaLongFlagBottom = function (x, y, w, h, options) {
        var anchorX = (options && options.anchorX) || x,
            anchorY = (options &&  options.anchorY) || y;

        return [
            'M', anchorX, anchorY + 3,
            'L', x, y + h + 3,
            'Z'
        ];
    };

    Highcharts.Renderer.prototype.symbols.maalkaFlagLeftBottom = function (x, y, w, h, options) {
         var anchorX = (options && options.anchorX) || x,
            anchorY = (options &&  options.anchorY) || y;

        return [
            'M', anchorX, anchorY + 3,
            'L', anchorX, y + h - 4,
            'Z'
        ];
    };

    Highcharts.Renderer.prototype.symbols.maalkaLongFlagLeftBottom = function (x, y, w, h, options) {
         var anchorX = (options && options.anchorX) || x,
            anchorY = (options &&  options.anchorY) || y;

        return [
            'M', anchorX, anchorY + 3,
            'L', anchorX, y + h + 3,
            'Z'
        ];
    };



    // The symbol callbacks are generated on the SVGRenderer object in all browsers. Even
    // VML browsers need this in order to generate shapes in export. Now share
    // them with the VMLRenderer.
    if (Highcharts.Renderer === Highcharts.VMLRenderer) {
        each(['maalkaFlag', 'maalkaFlagLeft'], function (shape) {
            VMLRenderer.prototype.symbols[shape] = symbols[shape];
        });
    }
});
