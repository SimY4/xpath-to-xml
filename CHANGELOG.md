# How xpath-to-xml is versioned

xpath-to-xml uses [semver](http://semver.org/) for its versioning convention.

# Change History

## 2.x Series

### Upcoming

- Core
  - preceding and preceding-sibling axises support.

### 2.3.1

Bugfixes:

- scala XML
  - Fix scala 3 publication

### 2.3.0 Scala 3

Features:

- Support for Scala 3
- Support for Scala 2.11 discontinued

Improvements:

- scala XML:
  - Bump scala-xml version to 2.0.0

### 2.2.3

Improvements:

- Jackson
  - Bump Jackson version to 2.12.3
- scala XML:
  - Scala 3.0.0-RC3 support
  - Scala 3.0.0-RC3 doc publishing

### 2.2.2

Improvements:

- Jackson
  - Bump Jackson version to 2.12.2
- scala XML:
  - Scala 3.0.0-RC2 support
- XOM
  - Bump XOM version to 1.3.7

### 2.2.1

Improvements:

- scala XML
  - Scala 3.0.0-RC1 support
- Jackson
  - Bump Jackson version to 2.12.1

### 2.2.0: Jakarta and Dotty

Features:

- Support for dotty
- Support for javax.json model superseded by jakarta.json

Improvements:

- Jackson
  - Bump Jackson version to 2.11.3
  
### 2.1.9

Bugfixes:

- Core
  - descendant and descendant-or-self axises now can resolve.
  - fix an issue when following and following-sibling may have wrong position during resolve.
  
### 2.1.7 - 2.1.8

Improvements:

- Jackson
  - Bump Jackson version to 2.11.0
- XOM
  - Bump XOM version to 1.3.5
  
### 2.1.6

Improvements:

- Jackson
  - Bump Jackson version to 2.10.3
  - Push minimal java version to Java 8
- DOM4J
  - Bump DOM4J version to 2.1.3
  - Push minimal java version to Java 8
- scala XML
  - Bump scala-xml version to 1.3.0
- XOM
  - Bump XOM version to 1.3.4
    
### 2.1.5

Improvements:

- Core
  - Performance optimisations
  - Support for following and following-sibling axises
- scala XML
  - Aternative, more idiomatic scala API
  
### 2.1.4

Improvements:

- Core
  - Threadsafe service loader adaptor
  
### 2.1.3

Improvements:

- Core
  - Descendant-or-Self axis is now stack-safe
  - util.FilteringIterator, util.TransformingIterator and util.TransformingAndFlatteningIterator were completely removed and their usages were replaced with private specialized iterators
- javax.json
  - Bump javax.json library version to 1.1.5
  - Use java 8 std-library features
- DOM4J
  - Push DOM4J version down to 2.0.2
- scala XML
  - Bump scala-xml version to 1.2.0
- XOM
  - Bump XOM version to 1.3.2

### 2.1.2: Serialization support

Improvements:

- Core
  - Overall optimisations to make library performant for all extensions.
  - Inconsistent use of index predicate (i.e. `node[2][3]`) now will raise an error.
  - XmlBuilder instances are now serializable
  - Expressions and views are serializable as long as particular XML model underneath is serializable

Bugfixes:

- Core
  - Partially resolvable axis nodes are now correctly created.
  
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

Improvements:

- Core
  - Fix performance regression between 1.1.0 and 1.2.0
  - Fixing JavaDoc publishing
- Gson, Jackson
  - Root node now can be overwritten
- XOM
  - Minor performance improvements

Bugfixes:

- Gson, Jackson
  - Fix parent resolution issue that may occur in some cases on prepend copy
- Dom4J
  - Correctly resolve parent for top level element.
- XOM
  - Align can handle with what XOM SPI can actually handle.
  
### 1.2.0 Jackson model support

Features:

- Support for Jackson JSON model

Improvements:

- Core
  - Improved XPath axis support. Newly supported axises are:
    - descendant
    - ancestor
    - ancestor-or-self

Bugfixes:

- Gson
  - Fix an infinite loop while fetching nodes parent
  - Fix a node identity error when traversing an array
- DOM 
  - Fix attribute removal issue
  - Fix navigator not always traversing element only nodes.

### 1.1.0 Gson model support

Features:

- Support for Gson JSON model

Improvements:

- DOM4J
  - Bump DOM4J version to 2.1.0
- XOM
  - Bump XOM version to 1.2.10
  
Bugfixes:

- Core:
  - EqualsExpr now correctly reports an error if unable to satisfy given case
  - NotEqualsExpr supports more cases now and also correctly reports an error
  - LessThatOrEquals and GreaterThanOrEquals are now supported (with fallback to EqualsExpr resolver)
  - Fix longstanding issue when index predicate may create more nodes than is has to 

### 1.0.0 Initial Release

Features:

- Basic create/remove node segments operations
- Initial XML Namespace Context support 
- Support for DOM, DOM4J, XOM models
