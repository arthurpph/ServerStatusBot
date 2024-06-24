package com.lunikmc.bot.botlisteners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        User user = event.getUser();
        TextChannel channel = event.getChannel().asTextChannel();

        switch(commandName) {
            case "nuclear":
                if(!(user.getId().equals("1223469239797551104") || user.getId().equals("395546295940415510"))) {
                    return;
                }

                event.deferReply().queue();

                channel.createCopy().setPosition(channel.getPosition()).queue(newChannel -> {
                    channel.delete().queue();
                    newChannel.sendMessage(String.format("Histórico de mensagens limpa por %s", user.getAsMention())).queue();
                });
        }
    }
}
