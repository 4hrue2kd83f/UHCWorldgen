package abby.mod.uhc;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraftforge.common.BiomeManager;

import java.util.ArrayList;
import java.util.List;

public class UHCBiomeLayer implements IC0Transformer {
	private Registry<Biome> biomeRegistry;
	private List<List<BiomeManager.BiomeEntry>> biomes = new ArrayList<>();

	public UHCBiomeLayer(Registry<Biome> biomeRegistryIn) {
		biomeRegistry = biomeRegistryIn;

		// Add entries to the list for each temp list
		for(int i = 0; i < BiomeManager.BiomeType.values().length; i++)
			biomes.add(new ArrayList<>());

		// replace the templist with the correct list at each place
		for (BiomeManager.BiomeType type : BiomeManager.BiomeType.values())
			biomes.set(type.ordinal(), new ArrayList<>(BiomeManager.getBiomes(type)));

		// filter out the biomes we do want to spawn by using registry keys
		biomes.forEach(tempBiomeList ->
				tempBiomeList.removeIf(biomeEntry -> UHCBiomeProvider.BLACKLISTED_BIOMES.contains(biomeEntry.getKey())));
	}

	public int apply(INoiseRandom context, int value) {
		int i = (value & 3840) >> 8;
		value = value & -3841;
		if (!UHCLayerUtil.isOcean(value) && value != 14) {
			switch (value) {
			case 1:
				if (i > 0) {
					return context.random(3) == 0 ? 39 : 38;
				}

				return getBiomeId(BiomeManager.BiomeType.DESERT, context);
			case 2:
				if (i > 0) {
					return 21;
				}

				return getBiomeId(BiomeManager.BiomeType.WARM, context);
			case 3:
				if (i > 0) {
					return 32;
				}

				return getBiomeId(BiomeManager.BiomeType.COOL, context);
			case 4:
				return getBiomeId(BiomeManager.BiomeType.ICY, context);
			default:
				return 14;
			}
		} else {
			return value;
		}
	}

	private int getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType type, INoiseRandom context) {
		return biomeRegistry.getId(biomeRegistry.getValueForKey(getBiome(type, context)));
	}

	protected RegistryKey<Biome> getBiome(BiomeManager.BiomeType type, INoiseRandom context) {
		List<BiomeManager.BiomeEntry> biomeList = biomes.get(type.ordinal());
		int totalWeight = WeightedRandom.getTotalWeight(biomeList);
		int weight = BiomeManager.isTypeListModded(type) ? context.random(totalWeight) : context.random(totalWeight / 10) * 10;
		return WeightedRandom.getRandomItem(biomeList, weight).getKey();
	}
}
