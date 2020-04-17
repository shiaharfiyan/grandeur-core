package org.grandeur;

import org.grandeur.logging.LogConfiguration;
import org.grandeur.logging.LogContext;
import org.grandeur.logging.LogManager;
import org.grandeur.logging.interfaces.LogAppender;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.Environment;

import java.io.File;
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
class GrandeurLock extends GrandeurLockBase {
    public GrandeurLock(String name) {
        super(name);
    }

    @Override
    protected void PreLock() {

    }

    @Override
    protected void DoLock() {
        Logger threadLogger = LogManager.GetLogger(GetName() + "-lock-monitor");
        for (LogContext ctx : LogManager.GetAllLogContext()) {
            for (Logger logger : ctx.GetLoggers()) {
                for (LogAppender appender : logger.GetLogAppenderList()) {
                    appender.Flush();
                }
            }
        }

        LogManager.Clean();

        long currentLastModified = LogConfiguration.Instance.GetLastModified();
        File file = new File(LogConfiguration.Instance.GetFullPath());
        if (currentLastModified != file.lastModified()) {
            LogConfiguration.Instance.SetLastModified(file.lastModified());
            LogConfiguration.Instance.Load();
            threadLogger.Debug("Log Configuration file has been touch! " + LogConfiguration.Instance.GetFullPath());
        }
    }

    @Override
    protected long GetDelayCheck() {
        return 0;
    }

    @Override
    protected long GetIntervalCheck() {
        return 250;
    }

    @Override
    public void close() {
        File monitoredFileLock = new File(Environment.GetGrandeurLocation() + GetName() + ".lock");
        if (monitoredFileLock.exists() && monitoredFileLock.delete())
            LogManager.GetLogger(Grandeur.class).Debug(GetName() + " stopped.");
    }
}
