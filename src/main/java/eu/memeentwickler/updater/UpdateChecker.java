package eu.memeentwickler.updater;

import eu.memeentwickler.ProxyBot;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final ProxyBot proxyBot;
    private final int resourceID;

    public UpdateChecker(ProxyBot proxyBot, int resourceID) {
        this.proxyBot = proxyBot;
        this.resourceID = resourceID;
    }

    public void getVersion(final Consumer<String> consumer) {
        ProxyServer.getInstance().getScheduler().runAsync(this.proxyBot, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceID).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.proxyBot.getLogger().info("ProxyBot :: Updates nicht Abrufbar: " + exception.getMessage());
            }
        });
    }
}
