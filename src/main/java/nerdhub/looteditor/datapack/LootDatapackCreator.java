package nerdhub.looteditor.datapack;

import nerdhub.looteditor.LootEditor;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;

import java.util.Map;
import java.util.Optional;

public class LootDatapackCreator implements ResourcePackCreator {

    private static final boolean forceLoad = false;

    @Override
    public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
        Optional.ofNullable(ResourcePackContainer.of(LootEditor.MODID + "/inject", forceLoad, LootDatapack::new, factory, ResourcePackContainer.InsertionPosition.TOP)).ifPresent(container -> map.put(container.getName(), container));
    }
}
