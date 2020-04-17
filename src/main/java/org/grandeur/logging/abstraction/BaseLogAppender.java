package org.grandeur.logging.abstraction;

import org.grandeur.logging.Level;
import org.grandeur.logging.interfaces.LogAppender;
import org.grandeur.logging.interfaces.Logger;

public abstract class BaseLogAppender implements LogAppender {
    protected Level level = Level.INFO;
    protected Logger logger;

    protected long lastModified;

    public BaseLogAppender(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void SetLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public long GetLastModified() {
        return lastModified;
    }
}
