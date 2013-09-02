package guy.theoneandonly.customs.scoreBoard;

import com.mini.Arguments;
import guy.theoneandonly.customs.CustomProfessions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoardHandler {

    CustomProfessions plugin;
    ScoreboardManager sbManager;
    private Map<String, Scoreboard> PLAYER_SCOREBOARDS = new HashMap<String, Scoreboard>();
    Score score;
    private Map<String, Boolean> isMainShowing = new HashMap<String, Boolean>();
    private Map<String, Boolean> isProfShowing = new HashMap<String, Boolean>();
    private Map<String, Boolean> isExpShowing = new HashMap<String, Boolean>();
    public Map<String, Object> professions;

    //Constructor
    public ScoreBoardHandler(CustomProfessions instance) {
        plugin = instance;
        sbManager = Bukkit.getScoreboardManager();
        professions = plugin.getConfig().getConfigurationSection("professions").getValues(false);
    }

    //Puts a scoreboard in the hashMap so they can see their own boards
    public void addScoreBoard(Player player) {
        PLAYER_SCOREBOARDS.put(player.getName(), makeBoard(player));

    }

    //returns a players board from the hashMap
    public Scoreboard getScoreBoard(Player player) {
        return PLAYER_SCOREBOARDS.get(player.getName());

    }

    //removes the players board when they are not viewing it or when they log out.
    public void removeScoreBoard(Player player) {
        PLAYER_SCOREBOARDS.remove(player.getName());
    }

    //Controls if the player is viewing their board or not
    public void setMainShowing(String player, boolean value) {
        isMainShowing.put(player, value);
    }

    public void setProfShowing(String player, boolean value) {
        isProfShowing.put(player, value);
    }

    public void setExpShowing(String player, boolean value) {
        isExpShowing.put(player, value);
    }

    public Boolean getMainShowing(String player) {
        return isMainShowing.get(player);
    }

    public Boolean getProfShowing(String player) {
        return isProfShowing.get(player);
    }

    public Boolean getExpShowing(String player) {
        return isExpShowing.get(player);
    }

    public boolean isBoardShowing(String player) {
        if (isMainShowing.get(player)) {
            return true;
        } else if (isProfShowing.get(player)) {
            return true;
        } else if (isExpShowing.get(player)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void showingRemove(String player){
        isMainShowing.remove(player);
        isProfShowing.remove(player);
        isExpShowing.remove(player);
    }

    //Makes a scoreboard for the player when /cpro is typed
    public Scoreboard makeBoard(Player player) {
        Scoreboard board = sbManager.getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BOLD + "Skills");
        return board;
    }

    //Resets the main board then repopulates and shows the board.
    public void setMainBoard(Player player) {
        Scoreboard playerBoard = resetBoard(player);
        Objective objective = playerBoard.getObjective(player.getName());
        objective.setDisplayName(ChatColor.BOLD + "Skills");
        for (String e : professions.keySet()) {
            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + e));
            score.setScore(plugin.database.getArguments(player.getName() + "_" + e).getInteger("level"));
        }
        player.setScoreboard(playerBoard);
    }
    //Sets the displayed board to show the provided profession or makes the board then sets it

    public void setProfBoard(Player player, String profession) {
        Scoreboard board = resetBoard(player);
        Objective objective = board.getObjective(player.getName());
        objective.setDisplayName(ChatColor.BOLD + profession);
        Map<String, Object> levels = plugin.getConfig().getConfigurationSection("professions." + profession).getValues(false);
        int level = getLevel(player.getName(), profession);
        for (String e : levels.keySet()) {
            if (e.contains("level")) {
                String[] sLevel = e.split(" ");
                int skillLevel = Integer.parseInt(sLevel[1]);
                if (skillLevel <= level) {
                    List<String> levelSkillList = plugin.getConfig().getStringList("professions." + profession + "." + e + ".skills");
                    for (String f : levelSkillList) {
                        if (f.length() > 16) {
                            String temp = f.substring(0, 15);
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + temp));
                            score.setScore(skillLevel);
                        } else {
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + f));
                            score.setScore(skillLevel);
                        }
                    }
                } else if (skillLevel > level) {
                    List<String> levelSkillList = plugin.getConfig().getStringList("professions." + profession + "." + e + ".skills");
                    for (String f : levelSkillList) {
                        if (f.length() > 16) {
                            String temp = f.substring(0, 15);
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + temp));
                            score.setScore(skillLevel);
                        } else {
                            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + f));
                            score.setScore(skillLevel);
                        }
                    }
                } else {
                    List<String> levelSkillList = plugin.getConfig().getStringList("professions." + profession + "." + e + ".skills");
                    for (String f : levelSkillList) {
                        player.sendMessage(ChatColor.AQUA + "Next Level: " + ChatColor.RED + level);
                    }
                }
            }
        }
        player.setScoreboard(board);
    }

    public void setExpBoard(Player player, String profession) {
        Scoreboard board = resetBoard(player);
        Objective objective = board.getObjective(player.getName());
        objective.setDisplayName(ChatColor.BOLD + profession);
        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GREEN + "To level: "));
        score.setScore(getExpNeeded(profession, (getLevel(player.getName(), profession) + 1)));
        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "XP: "));
        score.setScore(getCurrentExp(player.getName(), profession));
        
        score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Level: "));
        score.setScore(getLevel(player.getName(), profession));
        
        
        player.setScoreboard(board);
    }

    //resets the board to remove it from the players view
    public Scoreboard resetBoard(Player player) {
        Scoreboard board = getScoreBoard(player);
        for (Object e : board.getPlayers().toArray()) {
            board.resetScores((OfflinePlayer) e);
        }
        return board;
    }

    public void reloadBoard(Player player) {
        Scoreboard board = getScoreBoard(player);

        for (Object e : board.getPlayers().toArray()) {
            board.resetScores((OfflinePlayer) e);
        }
        player.setScoreboard(board);
    }
    
        //Gets the players current level in provided profession
    public int getLevel(String player, String profession) {
        if (professions.containsKey(profession)) {
            int currentLvl = plugin.database.getArguments(player + "_" + profession).getInteger("level");
            return currentLvl;
        } else {
            return 0;
        }
    }

    //Gets the players Current exp in the provided profession
    public int getCurrentExp(String player, String profession) {
        if (professions.containsKey(profession)) {
            int currentXP = plugin.database.getArguments(player + "_" + profession).getInteger("exp");
            return currentXP;
        } else {
            return 0;
        }
    }
    
    //Get the max level of the profession provided
    //Need to test this to see if it is going to work like I want it.
    public int getMaxProfLvl(String profession) {
        if (professions.containsKey(profession)) {
            Map<String, Object> levels = plugin.getConfig().getConfigurationSection("professions." + profession).getValues(false);
            return levels.size();
        } else {
            return 0;
        }
    }

    //gets the exp needed from the provided level in the provided profession
    public int getExpNeeded(String profession, int level) {
        if (professions.containsKey(profession)) {
            int xpNeeded = plugin.getConfig().getInt("professions." + profession + ".level " + level + ".xpneeded");
            return xpNeeded;
        } else {
            return 0;
        }
    }
}
