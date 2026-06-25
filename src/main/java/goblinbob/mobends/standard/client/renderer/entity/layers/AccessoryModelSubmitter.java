package goblinbob.mobends.standard.client.renderer.entity.layers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import goblinbob.mobends.core.asset.AssetLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;

final class AccessoryModelSubmitter
{
    private static final float MODEL_SCALE = 1.0F / 16.0F;

    private AccessoryModelSubmitter()
    {
    }

    static void submit(JsonElement modelJson, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                       AssetLocation texture, int packedLight, int color)
    {
        if (modelJson == null || !modelJson.isJsonObject() || texture == null)
        {
            return;
        }

        JsonObject model = modelJson.getAsJsonObject();
        if (!model.has("elements") || !model.get("elements").isJsonArray())
        {
            return;
        }

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture.getResourceLocation()),
                (pose, vertexConsumer) -> {
                    PoseStack submittedPoseStack = new PoseStack();
                    submittedPoseStack.last().set(pose);
                    applyHeadDisplayTransform(model, submittedPoseStack);
                    renderElements(model, submittedPoseStack, vertexConsumer, packedLight, color);
                });
    }

    private static void applyHeadDisplayTransform(JsonObject model, PoseStack poseStack)
    {
        if (!model.has("display"))
        {
            return;
        }

        JsonObject display = model.getAsJsonObject("display");
        if (!display.has("head"))
        {
            return;
        }

        JsonObject head = display.getAsJsonObject("head");
        applyTranslation(poseStack, head.getAsJsonArray("translation"));
        applyRotation(poseStack, head.getAsJsonArray("rotation"));
        applyScale(poseStack, head.getAsJsonArray("scale"));
    }

    private static void applyTranslation(PoseStack poseStack, JsonArray translation)
    {
        if (translation == null || translation.size() < 3)
        {
            return;
        }

        poseStack.translate(
                translation.get(0).getAsFloat() * MODEL_SCALE,
                translation.get(1).getAsFloat() * MODEL_SCALE,
                translation.get(2).getAsFloat() * MODEL_SCALE);
    }

    private static void applyRotation(PoseStack poseStack, JsonArray rotation)
    {
        if (rotation == null || rotation.size() < 3)
        {
            return;
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(rotation.get(0).getAsFloat()));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation.get(1).getAsFloat()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation.get(2).getAsFloat()));
    }

    private static void applyScale(PoseStack poseStack, JsonArray scale)
    {
        if (scale == null || scale.size() < 3)
        {
            return;
        }

        poseStack.scale(scale.get(0).getAsFloat(), scale.get(1).getAsFloat(), scale.get(2).getAsFloat());
    }

    private static void renderElements(JsonObject model, PoseStack poseStack, VertexConsumer vertexConsumer,
                                       int packedLight, int color)
    {
        float[] textureSize = readTextureSize(model);
        for (JsonElement elementValue : model.getAsJsonArray("elements"))
        {
            if (!elementValue.isJsonObject())
            {
                continue;
            }

            JsonObject element = elementValue.getAsJsonObject();
            if (!element.has("from") || !element.has("to") || !element.has("faces"))
            {
                continue;
            }

            Vector3f from = readVector(element.getAsJsonArray("from"));
            Vector3f to = readVector(element.getAsJsonArray("to"));
            JsonObject faces = element.getAsJsonObject("faces");

            submitFace(vertexConsumer, poseStack, packedLight, color, textureSize, element, faces, "north",
                    vertex(to.x, from.y, from.z), vertex(from.x, from.y, from.z),
                    vertex(from.x, to.y, from.z), vertex(to.x, to.y, from.z));
            submitFace(vertexConsumer, poseStack, packedLight, color, textureSize, element, faces, "south",
                    vertex(from.x, from.y, to.z), vertex(to.x, from.y, to.z),
                    vertex(to.x, to.y, to.z), vertex(from.x, to.y, to.z));
            submitFace(vertexConsumer, poseStack, packedLight, color, textureSize, element, faces, "west",
                    vertex(from.x, from.y, from.z), vertex(from.x, from.y, to.z),
                    vertex(from.x, to.y, to.z), vertex(from.x, to.y, from.z));
            submitFace(vertexConsumer, poseStack, packedLight, color, textureSize, element, faces, "east",
                    vertex(to.x, from.y, to.z), vertex(to.x, from.y, from.z),
                    vertex(to.x, to.y, from.z), vertex(to.x, to.y, to.z));
            submitFace(vertexConsumer, poseStack, packedLight, color, textureSize, element, faces, "up",
                    vertex(from.x, from.y, to.z), vertex(from.x, from.y, from.z),
                    vertex(to.x, from.y, from.z), vertex(to.x, from.y, to.z));
            submitFace(vertexConsumer, poseStack, packedLight, color, textureSize, element, faces, "down",
                    vertex(from.x, to.y, from.z), vertex(from.x, to.y, to.z),
                    vertex(to.x, to.y, to.z), vertex(to.x, to.y, from.z));
        }
    }

    private static float[] readTextureSize(JsonObject model)
    {
        if (model.has("texture_size") && model.get("texture_size").isJsonArray())
        {
            JsonArray size = model.getAsJsonArray("texture_size");
            if (size.size() >= 2)
            {
                return new float[] { size.get(0).getAsFloat(), size.get(1).getAsFloat() };
            }
        }

        float maxU = 16.0F;
        float maxV = 16.0F;
        if (model.has("elements") && model.get("elements").isJsonArray())
        {
            for (JsonElement elementValue : model.getAsJsonArray("elements"))
            {
                if (!elementValue.isJsonObject())
                {
                    continue;
                }

                JsonObject element = elementValue.getAsJsonObject();
                if (!element.has("faces"))
                {
                    continue;
                }

                for (String faceName : element.getAsJsonObject("faces").keySet())
                {
                    JsonObject face = element.getAsJsonObject("faces").getAsJsonObject(faceName);
                    if (face != null && face.has("uv") && face.get("uv").isJsonArray())
                    {
                        JsonArray uv = face.getAsJsonArray("uv");
                        if (uv.size() >= 4)
                        {
                            maxU = Math.max(maxU, Math.max(uv.get(0).getAsFloat(), uv.get(2).getAsFloat()));
                            maxV = Math.max(maxV, Math.max(uv.get(1).getAsFloat(), uv.get(3).getAsFloat()));
                        }
                    }
                }
            }
        }
        return new float[] { maxU, maxV };
    }

    private static void submitFace(VertexConsumer vertexConsumer, PoseStack poseStack, int packedLight, int color,
                                   float[] textureSize, JsonObject element, JsonObject faces, String faceName,
                                   Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3)
    {
        if (!faces.has(faceName))
        {
            return;
        }

        JsonObject face = faces.getAsJsonObject(faceName);
        if (face == null || !face.has("uv"))
        {
            return;
        }

        Vector3f[] vertices = new Vector3f[] { v0, v1, v2, v3 };
        applyElementRotation(element, vertices);
        float[][] uvs = readUvs(face, textureSize);

        Vector3f edgeA = new Vector3f(vertices[1]).sub(vertices[0]);
        Vector3f edgeB = new Vector3f(vertices[2]).sub(vertices[0]);
        Vector3f normal = edgeA.cross(edgeB).normalize();

        PoseStack.Pose pose = poseStack.last();
        for (int i = 0; i < vertices.length; i++)
        {
            Vector3f vertex = vertices[i];
            vertexConsumer.addVertex(pose, vertex.x, vertex.y, vertex.z)
                    .setColor(color)
                    .setUv(uvs[i][0], uvs[i][1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(pose, normal.x, normal.y, normal.z);
        }
    }

    private static float[][] readUvs(JsonObject face, float[] textureSize)
    {
        JsonArray uv = face.getAsJsonArray("uv");
        float u0 = uv.get(0).getAsFloat() / textureSize[0];
        float v0 = uv.get(1).getAsFloat() / textureSize[1];
        float u1 = uv.get(2).getAsFloat() / textureSize[0];
        float v1 = uv.get(3).getAsFloat() / textureSize[1];

        float[][] uvs = new float[][] {
                { u1, v0 },
                { u0, v0 },
                { u0, v1 },
                { u1, v1 }
        };

        int rotation = face.has("rotation") ? Math.floorMod(face.get("rotation").getAsInt(), 360) : 0;
        int steps = rotation / 90;
        for (int step = 0; step < steps; step++)
        {
            float[] last = uvs[uvs.length - 1];
            System.arraycopy(uvs, 0, uvs, 1, uvs.length - 1);
            uvs[0] = last;
        }

        return uvs;
    }

    private static void applyElementRotation(JsonObject element, Vector3f[] vertices)
    {
        if (!element.has("rotation"))
        {
            return;
        }

        JsonObject rotation = element.getAsJsonObject("rotation");
        if (!rotation.has("angle") || !rotation.has("axis") || !rotation.has("origin"))
        {
            return;
        }

        float angle = rotation.get("angle").getAsFloat();
        if (angle == 0.0F)
        {
            return;
        }

        Vector3f origin = readVector(rotation.getAsJsonArray("origin"));
        String axis = rotation.get("axis").getAsString();
        for (Vector3f vertex : vertices)
        {
            vertex.sub(origin);
            switch (axis)
            {
                case "x" -> vertex.rotateX((float) Math.toRadians(angle));
                case "y" -> vertex.rotateY((float) Math.toRadians(angle));
                case "z" -> vertex.rotateZ((float) Math.toRadians(angle));
                default -> {
                }
            }
            vertex.add(origin);
        }
    }

    private static Vector3f readVector(JsonArray array)
    {
        return vertex(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    private static Vector3f vertex(float x, float y, float z)
    {
        return new Vector3f(x * MODEL_SCALE, y * MODEL_SCALE, z * MODEL_SCALE);
    }
}
