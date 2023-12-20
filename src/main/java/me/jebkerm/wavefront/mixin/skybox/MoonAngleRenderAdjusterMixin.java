package me.jebkerm.wavefront.mixin.skybox;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class MoonAngleRenderAdjusterMixin {

    //testing
    @Unique
    private static final Identifier SUN = new Identifier("textures/environment/sun.png");
    @Unique
    private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");

    private float moonScalar = 0.97f;

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/math/MatrixStack$Entry;getPositionMatrix()Lorg/joml/Matrix4f;", ordinal = 2))
    private void wavefront$adjustRenderOrder(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(name = "matrix4f2") Matrix4f localMatrix, @Local(name = "bufferBuilder") BufferBuilder bufferBuilder){
        ClientWorld world = ((WorldRendererAccessor) (WorldRenderer) (Object) this).getWorld();
        float skyAngleDegrees = world.getSkyAngle(tickDelta) * 360.0f;
        long moonAngleMult = (((world.getTimeOfDay()-6000) / 24000L) % 4); //calculate which quadrant of the sky the moon should be in based on world time
        if (world.getTimeOfDay()-6000 <= 0) {moonAngleMult -= 1;}
        float moonAngle = (skyAngleDegrees + (moonAngleMult * 360.0f)) / 4.0f;

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(moonAngle-skyAngleDegrees));
        localMatrix.set(matrices.peek().getPositionMatrix());
        //render the MOON first, MINECRAFT!!
        float rainMask = 1.0f - world.getRainGradient(tickDelta);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        float localK = 20.0f;
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        int r = world.getMoonPhase();
        int s = r % 4;
        int m = r / 4 % 2;
        float t = (s + 0) / 4.0f;
        float o = (m + 0) / 2.0f;
        float p = (s + 1) / 4.0f;
        float q = (m + 1) / 2.0f;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(localMatrix, -localK, -100.0f * moonScalar, localK).texture(p, q).next();
        bufferBuilder.vertex(localMatrix, localK, -100.0f * moonScalar, localK).texture(t, q).next();
        bufferBuilder.vertex(localMatrix, localK, -100.0f * moonScalar, -localK).texture(t, o).next();
        bufferBuilder.vertex(localMatrix, -localK, -100.0f * moonScalar, -localK).texture(p, o).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        //reset matrix stack for sun rendering
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(skyAngleDegrees-moonAngle));
        localMatrix.set(matrices.peek().getPositionMatrix());

        localK = 30.0f;
        RenderSystem.setShaderTexture(0, SUN);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f, (float) (rainMask * (1-(Math.exp(-Math.pow((MathHelper.angleBetween(skyAngleDegrees+180,moonAngle)*(MathHelper.PI/180))/0.2f,2))))));
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(localMatrix, -localK, 100.0f, -localK).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(localMatrix, localK, 100.0f, -localK).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(localMatrix, localK, 100.0f, localK).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(localMatrix, -localK, 100.0f, localK).texture(0.0f, 1.0f).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        //commit heresy
        matrices.multiplyPositionMatrix(new Matrix4f().scale(100000));
        localMatrix.set(matrices.peek().getPositionMatrix());
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferRenderer;drawWithGlobalProgram(Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;)V", ordinal = 2))
    private void wavefront$cleanUpMyMess(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(name = "matrix4f2") Matrix4f localMatrix){
        matrices.multiplyPositionMatrix(new Matrix4f().scale(1/100000.0f));
        localMatrix.set(matrices.peek().getPositionMatrix());
    }
}

//note from jeb at 1:42am:
//IT WORKS LETS FUCKING GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
//TODO: add solar eclipses?