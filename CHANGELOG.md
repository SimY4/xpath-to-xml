# How xpath-to-xml is versioned

xpath-to-xml uses [semver](http://semver.org/) for its versioning convention.

# Change History

## 1.x Series

### Upcoming

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