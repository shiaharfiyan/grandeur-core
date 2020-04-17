package org.grandeur.utils.threading;

import org.grandeur.logging.interfaces.Logger;
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
public abstract class RunnerTask implements Runnable {
    private Thread thread;
    private String threadName;
    private Logger logger;
    private Object[] objectArgs;
    private int delay;
    private volatile boolean isRun = false;

    public RunnerTask(String threadName) {
        this.delay = 1000;
        this.threadName = threadName;
    }

    public RunnerTask(String threadName, int delay) {
        this(threadName);
        this.delay = delay;
    }

    public RunnerTask(String threadName, int delay, Logger logger) {
        this(threadName, delay);
        this.logger = logger;
    }

    public abstract void DoJob(Object[] args);

    public void run() {
        System.out.println("Engine is running..");
        do {
            try {
                DoJob(objectArgs);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                isRun = false;
                if (logger != null) {
                    logger.Exception(e);
                }
            }
        } while (this.isRun);
        System.out.println("Engine is stopped..");
    }

    public void Start() {
        this.isRun = true;
        if (this.thread == null) {
            this.thread = new Thread(this, this.threadName);
        }
        this.thread.start();
    }

    public void Start(Object[] args) {
        objectArgs = args;
        Start();
    }

    public void Stop() {
        this.isRun = false;
    }

    public void SetDelay(int delay) {
        this.delay = delay;
    }

    public int GetDelay() {
        return delay;
    }

    public void SetObjectArgs(Object[] args) {
        this.objectArgs = args;
    }

    public Object[] GetObjectArgs() {
        return objectArgs;
    }

    public void SetLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger GetLogger() {
        return logger;
    }
}