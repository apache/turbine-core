$Id$

The files in this directory are not currently being used in Turbine. The 
reason is that they are not currently needed. However, they may become
necessary someday if someone decides to implement the ability to configure
the Logging Service with a .xml file instead of a .properties file. The 
way that you would do this is to implement the interface...

org.apache.turbine.services.logging.LoggingConfig

...to read populate itself using a XML file. The format of the XML file
should be defined with the DTD that is in this directory.

thanks,

-The Turbine Team
