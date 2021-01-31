package org.jel.game.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jel.game.data.Shared;
import org.jel.game.data.Version;

public class Updater {

    private static final Updater INSTANCE = new Updater();
    private Version latestVersion;

    public static void update(Function<Boolean, Boolean> callback, Consumer<Boolean> downloadFinished) {
        if (callback.apply(INSTANCE.hasToUpdate())) {
            try {
                INSTANCE.downloadFile(new URL("https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/conquer-data/windows.zip"), Shared.BASE_DIRECTORY + "updates" + System.getProperty("file.separator") + "data.zip");
                INSTANCE.downloadFile(new URL("https://github.com/JCWasmx86/Conquer/releases/download/" + INSTANCE.latestVersion.toString() + "/Installer.exe"), Shared.BASE_DIRECTORY + "updates" + System.getProperty("file.separator") + "Installer.exe");
                downloadFinished.accept(true);
            } catch (IOException e) {
                Shared.LOGGER.exception(e);
                downloadFinished.accept(false);
            }
        }
    }

    private boolean hasToUpdate() {
        try {
            var urlVersion = new URL("https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/.conquer-version.windows");
            var data = new BufferedReader(new InputStreamReader(urlVersion.openStream())).readLine().split("\\.");
            latestVersion = new Version(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            if (latestVersion.compareTo(Shared.getReferenceImplementationVersion()) == 1) {
                return true;
            }
        } catch (IOException e) {
            Shared.LOGGER.exception(e);
        }
        return true; // TODO: Debug
    }

    private void downloadFile(URL url, String directory) throws IOException {
        var file = new File(directory);
        if (!file.exists()) {
            Files.createDirectories(file.toPath().getParent());
        }
        url.openStream().transferTo(new FileOutputStream(directory));
    }
}