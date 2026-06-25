package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import goblinbob.mobends.core.client.Mesh;
import goblinbob.mobends.core.client.event.MoBendsRenderState;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.util.Color;
import goblinbob.mobends.core.util.MeshBuilder;
import goblinbob.mobends.standard.data.WolfData;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;

/**
 * Layer for rendering wolf mouth and tongue with Mo' Bends animations.
 * Updated for Minecraft 26.2 to use PoseStack-based rendering.
 */
public class LayerWolfMisc extends RenderLayer<WolfRenderState, WolfModel>
{

    private static final Identifier WOLF_MISC_TEXTURE = Identifier.fromNamespaceAndPath(ModStatics.MODID,
            "textures/entity/wolf_misc.png");
    private static final int textureWidth = 8;
    private static final int textureHeight = 8;

    private Mesh mouthBottom;
    private Mesh mouthTop;
    private Mesh mouthInside;
    private Mesh tongue;

    public LayerWolfMisc(RenderLayerParent<WolfRenderState, WolfModel> renderer)
    {
        super(renderer);

        mouthBottom = new Mesh(6);
        mouthBottom.beginDrawing();
        MeshBuilder.texturedXZPlane(mouthBottom, -1.5F, 0F, -4F, 3, 4, true, Color.WHITE,
                new int[] { 0, 0, 3, 4 }, textureWidth, textureHeight);
        mouthBottom.finishDrawing();

        mouthTop = new Mesh(6);
        mouthTop.beginDrawing();
        MeshBuilder.texturedXZPlane(mouthTop, -1.5F, 1F, -4F, 3, 4, false, Color.WHITE,
                new int[] { 0, 0, 3, 4 }, textureWidth, textureHeight);
        mouthTop.finishDrawing();

        mouthInside = new Mesh(6);
        mouthInside.beginDrawing();
        MeshBuilder.texturedXZPlane(mouthInside, -1.5F, -4.1F, -3.0F, 3, 1, true, Color.WHITE,
                new int[] { 0, 0, 3, 4 }, textureWidth, textureHeight);
        mouthInside.finishDrawing();

        tongue = new Mesh(6);
        tongue.beginDrawing();
        MeshBuilder.texturedXZPlane(tongue, -1.5F, 0F, -4F, 3, 6, true, Color.WHITE,
                new int[] { 3, 0, 6, 6 }, textureWidth, textureHeight);
        tongue.finishDrawing();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                       WolfRenderState state, float yRot, float xRot)
    {
        Wolf wolf = (Wolf) state.getRenderData(MoBendsRenderState.LIVING_ENTITY);
        if (wolf == null)
        {
            return;
        }

        final EntityData<?> entityData = EntityDatabase.instance.get(wolf);
        if (entityData instanceof WolfData)
        {
            final WolfData data = (WolfData) entityData;
            final float scale = 0.0625F;

            poseStack.pushPose();
            if (state.isBaby)
            {
                poseStack.translate(0.0F, 10.0F * scale, 0.0F * scale);
                data.body.applyLocalTransform(poseStack, scale * 0.5F);
            }
            else
            {
                data.body.applyLocalTransform(poseStack, scale);
            }
            data.head.applyLocalTransform(poseStack, scale);

            // Mouth inside
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(scale, scale, scale);
            mouthInside.render(poseStack, submitNodeCollector, WOLF_MISC_TEXTURE, packedLight);
            poseStack.popPose();

            // Mouth bottom
            poseStack.pushPose();
            data.nose.applyLocalTransform(poseStack, scale);
            poseStack.scale(scale, scale, scale);
            mouthTop.render(poseStack, submitNodeCollector, WOLF_MISC_TEXTURE, packedLight);
            poseStack.popPose();

            // Mouth top
            poseStack.pushPose();
            data.mouth.applyLocalTransform(poseStack, scale);
            poseStack.scale(scale, scale, scale);
            mouthBottom.render(poseStack, submitNodeCollector, WOLF_MISC_TEXTURE, packedLight);
            poseStack.popPose();

            // Tongue
            poseStack.pushPose();
            data.tongue.applyLocalTransform(poseStack, scale);
            poseStack.scale(scale, scale, scale);
            tongue.render(poseStack, submitNodeCollector, WOLF_MISC_TEXTURE, packedLight);
            poseStack.popPose();

            poseStack.popPose();
        }
    }

}
