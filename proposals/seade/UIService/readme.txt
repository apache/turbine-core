This service and associated pull tool provide an enhanced replacement for the
existing UIManager pull tool.  Major enhancements include:
* Skin properties are shared between all users with lazy loading.
* Non-default skin files inherit properties from the default skin
* Access to skin properties from screen and action classes is now provided for
* Access is provided to the list of available skins

This service is dependant on PullService only in that it makes use of 
TurbinePull.getResourcesDirectory() during initialization.  It may be a good
idea to duplicate small amount of underlying code in order to eliminate this
dependency.

Configuration:

# -------------------------------------------------------------------
#
#  S E R V I C E S
#
# -------------------------------------------------------------------
...
services.UIService.classname = org.apache.turbine.services.ui.TurbineUIService
#services.UIService.earlyInit = true
...

# -------------------------------------------------------------------
#
#  P U L L  S E R V I C E
#
# -------------------------------------------------------------------
...
tool.session.ui = org.apache.turbine.services.ui.UITool
tool.ui.skin = default
...