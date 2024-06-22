package com.lunikmc.bot;

import com.lunikmc.bot.botlisteners.ButtonInteractionListener;
import com.lunikmc.bot.botlisteners.OnReadyListener;
import com.lunikmc.bot.managers.ConfigManager;
import com.lunikmc.bot.tasks.CreateVerificationMessageTask;
import com.lunikmc.bot.tasks.VerifyStatusTask;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.plugin.Plugin;

import java.awt.*;

@Getter
public final class LunikBot extends Plugin {
    public final String DEFAULT_ERROR_MESSAGE = "Ocorreu um erro, por favor entre em contato com algum staff.";
    public final Color DEFAULT_EMBED_COLOR = Color.YELLOW;
    private final EmbedBuilder defaultEmbed = new EmbedBuilder()
            .setColor(Color.YELLOW)
            .setTitle("Status - Lunik")
            .setFooter("Por favor reporte qualquer instabilidade para a staff.");

    private JDA bot;

    @Setter private VerifyStatusTask verifyStatusTask;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        createManagers();
        initializeBot();
    }

    @Override
    public void onDisable() {
        if(verifyStatusTask == null)
            return;

        verifyStatusTask.proxyOffline();
    }

    private void initializeBot() {
        JDABuilder jdaBuilder = JDABuilder.createDefault(configManager.getBotToken());
        jdaBuilder.setActivity(Activity.playing("lunikmc.com"));
        jdaBuilder.addEventListeners(
                new ButtonInteractionListener(this),
                new OnReadyListener(this)
        );

        bot = jdaBuilder.build();
    }

    private void createManagers() {
        configManager = new ConfigManager(this);
    }
}
