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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;

import static org.junit.Assert.assertTrue;

/**
 * ColorUtilsTest
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ColorUtilsTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static final String SNOW = "snow";
	private static final String SNOW_HEX = "#FFFAFA";
	private static final String INCORRECT_NAME = "blablabla";
	private static final String INCORRECT_HEX = "#ZZFAFA";
	private static final String COLOR_SHORT_HEX = "#192";
	private static final String COLOR_FULL_HEX = "#119922";

	@Test
	public void test() throws InvalidArgumentException {

		Color colorSnow = ColorUtils.toColor(SNOW);
		assertTrue(ColorUtils.toHex(colorSnow).equalsIgnoreCase(SNOW_HEX));

		Color shortHexColor = ColorUtils.toColor(COLOR_SHORT_HEX);
		assertTrue(ColorUtils.toHex(shortHexColor).equalsIgnoreCase(COLOR_FULL_HEX));

		Color fullHexColor = ColorUtils.toColor(COLOR_FULL_HEX);
		assertTrue(ColorUtils.toHex(fullHexColor).equalsIgnoreCase(COLOR_FULL_HEX));
	}

	@Test
	public void incorrectHexTest() throws InvalidArgumentException {
		expectedException.expect(InvalidArgumentException.class);
		ColorUtils.toColor(INCORRECT_HEX);
	}

	@Test
	public void incorrectNameTest() throws InvalidArgumentException {
		expectedException.expect(InvalidArgumentException.class);
		ColorUtils.toColor(INCORRECT_NAME);
	}

}
