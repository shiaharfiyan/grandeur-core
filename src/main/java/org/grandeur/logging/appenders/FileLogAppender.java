package org.grandeur.logging.appenders;

import org.grandeur.FileSystem;
import org.grandeur.logging.Level;
import org.grandeur.logging.LogPattern;
import org.grandeur.logging.LogRecord;
import org.grandeur.logging.abstraction.BaseLogAppender;
import org.grandeur.logging.interfaces.LogKeeper;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.logging.keepers.FileLogKeeper;
import org.grandeur.utils.Environment;
import org.grandeur.utils.helpers.FileHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
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
@SuppressWarnings("Duplicates")
public class FileLogAppender extends BaseLogAppender implements FileSystem {
    private final static Object lock = new Object();
    protected final LinkedBlockingQueue<String> bufferedMessages;
    private String path;
    private String filename;
    private FileLogKeeper keeper;
    private LogPattern logPattern;

    private String id;

    public FileLogAppender(Logger logger) {
        super(logger);
        this.id = UUID.randomUUID().toString();
        this.logPattern = LogPattern.Default();
        this.keeper = new FileLogKeeper(this);
        this.bufferedMessages = new LinkedBlockingQueue<>();
    }

    public void ReinitializeKeeper() {
        this.keeper = new FileLogKeeper(this);
    }

    @Override
    public String GetPath() {
        String pathSeparator = Environment.IsWindows() ? "\\" : "/";
        return ((this.path != null) && (this.path.endsWith(pathSeparator)) ? this.path : this.path + pathSeparator);
    }

    @Override
    public void SetPath(String path) {
        this.path = path;
    }

    @Override
    public String GetFileName() {
        return this.filename;
    }

    @Override
    public void SetFileName(String fileName) {
        this.filename = fileName;
    }

    @Override
    public String GetFullPath() {
        String pathSeparator = Environment.IsWindows() ? "\\" : "/";
        return ((this.path != null) && (this.path.endsWith(pathSeparator)) ? this.path + this.filename : this.path + pathSeparator + this.filename) + ".log";
    }

    @Override
    public void Append(LogRecord logRecord) {
        if (logRecord.GetLevel().GetValue() > GetLevel().GetValue()) {
            //Discard message if appender level is lower than log record level provided
            return;
        }

        if (bufferedMessages.size() >= 2000) {
            Flush();
        }

        try {
            if (logRecord.GetLevel().GetValue() <= GetLevel().GetValue()) {
                bufferedMessages.put(logPattern.Parse(logRecord));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    synchronized boolean Log(String message) {
        synchronized (lock) {
            String fullPath = GetFullPath();
            try {
                File f = new File(fullPath);
                boolean createFile = FileHelper.IsExistsOrCreated(f);
                if ((createFile) && (f.canWrite())) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, true));
                    writer.write(message);
                    writer.flush();
                    writer.close();
                }

                if (this.keeper.IsEnabled()) {
                    this.keeper.Keep();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    @Override
    public void Flush() {
        if (bufferedMessages.size() > 0) {
            final ExecutorService executorService = Executors.newFixedThreadPool(1);
            Future<Boolean> future = executorService.submit(() -> {
                StringBuilder sb = new StringBuilder();
                int maxNum = Math.min(2000, bufferedMessages.size());
                for (int i = 0; i < maxNum; i++) {
                    try {
                        sb.append(bufferedMessages.take()).append(Environment.GetNewLine());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return Log(sb.toString());
            });
            try {
                if (future.get()) {
                    executorService.shutdownNow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public LogKeeper GetKeeper() {
        return keeper;
    }
}
