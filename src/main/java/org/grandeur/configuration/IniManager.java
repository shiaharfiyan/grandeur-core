package org.grandeur.configuration;

import org.grandeur.utils.Procedure;
import org.grandeur.utils.helpers.ArrayHelper;

import java.io.File;
import java.util.HashMap;

public enum IniManager {
    Instance;

    private HashMap<String, Ini> iniConfigurationData = new HashMap<>();

    public boolean IniExists(Ini ini) {
        return iniConfigurationData.containsKey(ini.GetFullPath());
    }

    public void MonitorIni(Ini ini) {
        if (!IniExists(ini))
            iniConfigurationData.put(ini.GetFullPath(), ini);
    }

    public void ReleaseIni(Ini ini) {
        iniConfigurationData.remove(ini.GetFullPath());
    }

    public void Notify() {
        for(Ini ini : iniConfigurationData.values()) {
            File file = new File(ini.GetFullPath());
            if (file.exists() && file.lastModified() != ini.GetLastModified()) {
                ini.Update();
            }
        }
    }
}
