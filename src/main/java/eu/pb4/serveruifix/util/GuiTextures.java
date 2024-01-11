package eu.pb4.serveruifix.util;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Supplier;

import static eu.pb4.serveruifix.ModInit.id;
import static eu.pb4.serveruifix.util.UiResourceCreator.*;

public class GuiTextures {
    public static final GuiElement EMPTY = icon16("empty").get().build();
    public static final Function<Text, Text> STONECUTTER = background("stonecutter");
    public static final Function<Text, Text> ENCHANTMENT = background("enchantment");
    public static final char NEGATIVE_1 = UiResourceCreator.space(-1);
    public static final char NEGATIVE_19 = UiResourceCreator.space(-19);
    public static final char POSITIVE_19 = UiResourceCreator.space(19);
    public static final char POSITIVE_18 = UiResourceCreator.space(18);
    public static final char STONECUTTER_OFFSET = UiResourceCreator.space(52);
    public static final char STONECUTTER_NEGATIVE = UiResourceCreator.space(-52 - 18 * 3);
    public static final char[] STONECUTTER_SELECTED_FONT = new char[3];
    public static final char[] STONECUTTER_BACKGROUND_FONT = new char[3];

    public static final Supplier<GuiElementBuilder> POLYDEX_BUTTON = icon32("polydex");
    public static final Supplier<GuiElementBuilder> LEFT_BUTTON = icon16("left");
    public static final Supplier<GuiElementBuilder> RIGHT_BUTTON = icon16("right");

    public static void register() {
        var background = id("sgui/stonecutter/recipe");
        var selected = id("sgui/stonecutter/recipe_selected");

        for (int y = 0; y < 3; y++) {
            STONECUTTER_SELECTED_FONT[y] = UiResourceCreator.font(selected, -4 + y * -18, 18);
            STONECUTTER_BACKGROUND_FONT[y] = UiResourceCreator.font(background, -4 + y * -18, 18);
        }


    }

    public record Progress(GuiElement[] elements) {

        public GuiElement get(float progress) {
            return elements[Math.min((int) (progress * elements.length), elements.length - 1)];
        }

        public static Progress createVertical(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var elements = new GuiElement[size + 1];
            var function = verticalProgress16(path, start, stop, reverse);

            elements[0] = EMPTY;

            for (var i = 1; i <= size; i++) {
                elements[i] = function.apply(i - 1).build();
            }
            return new Progress(elements);
        }

        public static Progress createHorizontal(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var elements = new GuiElement[size + 1];
            var function = horizontalProgress16(path, start, stop, reverse);

            elements[0] = EMPTY;

            for (var i = 1; i <= size; i++) {
                elements[i] = function.apply(i - 1).build();
            }
            return new Progress(elements);
        }

        public static Progress createHorizontal32(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var elements = new GuiElement[size + 1];
            var function = horizontalProgress32(path, start, stop, reverse);

            elements[0] = EMPTY;

            for (var i = 1; i <= size; i++) {
                elements[i] = function.apply(i - 1).build();
            }
            return new Progress(elements);
        }

        public static Progress createHorizontal32Right(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var elements = new GuiElement[size + 1];
            var function = horizontalProgress32Right(path, start, stop, reverse);

            elements[0] = EMPTY;

            for (var i = 1; i <= size; i++) {
                elements[i] = function.apply(i - 1).build();
            }
            return new Progress(elements);
        }
        public static Progress createVertical32Right(String path, int start, int stop, boolean reverse) {
            var size = stop - start;
            var elements = new GuiElement[size + 1];
            var function = verticalProgress32Right(path, start, stop, reverse);

            elements[0] = EMPTY;

            for (var i = 1; i <= size; i++) {
                elements[i] = function.apply(i - 1).build();
            }
            return new Progress(elements);
        }
    }

}
