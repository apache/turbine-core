In this folder are the various files that make up my changes to support multiple
configuration files loaded up as a "CompositeConfiguration".

With this code, you can specify multiple property files, in multiple formats like 
.property, .xml, and JNDI.

You need to place the various classes and config files in the correct places.

Turbine/TurbineConfig go in the java tree.
The TurbineTest goes in the test-cactus tree.
the rest go in the test-cactus/testapp/web-inf/conf directory.

Lastly, you need CVS Head of commons-configuration to take advantage of the ConfigurationFactory.

Eric Pugh

