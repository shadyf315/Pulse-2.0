package dev.pulse.twopointzero.gui;

import dev.pulse.twopointzero.config.PulseConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Pulse-inspired settings panel, laid out from the public Pulse Visuals reference image. */
public final class PulseSettingsScreen extends Screen {
    private static final int PURPLE = 0xFF8758F5;
    private static final int WHITE = 0xFFF4F2FA;
    private static final int MUTED = 0xFF777485;
    private static final String[] TABS = {"Visuals", "HUD", "Utilities"};

    private int selectedTab;
    private String search = "";
    private int panelX, panelY, panelW, panelH;
    private int cardY, cardH, cardGap;
    private final List<CardHitbox> cardHitboxes = new ArrayList<>();

    public PulseSettingsScreen() { super(Component.literal("Pulse Visuals")); }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, width, height, 0x9906070B);
        panelW = Math.min(850, width - 36);
        panelH = Math.min(500, height - 34);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        cardHitboxes.clear();

        // Main dark canvas and top navigation, matching the wide Pulse layout.
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF50D0C13);
        g.fill(panelX, panelY, panelX + panelW, panelY + 1, 0xFF211E2C);
        int navY = panelY + 38;
        int navX = panelX + 30;
        for (int i = 0; i < TABS.length; i++) {
            int color = i == selectedTab ? WHITE : MUTED;
            g.drawString(font, TABS[i], navX, navY, color, false);
            int tabW = font.width(TABS[i]);
            if (i == selectedTab) g.fill(navX, navY + 19, navX + tabW, navY + 21, PURPLE);
            navX += tabW + 32;
        }

        int searchW = Math.min(220, Math.max(130, panelW / 4));
        int searchX = panelX + panelW - searchW - 60;
        int searchY = panelY + 28;
        g.fill(searchX, searchY, searchX + searchW, searchY + 28, 0xFF14131B);
        g.fill(searchX, searchY, searchX + searchW, searchY + 1, 0xFF292633);
        String query = search.isBlank() ? "Search" : search;
        g.drawString(font, "⌕", searchX + 10, searchY + 9, 0xFFAAA6B8, false);
        g.drawString(font, query, searchX + 29, searchY + 9, search.isBlank() ? MUTED : WHITE, false);

        int contentX = panelX + 30;
        int contentW = panelW - 60;
        int columnGap = 16;
        int cardW = (contentW - columnGap) / 2;
        cardY = panelY + 88;
        cardH = 54;
        cardGap = 12;

        List<Feature> features = featuresForTab();
        String normalized = search.trim().toLowerCase(java.util.Locale.ROOT);
        if (!normalized.isEmpty()) features = features.stream()
                .filter(f -> f.name.toLowerCase(java.util.Locale.ROOT).contains(normalized)
                        || f.description.toLowerCase(java.util.Locale.ROOT).contains(normalized)).toList();

        for (int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            int col = i % 2;
            int row = i / 2;
            int x = contentX + col * (cardW + columnGap);
            int y = cardY + row * (cardH + cardGap);
            drawCard(g, feature, x, y, cardW, cardH, mouseX, mouseY);
            cardHitboxes.add(new CardHitbox(x, y, cardW, cardH, feature));
        }

        // Floating hotkey card shown in the reference layout.
        int hotW = 182, hotH = 110;
        int hotX = panelX + panelW - hotW - 18;
        int hotY = panelY + panelH - hotH - 18;
        g.fill(hotX, hotY, hotX + hotW, hotY + hotH, 0xFF13121A);
        g.fill(hotX, hotY, hotX + hotW, hotY + 1, 0xFF282533);
        g.drawString(font, "⌨  Hot Keys", hotX + 14, hotY + 12, WHITE, false);
        g.drawString(font, "Menu", hotX + 14, hotY + 38, 0xFFB9B6C2, false);
        g.drawString(font, "RShift", hotX + hotW - 52, hotY + 38, 0xFF888493, false);
        g.drawString(font, "Target HUD", hotX + 14, hotY + 61, 0xFFB9B6C2, false);
        g.drawString(font, "Look", hotX + hotW - 42, hotY + 61, 0xFF888493, false);
        g.drawString(font, "Hit Particles", hotX + 14, hotY + 84, 0xFFB9B6C2, false);
        g.drawString(font, "Auto", hotX + hotW - 42, hotY + 84, 0xFF888493, false);

        super.render(g, mouseX, mouseY, delta);
    }

    private List<Feature> featuresForTab() {
        return switch (selectedTab) {
            case 0 -> List.of(
                    comingSoon("Aspect Ratio"), comingSoon("China Hit"),
                    comingSoon("Crosshair"), comingSoon("Custom Hand"),
                    comingSoon("Full Bright"), comingSoon("Hit Color"),
                    comingSoon("Hitbox Customizer"), comingSoon("Jump Circles"),
                    new Feature("Particles", "Combat hit particles", () -> PulseConfig.hitParticles,
                            value -> PulseConfig.hitParticles = value), comingSoon("Render Tweaks"),
                    comingSoon("Scope Overlay"), comingSoon("Tracers"));
            case 1 -> List.of(
                    new Feature("Target HUD", "Target health and status", () -> PulseConfig.targetHud,
                            value -> PulseConfig.targetHud = value),
                    comingSoon("Armor HUD"), comingSoon("Music Bar"), comingSoon("Status Effects"));
            default -> List.of(
                    new Feature("Pulse Visuals", "Master switch for available modules", () -> PulseConfig.enabled,
                            value -> PulseConfig.enabled = value),
                    comingSoon("AutoFish"), comingSoon("Hot Keys"), comingSoon("Render Tweaks"));
        };
    }

    private static Feature comingSoon(String name) {
        return new Feature(name, "Not available in this 1.21.11 build", null, null);
    }

    private void drawCard(GuiGraphics g, Feature feature, int x, int y, int w, int h, int mx, int my) {
        boolean available = feature.enabled != null;
        boolean on = available && feature.enabled.getAsBoolean();
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int bg = hovered ? 0xFF191722 : 0xFF121118;
        g.fill(x, y, x + w, y + h, bg);
        g.fill(x, y, x + w, y + 1, hovered ? 0xFF373144 : 0xFF1E1C26);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF1E1C26);
        g.drawString(font, feature.name, x + 18, y + 13, on ? WHITE : (available ? 0xFFB0ACBB : 0xFF656270), false);
        g.drawString(font, feature.description, x + 18, y + 31, available ? MUTED : 0xFF56535F, false);

        // Compact purple switch, drawn from rectangles for Minecraft's immediate-mode GUI.
        int sx = x + w - 48, sy = y + 18;
        g.fill(sx, sy, sx + 34, sy + 18, !available ? 0xFF1B1A21 : on ? 0xFF6D42D8 : 0xFF292733);
        int knobX = on ? sx + 19 : sx + 3;
        g.fill(knobX, sy + 3, knobX + 12, sy + 15, !available ? 0xFF3B3942 : on ? 0xFFF5F0FF : 0xFF777382);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x(), mouseY = event.y();
        if (event.button() != 0) return super.mouseClicked(event, doubleClick);
        int tabX = panelX + 30;
        int tabY = panelY + 38;
        for (int i = 0; i < TABS.length; i++) {
            int tabW = font.width(TABS[i]);
            if (mouseX >= tabX - 4 && mouseX <= tabX + tabW + 8 && mouseY >= tabY - 6 && mouseY <= tabY + 22) {
                selectedTab = i;
                return true;
            }
            tabX += tabW + 32;
        }
        for (CardHitbox hitbox : cardHitboxes) {
            if (mouseX >= hitbox.x && mouseX <= hitbox.x + hitbox.w
                    && mouseY >= hitbox.y && mouseY <= hitbox.y + hitbox.h) {
                Feature feature = hitbox.feature;
                if (feature.enabled == null) return true;
                feature.setter.accept(!feature.enabled.getAsBoolean());
                PulseConfig.save();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        char chr = (char) event.codepoint();
        if (!Character.isISOControl(chr)) {
            search += chr;
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == 256) { onClose(); return true; }
        if (keyCode == 259 && !search.isEmpty()) { search = search.substring(0, search.length() - 1); return true; }
        return super.keyPressed(event);
    }

    @Override public void onClose() { PulseConfig.save(); Minecraft.getInstance().setScreen(null); }
    @Override public boolean isPauseScreen() { return false; }

    private record Feature(String name, String description, java.util.function.BooleanSupplier enabled,
                           java.util.function.Consumer<Boolean> setter) { }
    private record CardHitbox(int x, int y, int w, int h, Feature feature) { }
}
