package io.github.shiaharfiyan.utils.threading;

import io.github.shiaharfiyan.logging.interfaces.Logger;

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