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
                    LogManager.GetLogger(Grandeur.class).Info(GetName() + ".lock has been created!");
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
                            DoLock();
                        }
                    }, GetDelayCheck(), GetIntervalCheck());
                    LogManager.GetLogger(Grandeur.class).Info(GetName() + " is running...");
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
                LogManager.GetLogger(Grandeur.class).Info(GetName() + " stopped...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
