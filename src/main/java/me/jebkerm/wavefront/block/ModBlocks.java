package me.jebkerm.wavefront.block;

import me.jebkerm.wavefront.Wavefront;
import me.jebkerm.wavefront.block.custom.GraviticCatapultPackedBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    //"basic" blocks with barely any functionality
    public static final Block COMPRESSED_STONE = registerBlock("compressed_stone",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).strength(2.0f,10.0f)));

    //more complex blocks with custom shit
    public static final Block GRAVITIC_CATAPULT_PACKED = registerBlock("gravitic_catapult_packed",
            new GraviticCatapultPackedBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));


    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(Wavefront.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(Wavefront.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks(){Wavefront.LOGGER.info("Registering blocks...");}
}
