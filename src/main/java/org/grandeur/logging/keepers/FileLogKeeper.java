package org.grandeur.logging.keepers;

import org.grandeur.logging.Level;
import org.grandeur.logging.appenders.FileLogAppender;
import org.grandeur.logging.interfaces.LogKeeper;
import org.grandeur.utils.Environment;
import org.grandeur.utils.helpers.FileHelper;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class FileLogKeeper implements LogKeeper {
    private FileLogAppender logAppender;
    private String path;
    private String dateTimeFormat;
    private BigInteger sizeLimit = new BigInteger("104857600");
    private int fileCountToKeep = 10;
    private String prefix;
    private String suffix;
    private boolean isAutoCreateArchivedDirectory;
    private boolean isNeedToMove;

    private volatile boolean isKeeping;

    private boolean isEnabled;

    public FileLogKeeper(FileLogAppender logAppender) {
        this.logAppender = logAppender;
        this.dateTimeFormat = "yyyyMMdd_HHmmss";

        this.prefix = null;
        this.suffix = null;
        this.path = logAppender.GetPath() + logAppender.GetFileName() + "_Archived";
    }

    public void SetPrefix(String pref) {
        this.prefix = pref;
    }

    public void SetSuffix(String suff) {
        this.suffix = suff;
    }

    public void SetFileCountToKeep(int fileCountToKeep) {
        this.fileCountToKeep = fileCountToKeep;
    }

    public void SetSizeLimit(long sizeLimit) {
        this.sizeLimit = new BigInteger(String.valueOf(sizeLimit));
    }

    @Override
    public void SetAutoCreateArchivedDirectory(boolean auto) {
        this.isAutoCreateArchivedDirectory = auto;
    }

    @Override
    public boolean GetAutoCreateArchivedDirectory() {
        return this.isAutoCreateArchivedDirectory;
    }

    public boolean SetDateTimeFormat(String newFormat) {
        try {
            String dateFormat = new SimpleDateFormat(newFormat).format(new Date());
            this.dateTimeFormat = newFormat;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public String GetPrefix() {
        return this.prefix;
    }

    @Override
    public String GetSuffix() {
        return this.suffix;
    }

    public String GetDateTimeFormat() {
        return this.dateTimeFormat;
    }

    @Override
    public int GetFileCountToKeep() {
        return this.fileCountToKeep;
    }

    @Override
    public BigInteger GetSizeLimit() {
        return this.sizeLimit;
    }

    @Override
    public void SetSizeLimit(BigInteger sizeLimit) {
        SetSizeLimit(sizeLimit.longValue());
    }

    public synchronized void Keep() {
        if (!IsEnabled())
            return;

        File file = new File(this.logAppender.GetFullPath());
        if (file.length() > GetSizeLimit().longValue()) {
            this.isKeeping = true;
            if (this.isNeedToMove) {
                String dateFormat = new SimpleDateFormat(this.dateTimeFormat).format(new Date());
                String filename = "";

                File dir = new File(this.path);
                if ((!dir.exists()) && (this.isAutoCreateArchivedDirectory)) {
                    boolean succeeded = dir.mkdir();
                    if (succeeded) {
                        this.logAppender.Append(Level.DEBUG, "Archive folder has been created successfully!");
                    } else {
                        this.logAppender.Append(Level.DEBUG, "Archive folder failed to create!");
                    }
                }
                filename = filename + this.path + (Environment.IsWindows() ? "\\" : "/");
                if (GetPrefix() != null) {
                    filename = filename + (!GetPrefix().equals("") ? GetPrefix() + "_" : "");
                }
                filename = filename + dateFormat + "_" + file.getName().substring(0, file.getName().lastIndexOf("."));
                if (GetSuffix() != null) {
                    filename = filename + (!GetSuffix().equals("") ? "_" + GetSuffix() : "");
                }
                filename = filename + file.getName().substring(file.getName().lastIndexOf("."));
                if (file.renameTo(new File(filename))) {
                    this.logAppender.Append(Level.DEBUG, file.getName() + " is moved successful!");
                } else {
                    this.logAppender.Append(Level.DEBUG, file.getName() + " is failed to move!");
                }
            }
            boolean delResult = file.delete();
            if (this.isNeedToMove) {
                if (delResult) {
                    this.logAppender.Append(Level.DEBUG, file.getName() + " has been deleted successfully!");
                } else {
                    this.logAppender.Append(Level.DEBUG, file.getName() + " cannot be deleted!");
                }
                File directory = new File(this.path);
                File[] files = directory.listFiles();
                if ((files != null) && (files.length > GetFileCountToKeep())) {
                    Arrays.sort(files, new FileHelper.LastModifiedComparator());
                    for (int i = 0; i < files.length - GetFileCountToKeep(); i++) {
                        boolean delhResult = files[(files.length - (i + 1))].delete();
                        if (delhResult) {
                            this.logAppender.Append(Level.DEBUG, files[(files.length - (i + 1))].getName() + " has been deleted successfully!");
                        } else {
                            this.logAppender.Append(Level.DEBUG, files[(files.length - (i + 1))].getName() + " cannot be deleted!");
                        }
                    }
                }
            }
            this.isKeeping = false;
        }
    }

    @Override
    public void SetEnable(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public boolean IsEnabled() {
        return isEnabled;
    }

    public boolean GetIsKeeping() {
        return this.isKeeping;
    }

    @Override
    public void SetNeedToMove(boolean move) {
        this.isNeedToMove = move;
    }

    @Override
    public boolean GetNeedToMove() {
        return this.isNeedToMove;
    }
}
