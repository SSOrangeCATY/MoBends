package goblinbob.mobends.core.pack;

import net.minecraft.resources.Identifier;

public interface IBendsPack
{

    String getKey();

    String getDisplayName();

    String getAuthor();

    String getDescription();

    Identifier getThumbnail();

    boolean canPackBeEdited();

}
