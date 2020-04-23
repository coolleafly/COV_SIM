# COVID-19 Simulation Project

Discrete simulation analysis of COVID-19 and prediction of isolation bed numbers

## Prerequisites
-JDK 1.8

## Tested on:
*windows 10 Home, IntelliJ IDEA 2019.3.4.
We recommend the operating environment like these:
Microsoft Windows 10/8/7/Vista/2003/XP (incl.64),
1 GB RAM minimum, 2 GB RAM recommended,
300 MB hard disk space + at least 1 G for caches,
1920×1080 minimum screen resolution,
JDK 1.8.

## Usage

### Step1: Adjust the parameters in "Constants.java" 
This is a file containing some basic parameters, which can be changed to achieve epidemic simulation under different conditions.

### Step2: Run program 
src/Main.java

### Step3: Get result

After closing or minifying the program interface, there is a '.csv' file which named by the corresponding run time of the results in the folder of this program. Each column shows the statistics for different time nodes.
- CITY_PERSON_SIZE shows the population of the city,
- NORMAL shows the scale of healthy people,
- SHADOW shows the scale of population in the incubation period,
- SUPER shows the scale of population in the super incubation period,
- CONFIRMED shows the scale of population have suspected symptoms,
- DIAGNOSIS shows the scale of population have been confirmed to be infected by the hospital,
- FREEZE shows the scale of population been isolated at the hospital,
- CURED shows the scale of population been cured,
- shadow_average shows the average incubation period in the population,
- shadow_std shows the incubation period's standard deviation in the population,
- shadow_max shows the longest incubation period in the population,
- shadow_min shows the shortest incubation period in the population,
- real_countTmp shows the scale of population actually diagnosed and isolated every record time,
- max_count shows the cumulative number of population actually diagnosed and isolated,
- count_shadow_average shows the average incubation period on the day of the maximum 
number of population actually diagnosed and isolated,
- count_shadow_std shows the incubation period's standard deviation on the day of the maximum 
number of population actually diagnosed and isolated,
- count_shadow_max shows the longest incubation period on the day of the maximum 
number of population actually diagnosed and isolated,
- count_shadow_min shows the shortest incubation period on the day of the maximum 
number of population actually diagnosed and isolated,
- sum_confirmed_mark shows the cumulative number of person have suspected symptoms,
- sum_diagnosis_mark shows the cumulative number of person confirmed to be infected ,
- sum_freeze_mark shows the cumulative number of person isolated,
- r0 shows the scale of the original patients,
- G1num shows the scale of the first generation of infectious patients,
- G2num shows the scale of the second generation of infectious patients,
- G3Num shows the scale of the third generation of infectious patients,
- G4Num shows the scale of the fourth generation of infectious patients,
- G5Num shows the scale of the fifth generation of infectious patients,
- BED_CanUse shows the scale of bed can be used,
- BED_Need shows the scale of bed in need,
- DEATH shows the scale of died people,
- worldTime(Day) shows the time in the simulation model  when record the row,
- recordTime shows the real world time when record the row.

## More details are in our article. 
The article is under review currently, we will give more explanation in the source code after being received.

## Authors:

Xinyu Li(#) , Yufeng Cai(#) , Yinghe Ding , Jiada Li , Ye Liang , Linyong Xu 

*Corresponding Author: Ye Liang , Linyong Xu

#Xinyu Li and Yufeng Cai are co-first authors on this work
