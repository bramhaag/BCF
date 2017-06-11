package me.bramhaag.bcf;

import lombok.Data;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class BaseCommand {

    private JDA jda = null;
    private User author = null;
    private Message message = null;
    private MessageChannel channel = null;
    private Guild guild = null;

    void setValues(JDA jda, User author, Message message, MessageChannel channel, Guild guild) {
        this.jda = jda;
        this.author = author;
        this.message = message;
        this.channel = channel;
        this.guild = guild;
    }

    private void setJda(JDA jda) {
        this.jda = jda;
    }

    private void setAuthor(User author) {
        this.author = author;
    }

    private void setMessage(Message message) {
        this.message = message;
    }

    private void setChannel(MessageChannel channel) {
        this.channel = channel;
    }

    private void setGuild(Guild guild) {
        this.guild = guild;
    }

    public JDA getJDA() {
        return jda;
    }

    public User getAuthor() {
        return author;
    }

    public Message getMessage() {
        return message;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public Guild getGuild() {
        return guild;
    }

    /*public BaseCommand copy() {
        BaseCommand copy = new BaseCommand();
        copy.setJda(this.jda);
        copy.setAuthor(this.author);
        copy.setMessage(this.message);
        copy.setChannel(this.channel);
        copy.setGuild(this.guild);

        return copy;
    }*/
}
