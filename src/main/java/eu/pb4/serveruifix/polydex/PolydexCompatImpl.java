package eu.pb4.serveruifix.polydex;

import eu.pb4.polydex.api.v1.recipe.*;

import eu.pb4.serveruifix.util.GuiTextures;
import eu.pb4.serveruifix.util.GuiUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;

public class PolydexCompatImpl {
    public static void register() {

    }
    public static GuiElement getButton(RecipeType<?> type) {
        var category = PolydexCategory.of(type);
        return GuiTextures.POLYDEX_BUTTON.get()
                .setName(Text.translatable("text.serveruifix.recipes"))
                .setCallback((index, type1, action, gui) -> {
                    PolydexPageUtils.openCategoryUi(gui.getPlayer(), category, gui::open);
                    GuiUtils.playClickSound(gui.getPlayer());
                }).build();
    }
}
