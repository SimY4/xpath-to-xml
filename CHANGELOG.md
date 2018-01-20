# How xpath-to-xml is versioned

xpath-to-xml uses [semver](http://semver.org/) for its versioning convention.

# Change History

## 1.x Series

### 1.0.1

Bugfixes:

- EqualsExpr now correctly reports an error if unable to satisfy given case
- NotEqualsExpr supports more cases now and also correctly reports an error
- LessThatOrEquals and GreaterThanOrEquals are now supported (with fallback to EqualsExpr resolver) 

### 1.0.0 Initial Release

Features:

- Basic create/remove node segments operations
- Initial XML Namespace Context support 
- Support for DOM, DOM4J, XOM models