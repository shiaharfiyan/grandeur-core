package org.grandeur.utils;

import org.grandeur.Grandeur;
import org.grandeur.configuration.Ini;
import org.grandeur.logging.LogManager;
import org.grandeur.utils.helpers.DateTimeHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
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
public final class Environment {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String userLocation = System.getProperty("user.dir");
    private static HashMap<String, String> variables = new HashMap<>();
    private static String domainLocation;
    private static String grandeurLocation = Environment.GetUserLocation() + "Grandeur" + (OS.contains("win") ? "\\" : "/");
    private static String defaultFilename = grandeurLocation + Grandeur.class.getSimpleName() + ".ini";
    private static Ini configuration;

    static {
        try {
            SetDomainLocation(java.net.URLDecoder.decode(new File(Grandeur.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getParent(), StandardCharsets.UTF_8.name()) + (OS.contains("win") ? "\\" : "/"));

            File grandeurDir = new File(grandeurLocation);
            if (!grandeurDir.exists()) {
                boolean result = grandeurDir.mkdir();
                if (result) {
                    System.out.println(grandeurLocation + " created!");
                } else {
                    System.out.println("Create Folder Failed");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        variables.put("{{grandeurlocation}}", Environment.GetGrandeurLocation());
        variables.put("{{domainlocation}}", Environment.GetDomainLocation());
        variables.put("{{userlocation}}", Environment.GetUserLocation());
        variables.put("{{userhome}}", System.getProperty("user.home"));
        variables.put("{{programfiles32}}", System.getenv("ProgramFiles(X86)"));
        variables.put("{{programfiles64}}", System.getenv("ProgramFiles"));
        variables.put("{{sysdate}}", DateTimeHelper.GetCompleteDateFormat(new Date()));
    }

    /***
     * @param resourceName
     * @param fromClass
     * @return
     * @throws Exception
     */
    static public String ExportResource(String resourceName, Class<?> fromClass) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = fromClass.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = GetGrandeurLocation();
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stream != null) stream.close();
            if (resStreamOut != null) resStreamOut.close();
        }

        return jarFolder + resourceName;
    }


    public static String Replace(String input) {
        final String[] temp = {input};
        variables.forEach((k, v) -> {
            temp[0] = temp[0].replace(k, v);
        });
        return temp[0];
    }

    private static void SetDomainLocation(String domainLocation) {
        Environment.domainLocation = domainLocation;
    }

    public static String GetGrandeurLocation() {
        return grandeurLocation;
    }

    public static String GetDomainLocation() {
        return domainLocation;
    }

    public static String GetUserLocation() {
        return userLocation + (OS.contains("win") ? "\\" : "/");
    }

    public static HashMap<String, String> GetVariables() {
        return variables;
    }

    public static boolean IsWindows() {
        return OS.contains("win");
    }

    public static boolean IsMac() {
        return OS.contains("mac");
    }

    public static boolean IsUnix() {
        return (OS.contains("nix")) || (OS.contains("nux")) || (OS.contains("aix"));
    }

    public static boolean IsSolaris() {
        return OS.contains("sunos");
    }

    public static String GetNewLine() {
        return IsWindows() ? "\r\n" : "\n";
    }

    public static String GetDirSeparator() {
        return IsWindows() ? "\\" : "/";
    }

    public static Ini GetConfiguration() throws IOException {
        File iniFile = new File(defaultFilename);
        if (!iniFile.exists()) {
            boolean result = iniFile.createNewFile();
            if (result) {
                LogManager.GetLogger(Grandeur.class).Info(iniFile.getName() + " has been created!");
            }
        }

        if (configuration == null) {
            configuration = Ini.Load(defaultFilename);
        }

        return configuration;
    }
}
