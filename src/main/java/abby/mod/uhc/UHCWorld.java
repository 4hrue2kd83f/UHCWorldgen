package abby.mod.uhc;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class UHCWorld {
    public static UHCWorldType uhcWorldType = new UHCWorldType();
    
    public static void setup() {
        uhcWorldType.setRegistryName(new ResourceLocation("uhc"));
        ForgeRegistries.WORLD_TYPES.register(uhcWorldType);
    }

}
