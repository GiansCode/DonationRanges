package io.alerium.donationranges.donations;

import io.alerium.donationranges.DonationRangesPlugin;
import io.alerium.donationranges.donations.commands.DonationCommand;
import io.alerium.donationranges.donations.listeners.DonationListener;
import io.alerium.donationranges.donations.objects.RangeReward;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class DonationManager {

    private final DonationRangesPlugin plugin;
    private final DonationDatabase database;

    private final Object2DoubleArrayMap<UUID> donationAmount = new Object2DoubleArrayMap<>();
    private final Int2ObjectArrayMap<RangeReward> rewards = new Int2ObjectArrayMap<>();

    public DonationManager(DonationRangesPlugin plugin) {
        this.plugin = plugin;
        this.database = new DonationDatabase(plugin.getMySQL());
    }

    public void enable() {
        loadRewards();

        new DonationCommand(plugin, this).register(plugin.getCommandManager());
        Bukkit.getPluginManager().registerEvents(new DonationListener(plugin, this), plugin);
    }

    public boolean loadPlayerData(UUID uuid) {
        try {
            double amount = database.getPlayerDonationAmount(uuid);
            if (amount == 0)
                return true;

            donationAmount.put(uuid, amount);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "An error occurred while loading the donation amount of " + uuid + ".", e);
            return false;
        }
    }

    public double getDonationAmount(UUID uuid) {
        return donationAmount.getOrDefault(uuid, 0);
    }

    public boolean updateDonationAmount(Player player, double amount) {
        try {
            database.updatePlayerDonationAmount(player.getUniqueId(), amount);
            double oldAmount = getDonationAmount(player.getUniqueId());
            donationAmount.put(player.getUniqueId(), amount);

            getRangeReward(amount).ifPresent(reward -> {
                if (reward.getMinAmount() <= oldAmount)
                    return;

                Bukkit.getScheduler().runTask(plugin, () -> plugin.getActionUtil().executeActions(player, reward.getActions()));
            });
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "An error occurred while updating the donation amount of " + player.getUniqueId() + " to " + amount + ".", e);
            return false;
        }
    }

    private Optional<RangeReward> getRangeReward(double amount) {
        RangeReward last = null;
        for (RangeReward reward : rewards.values()) {
            if (reward.getMinAmount() > amount)
                continue;

            if (last == null || reward.getMinAmount() > last.getMinAmount())
                last = reward;
        }
        return Optional.ofNullable(last);
    }

    private void loadRewards() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ranges");
        for (String id : section.getKeys(false)) {
            int min = Integer.parseInt(id);
            List<String> actions = section.getStringList(id);

            rewards.put(min, new RangeReward(min, actions));
        }
    }

}
