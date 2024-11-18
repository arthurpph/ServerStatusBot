package com.lunikmc.bot.tasks.verifystatus;

import com.lunikmc.bot.models.ServerStatus;
import com.lunikmc.bot.models.Status;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class ServerStatusService {
    private final ProxyServer proxy;

    public List<CompletableFuture<ServerStatus>> getServersStatus(ProxyServer proxy) {
        List<CompletableFuture<ServerStatus>> localFutures = new CopyOnWriteArrayList<>();

        proxy.getServers().values().forEach(registeredServer -> {
            CompletableFuture<ServerStatus> future = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                registeredServer.ping((serverPing, throwable) -> {
                    if (throwable != null || serverPing == null) {
                        future.complete(new ServerStatus(registeredServer, Status.OFFLINE, 0));
                    } else {
                        future.complete(new ServerStatus(registeredServer, Status.ONLINE, serverPing.getPlayers().getOnline()));
                    }
                });
            }).exceptionally(error -> {
                future.complete(new ServerStatus(registeredServer, Status.OFFLINE, 0));
                return null;
            });

            localFutures.add(future);
        });

        return localFutures;
    }
}
