package me.bramhaag.bcf.context.contexts;

import me.bramhaag.bcf.context.CommandContexts;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class JDACommandContexts extends CommandContexts {

    public JDACommandContexts(JDA jda) {
        registerContext(User.class, c -> {
            String value = c.pop().replaceAll("[^0-9]", "");
            return jda.getUserById(value);
        });

        registerContext(TextChannel.class, c -> {
            String value = c.pop().replaceAll("[^0-9]", "");
            return jda.getTextChannelById(value);

        });

        registerContext(Guild.class, c -> {
            String value = c.pop().replaceAll("[^0-9]", "");
            return jda.getGuildById(value);
        });
    }
}
