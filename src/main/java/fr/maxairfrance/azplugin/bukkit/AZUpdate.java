package fr.maxairfrance.azplugin.bukkit;

import lombok.Getter;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class AZUpdate {
    private final URL checkURL;
    @Getter
    private final String thisVersion;
    @Getter
    private String newVersion;

    public AZUpdate(AZPlugin main, Integer pluginId) {
        this.thisVersion = main.getPluginVersion();
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + pluginId);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkForUpdate() {
        try {
            URLConnection con = this.checkURL.openConnection();
            this.newVersion = "v" + (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
            if (compareVersion(this.thisVersion, this.newVersion) < 0)
                return true;
            return false;
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int compareVersion(String version1, String version2) {
        String[] arr1 = version1.replace("v", "").split("\\.");
        String[] arr2 = version2.replace("v", "").split("\\.");
        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i]))
                    return -1;
                if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i]))
                    return 1;
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0)
                    return 1;
            } else if (Integer.parseInt(arr2[i]) != 0) {
                return -1;
            }
            i++;
        }
        return 0;
    }
}
