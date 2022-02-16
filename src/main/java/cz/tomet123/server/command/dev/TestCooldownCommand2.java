package cz.tomet123.server.command.dev;

import cz.tomet123.server.utils.player.GamePlayer;
import lombok.SneakyThrows;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class TestCooldownCommand2 extends Command {


    public TestCooldownCommand2() {
        super("checkCooldown");
        setDefaultExecutor(this::usage);
    }


    @SneakyThrows
    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;


        for (int x = (int)p.getPosition().x()-10; x < (int)p.getPosition().x()+10; x++) {
            for (int z = (int)p.getPosition().z()-10; z < (int)p.getPosition().z()+10; z++) {
                if(p.getInstance().getBlock(x,40,z).isAir()){
                    p.getInstance().setBlock(x,40,z, Block.GRASS_BLOCK);
                }
            }
        }

        p.sendMessage("can use agin"+p.canUseEffect(1));
    }


}
