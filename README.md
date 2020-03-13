# suumo-search
Perform advanced searches on Suumo.jp

## Configuration

### search-conditions.cfg

| Property             | Possible values                                | Description                                                                           |
| -------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------- |
| area                 | see xyz.kebigon.housesearch.domain.Area        | Location of the researched property                                                   |
| type                 | see xyz.kebigon.housesearch.domain.AnounceType | Type of the researched property                                                       |
| price.min            | long                                           | Minimum price of the property (in yens) : all postings cheaper will be ignored        |
| price.max            | long                                           | Maximum price of the property (in yens) : all postings more expensive will be ignored |
| age.min              | int                                            | Minimum age of the property (in years) : all postings newer will be ignored           |
| age.max              | int                                            | Maximum age of the property (in yens) : all postings older will be ignored            |
| surface.land.min     | int                                            | Minimum surface of the property (in square meters)                                    |
| surface.land.max     | int                                            | Maximum surface of the property (in square meters)                                    |
| surface.house.min    | int                                            | Minimum surface of the house on the property (in square meters)                       |
| surface.house.max    | int                                            | Maximum surface of the house on the property (in square meters)                       |
| station.walktime.max | int                                            | Maximum time to go to the nearest station by foot (in minutes)                        |
| expression.file      | String                                         | Name of a file containing a Spring Expression to filter the postings (see below)      |

### Spring expression file (defined by property expression.file of search-conditions.cfg)

You can write an Spring expression to evaluate each postings that already successfully past the previous checks. To do so, you have access to the below functions 

| Function                                      | Description                                                                                                                                                                                                     |
| --------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| #timeToStation(#property, 'station name')     | Returns the time in minutes to travel from the property to the mentioned station (based on the walk time to the nearest station from the posting, and the time between this station and the mentioned station). |
| #fareToStation(#property, 'station name')     | Returns the price in yens to travel from the nearest station to the mentioned station.                                                                                                                          |
| #transferToStation(#property, 'station name') | Returns the number of transfers necessary to travel from the nearest station to the mentioned station.                                                                                                          |

NB: the station on the second argument must be written as found on Yahoo Transit.

### log4j2.xml

This file is used to configure how the application is managing its logs, see the log4j2 documentation