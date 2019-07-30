package nerdhub.looteditor.datapack;

import com.google.gson.JsonObject;
import nerdhub.looteditor.LootEditor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LootDatapack implements ResourcePack {

    private static final String PREFIX = "loot_tables/";
    private static final int PREFIX_LENGTH = PREFIX.length();
    private final Path rootPath = LootEditor.getOrCreateInjectionDir();

    @Environment(EnvType.CLIENT)
    @Override
    public InputStream openRoot(String path) {
        throw new IllegalStateException("tried to load datapack on the client");
    }

    @Override
    public InputStream open(ResourceType type, Identifier path) throws IOException {
        if(type != ResourceType.SERVER_DATA) throw new FileNotFoundException("tried to access datapack on client");
        return Files.newInputStream(rootPath.resolve(path.getPath().substring(PREFIX_LENGTH)));
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String path, int var3, Predicate<String> pathPredicate) {
        if(type == ResourceType.SERVER_DATA && path.startsWith("loot_tables")) {
            try {
                return Files.walk(rootPath).filter(((Predicate<Path>) Files::isDirectory).negate()).map(rootPath::relativize).map(Path::toString).map(s -> s.replace('\\', '/')).map(s -> PREFIX + s).filter(s -> s.startsWith(path)).filter(pathPredicate).map(s -> new Identifier(LootEditor.MODID, s)).collect(Collectors.toSet());
            }
            catch (IOException e) {
                LootEditor.getLogger().error("error resolving resource paths for " + path, e);
            }
        }
        return Collections.emptySet();
    }

    @Override
    public boolean contains(ResourceType type, Identifier path) {
        if(type != ResourceType.SERVER_DATA || !LootEditor.MODID.equals(path.getNamespace()) || !path.getPath().startsWith(PREFIX)) {
            return false;
        }
        return Files.exists(rootPath.resolve(path.getPath().substring(PREFIX_LENGTH)));
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if(type == ResourceType.SERVER_DATA && Files.exists(rootPath)) {
            return Collections.singleton(LootEditor.MODID);
        }
        return Collections.emptySet();
        //return Optional.ofNullable(rootPath.toFile().listFiles(File::isDirectory)).map(Arrays::stream).orElseGet(Stream::empty).map(File::getName).collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> reader) {
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "LootEditor injection datapack");
        pack.addProperty("pack_format", 4);
        return reader.fromJson(pack);
    }

    @Override
    public String getName() {
        return "LootEditor";
    }

    @Override
    public void close() {
        //nothing to close
    }
}
