package com.lunikmc.bot.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.config.ServerInfo;

@RequiredArgsConstructor
@Getter
public class ServerStatus {
    private final ServerInfo serverInfo;
    private final Status status;
    private final int players;
}
