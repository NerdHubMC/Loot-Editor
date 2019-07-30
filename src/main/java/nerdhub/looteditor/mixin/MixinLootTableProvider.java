package nerdhub.looteditor.mixin;

import nerdhub.looteditor.datapack.LootDatapackCreator;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = MinecraftServer.class, priority = 1001) //after fabric
public class MixinLootTableProvider {

    @Shadow @Final private ResourcePackContainerManager<ResourcePackContainer> dataPackContainerManager;

    @Inject(method = "loadWorldDataPacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackContainerManager;addCreator(Lnet/minecraft/resource/ResourcePackCreator;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void registerDatapackCreators(File levelFile, LevelProperties properties, CallbackInfo ci) {
        this.dataPackContainerManager.addCreator(new LootDatapackCreator());
    }
}
