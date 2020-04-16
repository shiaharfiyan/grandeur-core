package io.github.shiaharfiyan.utils;

import io.github.shiaharfiyan.Grandeur;
import io.github.shiaharfiyan.configuration.Ini;
import io.github.shiaharfiyan.logging.LogManager;
import io.github.shiaharfiyan.utils.helpers.DateTimeHelper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

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
