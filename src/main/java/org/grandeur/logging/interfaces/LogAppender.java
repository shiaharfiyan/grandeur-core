package org.grandeur.logging.interfaces;

import org.grandeur.logging.Level;
import org.grandeur.logging.LogPattern;
import org.grandeur.logging.LogRecord;

public interface LogAppender {
    LogPattern GetLogPattern();

    void SetLogPattern(LogPattern logPattern);

    Level GetLevel();

    void SetLevel(Level level);

    void Append(LogRecord logRecord);

    void Append(Level level, String message);

    void Append(Level level, byte[] message);

    void Flush();

    void SetLastModified(long lastModified);

    long GetLastModified();
}
