package guy.theoneandonly.customs.handlers;

import com.mini.Arguments;
import com.mini.Mini;
import guy.theoneandonly.customs.Customs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class PlayerHandler {

    Customs plugin;
    public Map<String, Boolean> showingExpGains = new HashMap<String, Boolean>();
    public Map<String, Mini> playerDB = new HashMap<String, Mini>();
    public ArrayList<Player> onlinePlayers = new ArrayList<Player>();

    public PlayerHandler(Customs instance) {
        plugin = instance;
    }

    public void setShowingExpGains(String playerName, Boolean showing){
        if(showingExpGains.containsKey(playerName)){
            showingExpGains.put(playerName, showing);
        }
        else if (!showingExpGains.containsKey(playerName)){
            showingExpGains.put(playerName, false);
        }else{
            System.out.println("PlayerHandler.setShowingExpGains(String playerName, Boolean showing) else Statement reached. TODO: fix this.");
        }
    }
    
    public void addDB(String playerName) {
        if (!playerDB.containsKey(playerName)) {
            playerDB.put(playerName, new Mini(plugin.getDataFolder().getPath() + "/Players", playerName + ".mini"));
        } else {
            return;
        }
    }

    public Mini getDB(String playerName) {
        if (playerDB.containsKey(playerName)) {
            return playerDB.get(playerName);
        } else {
            addDB(playerName);
            return playerDB.get(playerName);
        }
    }

    public void removeDB(String playerName) {
        if (playerDB.containsKey(playerName)) {
            playerDB.remove(playerName);
        } else {
            return;
        }
    }
    
    public void setSkill(String playerName, String skillName, int level, int exp){
        if(playerDB.containsKey(playerName)){
            if(playerDB.get(playerName).hasIndex(skillName)){
                
                playerDB.get(playerName).setArgument(skillName, "level", Integer.toString(level), true);
                playerDB.get(playerName).setArgument(skillName, "exp", Integer.toString(exp), true);
            }else{
                Arguments playerEntry = new Arguments(skillName);
                playerEntry.setValue("level", Integer.toString(level));
                playerEntry.setValue("exp", Integer.toString(exp));
                playerDB.get(playerName).addIndex(playerEntry.getKey(), playerEntry);
                playerDB.get(playerName).update();
            }
        }
    }
    
    public int getSkillLevel(String playerName, String skillName){
        if(playerDB.containsKey(playerName)){
            if(playerDB.get(playerName).hasIndex(skillName)){
                return playerDB.get(playerName).getArguments(skillName).getInteger("level");
            }else{
                Arguments playerEntry = new Arguments(skillName);
                playerEntry.setValue("level", "0");
                playerEntry.setValue("exp", "0");
                playerDB.get(playerName).addIndex(playerEntry.getKey(), playerEntry);
                playerDB.get(playerName).update();
                return 0;
            }
        }else{
            System.out.println("Error in PlayerHandler.getSkillLevel. Player is not in the playerDB");
        }
        return -1;
    }
    
    public int getSkillExp(String playerName, String skillName){
        if(playerDB.containsKey(playerName)){
            if(playerDB.get(playerName).hasIndex(skillName)){
                return playerDB.get(playerName).getArguments(skillName).getInteger("exp");
            }else{
                Arguments playerEntry = new Arguments(skillName);
                playerEntry.setValue("level", "0");
                playerEntry.setValue("exp", "0");
                playerDB.get(playerName).addIndex(playerEntry.getKey(), playerEntry);
                playerDB.get(playerName).update();
                return 0;
            }
        }else{
            System.out.println("Error in PlayerHandler.getSkillExp. Player is not in the playerDB.");
        }
        return -1;
    }
}
