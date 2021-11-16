package io.alerium.donationranges.donations.listeners;

import io.alerium.donationranges.DonationRangesPlugin;
import io.alerium.donationranges.donations.DonationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class DonationListener implements Listener {

    private final DonationRangesPlugin plugin;
    private final DonationManager manager;

    public DonationListener(DonationRangesPlugin plugin, DonationManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (!manager.loadPlayerData(event.getUniqueId()))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, plugin.getComponent("messages.errorLoadingData"));
    }

}
