define(['angular', './main'], function (angular) {
    'use strict';

    var mod = angular.module('maalka.common.utilities', []);
    mod.service('fileUtilities', [function () {
        var generateCSV = function (headersAndRows) {
            var i;
            var csvContent = "data:text/csv;charset=utf-8,";
            var headers = headersAndRows[0].join(",");
            var rows = headersAndRows[1].map(function (row) { 
                return row.join(",");
            });
            rows = rows.join("\n");
            csvContent += headers + "\n" + rows;
            var encodedUri = encodeURI(csvContent);
            window.open(encodedUri, "_self");
        };
        return {
            generateCSV: generateCSV
        };
    }]);
});
