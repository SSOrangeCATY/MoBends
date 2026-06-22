package goblinbob.mobends.standard.client.model.items;

import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBendsElytra extends ElytraModel
{
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public ModelBendsElytra(ModelPart root)
    {
        super(root);
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static LayerDefinition createLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left_wing",
                CubeListBuilder.create()
                        .texOffs(22, 0)
                        .addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, new CubeDeformation(1.0F)),
                PartPose.offset(5.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_wing",
                CubeListBuilder.create()
                        .texOffs(22, 0)
                        .mirror()
                        .addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, new CubeDeformation(1.0F)),
                PartPose.offset(-5.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(HumanoidRenderState renderState)
    {
        super.setupAnim(renderState);
        this.rightWing.x = -this.leftWing.x;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.y = this.leftWing.y;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.zRot = -this.leftWing.zRot;
    }
}
