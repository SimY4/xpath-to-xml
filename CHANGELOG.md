# How xpath-to-xml is versioned

xpath-to-xml uses [semver](http://semver.org/) for its versioning convention.

# Change History

## 2.x Series

### Upcoming
- ✨ [core] preceding and preceding-sibling axises support.

### 2.3.7
- 🛠 all modules are now fully JPMS modularized.
- 🧰 [Gson] Bump Gson version to 2.10.1
- 🧰 [Jackson] Bump Jackson version to 2.14.2

### 2.3.6
- 🧰 [Gson] Bump Gson version to 2.10
- 🧰 [jakarta.json] Bump jakarta.json version to 2.1.1
- 🧰 [Jackson] Bump Jackson version to 2.14.0
- 🧰 [XOM] Bump XOM version to 1.3.8

### 2.3.5
- 🧰 [Gson] Bump Gson version to 2.9.1
- 🧰 [jakarta.json] Bump jakarta.json version to 2.1.0
- 🧰 [Jackson] Bump Jackson version to 2.13.3
- 🧰 [scala XML] Bump scala-xml version to 2.1.0

### 2.3.4
- 🧰 [Gson] Bump Gson version to 2.9.0
- 🧰 [Gson] Push minimal java version to Java 7
- 🧰 [Jackson] Bump Jackson version to 2.13.1
- 🧰 [JDOM] Bump JDOM2 version to 2.0.6.1

### 2.3.3
- 🧰 [Gson] Bump Gson version to 2.8.8
- 🧰 [scala XML] Bump scala-xml version to 2.0.1
- 🧰 [scala XML] scala 3.1
- 🧰 [Jackson] Bump Jackson version to 2.13.0

### 2.3.2
- 🧰 [Gson] Bump Gson version to 2.8.7
- 🧰 [jakarta.json] Bump jakarta.json version to 2.0.1
- 🧰 [Jackson] Bump Jackson version to 2.12.4

### 2.3.1
- 🛠 [scala XML] Fix scala 3 publication

### 2.3.0 Scala 3
- ✨ [scala XML] Support for Scala 3. 
  Support for Scala 2.11 discontinued
- 🧰 [scala XML] Bump scala-xml version to 2.0.0

### 2.2.3
- 🧰 [Jackson] Bump Jackson version to 2.12.3
- 🧰 [scala XML] Scala 3.0.0-RC3 support
- 🧰 [scala XML] Scala 3.0.0-RC3 doc publishing

### 2.2.2
- 🧰 [Jackson] Bump Jackson version to 2.12.2
- 🧰 [scala XML] Scala 3.0.0-RC2 support
- 🧰 [XOM] Bump XOM version to 1.3.7

### 2.2.1
- 🧰 [scala XML] Scala 3.0.0-RC1 support
- 🧰 [Jackson] Bump Jackson version to 2.12.1

### 2.2.0: Jakarta and Dotty
- ✨ [scala XML] Support for dotty
- ✨ Support for javax.json model superseded by jakarta.json
- 🧰 [Jackson] Bump Jackson version to 2.11.3
  
### 2.1.9
- 🛠 [Core] descendant and descendant-or-self axises now can resolve.
- 🛠 [Core] fix an issue when following and following-sibling may have wrong position during resolve.
  
### 2.1.7 - 2.1.8
- 🧰 [Jackson] Bump Jackson version to 2.11.0
- 🧰 [XOM] Bump XOM version to 1.3.5
  
### 2.1.6
- 🧰 [Jackson] Bump Jackson version to 2.10.3
- 🧰 [Jackson] Push minimal java version to Java 8
- 🧰 [DOM4J] Bump DOM4J version to 2.1.3
- 🧰 [DOM4J] Push minimal java version to Java 8
- 🧰 [scala XML] Bump scala-xml version to 1.3.0
- 🧰 [XOM] Bump XOM version to 1.3.4
    
### 2.1.5
- 🧰 [Core] Performance optimisations
- 🧰 [Core] Support for following and following-sibling axises
- 🧰 [scala XML] Aternative, more idiomatic scala API
  
### 2.1.4
- 🧰 [Core] Threadsafe service loader adaptor
  
### 2.1.3
- 🧰 [Core] Descendant-or-Self axis is now stack-safe
- 🧰 [Core] util.FilteringIterator, util.TransformingIterator and util.TransformingAndFlatteningIterator were completely removed and their usages were replaced with private specialized iterators
- 🧰 [javax.json] Bump javax.json library version to 1.1.5
- 🧰 [javax.json] Use java 8 std-library features
- 🧰 [DOM4J] Push DOM4J version down to 2.0.2
- 🧰 [scala XML] Bump scala-xml version to 1.2.0
- 🧰 [XOM] Bump XOM version to 1.3.2

### 2.1.2: Serialization support
- 🧰 [Core] Overall optimisations to make library performant for all extensions.
- 🧰 [Core] Inconsistent use of index predicate (i.e. `node[2][3]`) now will raise an error.
- 🧰 [Core] XmlBuilder instances are now serializable
- 🧰 [Core] Expressions and views are serializable as long as particular XML model underneath is serializable
- 🛠 [Core] Partially resolvable axis nodes are now correctly created.
  
### 2.1.0: JDOM model support
- ✨ Support for JDOM model
- 🧰 [Core] Add runtime static method to check whether given model instance is supported.
- 🧰 [scala XML] Various performance improvements
- 🧰 [scala XML] Added 2.13 for cross compilation
- 🧰 [scala XML] Fixed JavaDoc and source code publishing
- 🛠 [XOM] Fix namespace resolution for attribute nodes.

### 2.0.0: Scala XML model support and javax.json model support
- ✨ Support for scala XML model
- ✨ Support for javax.json model
- 🧰 [Core][BREAKING] Effect interface moved into `com.github.simy4.xpath.spi` package for better modularisation story in Java9. 
  All existing core extensions adopted this change
- 🧰 [Core][BREAKING] automatic module names were changed
    
## 1.x Series
### 1.2.4
- 🧰 [Core] Fix performance regression between 1.1.0 and 1.2.0
- 🧰 [Core] Fixing JavaDoc publishing
- 🧰 [Gson, Jackson] Root node now can be overwritten
- 🧰 [XOM] Minor performance improvements
- 🛠 [Gson, Jackson] Fix parent resolution issue that may occur in some cases on prepend copy
- 🛠 [Dom4J] Correctly resolve parent for top level element.
- 🛠 [XOM] Align can handle with what XOM SPI can actually handle.
  
### 1.2.0 Jackson model support
- ✨ Support for Jackson JSON model
- 🧰 [Core] Improved XPath axis support. Newly supported axises are:
  - descendant
  - ancestor
  - ancestor-or-self
- 🛠 [Gson] Fix an infinite loop while fetching nodes parent
- 🛠 [Gson] Fix a node identity error when traversing an array
- 🛠 [DOM] Fix attribute removal issue
- 🛠 [DOM] Fix navigator not always traversing element only nodes.

### 1.1.0 Gson model support
- ✨ Support for Gson JSON model
- 🧰 [DOM4J] Bump DOM4J version to 2.1.0
- 🧰 [XOM] Bump XOM version to 1.2.10
- 🛠 [Core] EqualsExpr now correctly reports an error if unable to satisfy given case
- 🛠 [Core] NotEqualsExpr supports more cases now and also correctly reports an error
- 🛠 [Core] LessThatOrEquals and GreaterThanOrEquals are now supported (with fallback to EqualsExpr resolver)
- 🛠 [Core] Fix longstanding issue when index predicate may create more nodes than is has to 

### 1.0.0 Initial Release
- ✨ Basic create/remove node segments operations
- ✨ Initial XML Namespace Context support 
- ✨ Support for DOM, DOM4J, XOM models
