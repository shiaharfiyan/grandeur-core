# Welcome to Gandeur LogTrace

Gandeur LogTrace is a simple logging library with nested diagnostic context and mapped diagnostic context to allow easier tracing with customizable config for log appender.

# Usage

### Changes log

```text
    Version 0.1.0
    - Adding filters to logger, print specific log which contains filter

    Version 0.0.9
    - Mapped Diagnostic Context is now part of Context Object (read Log with Mapped Diagnostic Context section below)
    - Improved LogPattern parse routine
```

### Simple Usage
```java
import org.grandeur.logging.LogManager;

public class Sample {
    private static Logger logger = LogManager.GetLogger(Sample.class);
    public static void main(String[] args) {
        logger.Info("Hello, its grandeur log trace!");
    }
}
```
#### _Pattern_
```json
{
  "globalPattern": "%d{yyyy/MM/dd HH:mm:ss,SSS} [%t] %n %l : %v"
}
```
#### Output
```text
2020/04/17 16:42:06,685 [main] Sample INFO : Hello, its grandeur log trace!
```

### Log with _Nested Diagnostic Context_
```java
import org.grandeur.logging.DC;
import org.grandeur.logging.LogManager;

public class Sample {
    private static Logger logger = LogManager.GetLogger(Sample.class);
    public static void main(String[] args) {
        DC.Push("entry");
        DC.Push("update");
        //Do something
        logger.Info("Has been updated");
        DC.Pop();
        logger.Info("update completed!");
        DC.Pop();

        //or        

        try(Context parent = DC.Push("entry")) {
            logger.Info("Hello, its grandeur logtrace!");
            try (Context child = DC.Push("update")) {
                logger.Info("Has been updated");
            }
            logger.Info("update completed!");
        }
    }
}
```
#### _Pattern Nested Diagnostic Context_
```json
{
  "globalPattern": "%d{yyyy/MM/dd HH:mm:ss,SSS} [%t] [%c] %n %l : %v"
}
```
#### Output
```text
2020/04/17 14:16:05,550 [main] [entry] Sample INFO : Hello, its grandeur log trace
2020/04/17 14:16:05,550 [main] [update] Sample INFO : Has been updated
2020/04/17 14:16:05,550 [main] [entry] Sample INFO : update completed!
```
### Log with _Mapped Diagnostic Context_
```java
import org.grandeur.logging.DC;
import org.grandeur.logging.LogManager;

public class Sample {
    private static Logger logger = LogManager.GetLogger(Sample.class);
    public static void main(String[] args) {
        try(Context parent = DC.Push("entry")) {
            parent.Put("firstname", "harfiyan");
            parent.Put("lastname", "shia");
            logger.Info("Hello, its grandeur logtrace!");
            try (Context child = DC.Push("update")) {
                child.Put("firstname", "example 1");
                logger.Info("Has been updated");
                child.Remove("firstname");
            }
            parent.Remove("firstname");
            parent.Remove("lastname");
            logger.Info("update completed!");
        }
    }
}
```
#### _Pattern Mapped Diagnostic Context_
```json
{
  "globalPattern": "%d{yyyy/MM/dd HH:mm:ss,SSS} [%t] [%c] %n %l : [firstName=%x{firstname}, lastname=%x{lastname}] %v"
}
```
#### Output
```text
2020/04/17 16:04:23,036 [main] [entry] Sample INFO : [firstName=harfiyan, lastname=shia] Hello, its grandeur logtrace!
2020/04/17 16:04:23,037 [main] [update] Sample INFO : [firstName=harfiyan, lastname=shia] Has been updated
2020/04/17 16:04:23,041 [main] [entry] Sample INFO : [firstName=null, lastname=null] update completed!
```

# Config file
By default, this config file will be created during runtime if there's no any .json file provided and it is required to enable this logging, otherwise only stdout.
### Json Config file Default
```json
{
  "globalPattern": "%d{yyyy/MM/dd HH:mm:ss,SSS} [%t] %n %l : %v",
  "loggerList": [
    {
      "bindTo": "*",
      "filters": [
        {
          "method": "Regex",
          "area": "Value",
          "filter": ".*"
        }
      ],
      "appenderList": [
        {
          "type": "org.grandeur.logging.appenders.ConsoleLogAppender",
          "level": "info"
        },
        {
          "fileName": "Grandeur",
          "path": "{{grandeurlocation}}",
          "type": "org.grandeur.logging.appenders.FileLogAppender",
          "level": "info",
          "keeper": {
            "enabled": true,
            "sizeLimit": 104857600,
            "fileToKeep": 20,
            "prefix": null,
            "suffix": null,
            "autoMove": true,
            "autoCreateArchiveFolder": true
          }
        }
      ]
    }
  ]
}
```
### Json Config file Description
|Name|Data Type|Notes|
|----|---------|-----|
| globalPattern | String | Define the log looks like, will be used as default pattern if there's no specific pattern defined for each appender |
| loggerList | Array of Object |  |
|     bindTo | String | Put "*" to bind all Logger, or specify the name of Logger to bind specifically |
|     filters | Array of Object |  |
|         method | String | how to apply the filter. possible value: StartWith, EndWith, Contains, Equals, NotContains, Regex |
|         area | String | area of filter will be applied. possible value: Value, Thread, Context, Date, LoggerName, Level; |
|         filter | String | Acceptable type "com.rhapsody.logging.appenders.FileLogAppender" and "com.rhapsody.logging.appenders.ConsoleLogAppender" or any class extends LogAppender interface |
|     appenderList | Array of Object |  |
|         fileName | String | Name of log, automatically append extension ".log", only   available for "com.rhapsody.logging.appenders.FileLogAppender" |
|         path | String | Path of Log, automatically   create folder, failed if drive not exists |
|         type | String | Acceptable type   "com.rhapsody.logging.appenders.FileLogAppender" and   "com.rhapsody.logging.appenders.ConsoleLogAppender" |
|         level | String | Acceptable type   "info", "error", "warn", "debug" |
|         pattern | String | Define the log looks like |
|         keeper | Object | Only available for   "org.grandeur.logging.appenders.FileLogAppender" |
|             enabled | boolean | Enable the keeper |
|             sizeLimit | long | Log file maximum size   before archived |
|             fileToKeep | int | Maximum number of file will be kept |
|             prefix | String | Append archived log with   prefix |
|             suffix | String | Append archived log with suffix |
|             autoMove | boolean | Set "true" to   automatically move log to archived folder, otherwise "false" |
|             autoCreateArchiveFolder | boolean | By Default archive folder name will be set to   "{fileName}_Archived" and will be created automatically if set to   "true", otherwise "false" |

### Pattern 
| Pattern Name | Pattern Type | Pattern Description |
|----------------------|---------------------|---------------------------------------------------------------------------|
| %d | DateTime | Use Default DateTime Pattern _(yyyy-MM-dd HH:mm:ss)_|
| %d{datetime_pattern} | DateTime | Specify DateTime with   custom "datetime_pattern", see Java Documentation |
| %t | Thread Name | Thread Name which call the logger |
| %z | Duration | s,ms |
| %c | Nested Context | Display only latest tag within current Context |
| %C | Nested Context | Display all tag within   current Context |
| %n | Log Name | Display Log Name, if use class name, it will display simple name |
| %N | Log Name | Display Log Name, if use class name, it will display complete name |
| %l | Log Level | INFO, WARN, DEBUG, ERROR |
| %i | Nested Context   ID | Display only latest Nested Context ID |
| %v | Log Message | Content to log |
| %x{key} | Mapped Context | Specify "key" to get value of mapped   context |
| %X | Mapped Context | Display all Mapped Key   within current Context |

### Maven dependency
Add the following to your pom.xml
```xml
<!-- https://mvnrepository.com/artifact/io.github.shiaharfiyan/grandeur-core -->
<dependency>
    <groupId>io.github.shiaharfiyan</groupId>
    <artifactId>grandeur-core</artifactId>
    <version>0.1.0</version>
</dependency>
```
### Third-Party Library
Grandeur LogTrace use Gson 2.8.5 (https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5) to load config file. Many thanks to Google!

### Contribution
I will be glad if there is someone wants to contribute and optimize Grandeur LogTrace, make it more efficient and effective. Please contact me hshia.dev@gmail.com
