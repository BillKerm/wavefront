package me.jebkerm.wavefront.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface ClientAccessor {
    @Accessor
    ClientWorld getWorld();

    @Accessor
    ClientPlayerEntity getPlayer();
}
