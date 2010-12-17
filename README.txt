How to build, install and configure the registry
================================================

To build the registry, copy the file sample-build.properties as build.properties and edit it to
adjust some parameters according to your environment.

In my case, I had to change the following properties:

- war.webserver.home
- tomcatLibs
- output.jarname
- war.output.name
- war.webserver.deploypath

You may also have to edit the file: jsp/install/ariadne.properties.  The version in the
svn repository doesn't work.  A custom version has been provided by Jose Luis Santo d'Ariadne.

Once this is done, you can issue this command via the command interpreter:

ant -f build-registry.xml allfresh_testing

This will compile, build, and deploy the war file that implements the registry into Tomcat.


To configure the registry:

1) Login into the application.  By default, the administrator's login and password are blank.
2) In the Management tab, select Configuration Wizard.
3) Click Next.
4) Select File System, then click Next.
5) All the default locations for the metadata and logs are ok, so click Next.
6) The Handler System and Search Lucene Analyzer should both be Collection Handler.  Click Next.
7) Enter an administrator's login and password.  I left them unchanged: user and pass.  Click Next.
8) The message Installation Successful should appear.

Then, close the browser, and restart Tomcat.

1) Login again as the administrator.
2) In the Status tab, select Recreate Index, and click the Rebuild button.
3) The message "The Lucene index has been rebuild successfully" should be shown.
4) Logout.
5) Restart Tomcat.

At this point, the registry is properly configured and should work.


To add a repository:

1) In the New tab, select Add New repository.
2) Enter the repository's name, description, and contact information, then click Next.
3) Enter SPI information if any, then click Next.
4) Enter OAI-PHM information if any, then click Next.  For example, you could enter such a URL: 

http://helios.licef.ca:8080/LR_Lornet-OAI/OAIHandler

5) Enter SQI information if any, then click Next.
6) The message "Target published successfully!" should appear.


To browse the repositories:

1) In the Search tab, select Search registry.
2) Click Show all the content to get the list of all the repositories.
3) Click Show all the metadata or Edit Metadata to browse or edit, respectively, the metadata of the desired repository.
4) To edit the metadata, you will have to login as administrator and provide a new version of the metadata as a XML record.
Be sure to provide valid metadata otherwise your registry may become inoperational the next time you will restart Tomcat!
It happened only once but you never know...  After edition, verify that the changeѕ have been applied.  If the XML is invalid, 
changes can be ignored silently.


To upgrade the code
===================
1) svn update (to get the latest bug fixes and features)
2) Make a backup of the web application (better safe than sorry)
3) Make sure that the build.properties and ariadne.properties files are ok
4) Rebuild everything and deploy: ant -f build-registry.xml allfresh_testing
5) Stop Tomcat
6) Remove the previous registry webapp from Tomcat.
7) Uncompress the registry.war file in $TOMCAT_HOME/webapps and overwrite it with the previous installation's data
    * registry/install/ariadne.properties
    * registry/installation/index
    * registry/installation/logs
    * registry/installation/store
8) Restart Tomcat and check if everything is ok.


Additional interesting informations
===================================
The registry has at least 3 query interfaces: SQI, OAI-PMH, and REST-SQI.

For SQI, the endpoints are:

http://host:8080/registry/services/SqiSessionManagement
http://host:8080/registry/services/SqiTarget

For OAI-PMH, the endpoint is:

http://host:8080/registry/services/oai

For example:

http://host:8080/registry/services/oai?verb=ListMetadataFormats

should give oai_lom (schema: http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd, namespace: http://ltsc.ieee.org/xsd/LOM)

and:

http://host:8080/registry/services/oai?verb=ListRecords&metadataPrefix=oai_lom

should return all the metadata records of the repositories contained in the registry.

For REST-SQI, the endpoint is:

http://hera.licef.ca:8080/registry/api/sqitarget

and queries like this one can be used:

http://hera.licef.ca:8080/registry/api/sqitarget?query=LR_IDLD&start=1&size=2&lang=plql1&format=lom


