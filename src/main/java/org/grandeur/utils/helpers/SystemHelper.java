package io.github.shiaharfiyan.utils.helpers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SystemHelper {
    public static void Run(String... args) {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                try {
                    new ProcessBuilder(args).start();
                    t.cancel();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    public static void FreeMemory() {
        Runtime.getRuntime().gc();
    }
}
