package me.jebkerm.wavefront.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import me.jebkerm.wavefront.util.SkyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionEffects.class)
public class HorizonFogAdjusterMixin {

    @Shadow @Final private float[] rgba;
/*
    @Inject(method = "getFogColorOverride", at = @At("RETURN"), cancellable = true)
    private void wavefront$modifyFogCol(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> cir, @Local(name = "g") float localG){
        if (localG >= -0.4f && localG <= 0.4f) {cir.setReturnValue(null);}
        ClientWorld world = ((ClientAccessor) MinecraftClient.getInstance()).getWorld();
        float mask = MathHelper.clamp(SkyUtils.getOcclusionMask(world, tickDelta), 0.1f, 1.0f);
        rgba[0] *= mask;
        rgba[1] *= mask;
        rgba[2] *= mask;
        rgba[3] *= 1; //maybe set to mask? idk

        cir.setReturnValue(rgba);
    }
    
 */
}
