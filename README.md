# Covid Status

I created this SpringBoot project to display the COVID-19 updates in a way I think is more clear.
All the "number" are retrieved from the [Ministero della Salute](https://www.salute.gov.it/), they have made available all the data in a public [GitHub Repository](https://github.com/pcm-dpc/COVID-19).

[Live example](https://marco.selfip.net/Covid19Italy/)

## Requirements:
* JDK 11
* Maven 3.x.x
* Database (PostgreSQL)

## Info
* I am providing a **docker file** that you can use to run and initialise the database. You will find it insider the folde: Scripts/Docker/PostgreSQL/*

## Screenshots
![National Data](Misc/Pictures/1.png)
![Regions Data](Misc/Pictures/2.png)
![Provinces Data](Misc/Pictures/3.png)
![Regions Colors](Misc/Pictures/4.png)

## History
* **12/12/2020 v0.0.3**: Added the dark mode
* **12/12/2020 v0.0.2**: Added the map with the color of the regions
* **15/11/2020 v0.0.1**: First release

## TODO:
* Refactoring the code (When I have some extra time)
* Anything else that cames up in my mind
