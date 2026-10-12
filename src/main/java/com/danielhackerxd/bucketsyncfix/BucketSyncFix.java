package com.danielhackerxd.bucketsyncfix;

import net.minecraftforge.fml.common.Mod;

/**
 * Client-side only. All the logic lives in the mixin
 * {@link com.danielhackerxd.bucketsyncfix.mixin.MixinPlayerControllerMP}; this class only
 * exists so the jar shows up as a proper mod in the mod list.
 */
@Mod(
        modid = BucketSyncFix.MODID,
        name = BucketSyncFix.NAME,
        version = BucketSyncFix.VERSION,
        clientSideOnly = true,
        acceptedMinecraftVersions = "[1.12.2]"
)
public class BucketSyncFix {

    public static final String MODID = "bucketsyncfix";
    public static final String NAME = "Bucket Desync Fix (MC-12363)";
    public static final String VERSION = "1.0.0";
}
