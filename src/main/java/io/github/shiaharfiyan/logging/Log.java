package io.github.shiaharfiyan.logging;

import io.github.shiaharfiyan.Grandeur;
import io.github.shiaharfiyan.logging.appenders.FileLogAppender;
import io.github.shiaharfiyan.logging.interfaces.LogAppender;
import io.github.shiaharfiyan.logging.interfaces.Logger;
import io.github.shiaharfiyan.utils.helpers.ArrayHelper;

import java.util.HashMap;
import java.util.UUID;

public class Log implements Logger {
    private HashMap<String, LogAppender> logAppenderList;
    private String name;

    Log(String name) {
        this.name = name;
        this.logAppenderList = new HashMap<>();
        this.SetLevel(Level.INFO);

        Grandeur.Instance.Monitor();
        LogConfiguration.Instance.Load(this);
    }

    @Override
    public String GetName() {
        return name;
    }

    public boolean AddAppender(LogAppender appender) {
        for (LogAppender currentAppender : logAppenderList.values()) {
            if (currentAppender instanceof FileLogAppender && appender instanceof FileLogAppender) {
                if (((FileLogAppender) currentAppender).GetFullPath().equals(((FileLogAppender) appender).GetFullPath())) {
                    return false;
                }
            } else {
                if (currentAppender.getClass().getName().equals(appender.getClass().getName())) {
                    return false;
                }
            }
        }

        logAppenderList.put(UUID.randomUUID().toString(), appender);

        return true;
    }

    public LogAppender[] GetLogAppenderList() {
        return ArrayHelper.ToArray(LogAppender.class, logAppenderList.values());
    }

    public void CleanAppenderList() {
        logAppenderList.clear();
    }

    @Override
    public void Error(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.ERROR, data);
        });
    }

    @Override
    public void Error(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.ERROR, prefix + line);
        });
    }

    @Override
    public void Warn(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.WARN, data);
        });
    }

    @Override
    public void Warn(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.WARN, prefix + line);
        });
    }

    @Override
    public void Info(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.INFO, data);
        });
    }

    public void Info(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.INFO, prefix + line);
        });
    }

    @Override
    public void Debug(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.DEBUG, data);
        });
    }

    @Override
    public void Debug(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.DEBUG, prefix + line);
        });
    }

    @Override
    public void Trace(String message) {
        logAppenderList.values().forEach((la) -> {
            StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                la.Append(Level.DEBUG, traceElement.toString());
            }
        });
    }

    @Override
    public void Trace(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.TRACE, prefix + line);
        });
    }

    public <T> void Exception(T e) {
        if (e instanceof Exception) {
            Error("Message     ==> " + ((Exception) e).getMessage());
            Error("Stack Trace ==V ");
            StackTraceElement[] els = ((Exception) e).getStackTrace();
            for (StackTraceElement el : els) {
                Error(el.toString());
            }
        } else if (e instanceof Throwable) {
            Error("Message     ==> " + ((Throwable) e).getMessage());
            Error("Stack Trace ==V ");
            StackTraceElement[] els = ((Throwable) e).getStackTrace();
            for (StackTraceElement el : els) {
                Error(el.toString());
            }
        }
    }

    @Override
    public boolean HasAppender(LogAppender appender) {
        for (LogAppender myAppender : GetLogAppenderList()) {
            if (appender.getClass().getName().equals(myAppender.getClass().getName())) {
                if (appender instanceof FileLogAppender && myAppender instanceof FileLogAppender) {
                    if (((FileLogAppender) appender).GetFullPath().equals(((FileLogAppender) myAppender).GetFullPath()))
                        return true;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    public AppenderData GetAppender(LogAppender appender) {
        if (!HasAppender(appender))
            return null;

        for (String appenderKey : logAppenderList.keySet()) {
            LogAppender myAppender = logAppenderList.get(appenderKey);
            if (appender.getClass().getName().equals(myAppender.getClass().getName())) {
                if (appender instanceof FileLogAppender && myAppender instanceof FileLogAppender) {
                    if (((FileLogAppender) appender).GetFullPath().equals(((FileLogAppender) myAppender).GetFullPath()))
                        return new AppenderData(appenderKey, myAppender);
                } else {
                    return new AppenderData(appenderKey, myAppender);
                }
            }
        }

        return null;
    }

    @Override
    public boolean UpdateAppender(LogAppender appender) {
        AppenderData appenderData = null;
        if ((appenderData = GetAppender(appender)) != null) {
            logAppenderList.remove(appenderData.GetKey());
            logAppenderList.put(appenderData.GetKey(), appender);
        } else {
            AddAppender(appender);
        }

        return true;
    }

    @Override
    public void SetLevel(Level level) {
        this.logAppenderList.values().forEach((la) -> {
            la.SetLevel(level);
        });
    }

    private static class AppenderData {
        private String key;
        private LogAppender appender;

        public AppenderData(String key, LogAppender appender) {
            this.key = key;
            this.appender = appender;
        }

        public String GetKey() {
            return key;
        }

        public LogAppender GetAppender() {
            return appender;
        }
    }
}
