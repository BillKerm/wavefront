package me.jebkerm.wavefront.mixin.skybox;

import net.minecraft.world.LunarWorldView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LunarWorldView.class)
public class MoonRendererMixin {

    @Unique
    LunarWorldView lunarInterface = (LunarWorldView) this;

    @Inject(method = "getSkyAngle", at = @At("HEAD"))
    private void wavefront$getSkyAngle(float tickDelta, CallbackInfoReturnable<Float> ci){


    }
}
//TODO: get moon renderer working