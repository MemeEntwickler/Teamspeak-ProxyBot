package eu.memeentwickler.database;

import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import eu.memeentwickler.ProxyBot;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class VerifyManager {

    private final ProxyBot proxyBot;
    private final Database database;

    public VerifyManager(ProxyBot proxyBot, Database database) {
        this.proxyBot = proxyBot;
        this.database = database;
    }

    @SneakyThrows
    public boolean userIsRegistered(String uuid) {
        ResultSet resultSet = this.database.query("select * from verify_users where uuid= '" + uuid + "'");
        if (resultSet.next()) {
            String playerUUID = resultSet.getString("uuid");
            this.database.closeResultSet(resultSet);
            return playerUUID != null;

        }
        return false;
    }

    public void registerPlayer(ProxiedPlayer proxiedPlayer) {
        boolean result = false;
        if (userIsRegistered(proxiedPlayer.getUniqueId().toString())) {
            proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserIsVerified().replace("&", "§"));
            return;
        }

        proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getClientSearching().replace("&", "§"));

        for (Client client : this.proxyBot.getApi().getClients().getUninterruptibly()) {
            if (client.getIp().equalsIgnoreCase(proxiedPlayer.getAddress().getHostString())) {
                try {
                    this.database.update("insert into verify_users (uuid, name, ip, dbid) values ('"
                            + proxiedPlayer.getUniqueId().toString() + "', '"
                            + proxiedPlayer.getName() + "', '"
                            + proxiedPlayer.getAddress().getHostString() + "', '" +
                            +client.getDatabaseId() + "')");
                } catch (Exception e1) {
                    this.proxyBot.getLogger().warning("VerifySQLException :: " + e1.getMessage());
                    e1.printStackTrace();
                }

                try {
                    proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserVerificationSuccess().replace("%nickname%", client.getNickname()).replace("&", "§"));
                    this.proxyBot.getApi().sendPrivateMessage(client.getId(), this.proxyBot.getBotConfig().getUserVerificationSuccessTeamspeak().replace("%playername%", proxiedPlayer.getName()));
                    this.proxyBot.getApi().editClient(client.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, this.proxyBot.getBotConfig().getClientVerifyDescription().replace("%playername%", proxiedPlayer.getName()).replace("%playeruuid%", proxiedPlayer.getUniqueId().toString())));
                    if (this.proxyBot.getBotConfig().getSetVerifyGroup()) {
                        this.proxyBot.getApi().addClientToServerGroup(this.proxyBot.getBotConfig().getVerifyGroupID(), client.getDatabaseId());
                    }
                    result = true;
                } catch (Exception e2) {
                    this.proxyBot.getLogger().warning("VerifyException :: " + e2.getMessage());
                    proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserVerificationFailed().replace("&", "§"));
                }
            }
            // TODO
        }
        if (!result) {
            proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserVerificationFailed().replace("&", "§"));
        }
    }

    public void unregisterPlayer(ProxiedPlayer proxiedPlayer) {
        boolean result = false;
        if (!userIsRegistered(proxiedPlayer.getUniqueId().toString())) {
            proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserUnlinkFailed().replace("&", "§"));
            return;
        }
        for (Client client : this.proxyBot.getApi().getClients().getUninterruptibly()) {
            if (client.getIp().equalsIgnoreCase(proxiedPlayer.getAddress().getHostString())) {
                for (ServerGroup serverGroup : this.proxyBot.getApi().getServerGroupsByClientId(this.getClientDatabaseId(proxiedPlayer)).getUninterruptibly()) {
                    if (serverGroup.getId() == this.proxyBot.getBotConfig().getVerifyGroupID()) {
                        this.proxyBot.getApi().removeClientFromServerGroup(serverGroup.getId(), client.getDatabaseId());
                    }
                }
                this.proxyBot.getApi().editClient(client.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, "Nicht Verifiziert"));
                proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserUnlinkSuccess().replace("&", "§"));
                this.deleteFromDatabase(proxiedPlayer.getUniqueId().toString());
                result = true;
            }
        }
        if (!result) {
            this.deleteFromDatabase(proxiedPlayer.getUniqueId().toString());
            proxiedPlayer.sendMessage(this.proxyBot.getBotConfig().getPrefix().replace("&", "§") + this.proxyBot.getBotConfig().getUserUnlinkSuccess().replace("&", "§"));
        }
    }

    public String getUUIDbyDatabseId(Client client) {
        ResultSet resultSet = this.database.query("select * from verify_users where dbid= '" + client.getDatabaseId() + "'");
        try {
            if (resultSet.next()) {
                String playerUUID = resultSet.getString("uuid");
                this.database.closeResultSet(resultSet);
                return playerUUID;
            }
        } catch (SQLException ignored) {
        }
        this.database.closeResultSet(resultSet);
        return null;
    }

    private Integer getClientDatabaseId(ProxiedPlayer proxiedPlayer) {
        ResultSet resultSet = this.database.query("select * from verify_users where uuid= '" + proxiedPlayer.getUniqueId().toString() + "'");
        try {
            if (resultSet.next()) {
                Integer id = resultSet.getInt("dbid");
                this.database.closeResultSet(resultSet);
                return id;
            }
        } catch (SQLException ignored) {
        }
        this.database.closeResultSet(resultSet);
        return null;
    }

    private void deleteFromDatabase(String uuid) {
        this.database.update("delete from verify_users where uuid= '" + uuid + "'");
    }

}
