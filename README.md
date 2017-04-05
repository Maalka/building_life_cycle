# README #

TEMPORARY DOCUEMENTATION

# Data Import Template (from PM Export File)
  -  the following .xls export is a custom set of parameters created in Portfolio Manager using annual energy data. 
  * Time Frame: **Current Energy Year** 

# Portfolio Manager fields
Data is imported from an Excel spreadsheet that is populated from within Portfolio Manager. Please references:

  * [PM GLossary](https://portfoliomanager.energystar.gov/pm/glossary)
  * [Property Type Definitions(PDF)](https://www.energystar.gov/sites/default/files/tools/Property%20Use%20Details%20and%20Definition%20Jan%202017.pdf)
  
## Template Field Definitions

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

## Sample Template

## Sharing the Template
You may obtain a copy of the template that will allow you to export and validate your Portfolio Manager data. This is accomplished by first signing into the Energy Star Portfolio Manager site, and then clicking on the following link. Careful! Clicking on the link will create a copy of the template within your own account.


 [Copy Validation Template to your Portfolio Manager Account](https://portfoliomanager.energystar.gov/pm/reports/template/1732437/share/cddf455b-df6a-491f-a63f-ec9f4110b28b)

**Careful! Clicking on the link will create a copy of the template into your own account.**



  - having the .xls as the import via 
  - a (python) command line tool 
  - output is a csv 

# Data Validation

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
* [Learn Data Quality Tool](https://bitbucket.org/tutorials/dataqualitytool)

##Feature Notes from Rimas
* import your PM export
* run some tests re: national averages
* present some reports about the quality of your "code"

=== From the normal readme.md template ===

### How do I get set up? ###



* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact


## Dependencies

This application based on Play! Framework. Additional dependencies are 

## Run

from *sbt* command line run:

*sbt run*

Now the application can be invoked either from a browser or from the command line (curl)

## Call from browser

Point your browser to http://localhost:9000/