package WolfShotz.Wyrmroost.content.world.dimension;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.PhantomSpawner;

public class WyrmroostChunkGenerator extends NoiseChunkGenerator<WyrmroostGenSettings>
{
//    @ObjectHolder(Wyrmroost.MOD_ID + ":wr_surface")
    public static final ChunkGeneratorType<WyrmroostGenSettings, WyrmroostChunkGenerator> WR_SURFACE = null;

    private static final float[] field_222576_h = Util.make(new float[25], (p_222575_0_) -> {
        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
                p_222575_0_[i + 2 + (j + 2) * 5] = f;
            }
        }
    });
    private final OctavesNoiseGenerator depthNoise;

    public WyrmroostChunkGenerator(World worldIn, WyrmroostGenSettings settingsIn)
    {
        super(worldIn, new WyrmroostBiomeProvider(), 4, 8, 265, settingsIn, true);

        this.depthNoise = new OctavesNoiseGenerator(this.randomSeed, 16);
    }

    @Override
    protected double[] getBiomeNoiseColumn(int noiseX, int noiseZ)
    {
        double[] adouble = new double[2];
        float f = 0;
        float f1 = 0;
        float f2 = 0;
        float f3 = biomeProvider.getBiomeAtFactorFour(noiseX, noiseZ).getDepth();

        for(int j = -2; j <= 2; ++j) {
            for(int k = -2; k <= 2; ++k) {
                Biome biome = biomeProvider.getBiomeAtFactorFour(noiseX + j, noiseZ + k);
                float f4 = biome.getDepth();
                float f5 = biome.getScale();

                float f6 = field_222576_h[j + 2 + (k + 2) * 5] / (f4 + 2.0F);
                if (biome.getDepth() > f3) {
                    f6 /= 2.0F;
                }

                f += f5 * f6;
                f1 += f4 * f6;
                f2 += f6;
            }
        }

        f = f / f2;
        f1 = f1 / f2;
        f = f * 0.9F + 0.1F;
        f1 = (f1 * 4.0F - 1.0F) / 8.0F;
        adouble[0] = (double)f1 + getNoiseDepthAt(noiseX, noiseZ);
        adouble[1] = f;
        return adouble;
    }

    @Override
    protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_)
    {
        double d1 = ((double)p_222545_5_ - (8.5D + p_222545_1_ * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / p_222545_3_;
        if (d1 < 0.0D) {
            d1 *= 4.0D;
        }

        return d1;
    }

    @Override
    protected void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ)
    {
        double d0 = 684.412F;
        double d1 = 684.412F;
        double d2 = 8.555149841308594D;
        double d3 = 4.277574920654297D;
        int i = -10;
        int j = 3;
        func_222546_a(noiseColumn, noiseX, noiseZ, 684.412F, 684.412F, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    @Override
    public int getGroundHeight()
    {
        return getSeaLevel() + 1;
    }

    @Override
    public void func_203222_a(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs)
    {
        new PhantomSpawner().func_203232_a(worldIn, spawnHostileMobs, spawnPeacefulMobs);
    }

    private double getNoiseDepthAt(int noiseX, int noiseZ) {
        double d0 = this.depthNoise.getValue((double)(noiseX * 200), 10.0D, (double)(noiseZ * 200), 1.0D, 0.0D, true) / 8000.0D;
        if (d0 < 0.0D) {
            d0 = -d0 * 0.3D;
        }

        d0 = d0 * 3.0D - 2.0D;
        if (d0 < 0.0D) {
            d0 = d0 / 28.0D;
        } else {
            if (d0 > 1.0D) {
                d0 = 1.0D;
            }

            d0 = d0 / 40.0D;
        }

        return d0;
    }
}
