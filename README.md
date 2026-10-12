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
Put the jar in the `mods` folder of a Forge 1.12.2 instance together with **any one** of these Mixin providers:

- [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixin-booter)
- [FermiumBooter](https://www.curseforge.com/minecraft/mc-mods/fermiumbooter)
- [Mixin 0.7-0.8 Compatibility](https://www.curseforge.com/minecraft/mc-mods/mixin-0-7-0-8-compatibility)
- [MixinBootstrap](https://www.curseforge.com/minecraft/mc-mods/mixinbootstrap) (file 1.1.0 or newer; its 1.1.0 jar supports 1.12.2)

The jar uses the standard Mixin manifest entries (`TweakClass: org.spongepowered.asm.launch.MixinTweaker`,
`MixinConfigs: mixins.bucketsyncfix.json`, `ForceLoadAsMod`). The Mixin library is not bundled; it comes from
whichever provider is installed. Forge's `@Mod(dependencies=...)` cannot express "any of these", so there is no
hard dependency: with NONE of the providers installed the game crashes on startup with
`ClassNotFoundException: org.spongepowered.asm.launch.MixinTweaker`.

There is no IDE `runClient` setup for the mixin (no provider on the dev runtime classpath);
test by building the jar and using a normal instance.

Reproduce: give yourself a lava bucket, spin the camera and place/pick up lava repeatedly.


