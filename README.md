# Projects
* Android
  * MobileSensing
* NodeJS
  * SensingWeb
* Java
  * Collatz
  * SatellitesReactor

# Descriptions
## Sensing projects
MobileSensing and SensingWeb are linked projects combining Android device application together with MEAN Web application.

**MobileSensing** is Android application which uses gps, mqtt, SQL, JSON, wi-fi scanning and maybe something more and creates wi-fi coverage heatmap. A user chooses how often wi-fi signal strengths (and other measurements available to device, currently only light) are measured and how often is data sent to a broker and the application does it until stopped by user. Data is sent in JSON format using MQTT protocol and a broker. Every measurement has its gps location for later heatmap creation. The project was done for IoT university course.

**SensingWeb** is small MEAN application which combines two servers. One is only a resender of data that comes from MQTT broker from measuring device (MobileSensing) to database (MongoDB). The other takes data from database and creates wi-fi heatmaps. It is basically continuation of MobileSensing.

## Java projects

**SatellitesReactor** is a small project done for a competition. It uses standard Java with some Unit testing and some geometrics and Shortest path searching algorithm.

**Collatz** is one page implementation of Collatz conjecture also for a competition or so.
