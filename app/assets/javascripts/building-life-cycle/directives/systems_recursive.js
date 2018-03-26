
define(['angular', './main'], function(angular) {
    'use strict';
    var mod = angular.module('buildingLifeCycle.directives');
    mod.directive('systemsRecursive', ['$compile',

    function($compile) {
        return {
            priority: 100000,
            compile: function(tElement, tAttr) {
                var contents = tElement.contents().remove();
                var compiledContents;
                return function(scope, iElement, iAttr) {
                    if(!compiledContents) {
                        compiledContents = $compile(contents);
                    }
                    iElement.append(
                        compiledContents(scope,
                                         function(clone) {
                                             return clone; }));
                };
         },
        restrict: 'A',
        scope: {
            part: "=",
            depth: "="
        },
        templateUrl: "javascripts/building-life-cycle/partials/systems_recursive.html",
        controller: ['$scope', '$element', function ($scope, $element) {
            var value;
            var depth = $scope.depth === undefined ? 0 : $scope.depth;
            $scope.segments = Object.keys($scope.part).filter( function(v) {
                return v !== '$$hashKey';
            }).map( function(key) {
                value = $scope.part[key];
                if (key !== '$' && typeof value === "object") {
                    return {
                        part: value,
                        key: key,
                        depth: depth + 1,
                        recurse: true
                    };
                } else {
                    return {
                        part: value,
                        key: key,
                        depth: depth + 1,
                        recurse: false
                    };
                }
            }

            );
        }]
        };
       }]);
    return mod;
});
