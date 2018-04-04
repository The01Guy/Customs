package guy.theoneandonly.customs.handlers;

import guy.theoneandonly.customs.Customs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class pluginListener implements Listener {
    
    Customs plugin;
    
    public pluginListener(Customs instance){
        plugin = instance;
    }
    
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        plugin.ph.onlinePlayers.add(event.getPlayer());
        plugin.ph.addDB(event.getPlayer().getName());
        plugin.ph.setShowingExpGains(event.getPlayer().getName(), false);
        plugin.sbh.addScoreBoard(event.getPlayer());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        plugin.ph.onlinePlayers.remove(event.getPlayer());
        plugin.ph.removeDB(event.getPlayer().getName());
        plugin.sbh.removeBoardShowing(event.getPlayer().getName());
        plugin.sbh.removeScoreBoard(event.getPlayer());
    }
}
