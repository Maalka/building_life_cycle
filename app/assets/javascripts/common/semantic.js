// define some builtin semantic loaders

define(['angular', 'moment', 'flatpickr' ], function (angular, moment, flatpickr) {
  'use strict';

  var mod = angular.module('maalka.common.semantic', []);

    mod.directive('checkbox', ["$timeout", function ($timeout) {
      return {
        restrict: "C",
        scope: {
          'ngModel': "=",
          'maChange': "&",
          'ngChange': "&"
        },
        link: function (scope, elm) {
            $timeout(function () {
                var checkbox = angular.element(elm).checkbox({
                  onChecked: function() {
                    $timeout(function () {
                      scope.ngModel = true;
                      if(scope.maChange !== undefined){
                        scope.maChange({'value': scope.ngModel});
                      }
                    });
                  },
                  onUnchecked: function() {
                    // this event doesn't seem to fire for radio buttons;
                    $timeout(function () { 
                      scope.ngModel = false;
                      if(scope.maChange !== undefined) {
                        scope.maChange({'value': scope.ngModel});
                      }
                    });
                  }
                });
                if(scope.ngModel === true) {
                  angular.element(elm).checkbox("set checked");
                } else {
                  angular.element(elm).checkbox("set unchecked");
                }
                scope.$watch("ngModel", function (value) {
                  if (value === true) {
                    angular.element(elm).checkbox("set checked");
                  } else if (value === false) {
                    angular.element(elm).checkbox("set unchecked");
                  }
                });
            }, 0);
        }
      };
    }]);

    mod.directive('modal', ["$timeout", function ($timeout) {
      return {
          restrict: "E",
          scope: false,

          link: function (scope, element, attrs) {
            var modalElement, approved;
            var modal;

            var showFunction = function (shouldShow) {
              if (shouldShow === true) {
                modal = modal.modal('show');
              } else {
                modal = modal.modal('hide');
              }
            };

            scope.isModalShown = function () {
              if (modalElement === 'undefined')
                return false;
              return angular.element(modalElement).hasClass('active');
            };

            $timeout(function () {
              scope[attrs.model] = angular.copy(scope[attrs.model]);
              scope.approve = scope[attrs.approve];
              scope.deny = scope[attrs.deny];
              scope.hide = scope[attrs.hide];

              if (attrs.show) {
                scope[attrs.show] = showFunction;
              }

              modalElement = element.select();

              // there seems to be an issue where if the model element has a 'active' class
              // the element will now show with modal.modal('show');
              // so remove it until the bug is fixed.

              var show = modalElement.hasClass('active');
              if (show) {
                modalElement.removeClass('active');
              }

              modal = modalElement.modal({
                observeChanges: true,
                closable: attrs.closable !== undefined ? (attrs.closable.toLowerCase() === 'true') : false,
                autofocus: attrs.autofocus !== undefined ? (attrs.autofocus.toLowerCase() === 'true') : false,
                onApprove: function(){
                  if (scope.approve !== undefined) {
                    scope[attrs.approve](scope[attrs.model]);
                    approved = true;
                  }
                },
                onDeny: function () {
                  if (scope.deny !== undefined) {
                    scope[attrs.deny](scope[attrs.model]);
                  }
                }
              });
              if (attrs.attachmodel !== undefined) {
                modal = modal.modal('attach events', attrs.attachmodel, 'show');
              }
              if (show) {
                modal = modal.modal('show');
              }
            });
          }
      };
    }]);

    mod.directive('sidebar', function () {
      return {
          restrict: "E",
          replace: true,
          transclude: true,
          template: '<div class="ui sidebar" ng-transclude></div>',
          scope: {
              buttonClass : '='
          },
          link: function(scope, element, attrs){
              element.sidebar('attach events', scope.buttonClass);
          }
      };
    });

    mod.directive('dimmer', ["$timeout", function ($timeout) {
      return {
        restrict: 'E',
        replace: true,
        transclude: true,
        scope : {
            show : "=?",
            model: '=ngModel'
        },
        template: "<div class=\"{{dimmer_class}}\" ng-click=\"click_on_dimmer()\">" +
                    "<div class=\"content\">" +
                      "<div class=\"center\" ng-transclude></div>" +
                    "</div>" +
                  "</div>",
        link : function(scope, element, attrs, ngModel) {

            if (scope.show === true) {
                scope.dimmer_class = 'ui page active dimmer';
            }
            else {
                scope.show = false;
                scope.dimmer_class = 'ui page disable dimmer';
            }

            //
            // Click on dimmer handler
            //
            scope.click_on_dimmer = function(){
                scope.model = false;
                scope.dimmer_class = 'ui page dimmer';
            };

            //
            // Watch for the ng-model changing
            //
            scope.$watch('model', function(val){
                if (val === false || val === undefined)
                    return;
                else
                    scope.dimmer_class = 'ui page active dimmer';
            });
        }
      };
    }]);

    mod.directive('progress', ["$timeout", function($timeout) {
      return {
        restrict: 'A',
        scope : {
            model: '='
        },
        link : function(scope, element) {
          $timeout(function () {
                angular.element(element).progress({percent: 0});
            }, 0);
          scope.$watch('model', function(progress) {
            angular.element(element).progress('increment', progress);
          }, true);
        }
      };
    }]);

    mod.directive('dropdown', ["$timeout", "$compile", "helper", function ($timeout, $compile, helper) {
      return {
        scope: {
          items: "=?",
          itemKey: "=?",
          maChange: "&",
          applyScope: "=",
          ngModel: "=?"
        },
        restrict: "A",
        link: function ($scope, $element, $attributes, $controllers) {
          var currentModel = [];
          var setup = false;
          var dedupe;
          var previousValue;
          var ignoreNextUpdate;

          $scope.$watch("ngModel", function (ngModel) { 
            if (ignoreNextUpdate) {
              ignoreNextUpdate = false;
              return;
            }
            if (setup) {
              if (ngModel === undefined) {
                angular.element($element).dropdown().dropdown('clear');
              } else if (ngModel !== null && (!isNaN(ngModel) || typeof(ngModel) === "string" || typeof(ngModel) === "object")) {
                $timeout( function () { 
                  var value;
                  if ($scope.itemKey !== undefined) {
                    value = ngModel[$scope.itemKey];
                  } else {
                    value = ngModel;
                  }
                  if (angular.element($element).dropdown().dropdown('get value') !== value) {
                    angular.element($element).dropdown().dropdown('set selected', value);
                  }
                });
              }
            }
          });

          var updateScope = function (semanticValue) {
//            console.log("Updating Scope: " + semanticValue);
            if (typeof($scope.ngModel) === "object" && $scope.items === undefined) { 
              console.log("Semantic Dropdown: dropdown is an object.  use items and keys");
              return;
            }
            var i, ngModel;
            if (semanticValue.startsWith("string:")) {
              semanticValue = semanticValue.substring(7);
            }

            if ($scope.items !== undefined) {
              for (i = 0; i < $scope.items.length; i +=1) {
                if ($scope.items[i].$$hashKey === semanticValue) { 
                  ngModel = $scope.items[i];
                  break;
                } else if (semanticValue === $scope.items[i][$scope.itemKey]) {
                  ngModel = $scope.items[i];
                  break;
                } else if (semanticValue == $scope.items[i]) {
                  ngModel = $scope.items[i];
                  break;
                }
              }
            } else {
              ngModel = semanticValue;
            }
            $timeout(function () {
              if ($scope.ngModel !== ngModel) {
                ignoreNextUpdate = true;
                $scope.ngModel = ngModel;
              }

              if ($scope.maChange !== undefined) {
                  $scope.maChange({"value": ngModel});
              }
            });
          };
          var onLabelCreate = function (value, text) {
            var compiledText;
            if (true) {
              var linkFunction = $compile(this)($scope.$parent);
              linkFunction.children(":last").remove();
              return linkFunction;
            }
            return $(this).children().not('i.delete.icon:first').remove();
          };

            $timeout(function () {
                angular.element($element).dropdown().dropdown({
                  'fullTextSearch': "exact",
                  'preserveHTML': true,
  //                  onLabelCreate: onLabelCreate,
//                    debug: true,
//                    verbose: true,
                    onChange: updateScope,
                    keys : {
                      backspace  : -1,
                      delimiter  : 188, // comma
                      deleteKey  : 46,
                      enter      : 13,
                      escape     : 27,  
                      pageUp     : 33,
                      pageDown   : 34,
                      leftArrow  : -1,
                      upArrow    : 38,
                      rightArrow : -1,
                      downArrow  : 4
                    }
                });
                setup = true;
              });
          
        } 
      };
    }]);

    mod.directive('sortable', ["$timeout", "$interval", function($timeout, $interval) {
      return {
        restrict: "C",
        link: function (scope, elm, attr) {
          $timeout(function () {
            angular.element(elm).tablesort();
          });
        }
      };
    }]);

    mod.directive('sticky', ["$timeout", "$interval", function ($timeout, $interval) {
          return {
            restrict: "C",
            scope: {
              element: "="
            },
            link: function (scope, elm, attr) {
                $timeout(function () {
                    angular.element(elm).sticky({
                      context: scope.element,
                      offset: 100,
                      bottomOffset: 50,
                    }).accordion({ "content": scope.element});
                }, 0);
                var height = 0;
                var intervalP = $interval(function () {
                    var h = angular.element(scope.element).height();
                    if (h !== height) {
                      height = h;
                      angular.element(elm).parent().css({"max-height": h});
                      angular.element(elm).css({"max-height": h}).sticky('refresh');
                    }
                }, 1000);
                elm.on('$destroy', function () {
                  $interval.cancel(intervalP);
                });
            }
          };
    }]);

    mod.directive('accordion', ["$timeout", function ($timeout) {
          return {
            restrict: "C",
            link: function (scope, elm, attr) {
                $timeout(function () {
                    angular.element(elm).accordion().accordion('setting', {
                        onChange: function (value) {
                          $timeout(function () {
                            scope.$parent[attr.ngModel] = value;
                          });

                        }
                    });
                }, 0);
            }
          };
    }]);
    mod.directive('popup', ["$log", "$timeout", function ($log, $timeout) { 
      return {
        restrict: "EA",
        scope: {
          "inline" : "=",
          "popup" : "=",
          "on": "=",
          "popupPosition": "="
        },
        link: function (scope, elm, attr) {
          $timeout(function () {
            angular.element(elm).popup({
              inline: scope.inline === undefined ? !(scope.popup) : scope.inline,
              popup: scope.popup === undefined ? undefined : $(scope.popup),
              hoverable: true,
              position : scope.popupPosition === undefined ? "left center" : scope.popupPosition,
              delay: {
                show: 0,
                hide: 50
              }
            });
          }, 0);
        }
      };
    }]);

/*
    angular.module('xeditable').directive('editableSemanticDate', ['editableDirectiveFactory',
      function(editableDirectiveFactory) {
        return editableDirectiveFactory({
          directiveName: "editableSemanticDate",
          inputTpl: '<input type="text" daterangepicker>'
        });
    }]);
*/

    mod.directive('daterangepicker', ["$log", "$timeout", function($log, $timeout) {
      return {
        restrict: "A",
        require: 'ngModel',
        scope: {
          ngModel: "=",
          minDate: "=",
          ngChange: "&",
          format: "=",
        },
        priority: 100,
        link: function ($scope, $element, attrs, ngModel) { 
          // TODO: There are two ways to fire this.  from xeditable or from
          // daterangepicker directive. 
          // the daterangepicker uses the standard ngModel scope variable
          // the xeditable uses the model directive


          // don't let the default onChcange handler update the model
          ngModel.$overrideModelOptions({
            'updateOn': 'none'
          });

          var pickr;
          var localValue;

          var dateChanged = function () {
            if (pickr.selectedDates && pickr.selectedDates.length > 0) { 
              var m = moment(pickr.selectedDates[0]);
              var year = m.year();
              var month = m.month();
              var day = m.date();
              var hour = m.hour();
              var minute = m.minute();
              var second = m.second();

              // shift the value back to UTC
              localValue = Date.UTC(year, month, day, hour, minute, second);
              $timeout(function () {
                ngModel.$setViewValue(localValue);
                ngModel.$modelValue = localValue;
                $scope.ngModel = localValue;
                $timeout(function () {
                  $scope.ngChange({'value': localValue});
                });
              });
            }
          };

          $scope.$watch("ngModel", function (value) { 
            if (value === undefined && pickr !== undefined) { 
              pickr.clear();
            }
          });

          /*
          $scope.

          */

          $element.on("click", function () { 
            var d = ngModel.$modelValue;
            var date, minDate;

            if (d !== undefined && localValue !== d && d !== "") {
              var m = moment.utc(d);
              date = new Date(m.year(), m.month(), m.date(), m.hour(), m.minute(), m.second());
            }

            if ($scope.minDate !== undefined) { 
              minDate = $scope.minDate;
            }

            // only load this picker once
            if (pickr === undefined) {
              pickr = $element.flatpickr({
                utc: true,
                minDate: minDate,
                altFormat: "m/d/Y",
                altInput: true,
                enableTime: true,
                dateFormat: "Z",
                defaultDate: date,
                onChange: dateChanged
              });
              pickr.open();
            } else {
              // update min date
              pickr.set("minDate", minDate);
            }
          });

          ngModel.$render = function() {
            var d = ngModel.$modelValue;
            if (d !== undefined && d !== null && localValue !== d && d !== "") {
              var m = moment.utc(d);
              // set the value of date to be the UTC
              // i'll reset the value to UTC when the date changes.
              $element.val(m.format("MM/DD/YYYY"));
              //pickr.setDate(new Date(m.year(), m.month(), m.day(), m.hour(), m.minute(), m.second()));
            }
          };

          

          // angular.element(elm).daterangepicker({
          //   "autoApply": true,
          //   "startDate": model,
          //   "singleDatePicker": true
          // }).on('apply.daterangepicker', function(ev, picker) {
          //   // there is an issue where the ng-model fires
          //   // but the model hasn't been applied yet
          //   // not sure how to fix it.
          //   scope.model = scope.ngModel = picker.startDate.format('MM/DD/YYYY');
          //   // can't submit.. it sets data to a string
          //   // and it must stay a "moment"
          //   //$(elm).submit();
          // }).on('show.daterangepicker', function (e) {
          //   angular.element(".daterangepicker").on('click', function (e) {
          //     // stop the calendar click events from triggering the xeditable directive;
          //     //e.stopPropagation();
          //   });
          // });
        }
      };
    }]);

    mod.directive('menu', ["$log", function ($log) {
      return {
        restrict: "C",
        scope: false,
        link: function (scope, elm, attr) { 
            var handler = {
              activate: function(target) {
                $log.info("common.semantic: SemanticMenu - Menu Activated");
                if(!angular.element(target).hasClass('dropdown')) {
                  
                    angular.element(target)
                      .addClass('active')
                      .closest('.ui.menu')
                      .find('.item')
                      .not(angular.element(target))
                      .removeClass('active');
                }
              }
            };
            var parent = angular.element(elm).parent();
            if (!parent.hasClass("dropdown")) {
              angular.element(elm).children(".item:not(.transparent)").on('click', function (e) { 
                handler.activate(this);
              });
            }
        }
      };
    }]);
    mod.directive('tab', ["$log", function ($log) { 
      return {
        restrict: "A",
        link: function (scope, elm, attr) {
          angular.element(elm).tab();
        }
      };
    }]);
  return mod;
});