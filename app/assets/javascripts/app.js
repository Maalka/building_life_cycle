/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */
 /**
 * The app module, as both AngularJS as well as RequireJS module.
 * Splitting an app in several Angular modules serves no real purpose in Angular 1.2.
 * (Hopefully this will change in the near future.)
 * Splitting it into several RequireJS modules allows async loading. We cannot take full advantage
 * of RequireJS and lazy-load stuff because the angular modules have their own dependency system.
 */
define(['angular', 'json!data/BuildingSyncSchema.json', 'data-quality', 'maalka-templates'], function(angular, buildingSyncSchema) {
  'use strict';

    var advancedMeteringSystems = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:AdvancedMeteringSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var boilerPlantImprovements = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:BoilerPlantImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var buildingAutomationSystems = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:BuildingAutomationSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var buildingEnvelopeModifications = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:BuildingEnvelopeModifications"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var chilledWaterHotWaterAndSteamDistributionSystems = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ChilledWaterHotWaterAndSteamDistributionSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var chillerPlantImprovements = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ChillerPlantImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var distributedGeneration = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:DistributedGeneration"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var electricalPeakShavingLoadShifting = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ElectricalPeakShavingLoadShifting"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var electricMotorsAndDrives = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:ElectricMotorsAndDrives"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var energyCostReductionThroughRateAdjustments = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:EnergyCostReductionThroughRateAdjustments"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var energyDistributionSystems = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:EnergyDistributionSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var energyRelatedProcessImprovements = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:EnergyRelatedProcessImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var futureOtherECMs = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:FutureOtherECMs"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var lightingImprovements = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:LightingImprovements"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var otherHVAC = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:OtherHVAC"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var plugLoadReductions = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:PlugLoadReductions"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var refrigeration = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:Refrigeration"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var renewableEnergySystems = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:RenewableEnergySystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var uncategorized = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:Uncategorized"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;
    var waterAndSewerConservationSystems = buildingSyncSchema.definitions["auc:MeasureType"].properties["auc:TechnologyCategories"].properties["auc:TechnologyCategory"].anyOf["0"].properties["auc:WaterAndSewerConservationSystems"].properties["auc:MeasureName"].anyOf["0"].properties.$.enum;

  // We must already declare most dependencies here (except for common), or the submodules' routes
  // will not be resolved
  return angular.module('app', ['dataQuality.dashboard', 'matchmedia-ng', 'maalka-templates']).config(['matchmediaProvider', function (matchmediaProvider) {
      matchmediaProvider.rules.phone = "(max-width: 768px)";
      matchmediaProvider.rules.tablet = "(max-width: 1200px)";
      matchmediaProvider.rules.desktop = "(min-width: 1200px)";
   }]);
});
