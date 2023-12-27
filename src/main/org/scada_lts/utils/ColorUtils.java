/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.utils;

import com.serotonin.InvalidArgumentException;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ColorUtils
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ColorUtils {

	private static Map<String, Color> colors = new HashMap();

	static {
		colors.put("absolutezero", new Color(Integer.parseInt("0048BA", 16)));
		colors.put("aero", new Color(Integer.parseInt("7CB9E8", 16)));
		colors.put("aliceblue", new Color(Integer.parseInt("F0F8FF", 16)));
		colors.put("antiquewhite", new Color(Integer.parseInt("FAEBD7", 16)));
		colors.put("applegreen", new Color(Integer.parseInt("8DB600", 16)));
		colors.put("aqua", new Color(Integer.parseInt("00FFFF", 16)));
		colors.put("aquamarine", new Color(Integer.parseInt("7FFFD4", 16)));
		colors.put("azure", new Color(Integer.parseInt("F0FFFF", 16)));
		colors.put("bazaar", new Color(Integer.parseInt("98777B", 16)));
		colors.put("beige", new Color(Integer.parseInt("F5F5DC", 16)));
		colors.put("bisque", new Color(Integer.parseInt("FFE4C4", 16)));
		colors.put("black", new Color(Integer.parseInt("000000", 16)));
		colors.put("blanchedalmond", new Color(Integer.parseInt("FFEBCD", 16)));
		colors.put("blue", new Color(Integer.parseInt("0000FF", 16)));
		colors.put("blueviolet", new Color(Integer.parseInt("8A2BE2", 16)));
		colors.put("brown", new Color(Integer.parseInt("A52A2A", 16)));
		colors.put("burlywood", new Color(Integer.parseInt("DEB887", 16)));
		colors.put("cadetblue", new Color(Integer.parseInt("5F9EA0", 16)));
		colors.put("charcoal", new Color(Integer.parseInt("36454F", 16)));
		colors.put("chartreuse", new Color(Integer.parseInt("7FFF00", 16)));
		colors.put("cherry", new Color(Integer.parseInt("DE3163", 16)));
		colors.put("chocolate", new Color(Integer.parseInt("D2691E", 16)));
		colors.put("coral", new Color(Integer.parseInt("FF7F50", 16)));
		colors.put("cornflowerblue", new Color(Integer.parseInt("6495ED", 16)));
		colors.put("cornsilk", new Color(Integer.parseInt("FFF8DC", 16)));
		colors.put("crimson", new Color(Integer.parseInt("DC143C", 16)));
		colors.put("cyan", new Color(Integer.parseInt("00FFFF", 16)));
		colors.put("darkblue", new Color(Integer.parseInt("00008B", 16)));
		colors.put("darkcyan", new Color(Integer.parseInt("008B8B", 16)));
		colors.put("darkgoldenrod", new Color(Integer.parseInt("B8860B", 16)));
		colors.put("darkgray", new Color(Integer.parseInt("A9A9A9", 16)));
		colors.put("darkgreen", new Color(Integer.parseInt("006400", 16)));
		colors.put("darkkhaki", new Color(Integer.parseInt("BDB76B", 16)));
		colors.put("darkmagenta", new Color(Integer.parseInt("8B008B", 16)));
		colors.put("darkolivegreen", new Color(Integer.parseInt("556B2F", 16)));
		colors.put("darkorange", new Color(Integer.parseInt("FF8C00", 16)));
		colors.put("darkorchid", new Color(Integer.parseInt("9932CC", 16)));
		colors.put("darkred", new Color(Integer.parseInt("8B0000", 16)));
		colors.put("darksalmon", new Color(Integer.parseInt("E9967A", 16)));
		colors.put("darkseagreen", new Color(Integer.parseInt("8FBC8F", 16)));
		colors.put("darkslateblue", new Color(Integer.parseInt("483D8B", 16)));
		colors.put("darkslategray", new Color(Integer.parseInt("2F4F4F", 16)));
		colors.put("darkturquoise", new Color(Integer.parseInt("00CED1", 16)));
		colors.put("darkviolet", new Color(Integer.parseInt("9400D3", 16)));
		colors.put("deeppink", new Color(Integer.parseInt("FF1493", 16)));
		colors.put("deepskyblue", new Color(Integer.parseInt("00BFFF", 16)));
		colors.put("dimgray", new Color(Integer.parseInt("696969", 16)));
		colors.put("dodgerblue", new Color(Integer.parseInt("1E90FF", 16)));
		colors.put("firebrick", new Color(Integer.parseInt("B22222", 16)));
		colors.put("floralwhite", new Color(Integer.parseInt("FFFAF0", 16)));
		colors.put("forestgreen", new Color(Integer.parseInt("228B22", 16)));
		colors.put("freshair", new Color(Integer.parseInt("A6E7FF", 16)));
		colors.put("fuchsia", new Color(Integer.parseInt("FF00FF", 16)));
		colors.put("gainsboro", new Color(Integer.parseInt("DCDCDC", 16)));
		colors.put("ghostwhite", new Color(Integer.parseInt("F8F8FF", 16)));
		colors.put("gold", new Color(Integer.parseInt("FFD700", 16)));
		colors.put("goldenrod", new Color(Integer.parseInt("DAA520", 16)));
		colors.put("gray", new Color(Integer.parseInt("808080", 16)));
		colors.put("green", new Color(Integer.parseInt("008000", 16)));
		colors.put("greenyellow", new Color(Integer.parseInt("ADFF2F", 16)));
		colors.put("honeydew", new Color(Integer.parseInt("F0FFF0", 16)));
		colors.put("hotpink", new Color(Integer.parseInt("FF69B4", 16)));
		colors.put("indianred ", new Color(Integer.parseInt("CD5C5C", 16)));
		colors.put("indigo  ", new Color(Integer.parseInt("4B0082", 16)));
		colors.put("ivory", new Color(Integer.parseInt("FFFFF0", 16)));
		colors.put("khaki", new Color(Integer.parseInt("F0E68C", 16)));
		colors.put("lavender", new Color(Integer.parseInt("E6E6FA", 16)));
		colors.put("lavenderblush", new Color(Integer.parseInt("FFF0F5", 16)));
		colors.put("lawngreen", new Color(Integer.parseInt("7CFC00", 16)));
		colors.put("lemonchiffon", new Color(Integer.parseInt("FFFACD", 16)));
		colors.put("lightblue", new Color(Integer.parseInt("ADD8E6", 16)));
		colors.put("lightcoral", new Color(Integer.parseInt("F08080", 16)));
		colors.put("lightcyan", new Color(Integer.parseInt("E0FFFF", 16)));
		colors.put("lightgoldenrodyellow", new Color(Integer.parseInt("FAFAD2", 16)));
		colors.put("lightgrey", new Color(Integer.parseInt("D3D3D3", 16)));
		colors.put("lightgreen", new Color(Integer.parseInt("90EE90", 16)));
		colors.put("lightpink", new Color(Integer.parseInt("FFB6C1", 16)));
		colors.put("lightsalmon", new Color(Integer.parseInt("FFA07A", 16)));
		colors.put("lightseagreen", new Color(Integer.parseInt("20B2AA", 16)));
		colors.put("lightskyblue", new Color(Integer.parseInt("87CEFA", 16)));
		colors.put("lightslategray", new Color(Integer.parseInt("778899", 16)));
		colors.put("lightsteelblue", new Color(Integer.parseInt("B0C4DE", 16)));
		colors.put("lightyellow", new Color(Integer.parseInt("FFFFE0", 16)));
		colors.put("lime", new Color(Integer.parseInt("00FF00", 16)));
		colors.put("limegreen", new Color(Integer.parseInt("32CD32", 16)));
		colors.put("linen", new Color(Integer.parseInt("FAF0E6", 16)));
		colors.put("magenta", new Color(Integer.parseInt("FF00FF", 16)));
		colors.put("maroon", new Color(Integer.parseInt("800000", 16)));
		colors.put("mediumaquamarine", new Color(Integer.parseInt("66CDAA", 16)));
		colors.put("mediumblue", new Color(Integer.parseInt("0000CD", 16)));
		colors.put("mediumorchid", new Color(Integer.parseInt("BA55D3", 16)));
		colors.put("mediumpurple", new Color(Integer.parseInt("9370D8", 16)));
		colors.put("mediumseagreen", new Color(Integer.parseInt("3CB371", 16)));
		colors.put("mediumslateblue", new Color(Integer.parseInt("7B68EE", 16)));
		colors.put("mediumspringgreen", new Color(Integer.parseInt("00FA9A", 16)));
		colors.put("mediumturquoise", new Color(Integer.parseInt("48D1CC", 16)));
		colors.put("mediumvioletred", new Color(Integer.parseInt("C71585", 16)));
		colors.put("midnightblue", new Color(Integer.parseInt("191970", 16)));
		colors.put("mintcream", new Color(Integer.parseInt("F5FFFA", 16)));
		colors.put("mistyrose", new Color(Integer.parseInt("FFE4E1", 16)));
		colors.put("moccasin", new Color(Integer.parseInt("FFE4B5", 16)));
		colors.put("navajowhite", new Color(Integer.parseInt("FFDEAD", 16)));
		colors.put("navy", new Color(Integer.parseInt("000080", 16)));
		colors.put("neongreen", new Color(Integer.parseInt("39FF14", 16)));
		colors.put("oceanblue", new Color(Integer.parseInt("4F42B5", 16)));
		colors.put("oldlace", new Color(Integer.parseInt("FDF5E6", 16)));
		colors.put("olive", new Color(Integer.parseInt("808000", 16)));
		colors.put("olivedrab", new Color(Integer.parseInt("6B8E23", 16)));
		colors.put("orange", new Color(Integer.parseInt("FFA500", 16)));
		colors.put("orangered", new Color(Integer.parseInt("FF4500", 16)));
		colors.put("orchid", new Color(Integer.parseInt("DA70D6", 16)));
		colors.put("palegoldenrod", new Color(Integer.parseInt("EEE8AA", 16)));
		colors.put("palegreen", new Color(Integer.parseInt("98FB98", 16)));
		colors.put("paleturquoise", new Color(Integer.parseInt("AFEEEE", 16)));
		colors.put("palevioletred", new Color(Integer.parseInt("D87093", 16)));
		colors.put("papayawhip", new Color(Integer.parseInt("FFEFD5", 16)));
		colors.put("peachpuff", new Color(Integer.parseInt("FFDAB9", 16)));
		colors.put("peru", new Color(Integer.parseInt("CD853F", 16)));
		colors.put("pink", new Color(Integer.parseInt("FFC0CB", 16)));
		colors.put("plum", new Color(Integer.parseInt("DDA0DD", 16)));
		colors.put("powderblue", new Color(Integer.parseInt("B0E0E6", 16)));
		colors.put("purple", new Color(Integer.parseInt("800080", 16)));
		colors.put("quartz", new Color(Integer.parseInt("51484F", 16)));
		colors.put("red", new Color(Integer.parseInt("FF0000", 16)));
		colors.put("rose", new Color(Integer.parseInt("FF007F", 16)));
		colors.put("rosybrown", new Color(Integer.parseInt("BC8F8F", 16)));
		colors.put("royalblue", new Color(Integer.parseInt("4169E1", 16)));
		colors.put("saddlebrown", new Color(Integer.parseInt("8B4513", 16)));
		colors.put("salmon", new Color(Integer.parseInt("FA8072", 16)));
		colors.put("sand", new Color(Integer.parseInt("C2B280", 16)));
		colors.put("sandstorm", new Color(Integer.parseInt("ECD540", 16)));
		colors.put("sandybrown", new Color(Integer.parseInt("F4A460", 16)));
		colors.put("seagreen", new Color(Integer.parseInt("2E8B57", 16)));
		colors.put("seashell", new Color(Integer.parseInt("FFF5EE", 16)));
		colors.put("sienna", new Color(Integer.parseInt("A0522D", 16)));
		colors.put("silver", new Color(Integer.parseInt("C0C0C0", 16)));
		colors.put("skyblue", new Color(Integer.parseInt("87CEEB", 16)));
		colors.put("slateblue", new Color(Integer.parseInt("6A5ACD", 16)));
		colors.put("slategray", new Color(Integer.parseInt("708090", 16)));
		colors.put("snow", new Color(Integer.parseInt("FFFAFA", 16)));
		colors.put("springgreen", new Color(Integer.parseInt("00FF7F", 16)));
		colors.put("steelblue", new Color(Integer.parseInt("4682B4", 16)));
		colors.put("tan", new Color(Integer.parseInt("D2B48C", 16)));
		colors.put("teal", new Color(Integer.parseInt("008080", 16)));
		colors.put("thistle", new Color(Integer.parseInt("D8BFD8", 16)));
		colors.put("tomato", new Color(Integer.parseInt("FF6347", 16)));
		colors.put("turquoise", new Color(Integer.parseInt("40E0D0", 16)));
		colors.put("violet", new Color(Integer.parseInt("EE82EE", 16)));
		colors.put("wheat", new Color(Integer.parseInt("F5DEB3", 16)));
		colors.put("white", new Color(Integer.parseInt("FFFFFF", 16)));
		colors.put("whitesmoke", new Color(Integer.parseInt("F5F5F5", 16)));
		colors.put("yellow", new Color(Integer.parseInt("FFFF00", 16)));
		colors.put("yellowgreen", new Color(Integer.parseInt("9ACD32", 16)));
		colors.put("zaffre", new Color(Integer.parseInt("0014A8", 16)));
	}

	public static Color toColor(String colorText) throws InvalidArgumentException {
		String appropriateColorName = null;
		colorText = colorText.toLowerCase();
		if (colorText.startsWith("0x")) {
			colorText = "#" + colorText.substring(2);
		}
		if (colorText.startsWith("#")) {
			if (colorText.length() == 4) {
				char[] fullName = {'#', colorText.charAt(1), colorText.charAt(1),
						colorText.charAt(2), colorText.charAt(2),
						colorText.charAt(3), colorText.charAt(3)};
				appropriateColorName = String.valueOf(fullName);
			} else if (colorText.length() == 7) {
				try {
					return new Color(Integer.parseInt(colorText.substring(1), 16));
				} catch (NumberFormatException e) {
					throw new InvalidArgumentException("Invalid color format: " + colorText);
				}
			}
		} else if (colors.containsKey(colorText)) {
			return colors.get(colorText);
		}

		if (appropriateColorName == null) {
			throw new InvalidArgumentException("Invalid color format: " + colorText);
		} else {
			return Color.decode(appropriateColorName);
		}
	}

	public static String toHex(String colorText) throws InvalidArgumentException {
		if (colors.containsKey(colorText)) {
			return toHex(colors.get(colorText));
		} else {
			throw new InvalidArgumentException("Invalid color format: " + colorText);
		}
	}

	public static String toHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
}
