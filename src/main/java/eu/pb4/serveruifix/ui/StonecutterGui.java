package eu.pb4.serveruifix.ui;

import eu.pb4.serveruifix.mixin.StonecutterScreenHandlerAccessor;
import eu.pb4.serveruifix.polydex.PolydexCompat;
import eu.pb4.serveruifix.util.GuiTextures;
import eu.pb4.serveruifix.util.GuiUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class StonecutterGui extends SimpleGui {
    private static final int MAX_RECIPES = 3 * 3;
    private final StonecutterScreenHandler wrapped;
    private final List<GuiElement> recipeEntries = new ArrayList<>(16);
    private List<RecipeEntry<StonecuttingRecipe>> lastRecipes;
    private int page = 0;

    public StonecutterGui(Text text, StonecutterScreenHandler handler, ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X4, player, false);
        this.wrapped = handler;
        this.setTitle(GuiTextures.STONECUTTER.apply(text));
        this.setSlotRedirect(9 + 1, this.wrapped.getSlot(0));
        this.setSlotRedirect(9 + 7, this.wrapped.getSlot(1));

        this.setSlot(9 * 3 + 1, PolydexCompat.getButton(RecipeType.STONECUTTING));
        this.updateRecipes();
    }

    private void updateRecipes() {
        this.recipeEntries.clear();
        var recipes = ((StonecutterScreenHandlerAccessor) this.wrapped).getAvailableRecipes();
        var drm = this.player.getWorld().getRegistryManager();
        for (int i = 0; i < recipes.size(); i++) {
            var recipe = recipes.get(i);
            int finalI = i;
            this.recipeEntries.add(new GuiElement(recipe.value().getResult(drm), (index, type, action) -> {
                if (type.isLeft) {
                    GuiUtils.playClickSound(this.player);
                    this.wrapped.onButtonClick(this.player, finalI);
                }
            }));
        }
        this.lastRecipes = recipes;
        if (this.page >= Math.min(this.lastRecipes.size() / MAX_RECIPES, 1)) {
            this.page = 0;
        }
        this.updateRecipeDisplay();
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
        if (action == SlotActionType.QUICK_MOVE && (index == 9 + 1 || index == 9 + 7 || index > 9 * 4)) {
            var i = index > 9 * 4 ? index - 9 * 4 + 2 : (index == 9 + 1 ? 0 : 1);

            Slot slot = this.wrapped.slots.get(i);
            if (!slot.canTakeItems(player)) {
                return true;
            }

            ItemStack itemStack = this.wrapped.quickMove(player, i);

            while(!itemStack.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStack)) {
                itemStack = this.wrapped.quickMove(player, i);
            }

            return false;
        }

        return super.onAnyClick(index, type, action);
    }

    private void updateRecipeDisplay() {
        var offset = this.page * MAX_RECIPES;

        var max = this.wrapped.slots.get(0).getStack().isEmpty() ? 0 : Math.min(this.recipeEntries.size() - offset, MAX_RECIPES);
        int i = 0;
        for (; i < max; i++) {
            var x = i % 3;
            var y = i / 3;
            this.setSlot(x + y * 9 + 3, this.recipeEntries.get(i + offset));
        }

        for (; i < MAX_RECIPES; i++) {
            var x = i % 3;
            var y = i / 3;
            this.clearSlot(x + y * 9 + 3);
        }
        var empty = this.wrapped.getSlot(0).getStack().isEmpty();
        if (this.page != 0 && !empty) {
            this.setSlot(9 * 3 + 3, GuiTextures.LEFT_BUTTON.get().setName(Text.translatable("spectatorMenu.previous_page")).setCallback(() -> {
                GuiUtils.playClickSound(this.player);
                this.page--;
                this.updateRecipeDisplay();
            }));
        } else {
            this.clearSlot(9 * 3 + 3);
        }

        if (this.page < Math.min(this.lastRecipes.size() / MAX_RECIPES, 1) && !empty) {
            this.setSlot(9 * 3 + 5, GuiTextures.RIGHT_BUTTON.get().setName(Text.translatable("spectatorMenu.next_page")).setCallback(() -> {
                GuiUtils.playClickSound(this.player);
                this.page++;
                this.updateRecipeDisplay();
            }));
        } else {
            this.clearSlot(9 * 3 + 5);
        }
    }


    public OptionalInt openWithNumber() {
        this.open();

        if (this.screenHandler != null) {
            return OptionalInt.of(this.screenHandler.syncId);
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public void onTick() {
        if (!wrapped.canUse(player)) {
            this.close();
            return;
        }
        if (this.getSlot(3) != null && this.wrapped.getSlot(0).getStack().isEmpty()) {
            this.updateRecipeDisplay();
        }

        if (this.lastRecipes != ((StonecutterScreenHandlerAccessor) this.wrapped).getAvailableRecipes()) {
            this.updateRecipes();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        wrapped.onClosed(this.player);
    }
}
