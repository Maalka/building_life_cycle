# README #

# Building Lifecycle Tool #
Verson 1.0
The [**Building Lifecycle Tool**](https://lifecycle.maalka.com) is a simple, open source, application that offers municipalities and energy service providers a transparent, cost-effective approach to collecting and structuring building energy audit data in a standardized way.  The Building Life Cycle Tool guides users in the collection of the most relevant building information that can be used to assess strategies for upgrading the building to run more efficiently. Users are guided by a dynamic survey for collecting data on the most important building Systems (e.g. HVAC, Ventilation, Lighting Systems) and relevant Measures (e.g. Boiler Improvements, Lighting Upgrades). In this way, users can follow a standardized approach for collecting initial Building Audit data, as each system and measure is defined by a set of associated, standardized fields.

**The Building Lifecycle Tool key features:**

* Custom “Systems” and “Measurements” inputs 
* Printable visual timeline of Systems and Measures 
* CSV Export  
* Building sync compatible JSON Export 

The **Building Lifecycle Tool** is one of many open source tools created by the [Maalka Team](http://www.maalka.com) with support from the U.S. Department of Energy Better Buildings [Municipal Portfolio Benchmarking](https://www.maalka.com/mpb) project. 

For information on how to use the Building Life Cycle Tool,  check out the <a href="https://lifecycle.maalka.com/#/usecase">use case</a> In addition, we have prepared [a video tutorial](http://xxxxx) to help.

The following list includes all data points included with the Open-source Life-cycle tool

### Building parameters ###

**Address** 
 * City
	* State
	* PostalCode
	* StreetAddressDetail

**SiteType**
 * Facilities
	 * Facility
	 * CommercialFacilityType  
 * FloorsAboveGrade
 * FloorsBelowGrade
 * YearOfConstruction
 * OccupancyClassification
 * PremisesName
  	* FloorAreas
  	* FloorArea
 * FloorAreas
  	* FloorAreaValue

### Measures ###
 
**MeasureType**
 * ImplementationStatus
 * StartDate for each Measure
 * EndDate for each Measure
 * LongDescription free text box
 * SystemCategoryAffected for each Measure
 * TechnologyCategories [each will have its own subfields]
  	* AdvancedMeteringSystems
  	* BoilerPlantImprovements
  	* BuildingAutomationSystems
  	* BuildingEnvelopeModifications
  	* ChilledWaterHotWaterAndSteamDistributionSystems
  	* ChillerPlantImprovements
  	* DistributedGeneration
  	* ElectricMotorsAndDrives
  	* ElectricalPeakShavingLoadShifting
  	* EnergyDistributionSystems
  	* LightingImprovements
  	* PlugLoadReductions
  	* Other

### Systems ###

**SystemType [each will have its own subfields]**
 * HVACSystems
 * DomesticHotWaterSystems
 * FanSystems
 * FenestrationSystems
 * HeatRecoverySystems
 * LightingSystems

Each Measure and System category also contains a large set of associated fields required to better defined the entry. For example, HVAC Systems may have a Type Category with an option to select Boiler, which also has a list of associated fields.

In order to focus the task of data collection, subfield sets of each category were minimized to contain the most common and important data associated with the category. Included subfields were determined to be applicable to articulating more accurate EnergyPlus models. Due to the open-source nature of the tool, anyone can re-add and/or modify custom BuildingSync compatible fields as necessary.


 ### What is this repository for? ###

* [Learn Building Lifecycle Tool](https://lifecycle.maalka.com/#/usecase)

## Run
* from sbt command line run: sbt run

* Now the application can be invoked either from a browser at http://localhost:9000 or from the command line (curl)

 
