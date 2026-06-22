package goblinbob.mobends.core.asset;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AssetTexture extends AbstractTexture
{
    public static final String PREFIX = "textures";
    public static final String SUFFIX = ".png";

    protected AssetLocation location;
    protected NativeImage image;

    public AssetTexture(AssetLocation location)
    {
        this.location = location;
    }

    public void load(ResourceManager resourceManager) throws IOException
    {
        if (this.image != null)
        {
            this.image.close();
            this.image = null;
        }

        Identifier resourceLocation = Identifier.fromNamespaceAndPath(location.getNamespace(), PREFIX + "/" + location.getAssetPath() + SUFFIX);
        try (InputStream inputStream = resourceManager.open(resourceLocation))
        {
            this.image = NativeImage.read(inputStream);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
