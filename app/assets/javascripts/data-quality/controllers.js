/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', 'moment', 'matchmedia-ng', 'angular-file-upload', 'moment'], function(angular, moment) {
  'use strict';
  var DataQualityCtrl = function($rootScope, $scope, $window, $sce, $timeout, $q, $filter,
                            $log, playRoutes, Upload, matchmedia, fileUtilities) {

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

    $scope.validation = [];

    var verificationRows = [];

    var parse = function(row) {
        var j, k;
        var parsed = [];
        var tmp;

        for (j = 0; j < row.length; j += 1) {
            tmp = {
                validators: [],
                field: undefined,
                valid: true
            };
            for (k = 0; k < row[j].length; k += 1) {
                tmp.field = row[j][k].validator;
                if (row[j][k].valid === false) {
                    tmp.valid = false;
                }
                tmp.validators[k] = row[j][k];
            }
            parsed.push(tmp);
        }
        return parsed;
    };

    /**
      * Generate the tooltip for the status table
      */
    var generateTooltip = function (propertyIndex, fieldIndex) {
        var str, value, parentValidator;
        var validators = verificationRows[propertyIndex].validated[fieldIndex].validators.reduce(function (a, b) {
            parentValidator = b.parentValidator;
            if (b.valueType === "Date") {
                value = moment.utc(b.value).format("ll");
            } else if (b.valueType === "String") {
                value = b.value;
            } else {
                var int = parseInt(b.value);
                if (!isNaN(int)) {
                    value = $filter("number")(int, 0);
                } else {
                    value = b.value;
                }
            }
            a.push({
                message: b.message,
                field: b.validator,
                value: parentValidator === null ? undefined : value,
                valid: b.valid
            });
            return a;
        }, []).filter(function (validator) {
            if (parentValidator !== null) {
                return true;
            } else {
                return validator.message !== "" && validator.message !== undefined && validator.message !== null;
            }
        });

        return {
            'field': str = parentValidator || verificationRows[propertyIndex].validated[fieldIndex].field,
            'value': parentValidator === undefined ? undefined : (value || "Not Defined"),
            'validators': validators
        };
    };

    var verificationRow = function (i) {
        return {
            i: i,
            propertyName: verificationRows[i].propertyName,
            fields: verificationRows[i].validated.map(function (m, j) {
                return {
                    i: i,
                    tooltip: generateTooltip(i, j),
                    valid: m.valid
                };
            })
        };
    };

    $scope.loadMore = function () {
        var i;
        var last = verificationRows.length - 1;
        for(i = 0; i < 20; i += 1) {
            if ($scope.validation.length < verificationRows.length) {
                $scope.validation.push(verificationRow($scope.validation.length));
            }
        }
    };

    $scope.download = function () { 
        var headers = [];
        var rows = [];
        var row, sourceRow, toolTip, validator, validatorMessages, validatorMessage, tooltip;
        var i, j, k; 
        headers.push("Property Name");
        for (i = 0; i < $scope.validation.length; i += 1) {
            sourceRow = $scope.validation[i];
            row = [];
            row.push("\"" + sourceRow.propertyName.replace("\"", "\"\"") + "\"");
            for (j = 0; j < sourceRow.fields.length; j += 1) {
                tooltip = sourceRow.fields[j].tooltip;
                validatorMessages = [];

                if (tooltip.field !== "" && tooltip.field !== undefined) {
                    headers[j + 1] = tooltip.field;
                    if (tooltip.validators.length === 0) { 
                        if (sourceRow.fields[j].valid) { 
                            validatorMessages.push("OK - " + tooltip.value);
                        }
                    } else {
                        for (k = 0; k < tooltip.validators.length; k += 1) { 
                            validator = tooltip.validators[k];
                            if (!validator.valid) {
                                validatorMessage = validator.message + " (" + (validator.value || tooltip.value) + ")";
                                validatorMessages.push(validatorMessage.replace("\"", "\"\""));
                            } else {
                                validatorMessage = "OK - " + validator.value;
                            }
                        }
                    }
                    row[j + 1] = "\"" + validatorMessages.join(", \n") + "\"";
                }
            }
            rows.push(row);
            row = [];
        }
        return fileUtilities.generateCSV([headers, rows]);
    };

    var sortVerificationRows = function (a, b) {
       if (a.propertyName < b.propertyName) {
           return -1;
       }
       if (a.propertyName > b.propertyName) {
           return 1;
       }
       return 0;
    };

    var parseServerResponse = function(model) { 
        var i, j, propertyName, first = true;
        verificationRows = [];
        if(model.result !== undefined) {
            for(i = 0; i < model.result.length; i += 1) {
                if (model.result[i][0][0] !== undefined) {
                    propertyName = model.result[i][0][0].value;
                } else {
                    propertyName = "Unknonwn";
                }
                verificationRows.push({
                    propertyName: propertyName,
                    validated: parse(model.result[i])
                });
            }
            verificationRows = verificationRows.sort(sortVerificationRows);
            $scope.loadMore();
        }
    };

    $scope.loadingFileFiller = {
        loading: false
    };

    $scope.upload = function (file,form) {
        $scope.loadingFileFiller.loading = true;
        verificationRows = [];
        $scope.validation = [];

        Upload.upload({
            url: playRoutes.controllers.DataQuality.validate().url,
            data: {
                inputData: file
            }

        }).then(function (resp) {
            console.log('Success ' + resp.config.data.inputData.name + 'uploaded. Response: ' + resp.data);
            verificationRows = [];
            $scope.validation = [];

            parseServerResponse(resp.data);

            $scope.loadingFileFiller = {
                loading: false

            };

        }, function (resp) {
            $scope.loadingFileFiller = {
            'loading': true
            };
            $scope.model.value = resp.data;

        }, function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $scope.loadingFileFiller = {
                loading: true,
                progressPercentage: progressPercentage,
                attachmentName: evt.config.data.inputData.name
            };
            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.inputData.name);
        });
     };

  };

  DataQualityCtrl.$inject = ['$rootScope', '$scope', '$window','$sce','$timeout', 
        '$q', '$filter', '$log', 'playRoutes', 'Upload', 'matchmedia', 'fileUtilities'];
  return {
    DataQualityCtrl: DataQualityCtrl
  };
});
