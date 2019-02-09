# How xpath-to-xml is versioned

xpath-to-xml uses [semver](http://semver.org/) for its versioning convention.

# Change History

## 2.x Series

### Upcomming

Improvements:

- Core
  - Overall optimisations to make library performant for all extensions.
  - Inconsistent use of index predicate (i.e. `node[2][3]`) now will raise an error.

### 2.1.0: JDOM model support

Features:

- Support for JDOM model

Improvements:

- Core
  - Add runtime static method to check whether given model instance is supported.
- scala XML
  - Various performance improvements
  - Added 2.13 for cross compilation
  - Fixed JavaDoc and source code publishing

Bugfixes:

- XOM
  - Fix namespace resolution for attribute nodes.

### 2.0.0: Scala XML model support and javax.json model support

Features:

- Support for scala XML model
- Support for javax.json model

Improvements:

- Core
  - [BREAKING] Effect interface moved into `com.github.simy4.xpath.spi` package for better modularisation story in Java9.
    All existing core extensions adopted this change
  - [BREAKING] automatic module names were changed
    
## 1.x Series

### 1.2.4

Bugfixes:

- Gson, Jackson
  - Fix parent resolution issue that may occur in some cases on prepend copy
- Dom4J
  - Correctly resolve parent for top level element.
- XOM
  - Align can handle with what XOM SPI can actually handle.

### 1.2.3

Improvements:

- XOM
  - Minor performance improvements
- Gson, Jackson
  - Root node now can be overwritten

### 1.2.2

Improvements:

- Fixing JavaDoc publishing

### 1.2.1

Improvements:

- Core
  - Fix performance regression between 1.1.0 and 1.2.0

### 1.2.0 Jackson model support

Features:

- Support for Jackson JSON model

Improvements:

- Core
  - Improved XPath axis support. Newly supported axises are:
    - descendant
    - ancestor
    - ancestor-or-self

### 1.1.2

Bugfixes:

- DOM
  - Fix navigator not always traversing element only nodes.

- Gson
  - Fix an infinite loop while fetching nodes parent
  - Fix a node identity error when traversing an array

### 1.1.1

Bugfixes:

- DOM 
  - Fix attribute removal issue

### 1.1.0 Gson model support

Features:

- Support for Gson JSON model

### 1.0.3

Bugfixes:

- Core
  - Fix longstanding issue when index predicate may create more nodes than is has to

### 1.0.2

Improvements:

- DOM4J
  - Bump DOM4J version to 2.1.0
- XOM
  - Bump XOM version to 1.2.10

### 1.0.1

Bugfixes:

- Core:
  - EqualsExpr now correctly reports an error if unable to satisfy given case
  - NotEqualsExpr supports more cases now and also correctly reports an error
  - LessThatOrEquals and GreaterThanOrEquals are now supported (with fallback to EqualsExpr resolver) 

### 1.0.0 Initial Release

Features:

- Basic create/remove node segments operations
- Initial XML Namespace Context support 
- Support for DOM, DOM4J, XOM models
