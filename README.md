# Stock Tracker

A web application to track your personal investments with minimum maintenance.

Calculate your overall performance across all your investments.

# Prerequisite
- Java 8
- Tomcat 8.5.16
- MySQL

# Usage

Run script to create the database.

Rename `app.properties.template` to `app.properties` and update that file with correct value.

`./gradlew clean build`

Then deploy the artifact `build/libs/stock-tracker.war` to your local tomcat and go to `http://localhost:8080/stock-tracker`
