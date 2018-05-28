# CMDB Data Glossary
 
In the CMDB, what is stored is simply data. The CMDB should be considered a dumb store of data, with some additional
capability to process business rules to turn that data in to information.

**Data**: Data is raw, unorganized facts that need to be processed.
	
**Information**: When data is processed, organized, structured or presented in a given context so as to make it useful,
it is called information. This information typically has a "best before" date.

- [CMDB Data Glossary](#cmdb-data-glossary)
  - [States of data](#states-of-data)
  - [Classifications of data](#classifications-of-data)
  - [Key components](#key-components)
    - [Project](#project)
    - [Component](#component)
    - [Jenkins build](#jenkins-build)
    - [Jenkins promotion](#jenkins-promotion)

----------------------------------------

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
   
----------------------------------------

## Classifications of data

There are two elemental classifications of data: *events* and *assertions*.

* Events
  
  A record of something that happened at a particular point in time. For instance, a Jenkins build record.

* Assertions
  
  A statement of how something is, not necessarily time-bound. For instance, what port is in use on a server.
  
----------------------------------------
  
## Key components
### Project
A project is an entity that has a budget, a BPM and components. It must be registered with the organization (i.e. in IRS)

### Component
A component is, specifically, those pieces that make up a project. The term will frequently be used to describe a state 
of the component, such as a manifested service, a repository, the code in the repository, etc... Properly, the
component is the abstract, logical idea that is represented by these more tangible things.

### Jenkins build
A Jenkins build is, simply, a representation of a build event that happened in Jenkins. This is duplicating information
that is somewhat readily available inside Jenkins itself, but has the added benefit of being stored in a much more
accessible format. Jenkins uses the filesystem to store information, so pulling large amounts of data is slow. 

### Jenkins promotion
A promotion record details the promotion event. This *does not* include details about what changes were made by that 
promotion. 