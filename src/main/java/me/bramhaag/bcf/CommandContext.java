package me.bramhaag.bcf;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

public class CommandContext {
    @NotNull private JDA jda;
    @NotNull private User author;
    @NotNull private Message message;
    @NotNull private MessageChannel channel;
    @NotNull private Guild guild;

    public CommandContext(@NotNull JDA jda, @NotNull User author, @NotNull Message message, @NotNull MessageChannel channel, @NotNull Guild guild) {
        this.jda = jda;
        this.author = author;
        this.message = message;
        this.channel = channel;
        this.guild = guild;
    }

    @NotNull
    public JDA getJda() {
        return jda;
    }

    @NotNull
    public User getAuthor() {
        return author;
    }

    @NotNull
    public Message getMessage() {
        return message;
    }

    @NotNull
    public MessageChannel getChannel() {
        return channel;
    }

    @NotNull
    public Guild getGuild() {
        return guild;
    }
}
