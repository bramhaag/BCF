package me.bramhaag.bcf.context;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

public class DefaultContext implements Context {
    @NotNull private final Guild guild;
    @NotNull private final JDA jda;
    @NotNull private final User author;
    @NotNull private final Member member;
    @NotNull private final MessageChannel channel;
    
    public DefaultContext(@NotNull Guild guild, @NotNull JDA jda, @NotNull User author,
                          @NotNull Member member, @NotNull MessageChannel channel) {
        this.guild = guild;
        this.jda = jda;
        this.author = author;
        this.member = member;
        this.channel = channel;
    }
    
    @NotNull
    @Override
    public Guild getGuild() {
        return guild;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return jda;
    }
    
    @NotNull
    @Override
    public User getAuthor() {
        return author;
    }
    
    @NotNull
    @Override
    public Member getMember() {
        return member;
    }
    
    @NotNull
    @Override
    public MessageChannel getChannel() {
        return channel;
    }
}