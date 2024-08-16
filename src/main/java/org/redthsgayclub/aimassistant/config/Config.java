package org.redthsgayclub.aimassistant.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import org.redthsgayclub.aimassistant.AimAssistant;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class Config extends cc.polyfrost.oneconfig.config.Config {

    @DualOption(
            name = "", // Name of the Dropdown
            left = "Target Mode",
            right = "Range Mode"
    )
    public static boolean mode = false; // Default option (in this case "Option 2")


    @Slider(
            name = "Range Slider",
            min = 0f, max = 15f // Minimum and maximum values for the slider.
    )
    public static float range = 5f; // The default value for the float Slider.

    @Color(
            name = "Box Color"
    )
    public static OneColor boxColor = new OneColor(168, 45, 45, 255);

    @Color(
        name = "Box Color in Reach"
    )
    public static OneColor inReachColor = new OneColor(45, 168, 45, 255);

    @Slider(
            name = "Box Size",
            min = 0.05f,
            max = 0.6f
    )
    public static float size = 5f; // The default value for the float Slider.


    public Config() {
        super(new Mod(AimAssistant.NAME, ModType.UTIL_QOL), AimAssistant.MODID + ".json");
        initialize();
        //addDependency("range", "mode");
    }
}

