package goblinbob.mobends.core.asset;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetModels
{
    public static final AssetModels INSTANCE = new AssetModels();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<AssetLocation, Object> bakedModelMap = new HashMap<>();

    public Object register(AssetLocation location) throws IOException
    {
        LOGGER.warn("Skipping legacy asset model bake for {}", location.toString());
        bakedModelMap.put(location, null);
        return null;
    }

    public void clearCache()
    {
        bakedModelMap.clear();
    }

    public Object getModel(AssetLocation location)
    {
        if (!bakedModelMap.containsKey(location))
        {
            try
            {
                return register(location);
            }
            catch(IOException e)
            {
                LOGGER.warn("Failed to bake asset model: {}", location.toString(), e);
                bakedModelMap.put(location, null);
                return null;
            }
        }

        return bakedModelMap.get(location);
    }
}
