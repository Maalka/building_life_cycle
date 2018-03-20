/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
 /**
 * Dashboard, shown after user is logged in.
 * dashboard/main.js is the entry module which serves as an entry point so other modules only have
 * to include a single module.
 */
define(['angular', './routes', './services', './directives/systems', 'common', 'ng-infinite-scroll'], function(angular) {
  'use strict';

  return angular.module('buildingLifeCycle.dashboard', ['ngRoute', 'buildingLifeCycle.routes',
                    'buildingLifeCycle.services', 'buildingLifeCycle.directives', 'maalka.common.directives', 'infinite-scroll']);
});
