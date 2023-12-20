package me.jebkerm.wavefront.item;

import me.jebkerm.wavefront.Wavefront;
import me.jebkerm.wavefront.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup WAVEFRONT_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Wavefront.MOD_ID, "gravitic_condensate"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.wavefront"))
                    .icon(() -> new ItemStack(ModItems.GRAVITIC_CONDENSATE)).entries((displayContext, entries) -> {
                        entries.add(ModItems.GRAVITIC_CONDENSATE);
                        entries.add(ModBlocks.COMPRESSED_STONE);
                        entries.add(ModBlocks.GRAVITIC_CATAPULT_PACKED);
                        //other stuff
                    }).build());

    public static void registerItemGroups(){
        Wavefront.LOGGER.info("Registering item groups...");
    }
}
