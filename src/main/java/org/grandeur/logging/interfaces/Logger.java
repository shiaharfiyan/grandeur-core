package org.grandeur.logging.interfaces;

import org.grandeur.logging.Level;

public interface Logger {
    String GetName();

    boolean AddAppender(LogAppender appender);

    LogAppender[] GetLogAppenderList();

    void CleanAppenderList();

    void SetLevel(Level level);

    void Info(String message);

    void Info(String prefix, String[] lines);

    void Warn(String message);

    void Warn(String prefix, String[] lines);

    void Error(String message);

    void Error(String prefix, String[] lines);

    void Debug(String message);

    void Debug(String prefix, String[] lines);

    void Trace(String message);

    void Trace(String prefix, String[] lines);

    <T> void Exception(T e);

    boolean HasAppender(LogAppender appender);

    boolean UpdateAppender(LogAppender appender);
}
