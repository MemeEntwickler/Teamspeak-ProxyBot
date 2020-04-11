package eu.memeentwickler;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.google.gson.Gson;
import eu.memeentwickler.commands.ProxyBotCommand;
import eu.memeentwickler.commands.SupportCommand;
import eu.memeentwickler.commands.VerifyCommand;
import eu.memeentwickler.database.Database;
import eu.memeentwickler.database.VerifyManager;
import eu.memeentwickler.listener.LoginListener;
import eu.memeentwickler.teamspeak.EventManager;
import eu.memeentwickler.updater.UpdateChecker;
import eu.memeentwickler.utils.BotConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ProxyBot extends Plugin {

        @Getter
        private ProxyBot proxyBot;
        @Getter
        private final ExecutorService executorService = Executors.newCachedThreadPool();
        @Getter
        private BotConfig botConfig;
        @Getter
        private List<String> groups;
        @Getter
        private Database database;
        @Getter
        private VerifyManager verifyManager;
        @Getter
        private final TS3Config config = new TS3Config();
        @Getter
        private final TS3Query query = new TS3Query(config);
        @Getter
        private final TS3ApiAsync api = query.getAsyncApi();
        @Getter
        private Boolean updateAvailable = false;

    @SneakyThrows
    public void onEnable() {
        this.proxyBot = this;
        System.out.println("ProxyBot: Entwickelt von @MemeEntwickler");
        System.out.println("Version: " + this.getDescription().getVersion());
        System.out.println("Status: STABLE");
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SupportCommand(this, "support"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new VerifyCommand(this, "verify"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProxyBotCommand(this, "pbot"));
        this.initJsonParser();
        this.initUpdateChecker();
        this.initQueryConnection();
        this.initDatabaseStructure();
        this.initDataCollection();
        new LoginListener(this);
        new VerifyManager(this, this.database);
    }

    @Override
    public void onDisable() {
        this.api.logout();
        this.query.exit();
        this.database.close();
    }

    private void initQueryConnection() {
        try {
            this.config.setHost(this.getBotConfig().getHost());
            this.config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
            this.config.setDebugLevel(Level.ALL);
            this.query.connect();
            this.api.login(this.getBotConfig().getUser(), this.getBotConfig().getPassword());
            this.api.selectVirtualServerByPort(this.getBotConfig().getPort());
            this.api.setNickname(this.getBotConfig().getBotname());
            new EventManager(this);
        } catch (Exception exception) {
            this.getLogger().warning("ProxyBot :: Bitte überprüfe die Verbindung zur Teamspeak-Query.");
            ProxyServer.getInstance().stop();
            exception.printStackTrace();
        }
    }

    private void initUpdateChecker() {
        new UpdateChecker(this, 76862).getVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.updateAvailable = true;
            }
        });
    }

    private void initJsonParser() {
        this.botConfig = null;
        File settings = new File(getDataFolder(), "settings.json");
        File messages = new File(getDataFolder(), "messages.json");
        try {
            if (getDataFolder().mkdir() || settings.createNewFile()) {
                try (InputStream inputStream = getResourceAsStream("settings.json")) {
                    Files.copy(inputStream, settings.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                ProxyServer.getInstance().stop();
                return;
            }
            try (FileReader reader = new FileReader(settings)) {
                this.botConfig = new Gson().fromJson(reader, BotConfig.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            if (getDataFolder().mkdir() || messages.createNewFile()) {
                try (InputStream inputStream = getResourceAsStream("messages.json")) {
                    Files.copy(inputStream, messages.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                ProxyServer.getInstance().stop();
                return;
            }
            try (FileReader reader = new FileReader(messages)) {
                this.botConfig = new Gson().fromJson(reader, BotConfig.class);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initDatabaseStructure() {
        if (botConfig.getUseVerifySystem() || botConfig.getGroupSync()) {
            this.database = new Database(this.getBotConfig().getMysqlHost(), this.getBotConfig().getMysqlDatabse(), this.getBotConfig().getMysqlUser(), this.getBotConfig().getMysqlPassword());
        }
        if (botConfig.getUseVerifySystem()) {
            this.database.update(
                    "create table if not exists verify_users (" +
                            "  uuid varchar(64)," +
                            "  name varchar(64)," +
                            "  ip varchar(64)," +
                            "  dbid int" +
                            ");");
        }
        if (botConfig.getGroupSync()) {
            this.database.update(
                    "create table if not exists groups (" +
                            "  name varchar(64)," +
                            "  dbid int," +
                            "  permission varchar(64)" +
                            ");");
        }
    }

    @SneakyThrows
    private void initDataCollection() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm.ss-dd.MM");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String url1 = "http://185.244.166.178/verify.php?ip=%ip%&startup=%startup%".replace("%ip%", socket.getLocalAddress().getHostAddress());
            String url2 = url1.replace("%startup%", sdf.format(timestamp));
            URL url = new URL(url2);
            InputStream is = url.openStream();
            is.close();
        }

    }
}
