package com.lunikmc.bot.tasks.verifystatus;

import com.lunikmc.bot.LunikBot;
import com.lunikmc.bot.models.ServerStatus;
import com.lunikmc.bot.models.Status;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VerifyStatusTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyStatusTask.class);

    private final LunikBot lunikBot;
    private final DiscordMessageService discordMessageUpdater;
    private final ServerStatusService serverStatusService;

    private ScheduledTask scheduledTask;

    public VerifyStatusTask(LunikBot lunikBot, DiscordMessageService discordMessageUpdater, ServerStatusService serverStatusService) {
        this.lunikBot = lunikBot;
        this.discordMessageUpdater = discordMessageUpdater;
        this.serverStatusService = serverStatusService;
        startTask();
    }

    private void startTask() {
        scheduledTask = lunikBot.getProxy().getScheduler().schedule(lunikBot, this, 0, 5, TimeUnit.SECONDS);
    }

    public void proxyOffline() {
        scheduledTask.cancel();
        discordMessageUpdater.proxyOffline();
    }

    @Override
    public void run() {
        List<CompletableFuture<ServerStatus>> futures = serverStatusService.getServersStatus(lunikBot.getProxy());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        discordMessageUpdater.updateDiscordMessage(futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );
    }
}
