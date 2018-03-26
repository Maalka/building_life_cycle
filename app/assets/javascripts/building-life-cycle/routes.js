/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('buildingLifeCycle.routes', ['maalka.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/',  {templateUrl: 'javascripts/building-life-cycle/partials/building-life-cycle.html',  controller:controllers.BuildingLifeCycleCtrl})
        .when('/about',  {templateUrl: 'javascripts/building-life-cycle/partials/about.html'})
        .when('/guide',  {templateUrl: 'javascripts/building-life-cycle/partials/guide.html'})
        .when('/usecase',  {templateUrl: 'javascripts/building-life-cycle/partials/usecase.html'});
  }]);
  return mod;
});
