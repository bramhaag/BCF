package me.bramhaag.bcf;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {
    @NotNull private JDA jda;
    @NotNull private User author;
    @NotNull private Message message;
    @NotNull private MessageChannel channel;
    @NotNull private Guild guild;

    @NotNull private Map<String, String> flags;

    public CommandContext(@NotNull JDA jda, @NotNull User author, @NotNull Message message, @NotNull MessageChannel channel, @NotNull Guild guild, @NotNull Map<String, String> flags) {
        this.jda = jda;
        this.author = author;
        this.message = message;
        this.channel = channel;
        this.guild = guild;
        this.flags = flags;
    }

    @NotNull
    public JDA getJDA() {
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

    @NotNull
    public Map<String, String> getFlags() {
        return flags;
    }

    public boolean hasFlag(String name) {
        return flags.containsKey(name) ;
    }

    @Nullable
    public String getFlag(String name) {
        return flags.get(name);
    }
}
