package com.lunikmc.bot.botlisteners;

import com.lunikmc.bot.LunikBot;
import com.lunikmc.bot.exceptions.RoleNotFound;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class ButtonInteractionListener extends ListenerAdapter {
    private final LunikBot lunikBot;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if(buttonId == null) {
            return;
        }

        if(guild == null) {
            event.reply(lunikBot.DEFAULT_ERROR_MESSAGE).setEphemeral(true).queue();
            throw new NullPointerException("Guild can not null");
        }

        if(member == null) {
            event.reply(lunikBot.DEFAULT_ERROR_MESSAGE).setEphemeral(true).queue();
            throw new NullPointerException("Member can not null");
        }

        if(buttonId.equals("verification")) {
            if(lunikBot.getConfigManager().getMemberRoleId() == null) {
                event.reply(lunikBot.DEFAULT_ERROR_MESSAGE).setEphemeral(true).queue();
                throw new NullPointerException("Member role ID can not null");
            }

            Role role = guild.getRoleById(lunikBot.getConfigManager().getMemberRoleId());

            if(role == null) {
                event.reply(lunikBot.DEFAULT_ERROR_MESSAGE).setEphemeral(true).queue();
                throw new RoleNotFound("Member role not found on verification");
            }

            guild.addRoleToMember(member, role).queue(
                    success -> {
                        try {
                            event.reply("Você foi verificado com sucesso!").setEphemeral(true).queue();
                        } catch (Exception ignored) {}
                    },
                    error -> {
                        try {
                            event.reply(lunikBot.DEFAULT_ERROR_MESSAGE).setEphemeral(true).queue();
                        } catch (Exception ignored) {}
                    }
            );
        }
    }
}
