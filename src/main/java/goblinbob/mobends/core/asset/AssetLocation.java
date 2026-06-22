package goblinbob.mobends.core.asset;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.resources.Identifier;

import java.io.IOException;

public class AssetLocation
{
    private static final String PREFIX = "assets/";

    private final Identifier identifier;
    private final AssetType assetType;
    private final String assetPath;

    public AssetLocation(String assetPath)
    {
        this.identifier = Identifier.fromNamespaceAndPath(ModStatics.MODID, PREFIX + assetPath);
        this.assetPath = assetPath;

        if (assetPath.startsWith("models/"))
        {
            this.assetType = AssetType.MODEL;
        }
        else if (assetPath.startsWith("textures/"))
        {
            this.assetType = AssetType.TEXTURE;
        }
        else if (assetPath.endsWith(".json"))
        {
            this.assetType = AssetType.JSON;
        }
        else
        {
            this.assetType = AssetType.UNKNOWN;
        }
    }

    public AssetLocation(String assetPath, AssetType assetType)
    {
        this.identifier = Identifier.fromNamespaceAndPath(ModStatics.MODID, PREFIX + assetPath);
        this.assetPath = assetPath;
        this.assetType = assetType;
    }

    public Identifier getResourceLocation()
    {
        return identifier;
    }

    public String getNamespace()
    {
        return identifier.getNamespace();
    }

    public String getPath()
    {
        return identifier.getPath();
    }

    public String getAssetPath()
    {
        return assetPath;
    }

    public AssetType getAssetType()
    {
        return assetType;
    }

    @Override
    public String toString()
    {
        return identifier.toString();
    }

    public static class Adapter extends TypeAdapter<AssetLocation>
    {
        @Override
        public void write(JsonWriter out, AssetLocation value) throws IOException
        {
            out.value(value.assetPath);
        }

        @Override
        public AssetLocation read(JsonReader in) throws IOException
        {
            return new AssetLocation(in.nextString());
        }
    }
}
