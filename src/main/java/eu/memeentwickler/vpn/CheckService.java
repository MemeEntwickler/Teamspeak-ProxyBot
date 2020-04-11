package eu.memeentwickler.vpn;

import eu.memeentwickler.ProxyBot;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class CheckService {

    private final ProxyBot proxyBot;

    public CheckService(ProxyBot proxyBot) {
        this.proxyBot = proxyBot;
    }

    @SneakyThrows
    public boolean check(String address) {
        URL url = new URL(String.format("http://check.getipintel.net/check.php?ip=%s&contact=check@proxybot.de", address));
        InputStream is = url.openStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            if (line.equalsIgnoreCase("1")) {
                is.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        is.close();
        return false;
    }

}
