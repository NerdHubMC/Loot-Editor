package nerdhub.looteditor.loot;

import net.minecraft.util.StringIdentifiable;

public enum LootTableType implements StringIdentifiable {
    /**
     * injection present, but no full replacement
     */
    INJECT("inject"),
    /**
     * full replacement (injection might exist but has no effect)
     */
    REPLACE("replace"),
    /**
     * unmodified loot table, no injection or replacement present
     */
    DEFAULT("default");

    private final String name;

    LootTableType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
