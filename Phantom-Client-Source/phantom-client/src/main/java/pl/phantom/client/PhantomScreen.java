```java
package pl.phantom.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticlesMode;
import java.util.ArrayList;
import java.util.List;

/** Custom dark client menu. All switches here change real saved settings. */
public final class PhantomScreen extends Screen {
    private enum Tab { PERFORMANCE, VISUALS, HUD, TEXTURES, SETTINGS }
    private Tab tab = Tab.PERFORMANCE;
    private final List<ButtonWidget> dynamic = new ArrayList<>();
    private int left, top, panelW = 430, panelH = 286;

    public PhantomScreen() {
        super(Text.literal("Phantom Client"));
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void rebuild() {
        clearChildren();
        dynamic.clear();

        left = (width - panelW) / 2;
        top = (height - panelH) / 2;

        int sx = left + 14;
        int sy = top + 66;

        String[] tabs = {"Performance", "Visuals", "HUD", "Textures", "Settings"};
        Tab[] values = Tab.values();

        for (int i = 0; i < tabs.length; i++) {
            final Tab next = values[i];
            addDrawableChild(
                ButtonWidget.builder(Text.literal(tabs[i]), b -> {
                    tab = next;
                    rebuild();
                }).dimensions(sx, sy + i * 35, 112, 28).build()
            );
        }

        int cx = left + 145;
        int cy = top + 77;
        int w = panelW - 164;

        if (tab == Tab.PERFORMANCE) {
            addSwitch(cx, cy, w, "FPS Counter",
                () -> PhantomConfig.get().showFps,
                v -> PhantomConfig.get().showFps = v);

            addSwitch(cx, cy + 39, w, "Reduced particles (preference)",
                () -> PhantomConfig.get().reducedParticles,
                v -> PhantomConfig.get().reducedParticles = v);

            addAction(cx, cy + 84, w, "Apply: Low Graphics profile", () -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.options.getCloudRenderMode().setValue(
                    net.minecraft.client.option.CloudRenderMode.OFF
                );
                mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
            });

            addAction(cx, cy + 122, w, "Apply: Balanced profile", () -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.options.getCloudRenderMode().setValue(
                    net.minecraft.client.option.CloudRenderMode.FAST
                );
                mc.options.getParticles().setValue(ParticlesMode.DECREASED);
            });

        } else if (tab == Tab.HUD) {
            addSwitch(cx, cy, w, "FPS Counter",
                () -> PhantomConfig.get().showFps,
                v -> PhantomConfig.get().showFps = v);

            addSwitch(cx, cy + 39, w, "Compact HUD (saved preference)",
                () -> PhantomConfig.get().compactHud,
                v -> PhantomConfig.get().compactHud = v);

        } else if (tab == Tab.VISUALS) {
            addAction(cx, cy, w, "Clouds: OFF", () ->
                MinecraftClient.getInstance().options.getCloudRenderMode()
                    .setValue(net.minecraft.client.option.CloudRenderMode.OFF)
            );

            addAction(cx, cy + 39, w, "Particles: MINIMAL", () ->
                MinecraftClient.getInstance().options.getParticles()
                    .setValue(ParticlesMode.MINIMAL)
            );

            addAction(cx, cy + 78, w, "Particles: ALL", () ->
                MinecraftClient.getInstance().options.getParticles()
                    .setValue(ParticlesMode.ALL)
            );

        } else if (tab == Tab.TEXTURES) {
            addAction(cx, cy, w, "Accent: Phantom Blue", () -> {
                PhantomConfig.get().accent = "BLUE";
                PhantomConfig.save();
                rebuild();
            });

            addAction(cx, cy + 39, w, "Accent: Violet", () -> {
                PhantomConfig.get().accent = "VIOLET";
                PhantomConfig.save();
                rebuild();
            });

        } else {
            addAction(cx, cy, w, "Save settings", PhantomConfig::save);

            addAction(cx, cy + 39, w, "Reset settings", () -> {
                PhantomConfig.load();
                PhantomConfig.get().showFps = true;
                PhantomConfig.get().reducedParticles = false;
                PhantomConfig.get().compactHud = false;
                PhantomConfig.get().accent = "BLUE";
                PhantomConfig.save();
                rebuild();
            });
        }
    }

    private interface BoolGet {
        boolean get();
    }

    private interface BoolSet {
        void set(boolean value);
    }

    private void addSwitch(
        int x, int y, int w, String name, BoolGet get, BoolSet set
    ) {
        addAction(x, y, w, name + ": " + (get.get() ? "ON" : "OFF"), () -> {
            set.set(!get.get());
            PhantomConfig.save();
            rebuild();
        });
    }

    private void addAction(int x, int y, int w, String label, Runnable action) {
        addDrawableChild(
            ButtonWidget.builder(Text.literal(label), b -> action.run())
                .dimensions(x, y, w, 29).build()
        );
    }

    @Override
    public void render(
        DrawContext ctx, int mouseX, int mouseY, float delta
    ) {
        renderBackground(ctx, mouseX, mouseY, delta);

        int accent = "VIOLET".equals(PhantomConfig.get().accent)
            ? 0xFF9B7CFF
            : 0xFF58A6FF;

        ctx.fill(left, top, left + panelW, top + panelH, 0xF011141D);
        ctx.fill(left, top, left + panelW, top + 3, accent);
        ctx.fill(left + 12, top + 52, left + panelW - 12, top + 53, 0xFF303747);

        ctx.drawText(textRenderer, "PHANTOM", left + 16, top + 15, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "CLIENT  /  PVP EDITION", left + 16, top + 31, 0xFF9AA8BE, false);
        ctx.drawText(textRenderer, tab.name(), left + 145, top + 57, accent, false);

        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawText(
            textRenderer,
            "Right Shift  •  Save: config/phantomclient.json",
            left + 15, top + panelH - 17, 0xFF8792A6, false
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        PhantomConfig.save();
        super.close();
    }
}
```
