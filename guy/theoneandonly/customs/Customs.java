package guy.theoneandonly.customs;

import guy.theoneandonly.customs.commands.Cpro;
import guy.theoneandonly.customs.commands.Cpa;
import guy.theoneandonly.customs.commands.Cpd;
import guy.theoneandonly.customs.commands.Cproreload;
import guy.theoneandonly.customs.handlers.PlayerHandler;
import guy.theoneandonly.customs.handlers.ScoreBoardHandler;
import guy.theoneandonly.customs.handlers.pluginListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Customs extends JavaPlugin implements Listener {

    public ScoreBoardHandler sbh;
    public PlayerHandler ph;
    public pluginListener pl = new pluginListener(this);

    @Override
    public void onEnable() {
        try {
            new Config(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ph = new PlayerHandler(this);
        sbh = new ScoreBoardHandler(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(pl, this);
        getCommand("cpro").setExecutor(new Cpro(this));
        getCommand("cpa").setExecutor(new Cpa(this));
        getCommand("cproreload").setExecutor(new Cproreload(this));
        getCommand("cpd").setExecutor(new Cpd(this));

        //Code for handling reloads.
        for (int i = 0; i < getServer().getWorlds().size(); i++) {
            if (getServer().getWorlds().get(i).getPlayers().isEmpty()) {
                System.out.println("Server is empty");
            } else {
                for (int p = 0; p < getServer().getWorlds().get(i).getPlayers().size(); p++) {
                    Player playerTemp = getServer().getWorlds().get(i).getPlayers().get(p);
                    ph.addDB(playerTemp.getName());
                    ph.setShowingExpGains(playerTemp.getName(), true);
                    sbh.addScoreBoard(playerTemp);
                    System.out.println("Server is not empty" + getServer().getWorlds().get(i).getPlayers().get(p).getName());
                }
            }
        }
    }

    @Override
    public void onDisable() {

        for (int i = 0; i < getServer().getWorlds().size(); i++) {
            if (getServer().getWorlds().get(i).getPlayers().isEmpty()) {
                System.out.println("Server is empty");
            } else {
                for (int p = 0; p < getServer().getWorlds().get(i).getPlayers().size(); p++) {
                    Player playerTemp = getServer().getWorlds().get(i).getPlayers().get(p);
                    ph.removeDB(playerTemp.getName());
                    sbh.removeScoreBoard(playerTemp);
                    sbh.removeBoardShowing(playerTemp.getName());
                    System.out.println("Server was not empty" + getServer().getWorlds().get(i).getPlayers().get(p).getName());
                }
            }
        }
    }
}
