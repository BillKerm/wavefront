package me.jebkerm.wavefront.item;

import me.jebkerm.wavefront.Wavefront;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    //new "basic" items here (always use the registerItem helper method!!!!!!!!1!!)
    public static final Item GRAVITIC_CONDENSATE = registerItem("gravitic_condensate", new Item(new FabricItemSettings()));
    //items with more functionality

    public static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(Wavefront.MOD_ID, name), item);
    }

    public static void registerModItems(){Wavefront.LOGGER.info("Registering items...");}
}
