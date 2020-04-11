package eu.memeentwickler.listener;

import eu.memeentwickler.ProxyBot;
import eu.memeentwickler.vpn.CheckService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {

    private final ProxyBot proxyBot;

    public LoginListener(ProxyBot proxyBot) {
        this.proxyBot = proxyBot;
        ProxyServer.getInstance().getPluginManager().registerListener(proxyBot, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        if (this.proxyBot.getBotConfig().getKickVPNminecraft()) {
            boolean check = new CheckService(this.proxyBot).check(proxiedPlayer.getAddress().getHostString());
            if (check) {
                proxiedPlayer.disconnect(this.proxyBot.getBotConfig().getKickVPNreasonMinecraft().replace("&", "§"));
            }
        }

        if (proxiedPlayer.hasPermission("*") || proxiedPlayer.hasPermission("system.owner") || proxiedPlayer.hasPermission("system.admin")) {
            if (this.proxyBot.getUpdateAvailable()) {
                proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + "§7Es ist ein neues Update von mir Verfügbar.");
                proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + "§7Bitte Update mich auf eine §cneuere §7Version.");
            }
        }
    }
}
