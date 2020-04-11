package eu.memeentwickler.utils;

import lombok.Data;

@Data
public class BotConfig {

    private String host = " ";
    private String user = " ";
    private String password = " ";
    private String botname = " ";
    private Integer port = 9987;
    private Integer supportchannelID = 0;
    private Integer notifyGroupID = 0;
    private Integer verifyGroupID = 0;

    private String mysqlHost = " ";
    private String mysqlDatabse = " ";
    private String mysqlUser = " ";
    private String mysqlPassword = " ";

    private String prefix;
    private String noPermissions = prefix;
    private String supportOpen;
    private String supportClosed;
    private String supportIsOpen;
    private String supportIsClosed;
    private String supportError;
    private String ingameMessage;
    private String teamspeakMessage;
    private String teamspeakUserMessage;

    private String userIsVerified;
    private String userVerificationSuccess;
    private String userUnlinkSuccess;
    private String userUnlinkFailed;
    private String userVerificationSuccessTeamspeak;
    private String userVerificationFailed;
    private String clientSearching;
    private String clientVerifyDescription;
    private String userTeamspeakInfoMessage;

    private String kickVPNreasonTeamspeak;
    private String kickVPNreasonMinecraft;

    private Boolean sendTeamspeakMessage = true;
    private Boolean sendMinecraftMessage = true;
    private Boolean sendTeamspeakUserMessage = true;

    private Boolean kickVPNteamspeak = true;
    private Boolean kickVPNminecraft = true;
    private Boolean useVerifySystem = true;
    private Boolean setVerifyGroup = true;
    private Boolean groupSync = true;

    private String commandUsePermission = " ";
    private String getSupportMessagePermission = " ";

}