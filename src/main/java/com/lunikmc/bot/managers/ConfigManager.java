package com.lunikmc.bot.managers;

import com.lunikmc.bot.LunikBot;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigManager {
    private final File configFile;
    private final Configuration config;

    @SneakyThrows
    public ConfigManager(LunikBot lunikBot) {
        configFile = new File(lunikBot.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                lunikBot.getDataFolder().mkdirs();
                Files.copy(lunikBot.getResourceAsStream("config.yml"), configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    public String getBotToken() {
        return config.getString("bot.token");
    }

    public String getVerificationChannelId() {
        return config.getString("bot.verification-channel-id");
    }

    public String getVerificationMessageId() {
        return config.getString("bot.verification-message-id");
    }

    public String getMemberRoleId() {
        return config.getString("bot.member-role-id");
    }

    public String getStatusChannelId() {
        return config.getString("bot.status-channel-id");
    }

    public String getStatusMessageId() {
        return config.getString("bot.status-message-id");
    }

    public void setVerificationMessageId(String messageId) {
        config.set("bot.verification-message-id", messageId);
        saveConfig();
    }

    public void setStatusMessageId(String messageId) {
        config.set("bot.status-message-id", messageId);
        saveConfig();
    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
