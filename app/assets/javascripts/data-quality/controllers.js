/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', 'moment', 'matchmedia-ng', 'angular-file-upload', 'moment'], function(angular, moment) {
  'use strict';
  var DataQualityCtrl = function($rootScope, $scope, $window, $sce, $timeout, $q, 
                            $log, playRoutes, Upload, matchmedia) {

    $rootScope.pageTitle = "Data Quality Tool";
    $scope.forms = {'hasValidated': false};
    $scope.matchmedia = matchmedia;
    $scope.mainColumnWidth = "";
    $scope.model = {
        'value': {},
        'file': undefined
    };
    $scope.form = {};
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


    $scope.submitErrors = function () {
        for (var i = 0; i < $scope.forms.dataQualityForm.$error.required.length; i++){
            $log.info($scope.forms.dataQualityForm.$error.required[i].$name);
        }
    };

    $scope.submitFile = function() {
        if($scope.forms.dataQualityForm.$valid){
            if ($scope.model.file.name) {
                $scope.upload($scope.model.file, $scope.meter);
            }
        }
    };

    $scope.verificationRows = [];
    $scope.verificationHeaders = [];

    var verificationRows = [];

    $scope.verificationRow = function(i) { 
        return verificationRows[i];
    };
    $scope.verificationPropertyName = function(i) { 
        return verificationRows[i][0].value;
    };

    var parseHeaders = function(row) { 
        var j;
        for (j = 0; j < row.length; j += 1) {
            $scope.verificationHeaders[j] = row[j].validator;
        }
    };

    /**
      * Generate the tooltip for the status table
      */
    $scope.tooltip = function (r, i) {
        var str, value;
        var message = r.value;


        if (r.value === null || r.value === undefined || r.value === "") {
            message = "Not Defined";
        } else if (r.valueType === "Date") {
            message = moment(r.value).toString();
        }
        str = $scope.verificationHeaders[i] + ": " + message || "Not Defined";
        if (r.message) { 
            str += " (" + r.message + ")";
        }
        return str;
    };

    $scope.loadMore = function () { 
        var i;
        var last = $scope.verificationRows.length - 1;
        for(i = last; i < last + 8; i += 1) {
            if (i >= 0 && i < verificationRows.length) {
                $scope.verificationRows[i] = $scope.verificationRow(i);
            }
        }
    };

    var parseServerResponse = function(model) { 
        var i, j, first = true;
        verificationRows = [];
        if(model.result !== undefined) { 
            for(i = 0; i < model.result.length; i += 1) {
                if (first) { 
                    parseHeaders(model.result[i]);
                    first = false;
                }
                verificationRows[i] = model.result[i];
            }
            verificationRows = verificationRows.sort(function (a, b) { 
                if (a[0].value < b[0].value) { 
                    return -1;
                }
                if (a[0].value > b[0].value) { 
                    return 1;
                }
                return 0;
            });
        }
    };


    $scope.loadingFileFiller = {
        loading: false
    };

    $scope.upload = function (file,form) {
        $scope.loadingFileFiller.loading = true;

        Upload.upload({
            url: playRoutes.controllers.DataQuality.validate().url,
            data: {
                inputData: file
            }

        }).then(function (resp) {
            console.log('Success ' + resp.config.data.inputData.name + 'uploaded. Response: ' + resp.data);
            parseServerResponse(resp.data);
            $scope.loadingFileFiller = {
                loading: false
            };

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

  };

  DataQualityCtrl.$inject = ['$rootScope', '$scope', '$window','$sce','$timeout', 
        '$q', '$log', 'playRoutes', 'Upload', 'matchmedia'];
  return {
    DataQualityCtrl: DataQualityCtrl
  };
});
