package org.grandeur;

import org.grandeur.logging.DC;
import org.grandeur.logging.LogConfiguration;
import org.grandeur.logging.LogManager;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.Environment;

import java.util.HashMap;
/**
 *     Grandeur - a tool for logging, create config file based on ini and
 *     utils
 *
 *     Copyright (C) 2020-Present Harfiyan Shia.
 *
 *     This file is part of Grandeur.
 *
 *     Grandeur is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Grandeur is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Grandeur. If not, see http://www.gnu.org/licenses/.
 */
public enum Grandeur {
    Instance;

    private HashMap<String, GrandeurLockBase> grandeurLockData = new HashMap<>();

    Grandeur() {
        LogConfiguration.Instance.SetPath(Environment.GetGrandeurLocation());
        LogConfiguration.Instance.SetFileName(Grandeur.class.getSimpleName() + ".json");
    }

    public static void main(String[] args) {
        Logger logger = LogManager.GetLogger(Grandeur.class);
        DC.Push("api-payment");
        DC.Put("name", "Fitra");
        logger.Info("test ora opo opo to");
        logger.Info("test");
        DC.Pop();
        DC.Push("api-payment");
    }

    public boolean LockExists(GrandeurLockBase lock) {
        return grandeurLockData.containsKey(lock.GetName());
    }

    public void SubmitLock(GrandeurLockBase lock) {
        if (!grandeurLockData.containsKey(lock.GetName()))
            grandeurLockData.put(lock.GetName(), lock);
    }

    public void ReleaseLock(String name) {
        try {
            if (grandeurLockData.containsKey(name)) {
                grandeurLockData.get(name).close();
                grandeurLockData.remove(name);
            }
        } catch (Exception e) {
            LogManager.GetLogger(Grandeur.class).Exception(e);
            e.printStackTrace();
        }
    }

    public void Monitor() {
        try (GrandeurLock lock = new GrandeurLock("Grandeur")) {
            lock.Lock();
        }
    }
}
