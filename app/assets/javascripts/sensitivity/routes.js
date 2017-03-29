/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('sensitivity.routes', ['maalka.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/',  {templateUrl: 'javascripts/sensitivity/partials/sensitivity.html',  controller:controllers.DashboardCtrl})
        .when('/about',  {templateUrl: 'javascripts/sensitivity/partials/about.html'})
        .when('/guide',  {templateUrl: 'javascripts/sensitivity/partials/guide.html'})
        .when('/usecase',  {templateUrl: 'javascripts/sensitivity/partials/usecase.html'});
  }]);
  return mod;
});
