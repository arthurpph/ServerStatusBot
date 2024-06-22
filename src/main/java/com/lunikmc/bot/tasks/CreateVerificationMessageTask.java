package com.lunikmc.bot.tasks;

import com.lunikmc.bot.LunikBot;
import com.lunikmc.bot.exceptions.TextChannelNotFound;
import com.lunikmc.bot.managers.ConfigManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Objects;

@RequiredArgsConstructor
public class CreateVerificationMessageTask implements Runnable {
    private final LunikBot lunikBot;

    @Override
    public void run() {
        ConfigManager configManager = lunikBot.getConfigManager();
        JDA bot = lunikBot.getBot();

        TextChannel textChannel = bot.getTextChannelById(configManager.getVerificationChannelId());

        if(textChannel == null) {
            throw new TextChannelNotFound("Verification text channel not found");
        }

        if(configManager.getVerificationMessageId() != null && !Objects.equals(configManager.getVerificationMessageId(), "")) {
            try {
                textChannel.retrieveMessageById(configManager.getVerificationMessageId()).complete();
                return;
            } catch (ErrorResponseException ignored) {}
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("VERIFICAÇÃO")
                .setColor(lunikBot.DEFAULT_EMBED_COLOR)
                .setDescription("Para se verificar, clique no botão abaixo");

        Button verificationButton = Button.success("verification", "Verificar");

        MessageCreateData message = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .setActionRow(verificationButton)
                .build();

        textChannel.sendMessage(message).queue(sentMessage ->
            configManager.setVerificationMessageId(sentMessage.getId())
        );
    }
}
