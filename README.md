# README #

TEMPORARY DOCUEMENTATION

# Data Import Template (from PM Export File)
  -  the following .xls export is a custom set of parameters created in Portfolio Manager using annual energy data. 
  * Time Frame: **Current Energy Year** 
  * Portfolio Manager fields
    * 
<dl>
<dt>Property Id</dt><dd>xxx </dd>
<dt>Property Name<dt><dd></dd>
<dt>Year Ending<dt><dd></dd>
<dt>Address 1<dt><dd></dd>
<dt>City<dt><dd></dd>
<dt>Country<dt><dd></dd>
<dt>State/Province<dt><dd></dd>
<dt>Postal Code<dt><dd></dd>
<dt>Property GFA - EPA Calculated (Parking) (ft²)<dt><dd></dd>
<dt>Property GFA - EPA Calculated (Buildings) (ft²)<dt><dd></dd>
<dt>Property GFA - EPA Calculated (Buildings and Parking) (ft²) <dt><dd></dd>
<dt>Primary Property Type - EPA Calculated<dt><dd></dd>
<dt>Electricity Use - Grid Purchase and Generated from Onsite Renewable Systems (kWh)<dt><dd></dd>
<dt>Electricity Use - Grid Purchase and Generated from Onsite Renewable Systems (kBtu) <dt><dd></dd>
<dt>Electricity Use - Grid Purchase (kWh) <dt><dd></dd>
<dt>Electricity Use - Grid Purchase (kBtu) <dt><dd></dd>
<dt>Electricity Use - Generated from Onsite Renewable Systems and Used Onsite (kWh) <dt><dd></dd>
<dt>Electricity Use - Generated from Onsite Renewable Systems and Used Onsite (kBtu) <dt><dd></dd>
<dt>Natural Gas Use (kBtu)<dt><dd></dd>
<dt>Fuel Oil #1 Use (kBtu)<dt><dd></dd>
<dt>Fuel Oil #2 Use (kBtu)<dt><dd></dd>
<dt>Fuel Oil #4 Use (kBtu)<dt><dd></dd>
<dt>Fuel Oil #5 & 6 Use (kBtu)<dt><dd></dd>
<dt>Diesel #2 Use (kBtu)<dt><dd></dd>
<dt>Kerosene Use (kBtu)<dt><dd></dd>
<dt>Propane Use (kBtu)<dt><dd></dd>
<dt>District Steam Use (kBtu)<dt><dd></dd>
<dt>District Hot Water Use (kBtu)<dt><dd></dd>
<dt>District Chilled Water Use (kBtu)<dt><dd></dd>
<dt>Coal - Anthracite Use (kBtu)<dt><dd></dd>
<dt>Coal - Bituminous Use (kBtu)<dt><dd></dd>
<dt>Coke Use (kBtu)<dt><dd></dd>
<dt>Wood Use (kBtu)<dt><dd></dd>
<dt>Other Use (kBtu)<dt><dd></dd>
<dt>Site EUI (kBtu/ft²)<dt><dd></dd>
<dt>Source EUI (kBtu/ft²)<dt><dd></dd>
<dt>Weather Normalized Source EUI (kBtu/ft²)<dt><dd></dd>
<dt>Address 2<dt><dd></dd>
<dt>Energy Current Date<dt><dd></dd>
</dl>

To share this template with other portfolios in portfolio manager, please send them the following link:

 https://portfoliomanager.energystar.gov/pm/reports/template/1732437/share/cddf455b-df6a-491f-a63f-ec9f4110b28b

Careful! Clicking on the link will create a copy of the template into your own account.

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