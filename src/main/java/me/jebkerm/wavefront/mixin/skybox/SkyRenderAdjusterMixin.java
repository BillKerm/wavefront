package me.jebkerm.wavefront.mixin.skybox;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jebkerm.wavefront.mixin.client.ClientAccessor;
import me.jebkerm.wavefront.util.SkyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class SkyRenderAdjusterMixin {

    @Unique
    private static final Identifier SUN = new Identifier("wavefront:textures/environment/sun.png");
    //private static final Identifier SUN = new Identifier("textures/environment/sun.png");
    @Unique
    private static final Identifier MOON_PHASES = new Identifier("wavefront:textures/environment/moon_phases.png");
    //private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    @Unique
    private static final Identifier MOON_PHASES_GLOW = new Identifier("wavefront:textures/environment/moon_phases_glow.png");

    private float moonScalar = 0.97f;

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/math/MatrixStack$Entry;getPositionMatrix()Lorg/joml/Matrix4f;", ordinal = 2))
    private void wavefront$adjustRenderOrder(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(name = "matrix4f2") Matrix4f localMatrix, @Local(name = "bufferBuilder") BufferBuilder bufferBuilder){
        ClientWorld world = ((ClientAccessor) MinecraftClient.getInstance()).getWorld();
        if (world.getRegistryKey() == World.OVERWORLD) { // only do any of this if you're in the overworld
            float skyAngleDegrees = SkyUtils.getSkyAngleDegrees(world, tickDelta);
            float moonAngle = SkyUtils.getMoonAngle(world, tickDelta);
            float rainMask = 1.0f - world.getRainGradient(tickDelta);
            float localK;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);

            //render sun
            localK = 30.0f;
            RenderSystem.setShaderTexture(0, SUN);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, rainMask);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(localMatrix, -localK, 100.0f, -localK).texture(0.0f, 0.0f).next();
            bufferBuilder.vertex(localMatrix, localK, 100.0f, -localK).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(localMatrix, localK, 100.0f, localK).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(localMatrix, -localK, 100.0f, localK).texture(0.0f, 1.0f).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            //render moon
            RenderSystem.disableBlend(); //holy fucking shit
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(moonAngle - skyAngleDegrees));
            localMatrix.set(matrices.peek().getPositionMatrix());
            float UVScalar = 0.75f; //scales the UV coordinates of the moon quad, centered around a given square of the moon phase texture (please keep at 0.75f, or i will kill you)
            //int phaseIndex = world.getMoonPhase();
            int phaseIndex = (int) MathHelper.clamp(2.2 * (MathHelper.cos(MathHelper.angleBetween(skyAngleDegrees + 180, moonAngle) * (MathHelper.PI / 180)) + 0.92), 0, 4);
            if (MathHelper.subtractAngles(skyAngleDegrees, moonAngle) < 0) {
                phaseIndex = 8 - phaseIndex;
            }
            int col = phaseIndex % 4;
            int row = phaseIndex / 4 % 2;

            //fist pass; draw an opaque moon on a scaled-down quad
            float t = (col + (UVScalar / 2)) / 4.0f;
            float o = (row + (UVScalar / 2)) / 2.0f;
            float p = (col + (1 - (UVScalar / 2))) / 4.0f;
            float q = (row + (1 - (UVScalar / 2))) / 2.0f;
            RenderSystem.setShaderTexture(0, MOON_PHASES);
            localK = 5.0f; // original value was 20.0f
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(localMatrix, -localK, -100.0f * moonScalar, localK).texture(p, q).next();
            bufferBuilder.vertex(localMatrix, localK, -100.0f * moonScalar, localK).texture(t, q).next();
            bufferBuilder.vertex(localMatrix, localK, -100.0f * moonScalar, -localK).texture(t, o).next();
            bufferBuilder.vertex(localMatrix, -localK, -100.0f * moonScalar, -localK).texture(p, o).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            //second pass; render the moon glow texture, but with blending turned back on and on a normal-sized quad
            RenderSystem.enableBlend();
            t = (col + 0) / 4.0f;
            o = (row + 0) / 2.0f;
            p = (col + 1) / 4.0f;
            q = (row + 1) / 2.0f;
            RenderSystem.setShaderTexture(0, MOON_PHASES_GLOW); // get a different texture for the overlay
            localK = 20.0f;
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(localMatrix, -localK, -100.0f * moonScalar, localK).texture(p, q).next();
            bufferBuilder.vertex(localMatrix, localK, -100.0f * moonScalar, localK).texture(t, q).next();
            bufferBuilder.vertex(localMatrix, localK, -100.0f * moonScalar, -localK).texture(t, o).next();
            bufferBuilder.vertex(localMatrix, -localK, -100.0f * moonScalar, -localK).texture(p, o).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            //reset matrix stack after moon rendering
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(skyAngleDegrees - moonAngle));
            localMatrix.set(matrices.peek().getPositionMatrix());

            //commit heresy
            matrices.multiplyPositionMatrix(new Matrix4f().scale(100000));
            localMatrix.set(matrices.peek().getPositionMatrix());
        }
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferRenderer;drawWithGlobalProgram(Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;)V", ordinal = 2))
    private void wavefront$cleanUpMyMess(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local(name = "matrix4f2") Matrix4f localMatrix) {
        ClientWorld world = ((ClientAccessor) MinecraftClient.getInstance()).getWorld();
        if (world.getRegistryKey() == World.OVERWORLD) {
            matrices.multiplyPositionMatrix(new Matrix4f().scale(1 / 100000.0f));
            localMatrix.set(matrices.peek().getPositionMatrix());
        }
    }
}

//note from jeb at 1:42am:
//IT WORKS LETS FUCKING GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
//TODO: add solar eclipses?