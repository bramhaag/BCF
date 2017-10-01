package me.bramhaag.bcf;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class CommandContext {
    @NotNull private final JDA jda;
    @NotNull private final User author;
    @NotNull private final Message message;
    @NotNull private final MessageChannel channel;
    @NotNull private final Guild guild;
}