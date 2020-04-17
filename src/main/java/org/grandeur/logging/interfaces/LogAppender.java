package org.grandeur.logging.interfaces;

import org.grandeur.logging.Level;
import org.grandeur.logging.LogPattern;
import org.grandeur.logging.LogRecord;
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
