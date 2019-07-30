package nerdhub.looteditor.client;

import com.github.glasspane.mesh.api.annotation.CalledByReflection;
import nerdhub.looteditor.network.S2CEditScreenPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

@CalledByReflection
public class LootEditorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(S2CEditScreenPacket.ID, S2CEditScreenPacket::onPacket);
    }
}
