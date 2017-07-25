/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
 define(['angular', './services/helper', './services/playRoutes', './filters', './fileUtilities', './semantic', './popover_service',
    'angular-file-upload',
	'./directives/required',
	'./directives/files',
	'./directives/sensitivity_graph'
	],
    function(angular) {
  'use strict';

  return angular.module('maalka.common', ['maalka.common.helper', 'maalka.common.playRoutes', 'maalka.common.filters', 'maalka.common.utilities',
    'maalka.common.semantic', 'maalka.common.PopoverService', 'ngFileUpload',]);
});
