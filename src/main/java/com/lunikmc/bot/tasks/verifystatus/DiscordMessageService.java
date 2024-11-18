package com.lunikmc.bot.tasks.verifystatus;

import com.lunikmc.bot.LunikBot;
import com.lunikmc.bot.exceptions.InvalidMessageId;
import com.lunikmc.bot.managers.ConfigManager;
import com.lunikmc.bot.models.ServerStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscordMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMessageService.class);

    private final LunikBot lunikBot;
    private final HashMap<String, String> lastMessages = new HashMap<>();

    private Message message;
    private boolean shouldLogError = true;

    public DiscordMessageService(LunikBot lunikBot) {
        this.lunikBot = lunikBot;

        try {
            this.message = Objects.requireNonNull(lunikBot.getBot().getTextChannelById(lunikBot.getConfigManager().getStatusChannelId()))
                    .retrieveMessageById(lunikBot.getConfigManager().getStatusMessageId())
                    .complete();
        } catch (ErrorResponseException e) {
            LOGGER.info("Não foi possível encontrar a mensagem de status com o ID fornecido, criando nova mensagem...");
            createStatusMessage(lunikBot);
        }
    }

    public DiscordMessageService(LunikBot lunikBot, Message message) {
        this.lunikBot = lunikBot;
        this.message = message;
    }

    private void createStatusMessage(LunikBot lunikBot) {
        JDA bot = lunikBot.getBot();
        ConfigManager configManager = lunikBot.getConfigManager();

        TextChannel channel = bot.getTextChannelById(configManager.getStatusChannelId());

        if(channel == null)
            throw new InvalidMessageId("Nenhum canal foi encontrado com o ID fornecido, verifique o config.yml");

        channel.sendMessageEmbeds(lunikBot.getDefaultEmbed().build()).queue(msg -> {
            configManager.setStatusMessageId(msg.getId());
            DiscordMessageService discordMessageUpdater = new DiscordMessageService(lunikBot, msg);
            VerifyStatusTask verifyStatusTask = new VerifyStatusTask(lunikBot, discordMessageUpdater);
            lunikBot.setVerifyStatusTask(verifyStatusTask);
        });
    }

    public void updateDiscordMessage(List<ServerStatus> serversStatus) {
        EmbedBuilder embedBuilder = new EmbedBuilder(lunikBot.getDefaultEmbed().build());
        HashMap<String, String> fields = collectServerStatusFields(serversStatus);

        addFieldsToEmbed(embedBuilder, fields);

        if(shouldUpdateMessage(embedBuilder)) {
            editDiscordMessage(embedBuilder);
        }
    }

    public void proxyOffline() {
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(0xFF0000)
                .setTitle("Status - Lunik (Offline)")
                .setDescription("O servidor está offline no momento.")
                .build();

        message.editMessageEmbeds(messageEmbed).queue(msg -> {});

        try {
            TimeUnit.MILLISECONDS.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> collectServerStatusFields(List<ServerStatus> serversStatus) {
        HashMap<String, String> fields = new HashMap<>();

        for(ServerStatus serverStatus : serversStatus) {
            String serverName = formatServerName(serverStatus.getServerInfo().getName());
            String statusMessage = getServerStatusMessage(serverStatus);
            fields.put(serverName, statusMessage);
        }

        return fields;
    }

    private String getServerStatusMessage(ServerStatus serverStatus) {
        switch (serverStatus.getStatus()) {
            case ONLINE:
                return String.format("Online - %d jogador%s", serverStatus.getPlayers(), serverStatus.getPlayers() == 1 ? "" : "es");
            case OFFLINE:
                return "Offline";
            default:
                return "Erro";
        }
    }

    private String formatServerName(String serverName) {
        return Character.toUpperCase(serverName.charAt(0)) + serverName.substring(1);
    }

    private void addFieldsToEmbed(EmbedBuilder embedBuilder, HashMap<String, String> fields) {
        fields.entrySet().stream()
                .sorted(Map.Entry.<String, String>comparingByKey().reversed())
                .forEach(entry -> embedBuilder.addField(entry.getKey(), entry.getValue(), false));
    }

    private boolean shouldUpdateMessage(EmbedBuilder embedBuilder) {
        boolean shouldEditMessage = false;

        for(MessageEmbed.Field field : embedBuilder.getFields()) {
            if(lastMessages.get(field.getName()) != null && lastMessages.get(field.getName()).equals(field.getValue())) {
                continue;
            }

            lastMessages.put(field.getName(), field.getValue());
            shouldEditMessage = true;
        }

        return shouldEditMessage;
    }

    private void editDiscordMessage(EmbedBuilder embedBuilder) {
        message.editMessageEmbeds(embedBuilder.build()).queue(
                msg -> shouldLogError = true,
                error -> {
                    if(shouldLogError) {
                        shouldLogError = false;
                        LOGGER.error("Não foi possível editar a mensagem de status {}", error.getMessage());
                    }
                }
        );
    }
}
