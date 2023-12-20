package me.jebkerm.wavefront.mixin.skybox;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class MoonAngleRenderAdjusterMixin {

    @Unique
    WorldRenderer worldRenderer = (WorldRenderer) (Object) this;

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(Lnet/minecraft/client/render/VertexFormat$DrawMode;Lnet/minecraft/client/render/VertexFormat;)V", ordinal = 2))
    private void wavefront$renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(name = "matrix4f2") Matrix4f localMatrix){
        //commence the calculations!
        ClientWorld world = ((WorldRendererAccessor) worldRenderer).getWorld();
        float skyAngleDegrees = world.getSkyAngle(tickDelta) * 360.0f;
        long moonAngleMult = (((world.getTimeOfDay()-6000) / 24000L) % 4); //calculate which quadrant of the sky the moon should be in based on world time
        if (world.getTimeOfDay()-6000 <= 0) {moonAngleMult -= 1;}

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-1.0f * skyAngleDegrees));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(((skyAngleDegrees + (moonAngleMult * 360.0f)) / 4.0f)));
        localMatrix.set(matrices.peek().getPositionMatrix());
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferRenderer;drawWithGlobalProgram(Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;)V", ordinal = 2))
    private void wavefront$cleanUpMyMess(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci){
        //idc that im getting these values a second time, i just want this to work and its like 12:30am pls help me
        ClientWorld world = ((WorldRendererAccessor) worldRenderer).getWorld();
        float skyAngleDegrees = world.getSkyAngle(tickDelta) * 360.0f;
        long moonAngleMult = (((world.getTimeOfDay()-6000) / 24000L) % 4);
        if (world.getTimeOfDay()-6000 <= 0) {moonAngleMult -= 1;}

        //reset the matrix stack to detach the stars from the moon
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-1.0f * (((skyAngleDegrees + (moonAngleMult * 360.0f)) / 4.0f))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(skyAngleDegrees));
    }
}

//note from jeb at 1:42am:
//IT WORKS LETS FUCKING GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
//TODO: add solar eclipses?