# Natural Resource Ministries' Configuration Management Database

## What the CMDB is

The aim of the CMDB is to be a centralized location to store and reconcile three states that describe the ISSS 
environment:

1. The supposed state

   That is, what state we believe the environment to be. This will usually be comprised of data that was asserted at one
   point by users or services, but can now be considered stale. This data may or may not reflect reality.

1. The actual state

   The actual state describes what is actually happening. These details will come from a trusted source (e.g. a scripted
   service) that has *recently* discovered the information.

1. The desired state
   
   Configuration elements that describe what we *want* the environment to look like. This most frequently applies to 
   upcoming releases, middle-tier upgrades, patches, etc.
   
All three of these modes ought to be accounted for within this system. In addition to being a *dumb* store of data, 
there can also be some limited form of intelligence in the form of business rules being applied to this data to create 
information. 


## What the CMDB is not

The CMDB should not be too clever. Data should be fed to the system, not discovered by it. While those are crucial tasks
that are very intimately related to the concerns of the CMDB, they are best performed by external systems. Things change 
in the environment and a Java program is not as flexible as modifying a script. There are also much better tools for
managing a large number of servers (e.g. Ansible).
 