package WolfShotz.Wyrmroost.event;

import WolfShotz.Wyrmroost.Wyrmroost;
import WolfShotz.Wyrmroost.util.ModUtils;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Wyrmroost.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupSounds
{
    @ObjectHolder(Wyrmroost.modID + ":entity.minutus.idle")
    public static SoundEvent MINUTUS_IDLE;
    
    @ObjectHolder(Wyrmroost.modID + ":entity.minutus.screech")
    public static SoundEvent MINUTUS_SCREECH;
    
    @ObjectHolder(Wyrmroost.modID + ":entity.silverglider.idle")
    public static SoundEvent SILVERGLIDER_IDLE;
    
    @ObjectHolder(Wyrmroost.modID + ":entity.silverglider.hurt")
    public static SoundEvent SILVERGLIDER_HURT;
    
    @ObjectHolder(Wyrmroost.modID + ":entity.silverglider.death")
    public static SoundEvent SILVERGLIDER_DEATH;
    
    @SubscribeEvent
    public static void soundSetup(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                registerSound("entity.minutus.idle"),
                registerSound("entity.minutus.screech"),
                
                registerSound("entity.silverglider.idle"),
                registerSound("entity.silverglider.hurt"),
                registerSound("entity.silverglider.deahth")
        );
    }

    private static SoundEvent registerSound(String name) { return new SoundEvent(ModUtils.location(name)).setRegistryName(name); }
}
