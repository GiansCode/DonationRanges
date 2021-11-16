package io.alerium.donationranges;

import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import io.alerium.donationranges.donations.DonationManager;
import io.alerium.donationranges.hooks.PAPIHook;
import io.alerium.donationranges.utils.MySQL;
import io.samdev.actionutil.ActionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.function.Function;
import java.util.logging.Level;

public final class DonationRangesPlugin extends JavaPlugin {

    private MySQL mySQL;
    private PaperCommandManager<CommandSender> commandManager;
    private ActionUtil actionUtil;

    private DonationManager donationManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling DonationRanges...");

        saveDefaultConfig();
        if (!setupMySQL())
            return;

        if (!setupCommandManager())
            return;

        actionUtil = ActionUtil.init(this);
        donationManager = new DonationManager(this);
        donationManager.enable();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PAPIHook(this, donationManager).register();

        getLogger().info("DonationRanges enabled!");
    }

    @Override
    public void onDisable() {
        mySQL.disconnect();
    }

    public Component getComponent(String path) {
        return MiniMessage.get().parse(getConfig().getString(path));
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public PaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public ActionUtil getActionUtil() {
        return actionUtil;
    }

    private boolean setupMySQL() {
        mySQL = new MySQL(getConfig().getString("mysql.hostname"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"), getConfig().getString("mysql.database"), getConfig().getInt("mysql.port"));
        try {
            mySQL.connect();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "An error occurred while connecting to the database", e);
            return false;
        }
        return true;
    }

    private boolean setupCommandManager() {
        Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executorFunction = AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();
        try {
            commandManager = new PaperCommandManager<>(this, executorFunction, Function.identity(), Function.identity());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while loading the command manager.", e);
            return false;
        }
        return true;
    }

}
