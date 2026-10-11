package dev.pulse.twopointzero.gui;

import dev.pulse.twopointzero.config.PulseConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class PulseSettingsScreen extends Screen {
    public PulseSettingsScreen() { super(Component.literal("Pulse Visuals")); }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        int top = height / 2 - 30;
        addRenderableWidget(toggle("Модуль", () -> PulseConfig.enabled, v -> PulseConfig.enabled = v, left, top));
        addRenderableWidget(toggle("Target HUD", () -> PulseConfig.targetHud, v -> PulseConfig.targetHud = v, left, top + 26));
        addRenderableWidget(toggle("Hit particles", () -> PulseConfig.hitParticles, v -> PulseConfig.hitParticles = v, left, top + 52));
        addRenderableWidget(Button.builder(Component.literal("Готово"), button -> onClose())
                .bounds(left, top + 88, 200, 20).build());
    }

    private Button toggle(String label, java.util.function.BooleanSupplier getter,
                          java.util.function.Consumer<Boolean> setter, int x, int y) {
        return Button.builder(Component.literal(label + ": " + (getter.getAsBoolean() ? "ON" : "OFF")), button -> {
            boolean next = !getter.getAsBoolean();
            setter.accept(next);
            button.setMessage(Component.literal(label + ": " + (next ? "ON" : "OFF")));
            PulseConfig.save();
        }).bounds(x, y, 200, 20).build();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xB90A0B10);
        int panelWidth = 242, panelHeight = 164;
        int x = width / 2 - panelWidth / 2, y = height / 2 - panelHeight / 2;
        graphics.fill(x, y, x + panelWidth, y + panelHeight, 0xF0121520);
        graphics.fill(x, y, x + panelWidth, y + 2, 0xFFEB3158);
        graphics.drawCenteredString(font, title, width / 2, y + 12, 0xFFF4F5FA);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        PulseConfig.save();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
