package io.alerium.donationranges.hooks;

import io.alerium.donationranges.DonationRangesPlugin;
import io.alerium.donationranges.donations.DonationManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPIHook extends PlaceholderExpansion {

    private final DonationRangesPlugin plugin;
    private final DonationManager manager;

    public PAPIHook(DonationRangesPlugin plugin, DonationManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "donationranges";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xQuickGlare";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("amount"))
            return Double.toString(manager.getDonationAmount(player.getUniqueId()));

        return null;
    }
}
