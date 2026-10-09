package pl.phantom.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Small persistent configuration stored in .minecraft/config/phantomclient.json. */
public final class PhantomConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("phantomclient.json");
    public boolean showFps = true;
    public boolean reducedParticles = false;
    public boolean compactHud = false;
    public String accent = "BLUE";
    private static PhantomConfig INSTANCE = new PhantomConfig();
    private PhantomConfig() {}
    public static PhantomConfig get() { return INSTANCE; }
    public static void load() {
        try { if (Files.exists(FILE)) { PhantomConfig loaded = GSON.fromJson(Files.readString(FILE), PhantomConfig.class); if (loaded != null) INSTANCE = loaded; } }
        catch (Exception ignored) { INSTANCE = new PhantomConfig(); }
    }
    public static void save() {
        try { Files.createDirectories(FILE.getParent()); Files.writeString(FILE, GSON.toJson(INSTANCE)); }
        catch (IOException e) { System.err.println("[Phantom Client] Could not save config: " + e.getMessage()); }
    }
}
