package org.grandeur.logging.appenders;

import org.grandeur.logging.Level;
import org.grandeur.logging.LogPattern;
import org.grandeur.logging.LogRecord;
import org.grandeur.logging.abstraction.BaseLogAppender;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.StringHelper;

import java.util.Date;
/**
 *     Grandeur - a tool for logging, create config file based on ini and
 *     utils
 *     Copyright (C) 2020 Harfiyan Shia.
 *
 *     This file is part of grandeur-core.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/.
 */
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
        if (StringHelper.IsNullOrEmpty(message, true))
            return;

        System.out.println(message);
    }
}
