package io.github.shiaharfiyan.logging.interfaces;

import io.github.shiaharfiyan.logging.Level;
import io.github.shiaharfiyan.logging.LogPattern;
import io.github.shiaharfiyan.logging.LogRecord;

public interface LogAppender {
    LogPattern GetLogPattern();

    void SetLogPattern(LogPattern logPattern);

    Level GetLevel();

    void SetLevel(Level level);

    void Append(LogRecord logRecord);

    void Append(Level level, String message);

    void Append(Level level, byte[] message);

    void Flush();
}
