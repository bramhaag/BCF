package me.bramhaag.bcf;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class CommandContext {
    @NotNull private final JDA jda;
    @NotNull private final User author;
    @NotNull private final Message message;
    @NotNull private final MessageChannel channel;
    @NotNull private final Guild guild;
    
    @NotNull private final Map<String, String> flags;
    @NotNull private final Set<String> switches;
    
    public boolean hasFlag(String name) {
        return flags.containsKey(name);
    }
    
    @Nullable
    public String getFlag(String name) {
        return flags.get(name);
    }
    
    public boolean hasSwitch(String name) {
        return switches.contains(name);
    }
}
