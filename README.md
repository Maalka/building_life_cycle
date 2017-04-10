# README #

# Data Quality Tool #
The [**Data Quality Tool**](https://dataquality.maalka.com) is a simple, open source, application that allows you to validate the data you have stored within the [Energy Star Portfolio Manager (PM)](https://portfoliomanager.energystar.gov/). The tool provides an easy way to identify problems with both your building's meta data and resource consumption data. 

The Data Quality Tools allows you to:

* Validate PM  building meta data
* Validate Resource Consumption data
* Access the time frame of the validation in the **Current Energy Year** 
* Easily find data that needs your attention

The **Data Quality Tool** is one of many open source tools created by the [Maalka Team](http://www.maalka.com) with support from the U.S. Department of Energy Better Buildings [Municipal Portfolio Benchmarking](https://www.maalka.com/mpb) project.

# Portfolio Manager fields
Data is imported from an Excel spreadsheet that is populated from within Portfolio Manager. Please references:

  * [PM GLossary](https://portfoliomanager.energystar.gov/pm/glossary)
  * [Property Type Definitions(PDF)](https://www.energystar.gov/sites/default/files/tools/Property%20Use%20Details%20and%20Definition%20Jan%202017.pdf)

# Using the Data Quality Tool
The Data Quality Tool provides a way to validate information you have stored in PM.  This includes your building meta data; it's address, the date it was built, etc. It also provides validation of current year resource consumption and analytics. It can easily lead you directly to solutions to data problems and helps in curating your Portfolio Manager data.  

The steps below provide guidance in extracting your portfolio data and using the Data Quality Tool. In addition, we have prepared [a video tutorial](http://xxxxx) to help.

##Data Quality Tool Steps:

1. Sign in to your Energy Star Portfolio Manager Account
2. Upload the Data Quality Tool PM Report to your PM Account by pressing this link.  [Copy Validation Template to your Portfolio Manager Account](https://portfoliomanager.energystar.gov/pm/reports/template/1732437/share/cddf455b-df6a-491f-a63f-ec9f4110b28b)
    * you will be taken to the Portfolio Manager Custom Reports dashboard.
    * there will be a new custom report named **Municipal Portfolio Benchmark** report
3. Select the **Municipal Portfolio Benchmark** report and select **I want to" ... "Edit this Template"**
    * During the edit process, you will add the buildings from within your PM account that you wish to review the data quality.
    * Save the edit template
4. Select the **I want to" ... "Generate New Report"**
    * This will populate the **Municipal Portfolio Benchmark** report with data from the buildings you selected during step #3.
    * You will need to wait for the new report to be completed.
5. Select the **I want to" ... "Download Current Report in Excel"**
    * This will download the report to your local machine
    * You are now ready to upload the report to the Data Quality Tool.
6. Switch back to the **Data Quality Tool** in your browser
7. Press the **Select Energy Star Portfolio Manager Report" button
    * You will be presented with a select local file dialog
    * Select the **Municipal Portfolio Benchmark** Excel (xlsx) file that was downloaded from Portfolio Manager
8. Press the **Validate** link within the Data Quality Tool.


  

## Sample Template
You can also test out the **Data Quality Tool** using a [Sample PM Report](http://cms.maalka.net/resources/Municipal%20Portfolio%20Benchmariking_test%20report.xlsx). This will provide you with a full run-through of the tool. 

1. [Download the Sample Report](http://cms.maalka.net/resources/Municipal%20Portfolio%20Benchmariking_test%20report.xlsx) (note - this picks up at step #5 above)
2. go to the [**Data Quality Tool**](https://dataquality.maalka.com)
3. Press the **Select Energy Star Portfolio Manager Report" button
    * You will be presented with a select local file dialog
    * Select the **Municipal Portfolio Benchmariking_test report.xlsx** Excel (xlsx) file that was downloaded in #2 above
4. Press the **Validate** link within the Data Quality Tool.

You will see the following screen: 
![Alt text](http://cms.maalka.net/resources/DataQualityScreen.png)

## Sharing the Template
You may obtain a copy of the template that will allow you to export and validate your Portfolio Manager data. This is accomplished by first signing into the Energy Star Portfolio Manager site, and then clicking on the following link. 



 [Copy Validation Template to your Portfolio Manager Account](https://portfoliomanager.energystar.gov/pm/reports/template/1732437/share/cddf455b-df6a-491f-a63f-ec9f4110b28b)

****Be Aware!** Clicking on the link will create a copy of the template into your own account.**



  - having the .xls as the import via 
  - a (python) command line tool 
  - output is a csv 

# Data Validation
The **Data Validation Tool** applies a collection of rules to the data in the PM report. These rules are represented in the open source tool as a collection of programmatic conditions. They allow access to various information concerning your buildings.

### Building parameters ###

  * Use type parameters validated by national ranges (see national average document)
  * Property Floor Area-Building(s) (if > 0)
  * Year Built (if > 1800)
  * Property Name (if exists)
  * Location
    * Address (if exists)
    * City (if exists)
    * State (if one of 50 states)
    * Country (if exists)
    * Zipcode (if 6 digits, numeric)
  * ENERGY STAR Score
    * if("Primary Property Type - EPA Calculated" is in Property List Below) {ES Score shoud be 1-100}
    
        Bank branch |
        Barracks |
        Courthouse |
        Data center |
        Distribution center |
        Financial office |
        Hospital (general medical & surgical) |
        Hotel |
        K-12 school |
        Medical office |
        Multifamily housing |
        Non-refrigerated warehouse |
        Office |
        Refrigerated warehouse |
        Residence hall/ dormitory |
        Retail store |
        Senior care community |
        Supermarket/grocery store |
        Wastewater treatment plant |
        Wholesale club/supercenter |
        Worship facility |

### Energy parameters ###
 
  * Metered Areas (Energy)
    * Should Exist, Should  = "Whole Building"
  * Electricity Use-Grid Purchase & Generate
    * Should Exist, Should be > 0, Should not be "Not Available"
  * Natural Gas Use (kBtu)
    * Should Exist, Should be > 0, Should not be "Not Available"
  * Site EUI (kBtu/ft2)
    * Should exist, Should be >0, Should not be "Not Available", Should be >40 & <375
  * Weather Normalized Source EUI (kBtu/ft2)
    * Should exist, should be >0, should not be "Not Available"

# Municipal Benchmark Report Field Definitions
The **Municipal Benchmark Report** is a Portfolio Manager custom report that is used to collect information from your Portfolio Manager buildings. It is uploaded into your Portfolio Manager account, populated with your portfolio data, and downloaded for validation using the Data Quality tool. This downloaded report is a standard Microsoft Excel spreadsheet that has rows for each of your buildings and columns representing the data for those buildings.

The following data fields are contained in the **Municipal Benchmark Report**:

  * **Property Id**  - The Portfolio Mangager property ID
  * **Property Name** - A user friendly name of the property
  * **Year Ending** - The Date of the final data entry
  * **Address 1** - Street address of the property
  * **City** - The city of the property
  * **Country** - The country
  * **State/Province** - The state or provence
  * **Postal Code** - Address postal code
  * **Property GFA** - Gross Floor Area should include all space within the building(s), including classrooms, administrative space, conference rooms, kitchens used by staff, lobbies, cafeterias, auditoriums, stairways, atriums, elevator shafts, and storage areas.
    * **EPA Calculated** (Parking) (ft²) - Area of parking associated with the property
    * **EPA Calculated** (Buildings) (ft²) - Floor area of the building (all floors and uses)
    * **EPA Calculated** (Buildings and Parking) (ft²)  - Combination of building and parking areas
  * **Primary Property Type (EPA Calculated)**- The primary use of the property (reference the Portfolio Manager Glossary)
  * **Electricity Use** - The total of electric power used by the property represented by the specific source of the electricity. Note that in general, a single property will have only a subset of these metered sources.
    * **Grid Purchase and Generated from Onsite Renewable Systems (kWh)** - Total electricity used as measured in kilowatt hours
    * **Grid Purchase and Generated from Onsite Renewable Systems (kBtu)**  - Total electricity used as measured in thousands of British Thermo Units
    * **Grid Purchase (kWh)**  - Total electricity purchased from the electrical grid as measured in kilowatt hours
    * **Grid Purchase (kBtu)**  - Total electricity purchased from the electrical grid as measured in thousands of British Thermo Units
    * **Generated from Onsite Renewable Systems and Used Onsite (kWh)**  - Total electricity generated and used on the property as measured in kilowatt hours
    * **Generated from Onsite Renewable Systems and Used Onsite (kBtu)**  - Total electricity generated and used on the property as measured in thousands of British Thermo Units
  * **Natural Gas Use (kBtu)** - Total natural gas used on the property as measured in thousands of British Thermo Units
  * **Fuel Oil #1 Use (kBtu)** - Total #1 fuel oil used on the property as measured in thousands of British Thermo Units
  * **Fuel Oil #2 Use (kBtu)** - Total #2 fuel oil used on the property as measured in thousands of British Thermo Units
  * **Fuel Oil #4 Use (kBtu)** - Total #4 fuel oil used on the property as measured in thousands of British Thermo Units
  * **Fuel Oil #5 & 6 Use (kBtu)** - Total #5 or #6 fuel oil used on the property as measured in thousands of British Thermo Units
  * **Diesel #2 Use (kBtu)** - Total #2 diesel used on the property as measured in thousands of British Thermo Units
  * **Kerosene Use (kBtu)** - Total kerosene used on the property as measured in thousands of British Thermo Units
  * **Propane Use (kBtu)** - Total propane used on the property as measured in thousands of British Thermo Units
  * **District Steam Use (kBtu)** - Total district steam used on the property as measured in thousands of British Thermo Units
  * **District Hot Water Use (kBtu)** - Total district hot water used on the property as measured in thousands of British Thermo Units
  * **District Chilled Water Use (kBtu)** - Total district chilled water used on the property as measured in thousands of British Thermo Units
  * **Coal - Anthracite Use (kBtu)** - Total anthracite coal used on the property as measured in thousands of British Thermo Units
  * **Coal - Bituminous Use (kBtu)** - Total bituminous coal used on the property as measured in thousands of British Thermo Units
  * **Coke Use (kBtu)** - Total coke used on the property as measured in thousands of British Thermo Units
  * **Wood Use (kBtu)** - Total wood product used on the property as measured in thousands of British Thermo Units
  * **Other Use (kBtu)** - Total any other energy source used on the property as measured in thousands of British Thermo Units
  * **Site EUI (kBtu/ft²)** - The Energy Use Intensity of the site as calculated by on-site energy uses.
  * **Source EUI (kBtu/ft²)** - The Energy Use Intensity of the site as calculated by factoring in losses and characteristics of the off-site energy generation facilities.
  * **Weather Normalized Source EUI (kBtu/ft²)** - Source EUI as normalized by local weather metrics.
  * **Address 2** - If needed, a second address line
  * **Energy Current Date** - The date of the most recent energy consumption metrics

# Visualization 
# Documentation 
=== from the confluence

## Data Quality
* PM Importer: This tool will import an XLS export from PM reports. 
* Data Validation checker:  Apply a subset of the rules that are in the Chicago Data Set.  This will require the generalization of the BEDES importer, Chicago controller and CSV engines.   ... 80% done
* Data Validation visualization: What format is this in.  This could be a templated XLS file or a website that Maalka manages.  
* Documentation Website: Hosted on GitHub.  
* Close set of validation rules
* 12 month check
* Close to chicago
* PM Template
* What the XLS import is. 

* Develop the PM Template and develop the actual tool.  
Data Validation checker.
Documentation Website.
===
### What is this repository for? ###

* Quick summary
* Version
* [Learn Data Quality Tool](https://bitbucket.org/maalka/tutorials/dataqualitytool)

##Feature Notes from Rimas