package org.grandeur;

import org.grandeur.logging.LogManager;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.Environment;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Timer;
import java.util.TimerTask;
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
public abstract class GrandeurLockBase implements AutoCloseable {
    protected Logger logger;
    private String name;
    private Timer timer;
    private FileLock fileLock;

    public GrandeurLockBase(String name) {
        this.name = name;
    }

    public void Lock() {
        Monitor();
    }

    public String GetName() {
        return name;
    }

    protected abstract void PreLock();

    protected abstract void DoLock();

    protected abstract long GetDelayCheck();

    protected abstract long GetIntervalCheck();

    private void Monitor() {
        try {
            File monitoredFileLock = new File(Environment.GetGrandeurLocation() + GetName() + ".lock");
            if (!monitoredFileLock.exists()) {
                boolean created = monitoredFileLock.createNewFile();
                if (created) {
                    LogManager.GetLogger(Grandeur.class).Debug(GetName() + ".lock has been created!");
                }
            }

            if (monitoredFileLock.exists() && !Grandeur.Instance.LockExists(this)) {
                RandomAccessFile raf = new RandomAccessFile(monitoredFileLock, "rw");
                FileChannel fileChannel = raf.getChannel();
                fileLock = fileChannel.tryLock();
                if (fileLock != null) {
                    PreLock();
                    Grandeur.Instance.SubmitLock(this);
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                DoLock();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, GetDelayCheck(), GetIntervalCheck());
                    LogManager.GetLogger(Grandeur.class).Debug(GetName() + " is running...");
                }
            }
        } catch (OverlappingFileLockException ofle) {
            //Ignore this overlap file lock
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            timer.cancel();
            if (fileLock != null) {
                fileLock.release();
                fileLock.close();
            }
            File monitoredFileLock = new File(Environment.GetGrandeurLocation() + GetName() + ".lock");
            if (monitoredFileLock.exists() && monitoredFileLock.delete())
                LogManager.GetLogger(Grandeur.class).Debug(GetName() + " stopped...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
