/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', 'matchmedia-ng', 'angular-file-upload'], function(angular) {
  'use strict';
  var DataQualityCtrl = function($rootScope, $scope, $window, $sce, $timeout, $q, $log, playRoutes, Upload, matchmedia) {

    $rootScope.pageTitle = "Data Quality Tool";
    $scope.forms = {'hasValidated': false};
    $scope.matchmedia = matchmedia;
    $scope.mainColumnWidth = "";
    $scope.meter = {};
    $scope.model = {
        'value': {},
        'file': undefined
    };
    $scope.filter = [];
    $scope.hideDays = true;

    // check the media to handel the ng-if media statements
    // it turns out that form elements do not respect "display: none"
    // and need to be removed from the dom
    var setMedia = function (){
        if (matchmedia.isPrint()) {
            $scope.media = "print";
            $scope.mainColumnWidth = "eight wide column";
        } else if (matchmedia.isPhone()) {
            $scope.media = "phone";
            $scope.mainColumnWidth = "eight wide column";
        } else if (matchmedia.isTablet()) {
            $scope.media = "tablet";
            $scope.mainColumnWidth = "eight wide column";
        } else if (matchmedia.isDesktop()) {
            $scope.media = "desktop";
            $scope.mainColumnWidth = "eight wide column";
        } else {
            $scope.mainColumnWidth = "eight wide column";
        }
    };
    matchmedia.on('only screen and (min-width: 1200px) and (max-width: 1919px)', function(match){
        $scope.largeScreen = match.matches;
    });
    matchmedia.on('only screen and (max-width: 1199px)', function(match){
        $scope.largeScreen = !match.matches;
    });

    matchmedia.onPrint(setMedia, $scope);

    setMedia();
    angular.element($window).bind("resize", function () {
        setMedia();
        $scope.$apply();
    });


    $scope.$watch("meter.frequency", function (v) {
        if(v==="day"){
            $scope.hideDays = false;
        } else {
            $scope.hideDays = true;
        }

    });

    $scope.computeBenchmarkMix = function(results){

        console.log(results);
        var metricsTable = [
            //the return after weather normalization
        ];
        return metricsTable;
    };

    $scope.submitErrors = function () {
        for (var i = 0; i < $scope.forms.sensitivityForm.$error.required.length; i++){
            $log.info($scope.forms.sensitivityForm.$error.required[i].$name);
        }
    };

    $scope.submitFile = function() {
        if($scope.forms.sensitivityForm.$valid){
            if ($scope.model.file.name && $scope.meter) {
                $scope.upload($scope.model.file, $scope.meter);
            }
        }
    };

    $scope.loadingFileFiller = {};

    $scope.upload = function (file,form) {
        $scope.loadingFileFiller.loading = true;

        Upload.upload({
            url: playRoutes.controllers.DataQuality.validate().url,
            data: {inputData: file,
                   "userSubmitted": JSON.stringify(form)
                  }

        }).then(function (resp) {
            console.log('Success ' + resp.config.data.inputData.name + 'uploaded. Response: ' + resp.data);
            $scope.loadingFileFiller = {};
            $scope.model.value = resp.data;

        }, function (resp) {
            $scope.loadingFileFiller = {'loading': true};
            $scope.model.value = resp.data;

        }, function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $scope.loadingFileFiller = {
                progressPercentage: progressPercentage,
                attachmentName: evt.config.data.inputData.name
            };
            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.inputData.name);
        });
     };

    $scope.getMeter = function() {

        if($scope.forms.sensitivityForm === undefined) {
            return undefined;
        }
        if($scope.forms.sensitivityForm.$valid){
            $scope.meter.valid = true;
        } else {
            $scope.meter.valid = false;
        }

        $scope.meter.filter = [];
        $scope.meter.ddThreshold = $scope.meter.ddThreshold ? $scope.meter.ddThreshold  : 65;
        $scope.meter.ddType = $scope.meter.ddType ? $scope.meter.ddType  : "avg";
        $scope.meter.frequency = $scope.meter.frequency ? $scope.meter.frequency  : "month";
        $scope.daysOfWeek.forEach(function (v){
            if($scope.filter[v.name]===true){
                $scope.meter.filter.push(v.name);
            }
        });

        return $scope.meter;
    };

    $scope.daysOfWeek = [
        {
            name: "monday",
            default: false,
            type: "checkbox",
            title: "M",
        },
        {
            name: "tuesday",
            default: false,
            type: "checkbox",
            title: "Tu",
        },
        {
            name: "wednesday",
            default: false,
            type: "checkbox",
            title: "W",
        },
        {
            name: "thursday",
            default: false,
            type: "checkbox",
            title: "Th",
        },
        {
            name: "friday",
            default: false,
            type: "checkbox",
            title: "F",
        },
        {
            name: "saturday",
            default: false,
            type: "checkbox",
            title: "Sa",
        },
        {
            name: "sunday",
            default: false,
            type: "checkbox",
            title: "Su",
        }
    ];

    $scope.thresholdOptions =  [
            {id:"avg",name:"Average"},
            {id:"min",name:"Minimum"},
            {id:"max",name:"Maximum"}
    ];

    $scope.frequencyOptions =  [
            {id:"day",name:"Day"},
            {id:"month",name:"Month"}
    ];


  };

  DataQualityCtrl.$inject = ['$rootScope', '$scope', '$window','$sce','$timeout', '$q', '$log', 'playRoutes', 'Upload', 'matchmedia'];
  return {
    DataQualityCtrl: DataQualityCtrl
  };
});
