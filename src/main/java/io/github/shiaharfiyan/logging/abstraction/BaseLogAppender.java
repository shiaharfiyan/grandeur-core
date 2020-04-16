package io.github.shiaharfiyan.logging.abstraction;

import io.github.shiaharfiyan.logging.Level;
import io.github.shiaharfiyan.logging.interfaces.LogAppender;
import io.github.shiaharfiyan.logging.interfaces.Logger;

public abstract class BaseLogAppender implements LogAppender {
    protected Level level = Level.INFO;
    protected Logger logger;

    public BaseLogAppender(Logger logger) {
        this.logger = logger;
    }
}
