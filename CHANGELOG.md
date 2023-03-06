## 2.0.1 (6 Mar 2023)

### Bug Fixes
  - Fixed an issue where multiple values with the same key were not being parsed correctly.

## 2.0.0 (29 Jan 2023)

### Breaking Changes
```java
public static void main(String[] args) throws Exception {
    Arguments arguments = parseArguments(args);
}

private static Arguments parseArguments(String[] args) throws InvalidArgumentsException {
    return Arguments.parser(args)
        .help(arguments -> {
            System.out.println("Some Help Message");
            System.exit(0);
        })
        .validate(arguments -> {
            // some validation
            return true;
        })
    .parse();
}
```
### Deprecated APIs
  - `ArgumentsParser.parse(args)`
  - `ArgumentsParser.parse(args, validator)`
### New APIs for `Arguments` class
  - `String getValue(String key)`
  - `String getValue(String key, String defaultValue)`
  - `List<String> getValues(String key)`
  - `List<String> getValues(String key, List<String> defaultValues)`
  - `boolean requiresHelp()`

## 1.1.0 (10 Jul 2020)
  - Fix identity issue and missing key problem

## 1.0.0 (9 Jul 2020)
  - The first proper release