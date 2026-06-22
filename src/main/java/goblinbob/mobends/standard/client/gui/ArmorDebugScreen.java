package goblinbob.mobends.standard.client.gui;

import goblinbob.mobends.standard.client.model.armor.ArmorDebugRenderer;
import goblinbob.mobends.standard.client.model.armor.BoneRegion;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

public class ArmorDebugScreen extends Screen
{
    private static boolean showOriginalVertices = true;
    private static boolean showTransformedVertices = true;
    private static boolean showBoneRegions = true;
    private static boolean showBoneAxes = true;
    private static boolean showTestGeometry = false;
    private static boolean freezeAnimation = false;
    private static float debugScale = 50.0f;

    private static final Map<BoneRegion, Boolean> boneEnabled = new EnumMap<>(BoneRegion.class);

    static
    {
        for (BoneRegion region : BoneRegion.values())
        {
            boneEnabled.put(region, true);
        }
    }

    public ArmorDebugScreen()
    {
        super(Component.literal("Armor Debug"));
    }

    @Override
    protected void init()
    {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int x = 10;
        int y = 30;
        int spacing = 22;

        addRenderableWidget(Button.builder(Component.literal("Original: " + (showOriginalVertices ? "ON" : "OFF")), btn -> {
            showOriginalVertices = !showOriginalVertices;
            btn.setMessage(Component.literal("Original: " + (showOriginalVertices ? "ON" : "OFF")));
        }).pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Transformed: " + (showTransformedVertices ? "ON" : "OFF")), btn -> {
            showTransformedVertices = !showTransformedVertices;
            btn.setMessage(Component.literal("Transformed: " + (showTransformedVertices ? "ON" : "OFF")));
        }).pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Bone Regions: " + (showBoneRegions ? "ON" : "OFF")), btn -> {
            showBoneRegions = !showBoneRegions;
            btn.setMessage(Component.literal("Bone Regions: " + (showBoneRegions ? "ON" : "OFF")));
        }).pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Bone Axes: " + (showBoneAxes ? "ON" : "OFF")), btn -> {
            showBoneAxes = !showBoneAxes;
            btn.setMessage(Component.literal("Bone Axes: " + (showBoneAxes ? "ON" : "OFF")));
        }).pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Freeze: " + (freezeAnimation ? "ON" : "OFF")), btn -> {
            freezeAnimation = !freezeAnimation;
            btn.setMessage(Component.literal("Freeze: " + (freezeAnimation ? "ON" : "OFF")));
        }).pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Test Geo: " + (showTestGeometry ? "ON" : "OFF")), btn -> {
            showTestGeometry = !showTestGeometry;
            btn.setMessage(Component.literal("Test Geo: " + (showTestGeometry ? "ON" : "OFF")));
        }).pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Scale -"), btn -> debugScale = Math.max(10, debugScale - 10))
                .pos(x, y).size(55, buttonHeight).build());
        addRenderableWidget(Button.builder(Component.literal("Scale +"), btn -> debugScale = Math.min(200, debugScale + 10))
                .pos(x + 60, y).size(55, buttonHeight).build());
        y += spacing;

        addRenderableWidget(Button.builder(Component.literal("Reset"), btn -> debugScale = 50.0f)
                .pos(x, y).size(buttonWidth, buttonHeight).build());
        y += spacing + 10;

        BoneRegion[] regions = BoneRegion.values();
        for (BoneRegion region : regions)
        {
            int finalY = y;
            addRenderableWidget(Button.builder(Component.literal(getShortBoneName(region) + ": " + (boneEnabled.get(region) ? "ON" : "OFF")), btn -> {
                boneEnabled.put(region, !boneEnabled.get(region));
                btn.setMessage(Component.literal(getShortBoneName(region) + ": " + (boneEnabled.get(region) ? "ON" : "OFF")));
            }).pos(x, finalY).size(buttonWidth, buttonHeight).build());
            y += spacing;
        }
    }

    private String getShortBoneName(BoneRegion region)
    {
        return switch (region)
        {
            case HEAD -> "Head";
            case BODY -> "Body";
            case LEFT_ARM_UPPER -> "L.Arm Up";
            case LEFT_ARM_LOWER -> "L.Arm Low";
            case RIGHT_ARM_UPPER -> "R.Arm Up";
            case RIGHT_ARM_LOWER -> "R.Arm Low";
            case LEFT_LEG_UPPER -> "L.Leg Up";
            case LEFT_LEG_LOWER -> "L.Leg Low";
            case RIGHT_LEG_UPPER -> "R.Leg Up";
            case RIGHT_LEG_LOWER -> "R.Leg Low";
            case ROOT -> "Root";
        };
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks)
    {
        this.extractBackground(guiGraphicsExtractor, mouseX, mouseY, partialTicks);
        ActiveTextCollector text = guiGraphicsExtractor.textRenderer();
        text.accept(TextAlignment.CENTER, width / 2, 10, Component.literal("Armor Debug"));

        int infoX = width - 200;
        int infoY = 30;
        text.accept(infoX, infoY, Component.literal("Scale: " + (int) debugScale));
        infoY += 12;
        text.accept(infoX, infoY, Component.literal("Vertices: " + ArmorDebugRenderer.getCapturedVertexCount()));
        infoY += 12;
        text.accept(infoX, infoY, Component.literal("Armor: " + ArmorDebugRenderer.getArmorSlotInfo()));
        infoY += 12;
        text.accept(infoX, infoY, Component.literal(ArmorDebugRenderer.getCaptureStatus()));
        infoY += 20;
        text.accept(infoX, infoY, Component.literal("3D preview disabled for 26.2 compatibility"));

        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public static boolean isFreezeAnimation()
    {
        return freezeAnimation;
    }

    public static boolean isBoneEnabled(BoneRegion region)
    {
        return boneEnabled.getOrDefault(region, true);
    }

    public static boolean isShowTestGeometry()
    {
        return showTestGeometry;
    }
}
