# Arguments Parser

![release](https://img.shields.io/badge/release-v1.0.0-blue.svg)
[![Build Status](https://travis-ci.com/silentsoft/arguments-parser.svg?branch=master)](https://travis-ci.com/silentsoft/arguments-parser)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=silentsoft_arguments-parser&metric=coverage)](https://sonarcloud.io/dashboard?id=silentsoft_arguments-parser)
[![HitCount](http://hits.dwyl.com/silentsoft/arguments-parser.svg)](http://hits.dwyl.com/silentsoft/arguments-parser)

> Do not parsing main(args) anymore !

`Arguments Parser` is a simple java library to parse command line arguments.

## Supported Formats
  * -a -b
  * -key1=value1 -key2=value2
  * -key1 value1 -key2 value2
  * -key1=value1 value2 -key2=value3 value4
  * -key1 value1 value2 -key2 value3 value4
  * --a --b
  * --key1=value1 --key2=value2
  * --key1 value1 --key2 value2
  * --key1=value1 value2 --key2=value3 value4
  * --key1 value1 value2 --key2 value3 value4

## Maven Central
```xml
<dependencies>
    <dependency>
        <groupId>org.silentsoft</groupId>
        <artifactId>arguments-parser</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Usage
```java
public static void main(String[] args) throws Exception {
    Arguments arguments = ArgumentsParser.parse(args);
}
```

## Advanced Topics

### Arguments Validator Usage
```java
public static void main(String[] args) throws Exception {
    ArgumentsValidator validator = (arguments) -> {
        if (arguments.containsKey("-a") && arguments.containsKey("-b")) {
            throw new InvalidArgumentsException("'-a' and '-b' cannot be exists together.");
        }
        
        return true;
    };
    
    Arguments arguments = ArgumentsParser.parse(args, validator);
}
```

### Parsing Options with dash prefix
  * LEAVE_DASH_PREFIX `(default)`
  * CASE_SENSITIVE_SINGLE_DASH `(default)`
  * CASE_INSENSITIVE_SINGLE_DASH
  * CASE_SENSITIVE_DOUBLE_DASH
  * CASE_INSENSITIVE_DOUBLE_DASH `(default)`

### Parsing Options without dash prefix
  * REMOVE_DASH_PREFIX
  * CASE_SENSITIVE
  * CASE_INSENSITIVE

### Usage
```java
public static void main(String[] args) throws Exception {
    Arguments arguments = ArgumentsParser.parse(args).with(ParsingOptions...);
}
```

## Packaging
```
$ mvn clean package
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please note we have a [CODE_OF_CONDUCT](https://github.com/silentsoft/arguments-parser/blob/master/CODE_OF_CONDUCT.md), please follow it in all your interactions with the project.

## License
Please refer to [LICENSE](https://github.com/silentsoft/arguments-parser/blob/master/LICENSE.txt).
