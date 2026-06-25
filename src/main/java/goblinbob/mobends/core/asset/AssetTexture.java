package goblinbob.mobends.core.asset;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetTexture extends ReloadableTexture
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private final AssetLocation assetLocation;

    public AssetTexture(AssetLocation assetLocation)
    {
        super(assetLocation.getResourceLocation());
        this.assetLocation = assetLocation;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException
    {
        try (InputStream inputStream = new FileInputStream(AssetsModule.INSTANCE.getAssetFile(assetLocation)))
        {
            return new TextureContents(NativeImage.read(inputStream), null);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't load asset texture {}", assetLocation.toString(), ioexception);
            throw ioexception;
        }
    }
}
