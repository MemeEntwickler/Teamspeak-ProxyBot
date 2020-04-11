package eu.memeentwickler.commands;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import eu.memeentwickler.ProxyBot;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;

public class SupportCommand extends Command {

    private final ProxyBot proxyBot;

    public SupportCommand(ProxyBot proxyBot, String name) {
        super(name);
        this.proxyBot = proxyBot;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (player.hasPermission(proxyBot.getBotConfig().getCommandUsePermission())) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("open")) {
                    this.proxyBot.getProxyBot().getExecutorService().submit(() -> {
                        if (this.proxyBot.getProxyBot().getApi().getChannelInfo(this.proxyBot.getProxyBot().getBotConfig().getSupportchannelID()).getUninterruptibly().getMaxClients() == 0) {
                            Map<ChannelProperty, String> channelSettings = new HashMap<>();
                            channelSettings.put(ChannelProperty.CHANNEL_MAXCLIENTS, "5");
                            this.proxyBot.getProxyBot().getProxyBot().getApi().editChannel(this.proxyBot.getProxyBot().getBotConfig().getSupportchannelID(), channelSettings);
                            player.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getProxyBot().getBotConfig().getSupportOpen().replace("&", "§"));
                        } else {
                            player.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getProxyBot().getBotConfig().getSupportIsOpen().replace("&", "§"));
                        }
                    });
                }
                if (args[0].equalsIgnoreCase("close")) {
                    this.proxyBot.getProxyBot().getExecutorService().submit(() -> {
                        if (this.proxyBot.getProxyBot().getApi().getChannelInfo(this.proxyBot.getProxyBot().getBotConfig().getSupportchannelID()).getUninterruptibly().getMaxClients() != 0) {
                            Map<ChannelProperty, String> channelSettings = new HashMap<>();
                            channelSettings.put(ChannelProperty.CHANNEL_MAXCLIENTS, "0");
                            this.proxyBot.getProxyBot().getApi().editChannel(proxyBot.getProxyBot().getBotConfig().getSupportchannelID(), channelSettings);
                            player.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getProxyBot().getBotConfig().getSupportClosed().replace("&", "§"));
                        } else {
                            player.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getProxyBot().getBotConfig().getSupportIsClosed().replace("&", "§"));
                        }
                    });
                }
            } else {
                player.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + "§c/support (open/close)");
            }
        } else {
            player.sendMessage(this.proxyBot.getProxyBot().getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getProxyBot().getBotConfig().getNoPermissions().replace("&", "§"));
        }
    }
}
