package org.redthsgayclub.aimassistant.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.DualOption
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.redthsgayclub.aimassistant.AimAssistant

object ModConfig : Config(Mod(AimAssistant.NAME, ModType.UTIL_QOL), "${AimAssistant.MODID}.json") {
    @DualOption(name = "Mode", left = "Target", right = "Range")
    var mode = false

    @Slider(name = "Range Slider", min = 0f, max = 15f)
    var range = 5f

    @Color(name = "Box Color")
    var boxColor = OneColor(168, 45, 45, 255)

    @Color(name = "Box Color in Reach")
    var inReachColor = OneColor(45, 168, 45, 255)

    @Slider(name = "Box Size", min = 0.05f, max = 0.6f)
    var size = 0.1f

    init {
        initialize()
    }
}
