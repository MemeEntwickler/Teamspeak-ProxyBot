package eu.memeentwickler.teamspeak;

import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import eu.memeentwickler.ProxyBot;
import eu.memeentwickler.database.VerifyManager;
import eu.memeentwickler.vpn.CheckService;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;

import java.util.Collections;

public class EventManager {

    private final ProxyBot proxyBot;

    public EventManager(ProxyBot proxyBot) {
        this.proxyBot = proxyBot;
        this.loadEvent();
    }

    @SneakyThrows
    public void loadEvent() {

        this.proxyBot.getApi().registerAllEvents();
        this.proxyBot.getApi().addTS3Listeners(new TS3Listener() {

            public void onTextMessage(TextMessageEvent e) {
            }

            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent arg0) {
            }

            @SneakyThrows
            public void onClientMoved(ClientMovedEvent clientMovedEvent) {
                final ClientInfo clientInfo = proxyBot.getApi().getClientInfo(clientMovedEvent.getClientId()).getUninterruptibly();

                if (clientMovedEvent.getTargetChannelId() == proxyBot.getBotConfig().getSupportchannelID()) {
                    if (proxyBot.getProxyBot().getBotConfig().getSendTeamspeakMessage()) {
                        int count = 0;
                        for (Client team : proxyBot.getApi().getClients().getUninterruptibly()) {
                            if (team.isInServerGroup(proxyBot.getBotConfig().getNotifyGroupID())) {
                                proxyBot.getApi().sendPrivateMessage(team.getId(), proxyBot.getBotConfig().getTeamspeakMessage().replace("%user%", clientInfo.getNickname()));
                                count++;
                            }
                        }
                        if (proxyBot.getBotConfig().getSendTeamspeakUserMessage()) {
                            proxyBot.getApi().sendPrivateMessage(clientInfo.getId(), proxyBot.getBotConfig().getTeamspeakUserMessage().replace("%sups%", String.valueOf(count)));
                        }
                    }
                    if (proxyBot.getBotConfig().getSendMinecraftMessage()) {
                        ProxyServer.getInstance().getPlayers().forEach(player -> {
                            if (player.hasPermission(proxyBot.getBotConfig().getGetSupportMessagePermission())) {
                                player.sendMessage(proxyBot.getBotConfig().getPrefix().replace("&", "§") + proxyBot.getBotConfig().getIngameMessage().replace("%player%", clientInfo.getNickname().replace("&", "§")));
                            }
                        });
                    }
                }
            }

            @Override
            public void onClientJoin(ClientJoinEvent clientJoinEvent) {
                final ClientInfo clientInfo = proxyBot.getApi().getClientInfo(clientJoinEvent.getClientId()).getUninterruptibly();

                if (proxyBot.getBotConfig().getUseVerifySystem()) {
                    proxyBot.getApi().sendPrivateMessage(clientInfo.getId(), proxyBot.getBotConfig().getUserTeamspeakInfoMessage());

                    if (!new VerifyManager(proxyBot, proxyBot.getDatabase()).userIsRegistered(new VerifyManager(proxyBot, proxyBot.getDatabase()).getUUIDbyDatabseId(clientInfo))) {
                        proxyBot.getApi().editClient(clientInfo.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, "Nicht Verifiziert"));
                        if (clientInfo.isInServerGroup(proxyBot.getBotConfig().getVerifyGroupID())) {
                            proxyBot.getApi().removeClientFromServerGroup(proxyBot.getBotConfig().getVerifyGroupID(), clientInfo.getDatabaseId());
                        }
                    }
                }

                if (proxyBot.getBotConfig().getKickVPNteamspeak()) {
                    new CheckService(proxyBot).check(clientInfo.getIp());
                }

            }

            @Override
            public void onClientLeave(ClientLeaveEvent clientLeaveEvent) {
            }

            @Override
            public void onServerEdit(ServerEditedEvent serverEditedEvent) {
            }

            public void onChannelPasswordChanged(ChannelPasswordChangedEvent arg0) {
            }

            public void onChannelMoved(ChannelMovedEvent arg0) {
            }

            public void onChannelEdit(ChannelEditedEvent arg0) {
            }

            public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent arg0) {
            }

            public void onChannelDeleted(ChannelDeletedEvent arg0) {
            }

            public void onChannelCreate(ChannelCreateEvent arg0) {
            }
        });
    }
}
