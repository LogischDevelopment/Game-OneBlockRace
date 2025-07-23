package tv.logisch.oneBlockRace;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.WorldCreator;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.logisch.api.LogiAPI;
import tv.logisch.oneBlockRace.commands.EventCommand;
import tv.logisch.oneBlockRace.commands.completions.EventCompletion;
import tv.logisch.oneBlockRace.gui.SettingGUIListener;
import tv.logisch.oneBlockRace.listener.*;
import tv.logisch.oneBlockRace.objects.GameConfig;
import tv.logisch.oneBlockRace.utils.Config;

import java.io.File;
import java.util.logging.Logger;

@Getter
@Accessors(fluent = true)
public final class OneBlockRace extends JavaPlugin {

    @Getter @Accessors(fluent = true)
    private static OneBlockRace instance;

    private Logger logger;
    private GameConfig gameConfig;
    private LogiAPI logiAPI;

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();
    }

    @Override
    public void onEnable() {

        this.logiAPI = new LogiAPI(new Config(new File(Bukkit.getPluginsFolder().getPath()+"/obr/config.json")).get("logisch.api.key").getAsString());
        this.gameConfig = new GameConfig().initialize();

        System.setProperty("LOGISCH_TYPE", "GAME");
        String hostUUID = gameConfig.hostUUID() != null ? gameConfig.hostUUID().toString() : "null";
        String hostName = gameConfig.hostName() != null ? gameConfig.hostName() : "null";
        System.setProperty("LOGISCH_FLAGS", "host="+hostUUID+";hostName="+hostName+";sendJoinMe=true;retrieveJoinMe=false");

        logger.info("OneBlockRace is being enabled.");

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new QuitListener(), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new SettingGUIListener(), this);
        pm.registerEvents(new VehicleMoveListener(), this);
        pm.registerEvents(new PlayerBucketEmptyListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
        pm.registerEvents(new EntityPlaceListener(), this);
        pm.registerEvents(new PlayerLoginListener(), this);
        pm.registerEvents(new BlockRedstoneListener(), this);
        pm.registerEvents(new BlockPhysicsListener(), this);
        pm.registerEvents(new BlockGrowListener(), this);

        PluginCommand event = getCommand("event");
        event.setExecutor(new EventCommand());
        event.setTabCompleter(new EventCompletion());

        Bukkit.createWorld(new WorldCreator("world"));

        Bukkit.getWorlds().forEach(w -> {
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        });

    }

    @Override
    public void onDisable() {
        logger.info("OneBlockRace is being disabled.");
    }
}
