The SystemProperties service aims to provide a convenient way of getting 
properties defined in TurbineResources.properties into System.properties.

This is a very trivial service - I am completely open to alternative ways of
achieving the same end result.

Note that VelocityEmail grabs mail.server from TR.props and passes it through 
to commons-email.  It is a bit of a pain to have to remember to do this every 
time when commons-email is used directly and when a system property is provided
for this very purpose.

I propose that VelocityEmail and related classes be updated to no longer set
the mail host so that there is a single consistent means of setting it.  An
alternative to this proposal would be a very specific EmailInit service that
loads the TR.props mail.server value into System.properties mail.host).

Configuration:

# -------------------------------------------------------------------
#
#  S E R V I C E S
#
# -------------------------------------------------------------------
...
services.SystemPropertiesService.classname = org.apache.turbine.services.systemproperties.TurbineSystemPropertiesService
services.SystemPropertiesService.earlyInit = true
...

# -------------------------------------------------------------------
#
#  S Y S T E M   P R O P E R T I E S   S E R V I C E
#
# -------------------------------------------------------------------
# Properties defined as:
#     services.SystemPropertiesService.name = value
# will be added to System.properties as
#     name=value
# Suggested use is to configure mail.host for commons-email and JavaMail thus:
#     services.SystemPropertiesService.mail.host = localhost
# -------------------------------------------------------------------

services.SystemPropertiesService.mail.host = localhost
