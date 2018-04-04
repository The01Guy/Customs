package guy.theoneandonly.customs.commands;

import guy.theoneandonly.customs.Customs;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cpa implements CommandExecutor {

    Customs plugin;

    public Cpa(Customs instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        if (!(sender instanceof Player)) {
            if (commandLabel.equalsIgnoreCase("cpa")) {
                if (args.length < 3) {
                    System.out.println("Not enough paramaters. Useage 'cpa <PlayerName> <Skill> <EXP>'");
                } else if (args.length == 3) {
                    if (plugin.sbh.professions.containsKey(args[1])) {
                        Player player = null;
                        if (Bukkit.getPlayer(args[0]).isOnline()) {
                            player = Bukkit.getPlayer(args[0]);
                        } else {
                            String playerName = args[0];
                            for (String e : plugin.ph.playerDB.keySet()) {
                                if (playerName.contains(e)) {
                                    player = Bukkit.getPlayer(e);
                                    continue;
                                }
                            }
                        }
                        if (player != null) {
                            int playerSkillLevel = plugin.sbh.getLevel(player.getName(), args[1]);
                            int skillMaxLevel = plugin.sbh.getMaxProfLvl(args[1]);
                            int xpNeededToLevel = plugin.sbh.getExpNeeded(args[1], (playerSkillLevel + 1));
                            int currentExp = plugin.sbh.getCurrentExp(player.getName(), args[1]);
                            int expGained = Integer.parseInt(args[2]);
                            int remainingXP;
                            if (playerSkillLevel >= skillMaxLevel) {//if players level is the max level do nothing.
                                remainingXP = 0;
                                xpNeededToLevel = 0;
                            } else if ((expGained + currentExp) >= xpNeededToLevel) { //if new exp is greater then or equal to expNeededToLvl. Handle level gain.
                                playerSkillLevel++; //increase the level.
                                remainingXP = (expGained + currentExp) - xpNeededToLevel;//Get the exp left over exp.
                                xpNeededToLevel = plugin.sbh.getExpNeeded(args[1], (playerSkillLevel + 1));
                                if (playerSkillLevel == skillMaxLevel) {
                                    remainingXP = 0;
                                    xpNeededToLevel = 0;
                                }
                                if (plugin.ph.showingExpGains.get(player.getName()) && plugin.getConfig().getConfigurationSection(args[1]).getBoolean("visible")) {
                                    player.sendMessage(ChatColor.AQUA + "You've received " + ChatColor.GOLD + args[2] + ChatColor.AQUA + " in " + ChatColor.GREEN + args[1] + ChatColor.AQUA + ". You are now level " + ChatColor.YELLOW + playerSkillLevel + ChatColor.AQUA + ".");
                                }
                                List<String> commands = plugin.getConfig().getStringList(args[1] + ".custom.level " + playerSkillLevel + ".up_commands");
                                commandExecutor(player, commands);

                                while (remainingXP > xpNeededToLevel) {
                                    playerSkillLevel++;
                                    remainingXP = remainingXP - xpNeededToLevel;
                                    xpNeededToLevel = plugin.sbh.getExpNeeded(args[1], (playerSkillLevel + 1));
                                    if (playerSkillLevel == skillMaxLevel) {
                                        remainingXP = 0;
                                        xpNeededToLevel = 0;
                                    }
                                    if (plugin.ph.showingExpGains.get(player.getName()) && plugin.getConfig().getConfigurationSection(args[1]).getBoolean("visible")) {
                                        player.sendMessage(ChatColor.AQUA + "You are now level " + ChatColor.YELLOW + playerSkillLevel + ChatColor.AQUA + ".");
                                    }
                                    List<String> commands2 = plugin.getConfig().getStringList(args[1] + ".custom.level " + playerSkillLevel + ".up_commands");
                                    commandExecutor(player, commands2);

                                }
                            } else {
                                remainingXP = (expGained + currentExp);
                                if (plugin.ph.showingExpGains.get(player.getName()) && plugin.getConfig().getConfigurationSection(args[1]).getBoolean("visible")) {
                                    player.sendMessage(ChatColor.AQUA + "You've received " + ChatColor.GOLD + args[2] + ChatColor.AQUA + " EXP in " + ChatColor.GREEN + args[1] + ChatColor.AQUA + ".");
                                }
                            }
                            plugin.ph.setSkill(player.getName(), args[1], playerSkillLevel, remainingXP);
                            plugin.sbh.updateBoard(player.getName(), args[1], playerSkillLevel, remainingXP, xpNeededToLevel);
                        }
                    } else {
                        System.out.println("Error in cpa. Player was null.");
                        System.out.println(args[0] + " " + args[1] + " " + args[2]);
                    }
                } else {
                    System.out.println("Not a valid skill");
                    return false;
                }
            }
        }
        return false;
    }

    public void commandExecutor(Player player, List<String> cmds) {
        for (String c : cmds) {
            String temp = c.replace("@p", player.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), temp);
        }
    }
}
