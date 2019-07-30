package nerdhub.looteditor.network.base;

import io.netty.buffer.Unpooled;
import nerdhub.looteditor.LootEditor;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class BasePacket {

    protected static Identifier id(String name, NetworkSide targetSide) {
        return new Identifier(LootEditor.MODID, String.format("%s_%s", targetSide == NetworkSide.SERVERBOUND ? "c2s": "s2c", name));
    }

    protected static PacketByteBuf buffer() {
        return new PacketByteBuf(Unpooled.buffer());
    }
}
