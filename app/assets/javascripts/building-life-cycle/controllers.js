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

    var systemCategories = {
        "HVAC": buildingSyncSchema.definitions[".auc:Audits"].properties["auc:Audit"].anyOf["0"].properties["auc:Systems"].properties["auc:HVACSystems"],
        "Domestic Hot Water": buildingSyncSchema.definitions[".auc:Audits"].properties["auc:Audit"].anyOf["0"].properties["auc:Systems"].properties["auc:DomesticHotWaterSystems"],
        "Fan": buildingSyncSchema.definitions[".auc:Audits"].properties["auc:Audit"].anyOf["0"].properties["auc:Systems"].properties["auc:FanSystems"],
        "Fenestration": buildingSyncSchema.definitions[".auc:Audits"].properties["auc:Audit"].anyOf["0"].properties["auc:Systems"].properties["auc:FenestrationSystems"],
        "Heat Recovery": buildingSyncSchema.definitions[".auc:Audits"].properties["auc:Audit"].anyOf["0"].properties["auc:Systems"].properties["auc:HeatRecoverySystems"],
        "Lighting": buildingSyncSchema.definitions[".auc:Audits"].properties["auc:Audit"].anyOf["0"].properties["auc:Systems"].properties["auc:LightingSystems"]
    };

    $scope.format = 'hh:mm:ss';
    $scope.building = {};
    $scope.uploadType = {};

    $scope.useTypes = buildingSyncSchema.definitions[".auc:AssetScore"].properties["auc:UseType"].anyOf["0"].properties["auc:AssetScoreUseType"].properties.$.enum;
    $scope.implementationStatuses = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:ImplementationStatus"].properties.$.enum;
    $scope.systemCategories = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:SystemCategoryAffected"].properties.$.enum;

    $scope.selectedMeasureCategory = {};
    $scope.selectedMeasure = {};

    $scope.measureCategories = Object.keys(measureCategories);

    $scope.selectedMeasureCategoryChanged = function(value) {
            $scope.measure = {};
            $scope.implementationStatus = {};
            $scope.availableMeasures = measureCategories[value];
    };

    $scope.selectedMeasureChanged = function() {

    };

    $scope.systemCategories = Object.keys(systemCategories);

    $scope.selectedSystemCategory = {};
    $scope.selectedSystem = {};

    $scope.system = {};

    $scope.measures = {'list': [] };
    $scope.systemList = [];

    $scope.addMeasureToList = function() {

        var newMeasure = {
            "systemType": $scope.selectedMeasureCategory.selected,
            "detail": $scope.measure.selected,
            "implementationStatus": $scope.implementationStatus.selected,
//            "systemCategory": $scope.systemCategory.selected,
            "startDate": $scope.measure.startDate,
            "endDate": $scope.measure.endDate,
            "comment": $scope.measure.comment
        };
            if ($scope.measure.endDate !== undefined) {
                $scope.measures.list.push(newMeasure);
            } else {
                console.log('measure end date not defined');
            }
    };

    $scope.systemAdded = function(systemName) {
        if ($scope.systemList.filter( function (system) {
            return Object.keys(system)[0] === systemName;
        }).length > 0) {
            return true;
        } else {
            return false;
        }
    };

    $scope.selectedSystemCategoryChanged = function() {
        $scope.system = {};
        if ($scope.selectedSystemCategory.selected == "Domestic Hot Water") {
            $scope.showType1 = true;
            $scope.showType2 = false;
            $scope.showType3 = false;
            $scope.showType4 = false;
            $scope.showType5 = false;
            $scope.showType6 = false;
        }
        if ($scope.selectedSystemCategory.selected == "Fan") {
            $scope.showType1 = false;
            $scope.showType2 = true;
            $scope.showType3 = false;
            $scope.showType4 = false;
            $scope.showType5 = false;
            $scope.showType6 = false;
        }
        if ($scope.selectedSystemCategory.selected == "HVAC") {
            $scope.showType1 = false;
            $scope.showType2 = false;
            $scope.showType3 = true;
            $scope.showType4 = false;
            $scope.showType5 = false;
            $scope.showType6 = false;
        }
        if ($scope.selectedSystemCategory.selected == "Fenestration") {
            $scope.showType1 = false;
            $scope.showType2 = false;
            $scope.showType3 = false;
            $scope.showType4 = true;
            $scope.showType5 = false;
            $scope.showType6 = false;
        }
        if ($scope.selectedSystemCategory.selected == "Heat Recovery") {
            $scope.showType1 = false;
            $scope.showType2 = false;
            $scope.showType3 = false;
            $scope.showType4 = false;
            $scope.showType5 = true;
            $scope.showType6 = false;
        }
        if ($scope.selectedSystemCategory.selected == "Lighting") {
            $scope.showType1 = false;
            $scope.showType2 = false;
            $scope.showType3 = false;
            $scope.showType4 = false;
            $scope.showType5 = false;
            $scope.showType6 = true;
        }
    };

//    $scope.checkDates = function () {
//    if ($scope.measure.startDate !== undefined && $scope.measure.endDate !== undefined) {
//        if ($scope.measure.startDate > $scope.measure.endDate) {
//            console.log('wrong');
//        } else {
//            console.log('right');
//        }
//       }
//    };

    $scope.getSystemName = function(system) {
        return Object.keys(system)[0];
    };

    $scope.getFirstKey = function(system) {
        var key = $scope.getSystemName(system);
        return Object.keys(system[key])[0];
    };

    $scope.getSecondKey = function(system) {
        var key1 = $scope.getSystemName(system);
        var key2 = $scope.getSystemName(system[key1]);
        var level1 = system[key1];
        return Object.keys(level1[key2])[0];
    };

    $scope.propertyHasChildren = function(input) {
        return angular.isObject(Object.keys(input)[0]);
    };

    $scope.addSystemToList = function() {
        console.log('sys: ', $scope.form1);
//        console.log('sys: ', $scope.systems_form.system);

        console.log("system: ", $scope.system);
        if (Object.keys($scope.system).length === 0) {


            console.log("empty");
        } else {
            $scope.systemList.push($scope.system);
        }

        delete $scope.system;
        $scope.system = {};
    };

    $scope.removeMeasure = function(index) {
        $scope.measures.list.splice(index, 1);
    };

    $scope.removeSystem = function(index) {
       $scope.systemList.splice(index, 1);
    };

    $scope.downloadData = function(){
        var output = $scope.building;

        var userDefinedFields = {
                'auc:UserDefinedField': {
                    'auc:FieldName': { '$': 'buildingName' },
                    'auc:FieldValue': {"$": $scope.building.buildingName }
                }
        };

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

        var assetScore = {
            'auc:UseType':  [
               {
               'auc:AssetScoreData': {
                    'auc:Score': {}
                },
               'auc:AssetScoreUseType': {
                    '$': 'Library'
                    }
               }
              ]
        };

        var audits = {
            'auc:Audit': {
                'auc:Sites': {
                    'auc:Site': {
                        'auc:Facilities': {
                            'auc:Facility': {
                                "auc:FloorsAboveGrade": {
                                            "$": parseInt($scope.building.numberOfFloorsAboveGrade)
                                        },
                                        "auc:FloorsBelowGrade": {
                                            "$": parseInt($scope.building.numberOfFloorsBelowGrade)
                                        },
                                        "auc:YearOfConstruction": {
                                            "$": $scope.building.yearCompleted
                                        },
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

        var removeMeta = function(obj) {
            // obj is passed by reference and will return 
            // itself minus the "$" unless it's the leaf of a tree
            if (typeof obj === 'object') {
                var keys = Object.keys(obj);
                    keys.forEach( function (key) {
                        if (key === '$' && keys.length > 1) {
                            delete obj[key];
                        } else {
                            removeMeta(obj[key]);
                        }
                    });
            }
            return obj;
        };

        var convertInnerIntoObject = function(obj) {
            var newObj = {};
            if (typeof obj === 'object') {
                var key = Object.keys(obj)[0];
                var innerObj = obj[key];
                var newArray = [innerObj];
                obj[Object.keys(obj)[0]] = newArray;
            }
            return obj;
        };

        var systems = { };

        if ($scope.systemList.length > 0) {
            audits['auc:Audit']['auc:Systems'] = {};
            // in the future -> angular.toJson(obj);
            var j, sl, key;
            for (j = 0; j < $scope.systemList.length; j++) {
                sl = removeMeta($scope.systemList[j]);
                key = Object.keys(sl)[0];
                var newSystem = removeMeta(sl[key]);
                if (audits['auc:Audit']['auc:Systems'][key] !== undefined && Object.keys(audits['auc:Audit']['auc:Systems']).indexOf(key) > -1) {
                    console.log('type exists, adding new');
                     var newSystemInner = newSystem[Object.keys(newSystem)[0]];
                     var currentInner = audits['auc:Audit']['auc:Systems'][key][Object.keys(audits['auc:Audit']['auc:Systems'][key])[0]];
                     currentInner.push(newSystemInner);
                     audits['auc:Audit']['auc:Systems'][key][Object.keys(audits['auc:Audit']['auc:Systems'][key])[0]] = currentInner;
                } else {
                    audits['auc:Audit']['auc:Systems'][key] = convertInnerIntoObject(newSystem);
                }

            }
        }

        var generateMeasures = [];
        for (var i = 0; i < $scope.measures.list.length; i++) {

            var systemTypeRemoveSpaces = $scope.measures.list[i].systemType.replace(/\s/g, '');
            var systemTypeRemoveSpacesUpperCase = 'auc:' + systemTypeRemoveSpaces.charAt(0).toUpperCase()+systemTypeRemoveSpaces.slice(1);
            var inner = {'auc:MeasureName':  {'$': $scope.measures.list[i].detail } };
            var category = {};
            category[systemTypeRemoveSpacesUpperCase] = inner;

            var systemType = $scope.measures.list[i].systemType;
            var mes = {
                'auc:EndDate':  {
                    '$': moment.utc($scope.measures.list[i].endDate).format("ll")
                },
                'auc:ImplementationStatus': {
                    '$': $scope.measures.list[i].implementationStatus
                },
                'auc:LongDescription': {
                    '$': $scope.measures.list[i].comment
                },
                'auc:StartDate': {
                    '$': moment.utc($scope.measures.list[i].startDate).format("ll")
                },
                'auc:SystemCategoryAffected': {
                    '$': $scope.measures.list[i].systemCategory
                },
                'auc:TechnologyCategories': {
                    'auc:TechnologyCategory': category
                }
            };
            generateMeasures.push(mes);
        }

        if ($scope.measures.list.length > 0) {
            audits['auc:Audit']['auc:Measures'] = {};
            audits['auc:Audit']['auc:Measures'] = { 'auc:Measure': generateMeasures };
        }

        var out = {
            'auc:Address' : address,
            'auc:AssetScore': assetScore,
            'auc:Audits': audits,
            'auc:UserDefinedFields': userDefinedFields
        };
        var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(out));
        var downloadAnchorNode = document.createElement('a');
        downloadAnchorNode.setAttribute("href",     dataStr);
        downloadAnchorNode.setAttribute("download", "output.json");
        downloadAnchorNode.click();
        downloadAnchorNode.remove();
    };

    $scope.downloadCsv = function() {

        console.log("download csv");
        var measuresOut = "";
        var systemsOut = "";
        $scope.measures.list.forEach( function(m) {
                console.log("endDat: ", moment.utc(m.endDate).format("ll"));
                var startDate = moment.utc(m.startDate).format("ll");
                var endDate = moment.utc(m.endDate).format("ll");
            var comment = (m.comment === undefined) ? "\"\"" : "\""+m.comment+"\"";
            measuresOut += "\"" + m.systemType + "\",\"" + m.detail + "\",\"" + m.implementationStatus + "\",\"" + startDate + "\",\"" + endDate + "\"," + comment + "\r\n";
        });

        console.log('measuresOut: ' + $scope.measures.list);

        $scope.systemList.forEach( function(s) {
            var keys = Object.keys(s);
            keys.forEach(function(k) {
                systemsOut += k + "\r\n";
            });
        }
        );

        playRoutes.controllers.BuildingLifeCycle.buildXlsx($scope.measures.list).post(
            {
                'measures': $scope.measures.list,
                'systems': $scope.systemList
            }
        ).then ( function(response) {
                window.location.href = '/getFile?token='+response.data;
            }
        );

//        if ($scope.measures.list.length > 0) {
//            var dataStr = "data:text/csv;charset=utf-8," + encodeURIComponent(measuresOut);
//            var downloadAnchorNode = document.createElement('a');
//            downloadAnchorNode.setAttribute("href",     dataStr);
//            downloadAnchorNode.setAttribute("download", "measures.csv");
//            downloadAnchorNode.click();
//            downloadAnchorNode.remove();
//        }
//
//        if ($scope.systemList.length > 0) {
//            var dataStr2 = "data:text/csv;charset=utf-8," + encodeURIComponent(systemsOut);
//            var downloadAnchorNode2 = document.createElement('a');
//            downloadAnchorNode2.setAttribute("href",     dataStr2);
//            downloadAnchorNode2.setAttribute("download", "systems.csv");
//            downloadAnchorNode2.click();
//            downloadAnchorNode2.remove();
//        }

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
            if ($scope.model.file.name) {
                $scope.upload($scope.model.file, $scope.meter);
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
                    propertyName = "Unknown";
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
            url: playRoutes.controllers.BuildingLifeCycle.parseXls().url,
            data: {
                inputData: file,
                type: $scope.uploadType.type
            }

        }).then(function (resp) {
            console.log('Success ' + resp.config.data.inputData.name + 'uploaded. Response: ' + resp.data);
            verificationRows = [];
            $scope.validation = [];

            if ($scope.uploadType.type === "measures") {
                $scope.measures.list = resp.data;
            }
            else if ($scope.uploadType.type === "systems") {
                var results = [];
                var result = resp.data;
                result.forEach(function(r) {
                    // first level is not displayed in GUI
                    var item = { "placeholder" : {}};
                    item.placeholder[r.systemType] = {r: "value"};
                    results.push(item);
                }
                );
                console.log("results: ", results);
                $scope.systemList = results;
            }

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
