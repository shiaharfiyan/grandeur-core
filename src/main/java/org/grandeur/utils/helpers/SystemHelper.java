package org.grandeur.utils.helpers;

import java.io.IOException;
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
