package org.jel.game.utils;

import org.jel.game.data.Shared;
import org.jel.game.data.Version;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class Updater {

    private static final Updater INSTANCE = new Updater();

    public static void update() {
        if (!INSTANCE.hasToUpdate()) {
            return;
        }
        try {
            INSTANCE.downloadFile(new URL("https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/conquer-data/" + (System.getProperty("os.name").toLowerCase().contains("win1") ? "windows" : "linux") + ".zip"), Shared.BASE_DIRECTORY + "updates" + System.getProperty("file.separator") + "data.zip");

        } catch (IOException e) {
            Shared.LOGGER.exception(e);
        }
    }

    private boolean hasToUpdate() {
        try {
            var urlVersion = new URL("https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/.conquer-version." + (System.getProperty("os.name").toLowerCase().contains("win") ? "windows" : "linux"));
            var data = new BufferedReader(new InputStreamReader(urlVersion.openStream())).readLine().split("\\.");
            var latestVersion = new Version(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            if (latestVersion.compareTo(Shared.getReferenceImplementationVersion()) == 1) {
                return true;
            }
        } catch (IOException e) {
            Shared.LOGGER.exception(e);
        }
        return false;
    }

    private void downloadFile(URL url, String directory) throws IOException {
        var file = new File(directory);
        if (!file.exists()) {
            Files.createDirectories(file.toPath().getParent());
        }
        url.openStream().transferTo(new FileOutputStream(directory));
    }
}