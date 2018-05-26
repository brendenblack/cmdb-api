# CMDB Data Glossary

In the CMDB, what is stored is simply data. This data can appear to be, and can readily be used as information

**Data**: 

**Information**:

These data points can easily be used to represent or generate information, but due to the fluid nature of a complicated
system there is a shelf life to all data.

## States of data

There are three states that data may be in at any given time:

1. The supposed state

   That is, what state we believe the environment to be. This will usually be comprised of data that was asserted at one
   point by users or services, but can now be considered stale. This data may or may not reflect reality.

1. The actual state

   The actual state describes what is actually happening. These details will come from a trusted source (e.g. a scripted
   service) that has *recently* discovered the information.

1. The desired state
   
   Configuration elements that describe what we *want* the environment to look like. This most frequently applies to 
   upcoming releases, middle-tier upgrades, patches, etc.
   
## Classifications of data

There are two elemental classifications of data: *events* and *assertions*.

* Events
  
  A record of something that happened at a particular point in time. For instance, a Jenkins build record.

* Assertions
  
  A statement of how something is, not necessarily time-bound. For instance, what port is in use on a server.