/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('dataQuality.routes', ['maalka.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/',  {templateUrl: 'javascripts/data-quality/partials/data-quality.html',  controller:controllers.DataQualityCtrl})
        .when('/about',  {templateUrl: 'javascripts/data-quality/partials/about.html'})
        .when('/guide',  {templateUrl: 'javascripts/data-quality/partials/guide.html'})
        .when('/usecase',  {templateUrl: 'javascripts/data-quality/partials/usecase.html'});
  }]);
  return mod;
});
