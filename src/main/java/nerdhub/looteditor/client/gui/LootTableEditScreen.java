package nerdhub.looteditor.client.gui;

import nerdhub.looteditor.loot.LootTableType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;

public class LootTableEditScreen extends Screen {

    /**
     * the loot table that is currently being viewed
     */
    private final Identifier lootTable;
    private final LootTableType appliedType;
    //TODO search bar for loot tables
    //TODO REI integration

    public LootTableEditScreen(Identifier lootTable, LootTableType appliedType) {
        super(NarratorManager.EMPTY);
        this.lootTable = lootTable;
        this.appliedType = appliedType;
    }

    @Override
    protected void init() {
        super.init();
        //TODO parse loot table info
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        this.renderBackground();
        super.render(int_1, int_2, float_1);
        this.drawCenteredString(this.font, lootTable.toString(), width / 2, height / 2, 0xFFFFFFFF);
        this.drawCenteredString(this.font, appliedType.asString(), width / 2, height / 2 + font.fontHeight + 2, 0xFFFFFFFF);
    }
}
