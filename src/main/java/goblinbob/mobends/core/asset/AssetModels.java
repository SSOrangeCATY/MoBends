package goblinbob.mobends.core.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AssetModels
{
    public static final AssetModels INSTANCE = new AssetModels();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<AssetLocation, JsonElement> modelMap = new HashMap<>();
    private final Map<AssetLocation, Boolean> failedModelMap = new HashMap<>();

    public JsonElement register(AssetLocation location) throws IOException
    {
        try (InputStream stream = new FileInputStream(AssetsModule.INSTANCE.getAssetFile(location));
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
        {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            modelMap.put(location, jsonElement);
            failedModelMap.remove(location);
            return jsonElement;
        }
    }

    public void clearCache()
    {
        modelMap.clear();
        failedModelMap.clear();
    }

    public JsonElement getModel(AssetLocation location)
    {
        if (modelMap.containsKey(location))
        {
            return modelMap.get(location);
        }

        if (failedModelMap.containsKey(location))
        {
            return null;
        }

        try
        {
            return register(location);
        }
        catch(IOException e)
        {
            LOGGER.warn("Failed to load asset model: {}", location.toString(), e);
            failedModelMap.put(location, Boolean.TRUE);
            return null;
        }
    }
}
