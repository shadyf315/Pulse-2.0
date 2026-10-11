package dev.pulse.twopointzero;

import dev.pulse.twopointzero.config.PulseConfig;
import dev.pulse.twopointzero.gui.PulseSettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PulseClient implements ClientModInitializer {
    public static final String MOD_ID = "pulse_2_0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static boolean shiftWasDown;

    @Override
    public void onInitializeClient() {
        PulseConfig.load();
        registerHud();
        registerHitParticles();
        ClientTickEvents.END_CLIENT_TICK.register(PulseClient::onClientTick);
        LOGGER.info("Pulse 2.0 visuals loaded for Minecraft 1.21.11");
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null) return;
        boolean down = GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        if (down && !shiftWasDown && client.screen == null) client.setScreen(new PulseSettingsScreen());
        shiftWasDown = down;
    }

    private static void registerHud() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "target_hud"), (graphics, deltaTracker) -> {
            Minecraft client = Minecraft.getInstance();
            if (!PulseConfig.enabled || !PulseConfig.targetHud || client.player == null || client.screen != null) return;
            if (!(client.hitResult instanceof EntityHitResult hit)) return;
            if (!(hit.getEntity() instanceof LivingEntity target) || !target.isAlive()) return;

            int x = 14, y = 14, width = 148, height = 48;
            float health = Math.max(0.0f, target.getHealth());
            float maxHealth = Math.max(1.0f, target.getMaxHealth());
            int barWidth = Math.round((width - 18) * Math.min(1.0f, health / maxHealth));
            graphics.fill(x, y, x + width, y + height, 0xED0D0C13);
            graphics.fill(x, y, x + 3, y + height, 0xFF8758F5);
            graphics.drawString(client.font, target.getName(), x + 10, y + 8, 0xFFF4F5FA, false);
            graphics.drawString(client.font, String.format("%.1f / %.0f HP", health, maxHealth), x + 10, y + 22, 0xFFBEC4D1, false);
            graphics.fill(x + 10, y + 36, x + width - 8, y + 40, 0xFF292633);
            if (barWidth > 0) graphics.fill(x + 10, y + 36, x + 10 + barWidth, y + 40, 0xFF8758F5);
        });
    }

    private static void registerHitParticles() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() || !PulseConfig.enabled || !PulseConfig.hitParticles || !(entity instanceof LivingEntity living)) {
                return InteractionResult.PASS;
            }
            boolean critical = player.fallDistance > 0.0f && !player.onGround();
            var particle = critical ? ParticleTypes.CRIT : ParticleTypes.DAMAGE_INDICATOR;
            int count = critical ? 9 : 6;
            for (int i = 0; i < count; i++) {
                var random = player.getRandom();
                double px = living.getX() + (random.nextDouble() - 0.5) * living.getBbWidth();
                double py = living.getY() + random.nextDouble() * living.getBbHeight();
                double pz = living.getZ() + (random.nextDouble() - 0.5) * living.getBbWidth();
                world.addParticle(particle, px, py, pz,
                        (random.nextDouble() - 0.5) * 0.12,
                        0.12 + random.nextDouble() * 0.16,
                        (random.nextDouble() - 0.5) * 0.12);
            }
            return InteractionResult.PASS;
        });
    }
}
