package guy.theoneandonly.customs.handlers;

import guy.theoneandonly.customs.Customs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoardHandler {

    Customs plugin;
    ScoreboardManager sbManager;
    //Going to try and make a nested hashMap in this so players can have a few boards put in here.
    //Have to redo all the other classes to use this new HashMap.
    //First string is players name. Second String is the custom name. Scoreboard will be that customs board.
    //There will be a main board for all players. 
    private Map<String, HashMap<String, Scoreboard>> PLAYER_SCOREBOARDS = new HashMap<String, HashMap<String, Scoreboard>>();
    Score score;
    private Map<String, String> boardShowing = new HashMap<String, String>();
    public Map<String, Object> professions;

    //Constructor
    public ScoreBoardHandler(Customs instance) {
        plugin = instance;
        sbManager = Bukkit.getScoreboardManager();
        professions = plugin.getConfig().getValues(false);
    }

    //Puts a scoreboard in the hashMap so they can see their own boards
    //Have to change this so players can have a few different boards that they can switch between.
    public void addScoreBoard(Player player) {
        PLAYER_SCOREBOARDS.put(player.getName(), makeBoard(player));

    }

    //returns a players board from the hashMap
    public Scoreboard getScoreBoard(Player player, String scoreBoard) {
        return PLAYER_SCOREBOARDS.get(player.getName()).get(scoreBoard);

    }

    //removes the players board when they are not viewing it or when they log out.
    public void removeScoreBoard(Player player) {
        PLAYER_SCOREBOARDS.remove(player.getName());
    }

    //Updates all the boards when valuse change
    public void updateBoard(String player, String custom, int level, int exp, int toLevel) {
        //Main board
        Scoreboard tempBoard = PLAYER_SCOREBOARDS.get(player).get("main");
        Objective objective = tempBoard.getObjective("main");
        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + custom));
        score.setScore(level);

        //Custom Board
        tempBoard = PLAYER_SCOREBOARDS.get(player).get(custom);
        objective = tempBoard.getObjective(custom);

        Map<String, Object> customLevels = plugin.getConfig().getConfigurationSection(custom + ".custom").getValues(false);
        int level2 = plugin.ph.getSkillLevel(player, custom);
        for (String f : customLevels.keySet()) {
            if (f.contains("level")) {
                String[] sLevel = f.split(" ");
                int skillLevel = Integer.parseInt(sLevel[1]);
                if (skillLevel <= level2) {
                    List<String> levelSkillList = plugin.getConfig().getStringList(custom + ".custom." + f + ".skills");
                    for (String g : levelSkillList) {
                        if (g.length() > 14) {
                            String tempSkillLevel = g.substring(0, 13);
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + tempSkillLevel));
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + tempSkillLevel));
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + tempSkillLevel));
                            score.setScore(skillLevel);
                        } else {
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + g));
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + g));
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + g));
                            score.setScore(skillLevel);
                        }
                    }
                } else if (skillLevel > level2) {
                    List<String> levelSkillList = plugin.getConfig().getStringList(custom + ".custom." + f + ".skills");
                    for (String g : levelSkillList) {
                        if (g.length() > 14) {
                            String tempSkillLevel = g.substring(0, 13);
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + tempSkillLevel));
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + tempSkillLevel));
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + tempSkillLevel));
                            score.setScore(skillLevel);
                        } else {
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + g));
                            tempBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + g));
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + g));
                            score.setScore(skillLevel);
                        }
                    }
                }
            }
        }

        //Custom's Exp Board
        String skillTemp = "exp_" + custom;
        if (skillTemp.length() >= 16) {
            skillTemp = skillTemp.substring(0, 15);
        }
        tempBoard = PLAYER_SCOREBOARDS.get(player).get(skillTemp);
        objective = tempBoard.getObjective(skillTemp);
        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GREEN + "To level: "));
        score.setScore(toLevel);

        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "XP: "));
        score.setScore(exp);

        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Level: "));
        score.setScore(level);
    }

    public void setBoardShowing(String player, String board) {
        boardShowing.put(player, board);
    }

    public String getBoardShowing(String player) {
        if (boardShowing.containsKey(player)) {
            return boardShowing.get(player);
        } else {
            setBoardShowing(player, "blank");
            return boardShowing.get(player);
        }
    }

    public void removeBoardShowing(String player) {
        boardShowing.remove(player);
    }
    //Makes a scoreboard for the player when /cpro is typed

    public HashMap makeBoard(Player player) {
        HashMap<String, Scoreboard> temp = new HashMap<String, Scoreboard>();
        String skillTemp;
        //Sets the blank board for when players are not viewing a board. TODO: Test to see if it works
        Scoreboard blankBoard = sbManager.getNewScoreboard();
        Objective objective = blankBoard.registerNewObjective("blank", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        temp.put("blank", blankBoard);

        //Set the main board.
        blankBoard = sbManager.getNewScoreboard();
        objective = blankBoard.registerNewObjective("main", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BOLD + "Skills");
        for (String e : professions.keySet()) {
            boolean showSkill = plugin.getConfig().getConfigurationSection(e).getBoolean("visible");
            if (showSkill) {
                score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + e));
                score.setScore(plugin.ph.getSkillLevel(player.getName(), e));
            }
        }
        temp.put("main", blankBoard);

        //Set each custom's board, if it is set to visable
        for (String e : professions.keySet()) {
            boolean showSkill = plugin.getConfig().getConfigurationSection(e).getBoolean("visible");
            if (showSkill) {
                blankBoard = sbManager.getNewScoreboard();
                objective = blankBoard.registerNewObjective(e, "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                if (e.length() > 14) {
                    skillTemp = e.substring(0, 13);
                } else {
                    skillTemp = e;
                }
                objective.setDisplayName(ChatColor.BOLD + skillTemp);
                Map<String, Object> customLevels = plugin.getConfig().getConfigurationSection(e + ".custom").getValues(false);
                int level = plugin.ph.getSkillLevel(player.getName(), e);
                for (String f : customLevels.keySet()) {
                    if (f.contains("level")) {
                        String[] sLevel = f.split(" ");
                        int skillLevel = Integer.parseInt(sLevel[1]);
                        if (skillLevel <= level) {
                            List<String> levelSkillList = plugin.getConfig().getStringList(e + ".custom." + f + ".skills");
                            for (String g : levelSkillList) {
                                if (g.length() > 14) {
                                    String tempSkillLevel = g.substring(0, 13);
                                    score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + tempSkillLevel));
                                    score.setScore(skillLevel);
                                } else {
                                    score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + g));
                                    score.setScore(skillLevel);
                                }
                            }
                        } else if (skillLevel > level) {
                            List<String> levelSkillList = plugin.getConfig().getStringList(e + ".custom." + f + ".skills");
                            for (String g : levelSkillList) {
                                if (g.length() > 14) {
                                    String tempSkillLevel = g.substring(0, 13);
                                    score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + tempSkillLevel));
                                    score.setScore(skillLevel);
                                } else {
                                    score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + g));
                                    score.setScore(skillLevel);
                                }
                            }
                        } else {
                            List<String> levelSkillList = plugin.getConfig().getStringList(e + ".custom." + f + ".skills");
                            for (String g : levelSkillList) {
                                player.sendMessage(ChatColor.AQUA + "Next Level: " + ChatColor.RED + level);
                            }
                        }
                    }
                }
                temp.put(e, blankBoard);
            }
        }

        //sets each customs exp board in their map
        for (String e : professions.keySet()) {
            boolean showSkill = plugin.getConfig().getConfigurationSection(e).getBoolean("visible");
            if (showSkill) {
                skillTemp = "exp_" + e;
                if (skillTemp.length() > 16) {
                    skillTemp = skillTemp.substring(0, 15);
                }
                blankBoard = sbManager.getNewScoreboard();
                objective = blankBoard.registerNewObjective(skillTemp, "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(ChatColor.BOLD + e);

                score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GREEN + "To level: "));
                score.setScore(getExpNeeded(e, (getLevel(player.getName(), e) + 1)));

                score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "XP: "));
                score.setScore(getCurrentExp(player.getName(), e));

                score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Level: "));
                score.setScore(getLevel(player.getName(), e));

                temp.put(skillTemp, blankBoard);
            }
        }
        return temp;
    }

    //Gets the players current level in provided profession
    public int getLevel(String player, String profession) {
        if (professions.containsKey(profession)) {
            int currentLvl = plugin.ph.getSkillLevel(player, profession);
            return currentLvl;
        } else {
            return 0;
        }
    }

    //Gets the players Current exp in the provided profession
    public int getCurrentExp(String player, String profession) {
        if (professions.containsKey(profession)) {
            int currentXP = plugin.ph.getSkillExp(player, profession);
            return currentXP;
        } else {
            return 0;
        }
    }

    //Get the max level of the profession provided
    //Need to test this to see if it is going to work like I want it.
    public int getMaxProfLvl(String profession) {
        if (professions.containsKey(profession)) {
            Map<String, Object> levels = plugin.getConfig().getConfigurationSection(profession + ".custom").getValues(false);
            int numLevel = 0;
            for (String t : levels.keySet()) {
                if (t.contains("level") && !(t.equalsIgnoreCase("level 0"))) {
                    numLevel++;
                }
            }
            return numLevel;
        } else {
            return 0;
        }
    }

    //gets the exp needed from the provided level in the provided profession
    public int getExpNeeded(String profession, int level) {
        if (professions.containsKey(profession)) {
            int xpNeeded = plugin.getConfig().getInt(profession + ".custom.level " + level + ".xpneeded");
            return xpNeeded;
        } else {
            return 0;
        }
    }
}
