define(['angular', 'common'], function(angular) {
	'use strict';
var mod = angular.module('buildingLifeCycle.filters', ['maalka.common']);
mod.filter('tadasFilter', function() {
    return function(input) {
    console.log('calling filter');
        var out = iterate(JSON.parse(angular.toJson(input)), '');
        console.log('out: ', out);
        return '<li>' + getFirstKey(input) + '</li>';
        };
    })
    .filter('systemLabel', function () {
         return function(object) {
           if (object !== undefined) {
             var parts = object.split(":");
             if (parts.length > 0 ) {
                return parts.pop();
             } else {
                return "";
             }
           }
         };
       });
	return mod;

    function getFirstKey(input) {
        return Object.keys(input)[0];
	}

    function iterate(obj, stack) {
            for (var property in obj) {
                if (obj.hasOwnProperty(property)) {
                    if (typeof obj[property] == "object") {
                        iterate(obj[property], stack + '#' + property);
                    } else {
                        console.log(property + "   " + obj[property]);

                        $('#output').append($("<div/>").text(stack.split('#').pop() + ': ' + obj[property]));
                    }
                }
            }
        }

//	function recursive(input) {
//        var output = "";
//        console.log('entered recursive with input', input);
//        console.log('object has number of properties: ', Object.keys(input).length);
//        for (var i = 0; i < Object.keys(input).length; i++) {
//            console.log('and they are: key: ', Object.keys(input)[i]);
//            console.log('value: ', input[Object.keys(input)[i]]);
//            if (angular.isObject(input[Object.keys(input)[i]])) {
//                    console.log('going deeper', Object.keys(input)[i]);
//                    output += input[Object.keys(input)[i]];
//                    recursive(input[Object.keys(input)[i]]);
//                } else {
//                    console.log('reached end: ', input[Object.keys(input)[i]]);
//                    return "exit";
//                }
//        }
//    }
});