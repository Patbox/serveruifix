package eu.pb4.serveruifix.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class EnchantingPhrases {
	private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
	private final Random random = Random.create();
	private final String[] phrases = new String[]{
		"the",
		"elder",
		"scrolls",
		"klaatu",
		"berata",
		"niktu",
		"xyzzy",
		"bless",
		"curse",
		"light",
		"darkness",
		"fire",
		"air",
		"earth",
		"water",
		"hot",
		"dry",
		"cold",
		"wet",
		"ignite",
		"snuff",
		"embiggen",
		"twist",
		"shorten",
		"stretch",
		"fiddle",
		"destroy",
		"imbue",
		"galvanize",
		"enchant",
		"free",
		"limited",
		"range",
		"of",
		"towards",
		"inside",
		"sphere",
		"cube",
		"self",
		"other",
		"ball",
		"mental",
		"physical",
		"grow",
		"shrink",
		"demon",
		"elemental",
		"spirit",
		"animal",
		"creature",
		"beast",
		"humanoid",
		"undead",
		"fresh",
		"stale",
		"phnglui",
		"mglwnafh",
		"cthulhu",
		"rlyeh",
		"wgahnagl",
		"fhtagn",
		"baguette"
	};

	private EnchantingPhrases() {
	}

	public static EnchantingPhrases getInstance() {
		return INSTANCE;
	}

	public String generatePhrase() {
		StringBuilder stringBuilder = new StringBuilder();
		int i = this.random.nextInt(2) + 3;

		for(int j = 0; j < i; ++j) {
			if (j != 0) {
				//stringBuilder.append(" ");
			}

			stringBuilder.append(Util.getRandom((String[])this.phrases, this.random));
		}

		return stringBuilder.substring(0, Math.min(14, stringBuilder.length()));
	}

	public void setSeed(long seed) {
		this.random.setSeed(seed);
	}
}
