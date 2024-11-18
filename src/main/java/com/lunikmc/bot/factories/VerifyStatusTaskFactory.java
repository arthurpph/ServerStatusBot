package com.lunikmc.bot.factories;

import com.lunikmc.bot.LunikBot;
import com.lunikmc.bot.tasks.verifystatus.DiscordMessageService;
import com.lunikmc.bot.tasks.verifystatus.ServerStatusService;
import com.lunikmc.bot.tasks.verifystatus.VerifyStatusTask;
import net.dv8tion.jda.api.entities.Message;

public class VerifyStatusTaskFactory {

    private final LunikBot lunikBot;
    private final ServerStatusService serverStatusService;

    public VerifyStatusTaskFactory(LunikBot lunikBot, ServerStatusService serverStatusService) {
        this.lunikBot = lunikBot;
        this.serverStatusService = serverStatusService;
    }

    public VerifyStatusTask create() {
        DiscordMessageService discordMessageService = new DiscordMessageService(lunikBot);
        return new VerifyStatusTask(lunikBot, discordMessageService, serverStatusService);
    }

    public VerifyStatusTask create(Message message) {
        DiscordMessageService discordMessageService = new DiscordMessageService(lunikBot, message);
        return new VerifyStatusTask(lunikBot, discordMessageService, serverStatusService);
    }
}
