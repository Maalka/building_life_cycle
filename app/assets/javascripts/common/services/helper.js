/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular'], function(angular) {
  'use strict';

  var mod = angular.module('maalka.common.helper', []);
  mod.service('helper', function() {
    return {
      sayHi: function() {
        return 'hi';
      }
    };
  });
  return mod;
});
