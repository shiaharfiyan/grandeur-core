package org.grandeur.logging.appenders;

import org.grandeur.logging.Level;
import org.grandeur.logging.LogPattern;
import org.grandeur.logging.LogRecord;
import org.grandeur.logging.abstraction.BaseLogAppender;
import org.grandeur.logging.interfaces.Logger;

import java.util.Date;

public class ConsoleLogAppender extends BaseLogAppender {
    private LogPattern logPattern;

    public ConsoleLogAppender(Logger logger) {
        super(logger);
        logPattern = LogPattern.Default();
    }

    @Override
    public LogPattern GetLogPattern() {
        return logPattern;
    }

    @Override
    public void SetLogPattern(LogPattern logPattern) {
        this.logPattern = logPattern;
    }

    @Override
    public Level GetLevel() {
        return level;
    }

    @Override
    public void SetLevel(Level level) {
        this.level = level;
    }

    @Override
    public void Append(LogRecord logRecord) {
        if (logRecord.GetLevel().GetValue() > GetLevel().GetValue()) {
            //Discard message if appender level is lower than log record message
            return;
        }
        Log(logPattern.Parse(logRecord));
    }

    @Override
    public void Append(Level level, String message) {
        Append(new LogRecord(logger, new Date(), level, message));
    }

    @Override
    public void Append(Level level, byte[] message) {
        Append(new LogRecord(logger, new Date(), level, new String(message)));
    }

    @Override
    public void Flush() {

    }

    synchronized void Log(String message) {
        System.out.println(message);
    }
}
