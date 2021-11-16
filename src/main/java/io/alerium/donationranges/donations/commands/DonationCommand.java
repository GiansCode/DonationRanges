package io.alerium.donationranges.donations.commands;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.paper.PaperCommandManager;
import io.alerium.donationranges.DonationRangesPlugin;
import io.alerium.donationranges.donations.DonationManager;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonationCommand {

    private final DonationRangesPlugin plugin;
    private final DonationManager rangesManager;

    public DonationCommand(DonationRangesPlugin plugin, DonationManager rangesManager) {
        this.plugin = plugin;
        this.rangesManager = rangesManager;
    }

    public void register(PaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("donationranges");

        manager.command(builder.literal("add")
                .permission("donationranges.commands.add")
                .argument(PlayerArgument.of("player"))
                .argument(DoubleArgument.of("amount"))
                .handler(context -> {
                    Player player = context.get("player");
                    double amount = context.get("amount");
                    amount += rangesManager.getDonationAmount(player.getUniqueId());

                    if (rangesManager.updateDonationAmount(player, amount))
                        context.getSender().sendMessage(plugin.getComponent("messages.commands.add.success"));
                    else
                        context.getSender().sendMessage(plugin.getComponent("messages.commands.add.error"));
        }));

        manager.command(builder.literal("info")
                .permission("donationranges.commands.info")
                .argument(PlayerArgument.of("player"))
                .handler(context -> {
                    Player player = context.get("player");
                    double amount = rangesManager.getDonationAmount(player.getUniqueId());

                    context.getSender().sendMessage(
                            plugin.getComponent("messages.commands.info")
                                    .replaceText(TextReplacementConfig.builder().match("%player_name%").replacement(player.getName()).build())
                                    .replaceText(TextReplacementConfig.builder().match("%amount%").replacement(Double.toString(amount)).build())
                    );
                })
        );

        manager.command(builder.literal("remove")
                .permission("donationranges.commands.add")
                .argument(PlayerArgument.of("player"))
                .argument(DoubleArgument.of("amount"))
                .handler(context -> {
                    Player player = context.get("player");
                    double remAmount = context.get("amount");
                    double amount = rangesManager.getDonationAmount(player.getUniqueId()) - remAmount;
                    if (amount < 0)
                        amount = 0;

                    if (rangesManager.updateDonationAmount(player, amount))
                        context.getSender().sendMessage(plugin.getComponent("messages.commands.remove.success"));
                    else
                        context.getSender().sendMessage(plugin.getComponent("messages.commands.remove.error"));
                }));

        manager.command(builder.literal("set")
                .permission("donationranges.commands.add")
                .argument(PlayerArgument.of("player"))
                .argument(DoubleArgument.of("amount"))
                .handler(context -> {
                    Player player = context.get("player");
                    double amount = context.get("amount");

                    if (rangesManager.updateDonationAmount(player, amount))
                        context.getSender().sendMessage(plugin.getComponent("messages.commands.set.success"));
                    else
                        context.getSender().sendMessage(plugin.getComponent("messages.commands.set.error"));
                }));
    }

}
