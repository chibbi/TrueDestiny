package org.ranin.TrueDestiny.finance;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class McXpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 2) {
                player.setExp(player.getExp() - Integer.valueOf(args[1]));
                Bukkit.getPlayer(args[1]).setExp(player.getExp() + Integer.valueOf(args[1]));
                return true;
            }
            player.sendMessage("§e ---------- §fTip: givexp §e---------- \n" + "§7/givexp PLAYER AMOUNT");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> l = new ArrayList<String>();
        if (args.length == 1) {
            for (Player sing : Bukkit.getOnlinePlayers()) {
                l.add(sing.getName());
            }
        } else if (args.length == 2) {
            l.add("100");
        }
        return l;
    }
}