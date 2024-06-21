package com.lunikmc.bot;

import com.lunikmc.bot.botlisteners.ButtonInteractionListener;
import com.lunikmc.bot.managers.ConfigManager;
import com.lunikmc.bot.tasks.CreateVerificationMessageTask;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

@Getter
public final class LunikBot extends JavaPlugin {
    public final String DEFAULT_ERROR_MESSAGE = "Ocorreu um erro ao tentar verificar você, por favor entre em contato com algum staff.";
    public final Color DEFAULT_EMBED_COLOR = Color.YELLOW;

    private JDA bot;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createManagers();
        initializeBot();
    }

    @Override
    public void onDisable() {}

    private void initializeBot() {
        JDABuilder jdaBuilder = JDABuilder.createDefault(configManager.getBotToken());
        jdaBuilder.setActivity(Activity.playing("lunikmc.com"));
        jdaBuilder.addEventListeners(new ButtonInteractionListener(this));

        try {
            bot = jdaBuilder.build().awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new CreateVerificationMessageTask(this).run();
    }

    private void createManagers() {
        configManager = new ConfigManager(this);
    }
}
