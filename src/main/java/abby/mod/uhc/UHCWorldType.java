package abby.mod.uhc;

import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.world.ForgeWorldType;

public class UHCWorldType extends ForgeWorldType {

	public UHCWorldType() {
		super(null);
	}

	@Override
    public ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed, String generatorSettings)
    {
        return new NoiseChunkGenerator(new UHCBiomeProvider(seed, false, false, biomeRegistry), seed, () -> dimensionSettingsRegistry.getOrThrow(DimensionSettings.field_242734_c));
    }

}
