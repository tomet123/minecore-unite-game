package cz.tomet123.server.command.dev;

import cz.tomet123.server.Provider.GamePlayer;
import lombok.SneakyThrows;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import org.jetbrains.annotations.NotNull;

public class TestCooldownCommand extends Command {


    public TestCooldownCommand() {
        super("testCooldown");
        setDefaultExecutor(this::usage);
    }


    @SneakyThrows
    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        p.enableEffect(1);

        Thread.sleep(1000);

        p.disableEffect(1);
    }


}
