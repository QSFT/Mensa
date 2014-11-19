# Overview


**Mensa** is a generic, flexible, enhanced, and efficient Java implementation of a pattern matching state machine as described by the 1975 paper by Alfred V. Aho and Margaret J. Corasick: *Efficient string matching: An aid to bibliographic search*[^1] This implementation is

* *generic* in that it can be used to match any type of symbols as defined by the Java template type S — e.g., it is possible to create a machine to match bytes, characters, integers, gene sequences, bit sequences, etc.;
* *flexible* in that the architecture allows for granular extension, customization, or replacement of framework components;
* *enhanced* in that it supports a number of useful extension not addressed in the original paper, such as whole-word matching, case-sensitivity controls, fuzzy whitespace matching, fuzzy punctuation matching, incremental matching (i.e., iterators), matching event listeners, etc.; and
* *efficient* in that it performs well in terms of both time and resource usages on very large (~million term) keyword sets.

# Resources

The following are the primary Mensa resources:

- **Source code**: Source code can be downloaded from this Github project:

		https://github.com/dell-oss/Mensa
       
- **Documentation**: There are two primary sources of documentation: the [Mensa Wiki](https://github.com/dell-oss/Mensa/wiki) and the API Java docs.  The wiki contains details on building **Mensa**, tutorials, examples, contributor information, and more. The API java docs are generated as part of the [build process](https://github.com/dell-oss/Mensa/wiki/Building-Mensa) and contain detailed API documentation.
    
- **Issues**: Please feel free to post bug reports and feature enhancements in the Github Issues area:
  
		https://github.com/dell-oss/Mensa/issues
    
- **Downloads**: Binaries may be downloaded from the [Releases](https://github.com/dell-oss/Mensa/releases) page. Source, binary, and doc download bundles will be available soon from Maven Central. Stay tuned!


# Requirements

**Mensa** requires [Java](https://www.java.com/) 1.7 or higher. [Maven](http://maven.apache.org/) 3.2.3 or higher is needed to run automated build scripts.

# License
**Mensa** is available under the Apache License Version 2.0. See LICENSE.txt or
[http://www.apache.org/licenses/](http://www.apache.org/licenses/) for a copy of this license.

# Contributors

For a list of contributors, as well as information on how you can contribute, please see the [Contributors](https://github.com/dell-oss/Mensa/wiki/Contributors) page in the [Mensa Wiki](https://github.com/dell-oss/Mensa/wiki).

**Mensa** was created by [F. Andy Seidl](http://www.linkedin.com/in/faseidl/) and released as open source by [Dell Software](http://software.dell.com/).

[^1]: **Aho, Alfred V.; Corasick, Margaret J.** (June 1975). *Efficient string matching: An aid to bibliographic search.* Communications of the ACM 18 (6): 333–340. doi: [10.1145/360825.360855](http://dx.doi.org/10.1145%2F360825.360855)
 
