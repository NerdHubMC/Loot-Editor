package nerdhub.looteditor.network;

import nerdhub.looteditor.client.gui.LootTableEditScreen;
import nerdhub.looteditor.loot.LootTableType;
import nerdhub.looteditor.network.base.BasePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class S2CEditScreenPacket extends BasePacket {

    public static final Identifier ID = id("edit_screen", NetworkSide.CLIENTBOUND);

    @Environment(EnvType.CLIENT)
    public static void onPacket(PacketContext context, PacketByteBuf byteBuf) {
        Identifier lootTableName = byteBuf.readIdentifier();
        LootTableType appliedType = byteBuf.readEnumConstant(LootTableType.class);
        context.getTaskQueue().execute(() -> MinecraftClient.getInstance().openScreen(new LootTableEditScreen(lootTableName, appliedType)));
    }

    public static void sendPacket(PlayerEntity player, Identifier lootTableName, LootTableType type) {
        PacketByteBuf buf = buffer();
        buf.writeIdentifier(lootTableName);
        buf.writeEnumConstant(type);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ID, buf);
    }
}
