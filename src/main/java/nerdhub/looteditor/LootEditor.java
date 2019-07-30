package nerdhub.looteditor;

import com.github.glasspane.mesh.api.annotation.CalledByReflection;
import com.mojang.brigadier.CommandDispatcher;
import nerdhub.looteditor.command.LootEditorOpenCommand;
import nerdhub.looteditor.loot.LootTableType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootSupplier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CalledByReflection
public class LootEditor implements ModInitializer {

    public static final String MODID = "loot_editor";
    private static final Logger log = LogManager.getLogger(MODID);
    private static Path injectionDir;

    public static Logger getLogger() {
        return log;
    }

    static {
        if(Boolean.getBoolean("mesh.debug")) Configurator.setLevel(MODID, Level.ALL);
    }

    public static Path getOrCreateInjectionDir() {
        if(injectionDir == null) {
            injectionDir = FabricLoader.getInstance().getGameDirectory().toPath().resolve(MODID);
            for(LootTableType type : LootTableType.values()) {
                if(type != LootTableType.DEFAULT) {
                    try {
                        Path typePath = injectionDir.resolve(type.asString());
                        if(!Files.exists(typePath)) {
                            Files.createDirectories(typePath);
                        }
                    }
                    catch (IOException e) {
                        log.error("error creating injection directory: " + type.asString(), e);
                    }
                }
            }
        }
        return injectionDir;
    }

    public static LootSupplier getSupplier(LootManager manager, Identifier original, LootTableType type) {
        if(type == LootTableType.DEFAULT) return manager.getSupplier(original);
        else return manager.getSupplier(new Identifier(MODID, String.format("%s/%s/%s", type.asString(), original.getNamespace(), original.getPath())));
    }

    @Override
    public void onInitialize() {
        getOrCreateInjectionDir();
        ServerStartCallback.EVENT.register(server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            LootEditorOpenCommand.register(dispatcher);
        });
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, lootTableName, supplier, setter) -> {
            if(MODID.equals(lootTableName.getNamespace()) && (lootTableName.getPath().startsWith("inject/") || lootTableName.getPath().startsWith("replace/"))) {
                return; //don't inject into our own injection loot tables
            }
            LootSupplier replacementSupplier = getSupplier(lootManager, lootTableName, LootTableType.REPLACE);
            if(replacementSupplier != LootSupplier.EMPTY) {
                log.debug("adding loot table replacement for {}", lootTableName);
                setter.set(replacementSupplier);
            }
            else {
                LootSupplier extraSupplier = getSupplier(lootManager, lootTableName, LootTableType.INJECT);
                if(extraSupplier != LootSupplier.EMPTY) {
                    log.debug("adding loot table injection for {}", lootTableName);
                    supplier.withPools(((FabricLootSupplier) extraSupplier).getPools());
                }
            }
        });
    }
}
