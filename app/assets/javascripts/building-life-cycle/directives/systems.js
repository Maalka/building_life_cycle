
define(['angular'], function(angular) {
    'use strict';
    var mod = angular.module('buildingLifeCycle.directives', []);
    mod.directive('systems', ['$interval', 'dateFilter', function($interval, dateFilter) {

        function link(scope, element, attrs) {

        scope.$watch("system['auc:LightingSystems']['auc:LightingSystem']['auc:LampType'].$", function(value) {
            var compactFluorescentLamp = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:CompactFluorescent_auc:LampLabel_$']");
            var halogenLamp = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:Halogen_auc:LampLabel_$']");

            if (value == 'CompactFluorescent') {
                compactFluorescentLamp.show();
            }
            if (value == 'Halogen') {
                compactFluorescentLamp.hide();

            }
            if (value == 'Incandescent') {
                var incandescentLamp = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:Incandescent_auc:LampLabel']");
                incandescentLamp.css( "background-color", "red");
            }
            if (value == 'Neon') {
                var neon = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:Neon']");
                neon.css( "background-color", "red");
            }
            if (value == 'OtherCombination') {
                var otherCombination = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:OtherCombination']");
                otherCombination.css( "background-color", "red");
            }
            if (value == 'SolidStateLighting') {
                var solidStateLighting = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:SolidStateLighting']");
                solidStateLighting.css( "background-color", "red");
            }
            if (value == 'Unknown') {
                var unknown = element.find("[data-alpaca-container-item-name='LightingSystem_auc:LampType_auc:Unknown']");
                unknown.css( "background-color", "red");
            }
                console.log(value);

        });


        }
         return {
           link: link
         };
       }]);
    return mod;
});
