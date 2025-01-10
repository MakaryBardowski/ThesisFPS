package guiComponents.inventoryComponents;

import client.Main;

public class LemurUtils {

    public static float getHeightRatio() {
        return ((float) Main.getInstance().getAppSettings().getHeight() / 1080);
    }

    public static float getWidthRatio() {
        return ((float) Main.getInstance().getAppSettings().getWidth() / 1920);
    }

}
