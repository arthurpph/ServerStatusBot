package com.lunikmc.bot.managers;

import com.lunikmc.bot.LunikBot;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    private final File configFile;
    private final YamlConfiguration config;

    public ConfigManager(LunikBot lunikBot) {
        configFile = new File(lunikBot.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
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

    public void setVerificationMessageId(String messageId) {
        config.set("bot.verification-message-id", messageId);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
