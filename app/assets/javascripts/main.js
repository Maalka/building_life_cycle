/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
// `main.js` is the file that sbt-web will use as an entry point
(function (requirejs) {
  'use strict';

  // -- RequireJS config --
  requirejs.config({
    // Packages = top-level folders; loads a contained file named 'main.js"
    wrapShim: true,
    packages: ['common', 'data-quality'],
    shim: {
      'jsRoutes': {
        deps: [],
        // it's not a RequireJS module, so we have to tell it what var is returned
        exports: 'jsRoutes'
      },
      // Hopefully this all will not be necessary but can be fetched from WebJars in the future
      'angular': {
        deps: ['jquery'],
        exports: 'angular'
      },
      'angular-file-upload': {
        deps: ['angular']
      },
      'angular-route': ['angular'],
      'angular-cookies': ['angular'],
      'maalka-templates': {
        deps: ['angular']
      },
      'matchmedia-ng': ['angular'],
      'highcharts-core': {
        deps: ['jquery'],
        exports: 'highcharts-core'
      },
      'highcharts-more': {
        deps: ['highcharts-core'],
        exports: 'highcharts-more'
      },
      'highcharts-boost': {
        deps: ['highcharts-core']
      },
      'highcharts-drilldown': {
        deps: ['highcharts-core']
      },
      'highcharts-exporting': {
        deps: ['highcharts-core']
      },
      'highcharts': {
        deps: ['highcharts-more'],
        exports: 'highcharts'
      },
      'json-formatter': {
        deps: ['angular']
      },
      'semantic': {
        deps: ['jquery', 'angular']
      },
      'semantic-daterangepicker': { 
        deps: ['semantic', 'moment'],
        exports: "semantic-daterangepicker"
      }
    },
    paths: {
      'requirejs': '../lib/requirejs/require',
      'jquery': ['../lib/jquery/jquery'],
      'angular': '../lib/angularjs/angular',
      'angular-route': '../lib/angularjs/angular-route',
      'angular-cookies': '../lib/angularjs/angular-cookies',
      'angular-file-upload': '../lib/ng-file-upload/ng-file-upload',
      'highcharts-core': '../lib/highstock/highstock',
      'highcharts-more': '../lib/highstock/highcharts-more',
      'highcharts-boost': '../lib/highstock/modules/boost',
      'highcharts-drilldown': '../lib/highstock/modules/drilldown',
      'highcharts-exporting': '../lib/highstock/modules/exporting',
      'highcharts': './highcharts-theme',
      'maalkaflags': './highcharts/maalkaFlags',
      'matchmedia-ng': '../lib/matchmedia-ng/matchmedia-ng',
      'semantic': './semantic-ui/semantic.min',
      'jsRoutes': '/jsroutes',
      'json-formatter': '../lib/json-formatter/dist/json-formatter',
      'semantic-daterangepicker': './semantic-ui-daterangepicker/daterangepicker',
      'moment': './semantic-ui-daterangepicker/moment',
      'maalka-templates': '../templates'
    }
  });

  requirejs.onError = function (err) {
    console.log(err);
  };

  // Load the app. This is kept minimal so it doesn't need much updating.
  require(['angular', 'angular-cookies', 'angular-route', 'jquery', 'json-formatter', 'semantic', './app'],
    function (angular) {
      angular.bootstrap(document, ['app', 'matchmedia-ng', 'jsonFormatter']);
    }
  );
})(requirejs);
