# Pulse 2.0 Visuals

Client-side visual mod for Minecraft Java 1.21.11 and Fabric.

## Modules

- Target HUD with a health bar while aiming at a living entity.
- Hit particles on entity attacks, with a distinct critical-hit burst.
- Settings screen opened with **Right Shift**; options are saved in `.minecraft/config/pulse-2-0.json`.

## Install

Install Fabric Loader for Minecraft 1.21.11 and Fabric API `0.141.6+1.21.11`, then put `pulse-2-0-0.1.0.jar` in the Minecraft `mods` folder. The built JAR is in [`dist/`](dist/pulse-2-0-0.1.0.jar); the local build output is in `build/libs/`.

## Build

Requires Java 21. Run `gradlew.bat build` on Windows or import the folder into IntelliJ IDEA with Gradle support; the distributable JAR is written to `build/libs/`.

Fabric API `0.141.6+1.21.11` is required.

## Attribution

This is a separate 1.21.11 implementation based on the behavior described in the linked Pulse Visuals reference. See `NOTICE` for feature-reference attribution.
