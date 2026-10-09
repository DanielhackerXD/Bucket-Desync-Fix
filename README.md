# Bucket Desync Fix (MC-12363) — Forge 1.12.2 MDK project

Client-side backport of Mojang's fix for https://mojira.dev/MC-12363
(lava/water bucket desync when using a bucket while turning the camera quickly).

## How it works
`ItemBucket#onItemRightClick` raytraces on the SERVER with the last position/rotation the
client reported, so fast camera movement makes client and server pick different blocks.
The fix (Mojang, 21w11a / 1.17): right before the "use item" packet, the client sends its
CURRENT position+rotation. `MixinPlayerControllerMP` does exactly that for 1.12.2.

## Build
Needs JDK 8 (the MDK uses Gradle 4.9 + ForgeGradle 3).

    gradlew --refresh-dependencies
    gradlew build

The jar is in `build/libs/`. The task `reobfJar` runs automatically after `jar`.

## Install / test
Standalone SpongePowered Mixin 0.7.11 is bundled inside the jar and started through the manifest
(`TweakClass: org.spongepowered.asm.launch.MixinTweaker`, `MixinConfigs: mixins.bucketsyncfix.json`).
Just drop the jar in the `mods` folder of a Forge 1.12.2 instance - no MixinBooter needed.
If a modpack already contains MixinBooter (or another mod that bundles its own Mixin) there can be
conflicts between the two Mixin copies; in that case use the MixinBooter variant instead.

Reproduce: give yourself a lava bucket, spin the camera and place/pick up lava repeatedly.

