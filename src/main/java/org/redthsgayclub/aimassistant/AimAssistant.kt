package org.redthsgayclub.aimassistant;

import net.minecraftforge.common.MinecraftForge;
import org.redthsgayclub.aimassistant.config.Config;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.redthsgayclub.aimassistant.listener.EventListener;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = AimAssistant.MODID, name = AimAssistant.NAME, version = AimAssistant.VERSION)
public class AimAssistant {

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static AimAssistant INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static Config config;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new Config();
        MinecraftForge.EVENT_BUS.register(new EventListener());
    }
}
