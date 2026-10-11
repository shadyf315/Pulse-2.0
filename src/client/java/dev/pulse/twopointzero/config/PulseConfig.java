package dev.pulse.twopointzero.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PulseConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("pulse-2-0.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean enabled = true;
    public static boolean targetHud = true;
    public static boolean hitParticles = true;

    private PulseConfig() { }

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Stored loaded = GSON.fromJson(reader, Stored.class);
            if (loaded != null) {
                enabled = loaded.enabled;
                targetHud = loaded.targetHud;
                hitParticles = loaded.hitParticles;
            }
        } catch (Exception exception) {
            LoggerFactory.getLogger("pulse_2_0").warn("Could not read config; defaults are active", exception);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) { GSON.toJson(new Stored(enabled, targetHud, hitParticles), writer); }
        } catch (Exception exception) {
            LoggerFactory.getLogger("pulse_2_0").warn("Could not save config", exception);
        }
    }

    private static final class Stored {
        boolean enabled = true;
        boolean targetHud = true;
        boolean hitParticles = true;

        Stored() { }
        Stored(boolean enabled, boolean targetHud, boolean hitParticles) {
            this.enabled = enabled;
            this.targetHud = targetHud;
            this.hitParticles = hitParticles;
        }
    }
}
