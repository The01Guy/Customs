package guy.theoneandonly.customs.commands;

import guy.theoneandonly.customs.Customs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cpro implements CommandExecutor {

    Customs plugin;

    public Cpro(Customs instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        if (commandLabel.equalsIgnoreCase("cpro")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    if (plugin.sbh.getBoardShowing(player.getName()).equalsIgnoreCase("blank")) {
                        player.setScoreboard(plugin.sbh.getScoreBoard(player, "main"));
                        plugin.sbh.setBoardShowing(player.getName(), "main");
                    } else if (plugin.sbh.getBoardShowing(player.getName()).equalsIgnoreCase("main")) {
                        player.setScoreboard(plugin.sbh.getScoreBoard(player, "blank"));
                        plugin.sbh.setBoardShowing(player.getName(), "blank");
                    } else {
                        player.setScoreboard(plugin.sbh.getScoreBoard(player, "main"));
                        plugin.sbh.setBoardShowing(player.getName(), "main");
                    }
                    return true;
                } else if (args.length == 1) {
                    if (plugin.sbh.professions.containsKey(args[0])) {
                        if (plugin.getConfig().getConfigurationSection(args[0]).getBoolean("visible")) {
                            if (plugin.sbh.getBoardShowing(player.getName()).equalsIgnoreCase("blank")) {
                                player.setScoreboard(plugin.sbh.getScoreBoard(player, args[0]));
                                plugin.sbh.setBoardShowing(player.getName(), args[0]);
                            } else if (plugin.sbh.getBoardShowing(player.getName()).equalsIgnoreCase(args[0])) {
                                player.setScoreboard(plugin.sbh.getScoreBoard(player, "blank"));
                                plugin.sbh.setBoardShowing(player.getName(), "blank");
                            } else {
                                player.setScoreboard(plugin.sbh.getScoreBoard(player, args[0]));
                                plugin.sbh.setBoardShowing(player.getName(), args[0]);
                            }
                            return true;
                        } else {
                            return false;
                        }

                    } else if (args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("hide")) {
                        if (args[0].equalsIgnoreCase("show")) {
                            plugin.ph.setShowingExpGains(sender.getName(), Boolean.TRUE);
                            player.sendMessage("You will now be receiving exp messages.");
                        } else {
                            plugin.ph.setShowingExpGains(sender.getName(), Boolean.FALSE);
                            player.sendMessage("You will now stop receiving exp messages.");
                        }
                    }
                } else if (args.length == 2) {
                    if (plugin.sbh.professions.containsKey(args[0])) {
                        if (args[1].equalsIgnoreCase("exp")) {
                            if (plugin.getConfig().getConfigurationSection(args[0]).getBoolean("visible")) {
                                String skillTemp = "exp_" + args[0];
                                if (skillTemp.length() >= 16) {
                                    skillTemp = skillTemp.substring(0, 15);
                                }

                                if (plugin.sbh.getBoardShowing(player.getName()).equalsIgnoreCase("blank")) {
                                    player.setScoreboard(plugin.sbh.getScoreBoard(player, skillTemp));
                                    plugin.sbh.setBoardShowing(player.getName(), skillTemp);
                                } else if (plugin.sbh.getBoardShowing(player.getName()).equalsIgnoreCase(skillTemp)) {
                                    player.setScoreboard(plugin.sbh.getScoreBoard(player, "blank"));
                                    plugin.sbh.setBoardShowing(player.getName(), "blank");
                                } else {
                                    player.setScoreboard(plugin.sbh.getScoreBoard(player, skillTemp));
                                    plugin.sbh.setBoardShowing(player.getName(), skillTemp);
                                }
                                return true;
                            }
                        }
                    }
                } else {
                    player.sendMessage("Useage: /cpro [profession] [exp]");
                    return false;
                }
            } else {
                System.out.println("This command can only be used by players");
                return false;
            }
        } else {
            return false;
        }
        return false;
    }
}
