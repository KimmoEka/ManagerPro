# Ohjelmistotuotantoprojekti 1

## Ryhmä 7

This is a repository for the 'ManagerPro' restaurant reservation and order management software. It is created as a part of the 'Ohjelmistotuotanto 1' course.

The purpose of the software is to make the management of the reservations and orders and the menu in a restaurant easier and make the communication between the staff faster.

## What you need

* Maven 3
* MariaDB database (SQL script in the repository root)
* JDK 11 

After installing Maven and JDK 11 and configuring database you need to edit the hibernate.xml -files locatd in src/main/resources and src/test/resources folders. Fill in your database details. The testing and deployment databases that our group uses can only be accessed from Metropolia VPN.

When preparations are done, just run 

```
mvn clean javafx:run
```

to start the program. 


## How to package the program


Packaging the program can be done by running 
```
mvn package
```
This uses the maven shade plugin to create a fat jar, which has all the dependencies baked in. It can be run on any windows platform by double-clicking or executing
```
java -jar OTP-1.0-SNAPSHOT.jar
```
from the command line. Linux and Mac builds can also be made by editing the POM. 

## NOTE THAT:

 * Hibernation default connection pool is still used, which is not suitable for production.
 * Probably something else too
