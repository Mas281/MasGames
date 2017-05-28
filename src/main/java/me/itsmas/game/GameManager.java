package me.itsmas.game;

import me.itsmas.game.game.Game;
import me.itsmas.game.game.GameType;
import me.itsmas.game.game.LoginListener;
import me.itsmas.game.map.MapManager;
import me.itsmas.game.util.C;
import me.itsmas.game.util.Utils;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the 'MasGames' API
 */
public class GameManager extends JavaPlugin
{
    /**
     * The MapManager instance
     */
    private MapManager mapManager;

    /**
     * Gets the MapManager instance
     * @return The map manager
     */
    public MapManager getMapManager()
    {
        return mapManager;
    }

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        mapManager = new MapManager(this);

        initGame();

        Utils.register(new LoginListener(this));
    }

    /**
     * Initiates the game by getting a new game instance
     */
    private void initGame()
    {
        GameType type = EnumUtils.getEnum(GameType.class, getConfig().getString("game", GameType.values()[0].name()));

        if (type != null)
        {
            Game gameInst = type.newInstance(this);

            if (gameInst != null)
            {
                game = gameInst;

                Utils.log("Loading game of type " + type.toString() + " [" + type.name() + "]");
                return;
            }

            Utils.logErr("Game failed to load: Error creating game instance");
            return;
        }

        Utils.logErr("Game failed to load: Config game setting not valid");
    }

    @Override
    public void onDisable()
    {
        if (game != null)
        {
            game.unloadWorld();
            game = null;
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(C.RED + "Server is restarting"));
    }

    /**
     * Restarts the server
     */
    public void restart()
    {
        getServer().spigot().restart();
    }

    /**
     * The ongoing game
     */
    private Game game;

    /**
     * Gets the current ongoing game
     * @see #game
     * @return The current game
     */
    public Game getGame()
    {
        return game;
    }
}
