package org.grandeur.utils.threading;

import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.ArrayHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by harfiyan on 4/6/2017.
 */
public abstract class PeriodicTask {
    private Timer timer = null;
    private long previousInterval = 0L;
    private Date startDate = null;
    private int intervalLimit = 5;
    private Logger logger = null;
    private boolean condition = true;
    private long periodicIntervalResetLimit;

    public PeriodicTask() {
        SetPeriodicIntervalResetLimit(3600L);
    }

    public abstract void SetupCondition();

    public abstract void DoJob(Object[] args);

    public void RunScheduler(final Object... args) {
        this.timer = new Timer();
        this.startDate = new Date();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                long interval = new Date().getTime() - startDate.getTime();
                long intervalInSecond = TimeUnit.MILLISECONDS.toSeconds(interval);

                List<Object> newArgs = new ArrayList<>(Arrays.asList(args));
                newArgs.add(interval);
                newArgs.add(intervalInSecond);

                SetupCondition();

                if (((condition) || ((intervalInSecond != 0L) && (intervalInSecond % intervalLimit == 0L))) && (previousInterval != intervalInSecond)) {
                    previousInterval = intervalInSecond;

                    DoJob(ArrayHelper.ToArray(Object.class, newArgs));
                }

                if (intervalInSecond > GetPeriodicIntervalResetLimit()) {
                    startDate = new Date();
                    logger.Info("Interval reset");
                }
            }
        }, 1L, 1L);
    }

    public void StopScheduler() {
        timer.cancel();
    }

    public int GetIntervalLimit() {
        return intervalLimit;
    }

    public void SetIntervalLimit(int intervalLimit) {
        this.intervalLimit = intervalLimit;
    }

    public Logger GetLogger() {
        return logger;
    }

    public void SetLogger(Logger logger) {
        this.logger = logger;
    }

    public boolean Condition() {
        return condition;
    }

    public void SetCondition(boolean condition) {
        this.condition = condition;
    }

    public long GetPeriodicIntervalResetLimit() {
        return periodicIntervalResetLimit;
    }

    public void SetPeriodicIntervalResetLimit(long periodicIntervalResetLimit) {
        this.periodicIntervalResetLimit = periodicIntervalResetLimit;
    }
}
