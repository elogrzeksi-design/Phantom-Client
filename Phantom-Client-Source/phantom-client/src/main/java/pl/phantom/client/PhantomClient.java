package pl.phantom.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class PhantomClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;
    @Override public void onInitializeClient() {
        PhantomConfig.load();
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.phantomclient.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyBinding.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen instanceof PhantomScreen) client.setScreen(null);
                else if (client.player != null) client.setScreen(new PhantomScreen());
            }
        });
    }
}
