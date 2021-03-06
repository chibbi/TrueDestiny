package org.ranin.TrueDestiny;

/*
author: "chibbi"
*/

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.ranin.TrueDestiny.finance.McXpCommand;
import org.ranin.TrueDestiny.job.HobbyCommand;
import org.ranin.TrueDestiny.job.HobbyTabCompleter;
import org.ranin.TrueDestiny.job.JobCommand;
import org.ranin.TrueDestiny.job.JobListeners;
import org.ranin.TrueDestiny.job.JobTabCompleter;
import org.ranin.TrueDestiny.job.Sql;
import org.ranin.TrueDestiny.race.RaceCommand;
import org.ranin.TrueDestiny.race.RaceListener;
import org.ranin.TrueDestiny.race.RaceTabCompleter;
import org.ranin.TrueDestiny.repetual.JobTasks;

public class App extends JavaPlugin {

    public static BukkitTask effects;
    public static BukkitTask messages;

    @Override
    public void onEnable() {
        // Initiating Configs
        String comPath = "";
        try {
            // complete path to jar
            String[] completePath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
                    .split("/");
            for (int i = 0; i < completePath.length - 1; i++) {
                comPath += completePath[i] + "/";
            }
        } catch (URISyntaxException e) {
            this.getLogger().severe("Getting Complete Path of Jar didn't work");
        }
        File dir = new File(comPath + "TrueDestiny/");
        if (!dir.exists()) {
            this.getLogger().fine("Created Config Folder");
            dir.mkdir();
        }
        dir = new File(comPath + "TrueDestiny/db/");
        if (!dir.exists()) {
            this.getLogger().fine("Created Database");
            dir.mkdir();
        }
        getConf();
        // Initiating Database
        new Sql("hobby").createTable();
        new Sql("job").createTable();
        new Sql("race").createTable();
        // Initiating Commands
        this.getCommand("givexp").setExecutor(new McXpCommand());
        this.getCommand("givexp").setTabCompleter(new McXpCommand());
        this.getCommand("race").setExecutor(new RaceCommand());
        this.getCommand("race").setTabCompleter(new RaceTabCompleter());
        this.getCommand("job").setExecutor(new JobCommand());
        this.getCommand("job").setTabCompleter(new JobTabCompleter());
        this.getCommand("hobby").setExecutor(new HobbyCommand());
        this.getCommand("hobby").setTabCompleter(new HobbyTabCompleter());
        this.getCommand("warn").setExecutor(new WarnCommand(getLogger()));
        this.getCommand("warn").setTabCompleter(new WarnTabCompletion());
        // Initiating Event Listeners
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
        this.getServer().getPluginManager().registerEvents(new JobListeners(), this);
        this.getServer().getPluginManager().registerEvents(new RaceListener(), this);
        // Initiating Commands
        this.startScheduler();
        this.getLogger().info("Hello, SpigotMC!");
        // Custom Recipes
        this.addCustomSaddleRecipe();
    }

    // create the saddle
    public void addCustomSaddleRecipe() {
        ItemStack item = new ItemStack(Material.SADDLE);
        NamespacedKey item_key = new NamespacedKey((Plugin) this, "saddle_key");
        ShapedRecipe itemRecipe = new ShapedRecipe(item_key, item);
        String[] shape = { "dll", " i " };
        itemRecipe.shape(shape);
        itemRecipe.setIngredient('d', Material.DIAMOND);
        itemRecipe.setIngredient('l', Material.LEATHER);
        itemRecipe.setIngredient('i', Material.IRON_INGOT);
        Bukkit.getServer().addRecipe((Recipe) itemRecipe);
    }

    public void startScheduler() {
        effects = Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                new JobTasks().giveEveryoneJobEffect(Bukkit.getWorld("world")); // make world configurable
                new JobTasks().giveEveryoneJobEffect(Bukkit.getWorld("world_nether"));
                new JobTasks().giveEveryoneJobEffect(Bukkit.getWorld("world_the_end"));
                new JobTasks().giveNearbyReg(Bukkit.getWorld("world"));
            }
        }, 20L, 20L);
        messages = Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "say "
                        + "§6Quick Reminder, if you change your Username, you loose all your xp and money!"
                        + "If you have any issues or find bugs, don't hesitate, and contact an admin or a mod!");
                // "Ey don't change your name, you will loose your job and xp"
            }
        }, 48000L, 48000L); // 24000L => 20minutes (one mc day)
    }

    public FileConfiguration getConf() {
        File customConfigFile = new File("plugins/TrueDestiny/config.yml");
        FileConfiguration cusconf = YamlConfiguration.loadConfiguration(customConfigFile);
        if (!customConfigFile.exists()) {
            cusconf.set("pvpmode", "false");
            cusconf.set("imperatormode", "true");
            try {
                cusconf.save(customConfigFile);
            } catch (IOException e) {
                this.getLogger().severe("Could not create config File");
                this.getLogger().severe(e.getMessage());
            }
        }
        return cusconf;
    }

    @Override
    public void onDisable() {
        effects.cancel();
        messages.cancel();
        getLogger().info("See you again, SpigotMC!");
    }
}
