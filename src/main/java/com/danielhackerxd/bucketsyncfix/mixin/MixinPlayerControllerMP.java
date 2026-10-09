package com.danielhackerxd.bucketsyncfix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {


    @Inject(
            method = {"processRightClick", "func_187101_a"},
            at = @At("HEAD"),
            remap = false,
            require = 1
    )
    private void bucketsyncfix$syncLookBeforeUse(EntityPlayer player, World world, EnumHand hand,
                                                 CallbackInfoReturnable<EnumActionResult> cir) {

        if (player.isSpectator()) {
            return;
        }

        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return;
        }

        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;

        if (player.isRiding()) {

            connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, player.onGround));
        } else {
            connection.sendPacket(new CPacketPlayer.PositionRotation(
                    player.posX,
                    player.getEntityBoundingBox().minY,
                    player.posZ,
                    yaw,
                    pitch,
                    player.onGround
            ));
        }
    }
}
