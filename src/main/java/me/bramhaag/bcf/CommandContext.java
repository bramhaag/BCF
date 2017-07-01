package me.bramhaag.bcf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

@AllArgsConstructor
public class CommandContext {
    @Getter private JDA jda;
    @Getter private User author;
    @Getter private Message message;
    @Getter private MessageChannel channel;
    @Getter private Guild guild;
}
