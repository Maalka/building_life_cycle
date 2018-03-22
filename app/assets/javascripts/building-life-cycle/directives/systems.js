
define(['angular', './main'], function(angular) {
    'use strict';
    var mod = angular.module('buildingLifeCycle.directives');
    mod.directive('renderSystem', ['$log', 'dateFilter',

    function($log) {
        return {
            restrict: 'E',
            scope: {
                system: "="
            },
        templateUrl: "javascripts/building-life-cycle/partials/system.html",
        controller: ['$scope', '$element', function ($scope, $element) {
        }]
        };
       }]);
    return mod;
});
