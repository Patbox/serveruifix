package eu.pb4.serveruifix.mixin;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StonecutterScreenHandler.class)
public interface StonecutterScreenHandlerAccessor {
    @Accessor
    List<RecipeEntry<StonecuttingRecipe>> getAvailableRecipes();
}
