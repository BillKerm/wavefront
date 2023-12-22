package me.jebkerm.wavefront.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class SkyUtils {

    public static float getSkyAngleDegrees(ClientWorld world, float tickDelta){return world.getSkyAngle(tickDelta) * 360.0f;}

    public static float getMoonAngle(ClientWorld world, float tickDelta){
        long moonAngleMult = (((world.getTimeOfDay()-6000) / 24000L) % 4); //calculate which quadrant of the sky the moon should be in based on world time
        if (world.getTimeOfDay()-6000 <= 0) {moonAngleMult -= 1;}
        return (getSkyAngleDegrees(world, tickDelta) + (moonAngleMult * 360.0f)) / 4.0f;
    }

    public static float getOcclusionMask(ClientWorld world, float tickDelta){
        //gaussian kernel for a nice, smooth transition from zero to one in a very specific interval
        //pretty costly, but it's only called on the client like 5 times every render call :)
        //TODO: implement other kernels to select from?
        return (float)(Math.exp(-Math.pow((MathHelper.angleBetween(getSkyAngleDegrees(world, tickDelta)+180,getMoonAngle(world, tickDelta))*(MathHelper.PI/180))/0.1f,2)));
    }
}
