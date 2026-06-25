package goblinbob.mobends.test.core.asset;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssetLocationTest
{
    @Test
    public void resourceIdentifierUsesNamespaceRelativeAssetPath() throws IOException
    {
        String source = readSource();

        assertTrue(source.contains("Identifier.fromNamespaceAndPath(ModStatics.MODID, this.assetPath)"));
        assertFalse(source.contains("PREFIX + assetPath"));
        assertFalse(source.contains("Identifier.fromNamespaceAndPath(ModStatics.MODID, \"assets/\""));
    }

    @Test
    public void legacyAssetsPrefixIsNormalizedBeforeCreatingIdentifier() throws IOException
    {
        String source = readSource();

        assertTrue(source.contains("LEGACY_ASSETS_PREFIX = \"assets/\""));
        assertTrue(source.contains("assetPath.substring(LEGACY_ASSETS_PREFIX.length())"));
    }

    private static String readSource() throws IOException
    {
        return Files.readString(Path.of("src/main/java/goblinbob/mobends/core/asset/AssetLocation.java"));
    }
}
