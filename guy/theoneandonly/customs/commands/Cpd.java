package guy.theoneandonly.customs.commands;

import guy.theoneandonly.customs.Customs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cpd implements CommandExecutor{

    Customs plugin;
    
    public Cpd(Customs instance){
        plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if(!(sender instanceof Player)){
            if(commandLable.equalsIgnoreCase("cpd")){
                if(args.length < 3){
                    System.out.println("Not enough paramaters. Useage 'cpd <PlayerName> <Skill> <EXP>'");
                }
                else if(args.length == 3){
                    
                }
            }
        }
        return false;
    }
    
}
