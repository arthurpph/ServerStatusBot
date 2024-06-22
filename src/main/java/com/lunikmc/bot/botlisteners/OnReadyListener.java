package com.lunikmc.bot.botlisteners;

import com.lunikmc.bot.LunikBot;
import com.lunikmc.bot.exceptions.InvalidChannelId;
import com.lunikmc.bot.managers.ConfigManager;
import com.lunikmc.bot.tasks.CreateVerificationMessageTask;
import com.lunikmc.bot.tasks.VerifyStatusTask;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

@RequiredArgsConstructor
public class OnReadyListener extends ListenerAdapter {
    private final LunikBot lunikBot;

    @Override
    public void onReady(ReadyEvent event) {
        JDA bot = lunikBot.getBot();
        ConfigManager configManager = lunikBot.getConfigManager();
        String statusChannelId = configManager.getStatusChannelId();

        if(statusChannelId == null)
            throw new NullPointerException("Status channel ID is null, check config.yml");

        TextChannel channel;
        try {
            channel = bot.getTextChannelById(statusChannelId);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Status channel ID is not a valid number, check config.yml");
        }

        if(channel == null)
            throw new InvalidChannelId("No channel was found with the provided ID, check config.yml");

        if(configManager.getStatusMessageId() != null && !Objects.equals(configManager.getStatusMessageId(), "")) {
            VerifyStatusTask verifyStatusTask = new VerifyStatusTask(lunikBot);
            lunikBot.setVerifyStatusTask(verifyStatusTask);
            return;
        }

        channel.sendMessageEmbeds(lunikBot.getDefaultEmbed().build()).queue(message -> {
            configManager.setStatusMessageId(message.getId());
            VerifyStatusTask verifyStatusTask = new VerifyStatusTask(lunikBot, message);
            lunikBot.setVerifyStatusTask(verifyStatusTask);
        });

        new CreateVerificationMessageTask(lunikBot).run();
    }
}
