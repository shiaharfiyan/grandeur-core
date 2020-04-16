package io.github.shiaharfiyan;

import io.github.shiaharfiyan.logging.LogConfiguration;
import io.github.shiaharfiyan.logging.LogContext;
import io.github.shiaharfiyan.logging.LogManager;
import io.github.shiaharfiyan.logging.interfaces.LogAppender;
import io.github.shiaharfiyan.logging.interfaces.Logger;
import io.github.shiaharfiyan.utils.Environment;

import java.io.File;

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
            threadLogger.Info("Log Configuration file has been touch! " + LogConfiguration.Instance.GetFullPath());
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
            LogManager.GetLogger(Grandeur.class).Info(GetName() + " stopped.");
    }
}
