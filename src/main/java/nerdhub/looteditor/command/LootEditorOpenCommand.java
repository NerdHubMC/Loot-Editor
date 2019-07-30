package nerdhub.looteditor.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import nerdhub.looteditor.LootEditor;
import nerdhub.looteditor.loot.LootTableType;
import nerdhub.looteditor.network.S2CEditScreenPacket;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootSupplier;

import java.util.stream.Stream;

public class LootEditorOpenCommand {

    private static Stream<Identifier> getLootTables(CommandContext<ServerCommandSource> context) {
        LootManager lootManager = context.getSource().getMinecraftServer().getLootManager();
        return lootManager.getSupplierNames().stream().filter(identifier -> !LootEditor.MODID.equals(identifier.getNamespace()));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("loot_editor")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier())
                        .suggests((context, builder) -> CommandSource.suggestIdentifiers(getLootTables(context), builder))
                        .executes(context -> {
                            Identifier lootTableName = IdentifierArgumentType.getIdentifier(context, "loot_table");
                            if(getLootTables(context).anyMatch(lootTableName::equals)) {
                                LootManager manager = context.getSource().getMinecraftServer().getLootManager();
                                //TODO encode actual loot tables and send them to the client
                                LootTableType type = LootEditor.getSupplier(manager, lootTableName, LootTableType.REPLACE) != LootSupplier.EMPTY ? LootTableType.REPLACE : LootEditor.getSupplier(manager, lootTableName, LootTableType.INJECT) != LootSupplier.EMPTY ? LootTableType.INJECT : LootTableType.DEFAULT;
                                S2CEditScreenPacket.sendPacket(context.getSource().getPlayer(), lootTableName, type);
                                return 1;
                            }
                            context.getSource().sendFeedback(new LiteralText("unknown loot table: " + lootTableName).formatted(Formatting.RED), false);
                            return 0;
                        })
                )
        );
    }
}
