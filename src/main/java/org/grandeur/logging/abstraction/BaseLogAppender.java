package org.grandeur.logging.abstraction;

import org.grandeur.logging.Level;
import org.grandeur.logging.interfaces.LogAppender;
import org.grandeur.logging.interfaces.Logger;

public abstract class BaseLogAppender implements LogAppender {
    protected Level level = Level.INFO;
    protected Logger logger;

    public BaseLogAppender(Logger logger) {
        this.logger = logger;
    }
}
