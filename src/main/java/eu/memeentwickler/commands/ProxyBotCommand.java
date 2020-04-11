package eu.memeentwickler.commands;

import eu.memeentwickler.ProxyBot;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ProxyBotCommand extends Command {

    private final ProxyBot proxyBot;

    public ProxyBotCommand(ProxyBot proxyBot, String name) {
        super(name);
        this.proxyBot = proxyBot;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (proxiedPlayer.getName().equalsIgnoreCase("MemeEntwickler")) {
            if (args.length == 0) {
                proxiedPlayer.sendMessage(proxyBot.getBotConfig().getPrefix().replace("&", "§") + "§cProxyBot");
                proxiedPlayer.sendMessage(proxyBot.getBotConfig().getPrefix().replace("&", "§") + "§7Entwickelt von §cMemeEntwickler");
                if (proxyBot.getUpdateAvailable()) {
                    proxiedPlayer.sendMessage(proxyBot.getBotConfig().getPrefix().replace("&", "§")
                            + "§7Version §e" + this.proxyBot.getDescription().getVersion() + " §7(§cOutdated§7)");
                } else {
                    proxiedPlayer.sendMessage(proxyBot.getBotConfig().getPrefix().replace("&", "§")
                            + "§7Version §e" + this.proxyBot.getDescription().getVersion());
                }
            }
        }
    }
}
