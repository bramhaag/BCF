package me.bramhaag.bcf.resolver.resolvers;

import me.bramhaag.bcf.resolver.ArgumentsResolver;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

public class JDAArgumentResolver extends ArgumentsResolver {

    @NotNull private JDA jda;

    public JDAArgumentResolver(@NotNull JDA jda) {
        this.jda = jda;
    }

    @Override
    public void register() {
        registerResolver(User.class, c -> {
            String value = c.pop().replaceAll("[^0-9]", "");
            return jda.getUserById(value);
        });

        registerResolver(TextChannel.class, c -> {
            String value = c.pop().replaceAll("[^0-9]", "");
            return jda.getTextChannelById(value);

        });

        registerResolver(Guild.class, c -> {
            String value = c.pop().replaceAll("[^0-9]", "");
            return jda.getGuildById(value);
        });
    }
}
