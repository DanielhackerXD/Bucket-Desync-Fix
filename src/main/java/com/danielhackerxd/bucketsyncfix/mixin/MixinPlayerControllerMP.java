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

/**
 * MC-12363: "Lava / water / powder snow bucket desync when using it while rapidly turning".
 *
 * Cause: the server runs the bucket raytrace itself (ItemBucket#onItemRightClick) using the
 * LAST position/rotation it received from the client. Since the client only reports those
 * once per tick, the server can trace against a different look direction than the one the
 * client used for its own prediction -> client and server pick/place different blocks.
 *
 * Mojang's fix (snapshot 21w11a, 1.17): before sending the "use item" packet, the client
 * first sends its CURRENT position and rotation, so the server traces with exactly the same
 * view as the client. This mixin does the same thing for 1.12.2.
 *
 * The injection uses remap = false with both the MCP and the SRG name, so no refmap is
 * needed (works in the dev environment and in production). require = 1 makes the game fail
 * loudly at startup if neither name matches, instead of silently doing nothing.
 */
@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    // MCP: processRightClick | SRG: func_187101_a
    @Inject(
            method = {"processRightClick", "func_187101_a"},
            at = @At("HEAD"),
            remap = false,
            require = 1
    )
    private void bucketsyncfix$syncLookBeforeUse(EntityPlayer player, World world, EnumHand hand,
                                                 CallbackInfoReturnable<EnumActionResult> cir) {
        // Vanilla returns PASS immediately for spectators and sends nothing.
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
            // While riding, the server ignores the player's own position; only sync the rotation.
            connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, player.onGround));
        } else {
            connection.sendPacket(new CPacketPlayer.PositionRotation(
                    player.posX,
                    player.getEntityBoundingBox().minY, // same Y the vanilla movement packet uses (feet)
                    player.posZ,
                    yaw,
                    pitch,
                    player.onGround
            ));
        }
    }
}
