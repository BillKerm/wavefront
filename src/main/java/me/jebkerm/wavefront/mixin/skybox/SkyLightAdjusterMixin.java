package me.jebkerm.wavefront.mixin.skybox;

import com.llamalad7.mixinextras.sugar.Local;
import me.jebkerm.wavefront.util.SkyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class SkyLightAdjusterMixin {

    @Shadow @Final private MinecraftClient client;
    @Inject(method = "getSkyBrightness", at = @At("TAIL"), cancellable = true)
    private void wavefront$modifySkyLight(float tickDelta, CallbackInfoReturnable<Float> cir, @Local(name = "g") float localG){
        if ((client.world != null)&&(client.world.getRegistryKey() == World.OVERWORLD)) {
            localG *= (float) MathHelper.clamp((1.0 - SkyUtils.getOcclusionMask(client.world, tickDelta)), 0.1, 1.0);
            localG *= (1.0f - client.world.getThunderGradient(tickDelta) * 5.0f / 16.0f) * 0.8f + 0.2f;
            cir.setReturnValue(localG);
        }
    }

    @Inject(method = "getSkyColor", at = @At("TAIL"), cancellable = true)
    private void wavefront$modifySkyCol(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir, @Local(name = "h") float localH, @Local(name = "i") float localI, @Local(name = "j") float localJ){
        if ((client.world != null)&&(client.world.getRegistryKey() == World.OVERWORLD)) {
            float mask = (float) (1.0 - SkyUtils.getOcclusionMask(client.world, tickDelta));
            localH *= mask;
            localI *= mask;
            localJ *= mask;
            localH = (float) MathHelper.clamp(localH, 0.1, 1);
            localI = (float) MathHelper.clamp(localI, 0.1, 1);
            localJ = (float) MathHelper.clamp(localJ, 0.1, 1);
            cir.setReturnValue(new Vec3d(localH, localI, localJ));
        }
    }

    @Inject(method = "method_23787", at= @At("TAIL"), cancellable = true)
    private void wavefront$modifyStarAlpha(float tickDelta, CallbackInfoReturnable<Float> cir, @Local(name = "h") float LocalH){
        if ((client.world != null)&&(client.world.getRegistryKey() == World.OVERWORLD)) {
            cir.setReturnValue((float) (LocalH * LocalH * 0.5) + (SkyUtils.getOcclusionMask(client.world, tickDelta) / 2));
        }
    }

}
