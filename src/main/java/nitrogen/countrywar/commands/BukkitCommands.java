package nitrogen.countrywar.commands;

import nitrogen.countrywar.settings.CustomInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player p = (Player) sender;

        if(cmd.getName().equalsIgnoreCase("setting")) {
            CustomInventory gui = new CustomInventory();
            p.openInventory(gui.getInventory());
        }
        return true;
    }
}