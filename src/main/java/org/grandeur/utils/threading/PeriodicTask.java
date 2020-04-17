package org.grandeur.utils.threading;

import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.ArrayHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
