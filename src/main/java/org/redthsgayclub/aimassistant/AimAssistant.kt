package org.redthsgayclub.aimassistant

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.redthsgayclub.aimassistant.config.ModConfig
import org.redthsgayclub.aimassistant.listener.EventListener

@Mod(
    modid = AimAssistant.MODID,
    name = AimAssistant.NAME,
    version = AimAssistant.VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object AimAssistant {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        MinecraftForge.EVENT_BUS.register(EventListener)
    }
}
