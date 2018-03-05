/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
define(['angular', 'moment', 'json!data/BuildingSyncSchema.json', 'matchmedia-ng', 'angular-file-upload', 'moment'], function(angular, moment, buildingSyncSchema) {
  'use strict';



  var BuildingLifeCycleCtrl = function($scope, $window, $sce, $timeout, $q, $filter,
                            $log, playRoutes, Upload, matchmedia, fileUtilities) {

    $scope.pageTitle = "Building Life Cycle Tool";
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

    var measureCategories = {
        "Advanced Metering Systems": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:AdvancedMeteringSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Boiler Plant Improvements": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:BoilerPlantImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Building Automation Systems": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:BuildingAutomationSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Building Envelope Modifications": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:BuildingEnvelopeModifications"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Chilled Water Hot Water And Steam Distribution Systems": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ChilledWaterHotWaterAndSteamDistributionSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Chiller Plant Improvements": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ChillerPlantImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Distributed Generation": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:DistributedGeneration"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Electrical Peak Shaving Load Shifting": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ElectricalPeakShavingLoadShifting"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Electric Motors And Drives": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ElectricMotorsAndDrives"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Energy Cost Reduction Through Rate Adjustments": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:EnergyCostReductionThroughRateAdjustments"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Energy Distribution Systems": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:EnergyDistributionSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Energy Related Process Improvements": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:EnergyRelatedProcessImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Future Other ECMs": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:FutureOtherECMs"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Lighting Improvements": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:LightingImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Other HVAC": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:OtherHVAC"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Plug Load Reductions": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:PlugLoadReductions"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Refrigeration": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:Refrigeration"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Renewable Energy Systems": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:RenewableEnergySystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Uncategorized": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:Uncategorized"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum,
        "Water And Sewer Conservation Systems": buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:WaterAndSewerConservationSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum
    };

    // assets

    // end assets

    $scope.building = {};
    $scope.building.buildingName = 'example buildingName';
    $scope.building.addressStreet = 'example street';
    $scope.building.addressCity = 'example city';
    $scope.building.addressState = 'KS';
    $scope.building.addressZip = '12345';
    $scope.building.yearCompleted = '1999';
    $scope.building.numberOfFloors = '5';
    $scope.building.useType = 'Library';
    $scope.building.floorArea = '2250';
    $scope.building.orientation = 'North';

    $scope.useTypes = buildingSyncSchema.definitions[".auc:AssetScore"].properties["auc:UseType"].anyOf["0"].properties["auc:AssetScoreUseType"].properties.$.enum;

    $scope.selectedMeasureCategory = {};
    $scope.selectedMeasure = {};

    $scope.measureCategories = Object.keys(measureCategories);

    $scope.selectedMeasureCategoryChanged = function(value) {
            $scope.availableMeasures = measureCategories[value];
    };

    $scope.selectedMeasureChanged = function() {

    };

    $scope.assetCategories = ['Absorption Heat Source', 'AC Adjusted', 'Air Delivery Type', 'Air Inflitration Test'];
    $scope.assets = ['Steam', 'Combustion', 'Waste heat', 'Other', 'Unknown'];

    $scope.selectedAssetCategory = {};
    $scope.selectedAsset = {};



    $scope.measures = {'list': [] };
    $scope.assetList = [];

    $scope.addMeasureToList = function() {

        var newMeasure = {
            "category": $scope.selectedMeasureCategory.selected,
            "measure": $scope.measure.selected,
            "startDate": $scope.measure.startDate,
            "endDate": $scope.measure.endDate,
            "comment": $scope.measure.comment
        };

                 $scope.measures.list.push(newMeasure);
           };

           $scope.selectedAssetCategoryChanged = function() {
               console.log('selectedAssetCategoryChanged');
           };

           $scope.selectedAssetChanged = function() {
               console.log('selectedAssetChanged');
           };


           $scope.addAssetToList = function() {

               var newAsset = {
                   "category": $scope.selectedAssetCategory.selected,
                   "asset": $scope.asset.selected,
                   "comment": $scope.asset.comment
               };

               $scope.assetList.push(newAsset);

    };

    $scope.remove = function() {
        console.log('remove');
    };


    $scope.downloadData = function(){
        var output = $scope.building;

        output.measures = $scope.measures.list;
        output.assets = $scope.assetList;
        var address = {
            'auc:StreetAddressDetail' : {
                'auc:Simplified': {
                    'auc:StreetAddress': {
                        '$': $scope.building.addressStreet
                    }
                }
            },
            'auc:City': {
                '$': $scope.building.addressCity
            },
            'auc:State': {
                '$': $scope.building.addressState
            },
            'auc:PostalCode': {
                '$': $scope.building.addressZip
            }
        };
        var audits = {
            'auc:Audit': {
                        'auc:Sites': {
                            'auc:Site': {
                                "auc:Facilities": {
                                    "auc:Facility": {
                                        "auc:FacilityClassification": {
                                            "$": "Commercial"
                                            }
                                        }
                                },
                                "auc:FloorAreas": {
                                    "auc:FloorArea": {
                                        "auc:FloorAreaValue": {
                                            "$": parseFloat($scope.building.floorArea)
                                            }
                                        }
                                    }
                                }
                            }
                        }
        };
        var out = {
            'auc:Address' : address,
              'auc:Audits': audits
        };
        var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(out));
        var downloadAnchorNode = document.createElement('a');
        downloadAnchorNode.setAttribute("href",     dataStr);
        downloadAnchorNode.setAttribute("download", "output.json");
        downloadAnchorNode.click();
        downloadAnchorNode.remove();
  };

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
        for (var i = 0; i < $scope.forms.buildingLifeCycleForm.$error.required.length; i++){
            $log.info($scope.forms.buildingLifeCycleForm.$error.required[i].$name);
        }
    };

    $scope.submitFile = function() {
        if($scope.forms.buildingLifeCycleForm.$valid){
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
            url: playRoutes.controllers.BuildingLifeCycle.validate().url,
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

  BuildingLifeCycleCtrl.$inject = ['$scope', '$window','$sce','$timeout',
        '$q', '$filter', '$log', 'playRoutes', 'Upload', 'matchmedia', 'fileUtilities'];
  return {
    BuildingLifeCycleCtrl: BuildingLifeCycleCtrl
  };
});
