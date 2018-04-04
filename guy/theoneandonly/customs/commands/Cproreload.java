package guy.theoneandonly.customs.commands;

import guy.theoneandonly.customs.Customs;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class Cproreload implements CommandExecutor {

    Customs plugin;

    public Cproreload(Customs instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        if (cmd.getName().equalsIgnoreCase("cproreload")) {
            if (!(sender instanceof Player)) {
                plugin.reloadConfig();
                PluginManager plm = Bukkit.getPluginManager();
                Plugin pin = plm.getPlugin("Customs");
                plm.disablePlugin(pin);
                plm.enablePlugin(pin);
                System.out.println("Customs reloaded.");
                return true;
            }
        }
        return false;
    }
}
