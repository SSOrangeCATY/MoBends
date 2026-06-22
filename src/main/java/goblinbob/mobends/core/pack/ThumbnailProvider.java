package goblinbob.mobends.core.pack;

import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.resources.Identifier;

public class ThumbnailProvider
{
    public static final Identifier DEFAULT_THUMBNAIL_LOCATION = Identifier.fromNamespaceAndPath(ModStatics.MODID,
            "textures/gui/thumbnail.png");

    public ThumbnailProvider(PackCache packCache)
    {
    }

    public Identifier getThumbnailLocation(String packName, String thumbnailUrl)
    {
        return DEFAULT_THUMBNAIL_LOCATION;
    }
}
