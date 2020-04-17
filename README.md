# Welcome to Gandeur LogTrace (grandeur-core)

This is a simple logging library with nested diagnostic context and mapped diagnostic context to allow easier tracing with customizable config for log appender.


# Config file
By default, this config file will be created during runtime if there's no any .json file provided and it is required to enable this logging, otherwise only stdout.

### Json Config file Default
```json
{
  "globalPattern": "%d{yyyy/MM/dd HH:mm:ss,SSS} [%t] [%c] %n %N %z %l : %v",
  "loggerList": [
    {
      "bindTo": "*",
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
| %d | DateTime | Use Default DateTime Pattern |
| %d{datetime_pattern} | DateTime | Specify DateTime with   custom "datetime_pattern", see Java Documentation |
| %t | Thread Name | Thread Name which call the logger |
| %z | Duration | s,ms |
| %c | Nested Context | Display only latest tag within current Context |
| %C | Nested Context | Display all tag within   current Context |
| %n | Log Name | Display Log Name |
| %l | Log Level | INFO, WARN, DEBUG, ERROR |
| %i | Nested Context   ID | Display only latest Nested Context ID |
| %v | Log Message | Content to log |
| %x{key} | Mapped Context | Specify "key" to get value of mapped   context |
| %X | Mapped Context | Display all Mapped Key   within current Context |
