# rhies_PosttoOpenHIM_Module
This repository contains an OpenMRS module allowing to post Patients and Encounters to OpenHIM developed for the RHIES project. To be specific, it is an OpenMRS 1.9.11 module used to post OpenMRS Patients and context informations to a specifific OpenHIM mediator.

## RHIES Project
The purpose of Rwandan Health Information Exchange System (RHIES) project is to develop a system that allows for information- exchange within electronic medical record systems and to develop linkage solutions for generating EMR data directly to HMIS in the specific use case of HIV Case based surveillance (CBS). RHIES is a set of applications that work together in the Open Health Information Exchange (OpenHIE) architecture to serve point-of-service systems, like EMRs, DHIS2, National ID database and laboratory information system.

## Dependencies
This module depends on:
- The OpenMRS Event module
- The OpenMRS Web Rest service

## Building from Source
You will need to have Java 1.6+ and Maven 2.x+ installed.  Use the command 'mvn package' to 
compile and package the module.  The .omod file will be in the omod/target folder.

Alternatively you can add the snippet provided in the [Creating Modules](https://wiki.openmrs.org/x/cAEr) page to your 
omod/pom.xml and use the mvn command:

    mvn package -P deploy-web -D deploy.path="../../openmrs-1.8.x/webapp/src/main/webapp"

It will allow you to deploy any changes to your web 
resources such as jsp or js files without re-installing the module. The deploy path says 
where OpenMRS is deployed.

## Installation
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

If uploads are not allowed from the web (changable via a runtime property), you can drop the omod
into the ~/.OpenMRS/modules folder (Where ~/.OpenMRS is assumed to be the Application 
Data Directory that the running OpenMRS is currently using).  After putting the file in there 
simply restart OpenMRS/tomcat and the module will be loaded and started.

## Documentation
[Wiki](https://github.com/savicsorg/rhies_PosttoOpenHIM_Module/wiki)

## License
Mozilla Public License 2.0

## Created and Developed By
[Savics SRL](https://savics.org)

## In Collaboration with
[Rwanda Biomedical Centre (RBC)](https://www.rbc.gov.rw/)

## Main Contributors
* Developers: Gilbert AGBODAMKOU

## Libraries We Use
The following sets forth attribution notices for third party software that may be contained in portions of this repository. We thank the open source community for all of their contributions.

* OpenMRS module (License: OpenMRS Public License 1.0)
