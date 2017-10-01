package me.bramhaag.bcf.context;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

public interface Context {
    @NotNull Guild getGuild();
    @NotNull JDA getJDA();
    @NotNull User getAuthor();
    @NotNull Member getMember();
    @NotNull MessageChannel getChannel();
}