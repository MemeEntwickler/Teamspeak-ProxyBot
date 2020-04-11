package eu.memeentwickler.commands;

import eu.memeentwickler.ProxyBot;
import eu.memeentwickler.database.VerifyManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class VerifyCommand extends Command {

    private final ProxyBot proxyBot;

    public VerifyCommand(ProxyBot proxyBot, String name) {
        super(name);
        this.proxyBot = proxyBot;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (proxyBot.getBotConfig().getUseVerifySystem()) {
            if (args.length == 0) {
                this.proxyBot.getProxyBot().getExecutorService().submit(() -> new VerifyManager(proxyBot, proxyBot.getDatabase()).registerPlayer(proxiedPlayer));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("delete")) {
                    this.proxyBot.getProxyBot().getExecutorService().submit(() -> new VerifyManager(proxyBot, proxyBot.getDatabase()).unregisterPlayer(proxiedPlayer));
                } else {
                    proxiedPlayer.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + "§c/verify delete");
                }
            } else {
                proxiedPlayer.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + "§c/verify");
            }
        }
    }
}
