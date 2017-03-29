/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
 /**
 * A directive
 * for uploading
 * CSV files
 */
define(['angular', './main', 'angular-file-upload'], function(angular) {
    'use strict';

    var mod = angular.module('maalka.common.directives');

    mod.directive('files', ['$log', 'errorPopoverService', 'playRoutes', 'Upload', function ($log, errorPopover, playRoutes, Upload) {
        return {
            restrict: 'E',
            scope: {
                meter: '=meter',
                model: '=model',
                file: '=file'

            },
            templateUrl: "javascripts/common/partials/files.html",
            controller: ["$scope", "$element", "$timeout", "playRoutes",
                function ($scope, $element, $timeout, playRoutes) {
                $scope.searchInput = "";
            }]
        };
    }]);
});