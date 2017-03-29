/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular'], function(angular) {
  'use strict';

  var mod = angular.module('maalka.common.filters', []);
  /**
   * Extracts a given property from the value it is applied to.
   * {{{
   * (user | property:'name')
   * }}}
   */
  mod.filter('property', function(value, property) {
    if (angular.isObject(value)) {
      if (value.hasOwnProperty(property)) {
        return value[property];
      }
    }
  });
  return mod;
});
