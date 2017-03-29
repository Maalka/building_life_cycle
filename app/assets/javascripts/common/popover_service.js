/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', 'common'], function (angular) {
  'use strict';

  var mod = angular.module('maalka.common.PopoverService', []);
  mod.factory('errorPopoverService', [function () {
    var errorPopoverHandlers = [];

    return {
      registerErrorPopoverHandler: function(handler) {
        errorPopoverHandlers.push(handler);
      },
      errorPopoverEvent: function(string, options) {
        errorPopoverHandlers.forEach(function (handler) {
          handler(string, options);
        });
      }
    };
  }]);
  return mod;
});

