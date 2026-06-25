package goblinbob.mobends.core.pack;

import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class ThumbnailProvider
{
    public static final Identifier DEFAULT_THUMBNAIL_LOCATION = Identifier.fromNamespaceAndPath(ModStatics.MODID,
            "textures/gui/default_pack_thumbnail.png");

    private final PackCache packCache;

    public ThumbnailProvider(PackCache packCache)
    {
        this.packCache = packCache;
    }

    public Identifier getThumbnailLocation(String packName, String thumbnailUrl)
    {
        final Identifier identifier = Identifier.fromNamespaceAndPath(ModStatics.MODID,
                "bendspackthumbnails/" + packName);
        @Nullable AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(identifier);

        if (texture == null)
        {
            new SkinTextureDownloader(
                    Minecraft.getInstance().getProxy(),
                    Minecraft.getInstance().getTextureManager(),
                    Minecraft.getInstance()
            ).downloadAndRegisterSkin(
                    identifier,
                    packCache.getThumbnailFile(packName).toPath(),
                    thumbnailUrl,
                    false
            );
            return identifier;
        }

        return identifier;
    }
}
