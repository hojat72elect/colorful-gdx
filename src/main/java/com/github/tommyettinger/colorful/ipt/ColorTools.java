package com.github.tommyettinger.colorful.ipt;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;
import com.github.tommyettinger.colorful.FloatColors;
import com.github.tommyettinger.colorful.Shaders;
import com.github.tommyettinger.colorful.ycwcm.Palette;

/*
_                         _   _   _   _   _
|  0.4002  0.7075 −0.0807 |   | X |   | L |
| −0.2280  1.1500  0.0612 | * | Y | = | M |
|  0.0000  0.0000  0.9184 |   | Z |   | S |
_                         _   _   _   _   _


Adobe RGB (1998), more at http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html 
RGB->XYZ                                XYZ->RGB                            
_                                 _     _ 	                               _ 
| 0.5767309  0.1855540  0.1881852 |     |  2.0413690 -0.5649464 -0.3446944 |  
| 0.2973769  0.6273491  0.0752741 |     | -0.9692660  1.8760108  0.0415560 | 
| 0.0270343  0.0706872  0.9911085 |     |  0.0134474 -0.1183897  1.0154096 | 
_							      _    	_                                  _ 
 */                           


/**
 * Contains code for manipulating colors as {@code int}, packed {@code float}, and {@link Color} values in the IPT
 * color space.
 */
public class ColorTools {
	/**
	 * Gets a packed float representation of a color given as 4 float components, here, I (intensity or lightness), P
	 * (protan, a chromatic component ranging from greenish to reddish), T (tritan, a chromatic component ranging from
	 * bluish to yellowish), and A (alpha or opacity). As long as you use a batch with {@link Shaders#fragmentShaderIPT}
	 * as its shader, colors passed with {@link com.badlogic.gdx.graphics.g2d.Batch#setPackedColor(float)} will be
	 * interpreted as IPT. Intensity should be between 0 and 1, inclusive, with 0 used for very dark colors (almost only
	 * black), and 1 used for very light colors (almost only white). Protan and tritan range from 0.0 to 1.0, with
	 * grayscale results when both are about 0.5. There's some aesthetic value in changing just one chroma value. When
	 * protan is high and tritan is low, the color is more purple/magenta, when both are low it is more bluish, when
	 * tritan is high and protan is low, the color tends to be greenish, and when both are high it tends to be orange.
	 * When warm and mild are both near 0.5f, the color is closer to gray.  Alpha is the multiplicative opacity of the
	 * color, and acts like RGBA's alpha.
	 * <br>
	 * This method bit-masks the resulting color's byte values, so any values can technically be given to this as luma,
	 * warm, and mild, but they will only be reversible from the returned float color to the original Y, Cw, and Cm
	 * values if the original values were in the range that {@link #protan(float)}, {@link #tritan(float)}, and
	 * {@link #intensity(float)} return.
	 *
	 * @param intens       0f to 1f, intensity or I component of IPT, with 0.5f meaning "no change" and 1f brightening
	 * @param protan       0f to 1f, protan or P component of IPT, with 1f more orange, red, or magenta
	 * @param tritan       0f to 1f, tritan or T component of IPT, with 1f more green, yellow, or red
	 * @param alpha      0f to 1f, 0f makes the color transparent and 1f makes it opaque 
	 * @return a float encoding a color with the given properties
	 */
	public static float ipt(float intens, float protan, float tritan, float alpha) {
		return NumberUtils.intBitsToFloat(((int) (alpha * 255) << 24 & 0xFE000000) | ((int) (tritan * 255) << 16 & 0xFF0000)
				| ((int) (protan * 255) << 8 & 0xFF00) | ((int) (intens * 255) & 0xFF));
	}

	/**
	 * Converts a packed float color in the format produced by {@link ColorTools#ipt(float, float, float, float)} to an RGBA8888 int.
	 * This format of int can be used with Pixmap and in some other places in libGDX.
	 * @param packed a packed float color, as produced by {@link ColorTools#ipt(float, float, float, float)}
	 * @return an RGBA8888 int color
	 */
	public static int toRGBA8888(final float packed)
	{
		final int decoded = NumberUtils.floatToIntBits(packed);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.097569f * p + 0.205226f * t;
		final float m = i - 0.11388f * p + 0.133217f * t;
		final float s = i + 0.032615f * p - 0.67689f * t;
		final int r = MathUtils.clamp((int) ((5.432622 * l - 4.679100 * m + 0.246257 * s) * 256.0), 0, 255);
		final int g = MathUtils.clamp((int) ((-1.10517 * l + 2.311198 * m - 0.205880 * s) * 256.0), 0, 255);
		final int b = MathUtils.clamp((int) ((0.028104 * l - 0.194660 * m + 1.166325 * s) * 256.0), 0, 255);
		return r << 24 | g << 16 | b << 8 | (decoded & 0xfe000000) >>> 24 | decoded >>> 31;
	}

	/**
	 * Converts a packed float color in the format produced by {@link ColorTools#ipt(float, float, float, float)}
	 * to a packed float in RGBA format.
	 * This format of float can be used with the standard SpriteBatch and in some other places in libGDX.
	 * @param packed a packed float color, as produced by {@link ColorTools#ipt(float, float, float, float)}
	 * @return a packed float color as RGBA
	 */
	public static float toRGBA(final float packed)
	{
		final int decoded = NumberUtils.floatToIntBits(packed);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.097569f * p + 0.205226f * t;
		final float m = i - 0.11388f * p + 0.133217f * t;
		final float s = i + 0.032615f * p - 0.67689f * t;
		final int r = MathUtils.clamp((int) ((5.432622 * l - 4.679100 * m + 0.246257 * s) * 256.0), 0, 255);
		final int g = MathUtils.clamp((int) ((-1.10517 * l + 2.311198 * m - 0.205880 * s) * 256.0), 0, 255);
		final int b = MathUtils.clamp((int) ((0.028104 * l - 0.194660 * m + 1.166325 * s) * 256.0), 0, 255);
		return NumberUtils.intBitsToFloat(r | g << 8 | b << 16 | (decoded & 0xfe000000));
	}
	/**
	 * Writes an IPT-format packed float color (the format produced by {@link ColorTools#ipt(float, float, float, float)})
	 * into an RGBA8888 Color as used by libGDX (called {@code editing}).
	 * @param editing a libGDX color that will be filled in-place with an RGBA conversion of {@code packed}
	 * @param packed a packed float color, as produced by {@link ColorTools#ipt(float, float, float, float)}
	 * @return an RGBA8888 int color
	 */
	public static Color toColor(Color editing, final float packed)
	{
		final int decoded = NumberUtils.floatToIntBits(packed);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.097569f * p + 0.205226f * t;
		final float m = i - 0.11388f * p + 0.133217f * t;
		final float s = i + 0.032615f * p - 0.67689f * t;
		editing.r = (5.432622f * l - 4.679100f * m + 0.246257f * s);
		editing.g = (-1.10517f * l + 2.311198f * m - 0.205880f * s);
		editing.b = (0.028104f * l - 0.194660f * m + 1.166325f * s);
		editing.a = (decoded >>> 25) * 0x1.020408p-7f; // this is 1/127 as a float
		return editing.clamp();
	}

	/**
	 * Takes a color encoded as an RGBA8888 int and converts to a packed float in the IPT format this uses.
	 * @param rgba an int with the channels (in order) red, green, blue, alpha; should have 8 bits per channel
	 * @return a packed float as IPT, which this class can use
	 */
	public static float fromRGBA8888(final int rgba) {
		final float r = (rgba >>> 24) * 0x1.010101010101p-8f;
		final float g = (rgba >>> 16 & 0xFF) * 0x1.010101010101p-8f;
		final float b = (rgba >>> 8 & 0xFF) * 0x1.010101010101p-8f;

		float l = 0.313921f * r + 0.639468f * g + 0.0465970f * b;
		float m = 0.151693f * r + 0.748209f * g + 0.1000044f * b;
		float s = 0.017753f * r + 0.109468f * g + 0.8729690f * b;
		
		return NumberUtils.intBitsToFloat(
						  MathUtils.clamp((int)((0.4000f * l + 0.4000f * m + 0.2000f * s) * 255.0f + 0.500f), 0, 255)
						| MathUtils.clamp((int)((4.4550f * l - 4.8510f * m + 0.3960f * s) * 127.5f + 127.5f), 0, 255) << 8
						| MathUtils.clamp((int)((0.8056f * l + 0.3572f * m - 1.1628f * s) * 127.5f + 127.5f), 0, 255) << 16
						| (rgba & 0xFE) << 24);
	}

	/**
	 * Takes a color encoded as an RGBA8888 packed float and converts to a packed float in the IPT format this uses.
	 * @param packed a packed float in RGBA8888 format, with A in the MSB and R in the LSB
	 * @return a packed float as IPT, which this class can use
	 */
	public static float fromRGBA(final float packed) {
		final int abgr = NumberUtils.floatToIntBits(packed);
		final float r = (abgr & 0xFF) * 0x1.010101010101p-8f;
		final float g = (abgr >>> 8 & 0xFF) * 0x1.010101010101p-8f;
		final float b = (abgr >>> 16 & 0xFF) * 0x1.010101010101p-8f;
		final float l = 0.313921f * r + 0.639468f * g + 0.0465970f * b;
		final float m = 0.151693f * r + 0.748209f * g + 0.1000044f * b;
		final float s = 0.017700f * r + 0.109400f * g + 0.8729000f * b;

		return NumberUtils.intBitsToFloat(
				MathUtils.clamp((int)((0.4000f * l + 0.4000f * m + 0.2000f * s) * 255.0f + 0.500f), 0, 255)
						| MathUtils.clamp((int)((4.4550f * l - 4.8510f * m + 0.3960f * s) * 127.5f + 127.5f), 0, 255) << 8
						| MathUtils.clamp((int)((0.8056f * l + 0.3572f * m - 1.1628f * s) * 127.5f + 127.5f), 0, 255) << 16
						| (abgr & 0xFE000000));
	}

	/**
	 * Takes a libGDX Color that uses RGBA8888 channels and converts to a packed float in the IPT format this uses.
	 * @param color a libGDX RGBA8888 Color
	 * @return a packed float as IPT, which this class can use
	 */
	public static float fromColor(final Color color) {
		final float l = 0.313921f * color.r + 0.639468f * color.g + 0.0465970f * color.b;
		final float m = 0.151693f * color.r + 0.748209f * color.g + 0.1000044f * color.b;
		final float s = 0.017700f * color.r + 0.109400f * color.g + 0.8729000f * color.b;

		return NumberUtils.intBitsToFloat(
				MathUtils.clamp((int)((0.4000f * l + 0.4000f * m + 0.2000f * s) * 255.0f + 0.500f), 0, 255)
						| MathUtils.clamp((int)((4.4550f * l - 4.8510f * m + 0.3960f * s) * 127.5f + 127.5f), 0, 255) << 8
						| MathUtils.clamp((int)((0.8056f * l + 0.3572f * m - 1.1628f * s) * 127.5f + 127.5f), 0, 255) << 16
						| ((int)(color.a * 255f) << 24 & 0xFE000000));
	}

	/**
	 * Takes RGBA components from 0.0 to 1.0 each and converts to a packed float in the IPT format this uses.
	 * @param r red, from 0.0 to 1.0 (both inclusive)
	 * @param g green, from 0.0 to 1.0 (both inclusive)
	 * @param b blue, from 0.0 to 1.0 (both inclusive)
	 * @param a alpha, from 0.0 to 1.0 (both inclusive)
	 * @return a packed float as IPT, which this class can use
	 */
	public static float fromRGBA(final float r, final float g, final float b, final float a) {
		final float l = 0.313921f * r + 0.639468f * g + 0.0465970f * b;
		final float m = 0.151693f * r + 0.748209f * g + 0.1000044f * b;
		final float s = 0.017700f * r + 0.109400f * g + 0.8729000f * b;

		return NumberUtils.intBitsToFloat(
				MathUtils.clamp((int)((0.4000f * l + 0.4000f * m + 0.2000f * s) * 255.0f + 0.500f), 0, 255)
						| MathUtils.clamp((int)((4.4550f * l - 4.8510f * m + 0.3960f * s) * 127.5f + 127.5f), 0, 255) << 8
						| MathUtils.clamp((int)((0.8056f * l + 0.3572f * m - 1.1628f * s) * 127.5f + 127.5f), 0, 255) << 16
						| ((int)(a * 255f) << 24 & 0xFE000000));
	}

	/**
	 * Gets the red channel value of the given encoded color, as an int ranging from 0 to 255, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return an int from 0 to 255, inclusive, representing the red channel value of the given encoded color
	 */
	public static int redInt(final float encoded)
	{
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		return MathUtils.clamp((int) ((5.432622 * l - 4.679100 * m + 0.246257 * s) * 256.0), 0, 255);
	}

	/**
	 * Gets the green channel value of the given encoded color, as an int ranging from 0 to 255, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return an int from 0 to 255, inclusive, representing the green channel value of the given encoded color
	 */
	public static int greenInt(final float encoded)
	{
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		return MathUtils.clamp((int) ((-1.10517 * l + 2.311198 * m - 0.205880 * s) * 256.0), 0, 255); 
	}

	/**
	 * Gets the blue channel value of the given encoded color, as an int ranging from 0 to 255, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return an int from 0 to 255, inclusive, representing the blue channel value of the given encoded color
	 */
	public static int blueInt(final float encoded)
	{
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		return MathUtils.clamp((int) ((0.028104 * l - 0.194660 * m + 1.166325 * s) * 256.0), 0, 255);
	}

	/**
	 * Gets the alpha channel value of the given encoded color, as an even int ranging from 0 to 254, inclusive. Because
	 * of how alpha is stored in libGDX, no odd-number values are possible for alpha.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return an even int from 0 to 254, inclusive, representing the alpha channel value of the given encoded color
	 */
	public static int alphaInt(final float encoded)
	{
		return (NumberUtils.floatToIntBits(encoded) & 0xfe000000) >>> 24;
	}

	/**
	 * Gets the red channel value of the given encoded color, as a float from 0.0f to 1.0f, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return a float from 0.0f to 1.0f, inclusive, representing the red channel value of the given encoded color
	 */
	public static float red(final float encoded)
	{
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		return (5.432622f * l - 4.679100f * m + 0.246257f * s);
	}

	/**
	 * Gets the green channel value of the given encoded color, as a float from 0.0f to 1.0f, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return a float from 0.0f to 1.0f, inclusive, representing the green channel value of the given encoded color
	 */
	public static float green(final float encoded)
	{
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		return (-1.10517f * l + 2.311198f * m - 0.205880f * s);
	}

	/**
	 * Gets the blue channel value of the given encoded color, as a float from 0.0f to 1.0f, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return a float from 0.0f to 1.0f, inclusive, representing the blue channel value of the given encoded color
	 */
	public static float blue(final float encoded)
	{
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		return (0.028104f * l - 0.194660f * m + 1.166325f * s);
	}

	/**
	 * Gets the alpha channel value of the given encoded color, as a float from 0.0f to 1.0f, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return a float from 0.0f to 1.0f, inclusive, representing the alpha channel value of the given encoded color
	 */
	public static float alpha(final float encoded)
	{
		return ((NumberUtils.floatToIntBits(encoded) & 0xfe000000) >>> 24) * 0x1.020408p-8f;
	}

	/**
	 * Gets a color as an IPT packed float given floats representing hue, saturation, lightness, and opacity.
	 * All parameters should normally be between 0 and 1 inclusive, though any hue is tolerated (precision loss may
	 * affect the color if the hue is too large). A hue of 0 is red, progressively higher hue values go to orange,
	 * yellow, green, blue, and purple before wrapping around to red as it approaches 1. A saturation of 0 is grayscale,
	 * a saturation of 1 is brightly colored, and values close to 1 will usually appear more distinct than values close
	 * to 0, especially if the hue is different. A lightness of 0.001f or less is always black (also using a shortcut if
	 * this is the case, respecting opacity), while a lightness of 1f is white. Very bright colors are mostly in a band
	 * of high-saturation where lightness is 0.5f.
	 *
	 * @param hue        0f to 1f, color wheel position
	 * @param saturation 0f to 1f, 0f is grayscale and 1f is brightly colored
	 * @param lightness  0f to 1f, 0f is black and 1f is white
	 * @param opacity    0f to 1f, 0f is fully transparent and 1f is opaque
	 * @return a float encoding a color with the given properties
	 */
	public static float floatGetHSL(float hue, float saturation, float lightness, float opacity) {
		if (lightness <= 0.001f) {
			return NumberUtils.intBitsToFloat((((int) (opacity * 255f) << 24) & 0xFE000000) | 0x7F7F00);
		} else {
			return fromRGBA(FloatColors.hsl2rgb(hue, saturation, lightness, opacity));
		}
	}

	/**
	 * Gets the saturation of the given encoded color, as a float ranging from 0.0f to 1.0f, inclusive.
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return the saturation of the color from 0.0 (a grayscale color; inclusive) to 1.0 (a bright color, inclusive)
	 */
	public static float saturation(final float encoded) {
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		if(Math.abs(i - 0.5) > 0.495f) return 0f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		final float r = MathUtils.clamp((5.432622f * l - 4.679100f * m + 0.246257f * s), 0f, 1f);
		final float g = MathUtils.clamp((-1.10517f * l + 2.311198f * m - 0.205880f * s), 0f, 1f);
		final float b = MathUtils.clamp((0.028104f * l - 0.194660f * m + 1.166325f * s), 0f, 1f);
		float x, y, w;
		if(g < b) {
			x = b;
			y = g;
		}
		else {
			x = g;
			y = b;
		}
		if(r < x) {
			w = r;
		}
		else {
			w = x;
			x = r;
		}
		return x - Math.min(w, y);
//		float d = x - Math.min(w, y);
//		float li = x * (1f - 0.5f * d / (x + 1e-10f));
//		return (x - li); // (Math.min(li, 1f - li) + 1e-10f);
	}

	public static float lightness(final float encoded) {
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		final float r = MathUtils.clamp((5.432622f * l - 4.679100f * m + 0.246257f * s), 0f, 1f);
		final float g = MathUtils.clamp((-1.10517f * l + 2.311198f * m - 0.205880f * s), 0f, 1f);
		final float b = MathUtils.clamp((0.028104f * l - 0.194660f * m + 1.166325f * s), 0f, 1f);

		float x, y, w;
		if(g < b) {
			x = b;
			y = g;
		}
		else {
			x = g;
			y = b;
		}
		if(r < x) {
			w = r;
		}
		else {
			w = x;
			x = r;
		}
		float d = x - Math.min(w, y);
		return x * (1f - 0.5f * d / (x + 1e-10f));
	}

	/**
	 * Gets the hue of the given encoded color, as a float from 0f (inclusive, red and approaching orange if increased)
	 * to 1f (exclusive, red and approaching purple if decreased).
	 * @param encoded a color as a packed float that can be obtained by {@link #ipt(float, float, float, float)}
	 * @return The hue of the color from 0.0 (red, inclusive) towards orange, then yellow, and
	 * eventually to purple before looping back to almost the same red (1.0, exclusive)
	 */
	public static float hue(final float encoded) {
		final int decoded = NumberUtils.floatToIntBits(encoded);
		final float i = (decoded & 0xff) / 255f;
		final float p = ((decoded >>> 8 & 0xff) - 127.5f) / 127.5f;
		final float t = ((decoded >>> 16 & 0xff) - 127.5f) / 127.5f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		final float r = MathUtils.clamp((5.432622f * l - 4.679100f * m + 0.246257f * s), 0f, 1f);
		final float g = MathUtils.clamp((-1.10517f * l + 2.311198f * m - 0.205880f * s), 0f, 1f);
		final float b = MathUtils.clamp((0.028104f * l - 0.194660f * m + 1.166325f * s), 0f, 1f);
		float x, y, z, w;
		if(g < b) {
			x = b;
			y = g;
			z = -1f;
			w = 2f / 3f;
		}
		else {
			x = g;
			y = b;
			z = 0f;
			w = -1f / 3f;
		}
		if(r < x) {
			z = w;
			w = r;
		}
		else {
			w = x;
			x = r;
		}
		float d = x - Math.min(w, y);
		return Math.abs(z + (w - y) / (6f * d + 1e-10f));
	}

	/**
	 * The "intensity" of the given packed float in IPT format, which is like its lightness; ranges from 0.0f to
	 * 1.0f . You can edit the intensity of a color with {@link #lighten(float, float)} and
	 * {@link #darken(float, float)}.
	 *
	 * @param encoded a color encoded as a packed float, as by {@link #ipt(float, float, float, float)}
	 * @return the intensity value as a float from 0.0f to 1.0f
	 */
	public static float intensity(final float encoded)
	{
		return (NumberUtils.floatToIntBits(encoded) & 0xff) / 255f;
	}

	/**
	 * The "protan" of the given packed float in IPT format, which when combined with tritan describes the
	 * hue and saturation of a color; ranges from 0f to 1f . If protan is 0f, the color will be cooler, more green or
	 * blue; if protan is 1f, the color will be warmer, from magenta to orange. You can edit the protan of a color with
	 * {@link #protanUp(float, float)} and {@link #protanDown(float, float)}.
	 * @param encoded a color encoded as a packed float, as by {@link #ipt(float, float, float, float)}
	 * @return the protan value as a float from 0.0f to 1.0f
	 */
	public static float protan(final float encoded)
	{
		return ((NumberUtils.floatToIntBits(encoded) >>> 8 & 0xff)) / 255f;
	}

	/**
	 * The "tritan" of the given packed float in IPT format, which when combined with protan describes the
	 * hue and saturation of a color; ranges from 0f to 1f . If tritan is 0f, the color will be more "artificial", more
	 * blue or purple; if tritan is 1f, the color will be more "natural", from green to yellow to orange. You can edit
	 * the tritan of a color with {@link #tritanUp(float, float)} and {@link #tritanDown(float, float)}.
	 * @param encoded a color encoded as a packed float, as by {@link #ipt(float, float, float, float)}
	 * @return the tritan value as a float from 0.0f to 1.0f
	 */
	public static float tritan(final float encoded)
	{
		return ((NumberUtils.floatToIntBits(encoded) >>> 16 & 0xff)) / 255f;
	}

	/**
	 * Gets a variation on the packed float color basis as another packed float that has its hue, saturation, value, and
	 * opacity adjusted by the specified amounts. Takes floats representing the amounts of change to apply to hue,
	 * saturation, value, and opacity; these can be between -1f and 1f. Returns a float that can be used as a packed or
	 * encoded color with methods like {@link com.badlogic.gdx.graphics.g2d.Batch#setPackedColor(float)}. The float is
	 * likely to be different than the result of {@link #ipt(float, float, float, float)} unless hue saturation,
	 * value, and opacity are all 0. This won't allocate any objects.
	 * <br>
	 * The parameters this takes all specify additive changes for a color component, clamping the final values so they
	 * can't go above 1 or below 0, with an exception for hue, which can rotate around if lower or higher hues would be
	 * used. As an example, if you give this 0.4f for saturation, and the current color has saturation 0.7f, then the
	 * resulting color will have 1f for saturation. If you gave this -0.1f for saturation and the current color again
	 * has saturation 0.7f, then resulting color will have 0.6f for saturation.
	 *
	 * @param basis      a packed float color that will be used as the starting point to make the next color
	 * @param hue        -1f to 1f, the hue change that can be applied to the new float color (not clamped, wraps)
	 * @param saturation -1f to 1f, the saturation change that can be applied to the new float color
	 * @param value      -1f to 1f, the value/brightness change that can be applied to the new float color
	 * @param opacity    -1f to 1f, the opacity/alpha change that can be applied to the new float color
	 * @return a float encoding a variation of basis with the given changes
	 */
	public static float toEditedFloat(float basis, float hue, float saturation, float value, float opacity) {
		final int e = NumberUtils.floatToIntBits(basis);
		final float i = MathUtils.clamp(value + (e & 0xff) / 255f, 0f, 1f);
		opacity = MathUtils.clamp(opacity + (e >>> 24 & 0xfe) * 0x1.020408p-8f, 0f, 1f);
		if (i <= 0.001f)
			return NumberUtils.intBitsToFloat((((int) (opacity * 255f) << 24) & 0xFE000000) | 0x808000);
		final float p = ((e >>> 7 & 0x1fe) - 0xff) / 255f;
		final float t = ((e >>> 15 & 0x1fe) - 0xff) / 255f;
		final float l = i + 0.06503950f * p + 0.15391950f * t;
		final float m = i - 0.07591241f * p + 0.09991275f * t;
		final float s = i + 0.02174116f * p - 0.50766750f * t;
		final float r = MathUtils.clamp((5.432622f * l - 4.679100f * m + 0.246257f * s), 0f, 1f);
		final float g = MathUtils.clamp((-1.10517f * l + 2.311198f * m - 0.205880f * s), 0f, 1f);
		final float b = MathUtils.clamp((0.028104f * l - 0.194660f * m + 1.166325f * s), 0f, 1f);
		float x, y, z, w;
		if(g < b) {
			x = b;
			y = g;
			z = -1f;
			w = 2f / 3f;
		}
		else {
			x = g;
			y = b;
			z = 0f;
			w = -1f / 3f;
		}
		if(r < x) {
			z = w;
			w = r;
		}
		else {
			w = x;
			x = r;
		}
		float d = x - Math.min(w, y);
		float hue2 = Math.abs(z + (w - y) / (6f * d + 1e-10f));
		float sat2 = (x - i) / (Math.min(i, 1f - i) + 1e-10f);
		return fromRGBA(FloatColors.hsl2rgb(hue2 + hue + 1 - (int)(hue2 + hue + 1), MathUtils.clamp(saturation + sat2, 0f, 1f), i, opacity));
	}

	/**
	 * Interpolates from the packed float color start towards white by change. While change should be between 0f (return
	 * start as-is) and 1f (return white), start should be a packed color, as from
	 * {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary Colors, and
	 * is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp towards
	 * white. Unlike {@link FloatColors#lerpFloatColors(float, float, float)}, this keeps the alpha and both chroma of start as-is.
	 * @see #darken(float, float) the counterpart method that darkens a float color
	 * @param start the starting color as a packed float
	 * @param change how much to go from start toward white, as a float between 0 and 1; higher means closer to white
	 * @return a packed float that represents a color between start and white
	 */
	public static float lighten(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), i = s & 0xFF, other = s & 0xFEFFFF00;
		return NumberUtils.intBitsToFloat(((int) (i + (0xFF - i) * change) & 0xFF) | other);
	}

	/**
	 * Interpolates from the packed float color start towards black by change. While change should be between 0f (return
	 * start as-is) and 1f (return black), start should be a packed color, as from
	 * {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary Colors, and
	 * is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp towards
	 * black. Unlike {@link FloatColors#lerpFloatColors(float, float, float)}, this keeps the alpha and both chroma of start as-is.
	 * @see #lighten(float, float) the counterpart method that lightens a float color
	 * @param start the starting color as a packed float
	 * @param change how much to go from start toward black, as a float between 0 and 1; higher means closer to black
	 * @return a packed float that represents a color between start and black
	 */
	public static float darken(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), i = s & 0xFF, other = s & 0xFEFFFF00;
		return NumberUtils.intBitsToFloat(((int) (i * (1f - change)) & 0xFF) | other);
	}

	/**
	 * Interpolates from the packed float color start towards a warmer color (orange to magenta) by change. While change
	 * should be between 0f (return start as-is) and 1f (return fully warmed), start should be a packed color, as from
	 * {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary Colors,
	 * and is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp towards
	 * a warmer color. Unlike {@link FloatColors#lerpFloatColors(float, float, float)}, this keeps the alpha and luma of start
	 * as-is.
	 * @see #protanDown(float, float) the counterpart method that cools a float color
	 * @param start the starting color as a packed float
	 * @param change how much to warm start, as a float between 0 and 1; higher means a warmer result
	 * @return a packed float that represents a color between start and a warmer color
	 */
	public static float protanUp(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), p = s >>> 8 & 0xFF, other = s & 0xFEFF00FF;
		return NumberUtils.intBitsToFloat(((int) (p + (0xFF - p) * change) << 8 & 0xFF) | other);
	}

	/**
	 * Interpolates from the packed float color start towards a cooler color (green to blue) by change. While change
	 * should be between 0f (return start as-is) and 1f (return fully cooled), start should be a packed color, as from
	 * {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary Colors, and
	 * is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp towards
	 * a cooler color. Unlike {@link FloatColors#lerpFloatColors(float, float, float)}, this keeps the alpha and luma of start
	 * as-is.
	 * @see #protanUp(float, float) the counterpart method that warms a float color
	 * @param start the starting color as a packed float
	 * @param change how much to cool start, as a float between 0 and 1; higher means a cooler result
	 * @return a packed float that represents a color between start and a cooler color
	 */
	public static float protanDown(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), p = s >>> 8 & 0xFF, other = s & 0xFEFF00FF;
		return NumberUtils.intBitsToFloat(((int) (p * (1f - change)) & 0xFF) << 8 | other);
	}

	/**
	 * Interpolates from the packed float color start towards a "natural" color (between green and orange) by change.
	 * While change should be between 0f (return start as-is) and 1f (return fully natural), start should be a packed color, as
	 * from {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary
	 * Colors, and is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp
	 * towards a more natural color. Unlike {@link FloatColors#lerpFloatColors(float, float, float)}, this keeps the alpha and luma of
	 * start as-is.
	 * @see #tritanDown(float, float) the counterpart method that makes a float color less natural
	 * @param start the starting color as a packed float
	 * @param change how much to change start to a natural color, as a float between 0 and 1; higher means a more natural result
	 * @return a packed float that represents a color between start and a more natural color
	 */
	public static float tritanUp(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), t = s >>> 16 & 0xFF, other = s & 0xFE00FFFF;
		return NumberUtils.intBitsToFloat(((int) (t + (0xFF - t) * change) << 8 & 0xFF) | other);
	}

	/**
	 * Interpolates from the packed float color start towards an "artificial" color (between blue and purple) by change.
	 * While change should be between 0f (return start as-is) and 1f (return fully artificial), start should be a packed color, as
	 * from {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary
	 * Colors, and is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp
	 * towards a more artificial color. Unlike {@link FloatColors#lerpFloatColors(float, float, float)}, this keeps the
	 * alpha and luma of start as-is.
	 * @see #tritanUp(float, float) the counterpart method that makes a float color less artificial
	 * @param start the starting color as a packed float
	 * @param change how much to change start to a bolder color, as a float between 0 and 1; higher means a more artificial result
	 * @return a packed float that represents a color between start and a more artificial color
	 */
	public static float tritanDown(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), t = s >>> 16 & 0xFF, other = s & 0xFE00FFFF;
		return NumberUtils.intBitsToFloat(((int) (t * (1f - change)) & 0xFF) << 8 | other);
	}

	/**
	 * Interpolates from the packed float color start towards that color made opaque by change. While change should be
	 * between 0f (return start as-is) and 1f (return start with full alpha), start should be a packed color, as from
	 * {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary Colors, and
	 * is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp towards
	 * transparent. This won't change the intensity, protan, or tritan of the color.
	 * @see #fade(float, float) the counterpart method that makes a float color more translucent
	 * @param start the starting color as a packed float
	 * @param change how much to go from start toward opaque, as a float between 0 and 1; higher means closer to opaque
	 * @return a packed float that represents a color between start and its opaque version
	 */
	public static float blot(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), opacity = s >>> 24 & 0xFE, other = s & 0x00FFFFFF;
		return NumberUtils.intBitsToFloat(((int) (opacity + (0xFE - opacity) * change) & 0xFE) << 24 | other);
	}

	/**
	 * Interpolates from the packed float color start towards transparent by change. While change should be between 0
	 * (return start as-is) and 1f (return the color with 0 alpha), start should be a packed color, as from
	 * {@link #ipt(float, float, float, float)}. This is a good way to reduce allocations of temporary Colors,
	 * and is a little more efficient and clear than using {@link FloatColors#lerpFloatColors(float, float, float)} to lerp towards
	 * transparent. This won't change the intensity, protan, or tritan of the color.
	 * @see #blot(float, float) the counterpart method that makes a float color more opaque
	 * @param start the starting color as a packed float
	 * @param change how much to go from start toward transparent, as a float between 0 and 1; higher means closer to transparent
	 * @return a packed float that represents a color between start and transparent
	 */
	public static float fade(final float start, final float change) {
		final int s = NumberUtils.floatToIntBits(start), opacity = s & 0xFE, other = s & 0x00FFFFFF;
		return NumberUtils.intBitsToFloat(((int) (opacity * (1f - change)) & 0xFE) << 24 | other);
	}

	/**
	 * Given a packed float IPT color {@code mainColor} and another IPT color that it should be made to contrast with,
	 * gets a packed float IPT color with roughly inverted intnsity but the same chromatic channels and opacity (P and T
	 * are likely to be clamped if the result gets close to white or black). This won't ever produce black or other very
	 * dark colors, and also has a gap in the range it produces for intensity values between 0.5 and 0.55. That allows
	 * most of the colors this method produces to contrast well as a foreground when displayed on a background of
	 * {@code contrastingColor}, or vice versa. This will leave the intensity unchanged if the chromatic channels of the
	 * contrastingColor and those of the mainColor are already very different. This has nothing to do with the contrast
	 * channel of the tweak in ColorfulBatch; where that part of the tweak can make too-similar lightness values further
	 * apart by just a little, this makes a modification on {@code mainColor} to maximize its lightness difference from
	 * {@code contrastingColor} without losing its other qualities.
	 * @param mainColor a packed float color, as produced by {@link #ipt(float, float, float, float)}; this is the color that will be adjusted
	 * @param contrastingColor a packed float color, as produced by {@link #ipt(float, float, float, float)}; the adjusted mainColor will contrast with this
	 * @return a different IPT packed float color, based on mainColor but with potentially very different lightness
	 */
	public static float inverseIntensity(final float mainColor, final float contrastingColor)
	{
		final int bits = NumberUtils.floatToIntBits(mainColor),
				contrastBits = NumberUtils.floatToIntBits(contrastingColor),
				i = (bits & 0xff),
				p = (bits >>> 8 & 0xff),
				t = (bits >>> 16 & 0xff),
				ci = (contrastBits & 0xff),
				cp = (contrastBits >>> 8 & 0xff),
				ct = (contrastBits >>> 16 & 0xff);
		if((p - cp) * (p - cp) + (t - ct) * (t - ct) >= 0x10000)
			return mainColor;
		return ipt(ci < 128 ? i * (0.45f / 255f) + 0.55f : 0.5f - i * (0.45f / 255f), p, t, 0x1.0p-8f * (bits >>> 24));
	}

	/**
	 * Makes the additive IPT color stored in {@code color} cause less of a change when used as a tint, as if it were
	 * mixed with neutral gray. When {@code fraction} is 1.0, this returns color unchanged; when fraction is 0.0, it
	 * returns {@link Palette#GRAY}, and when it is in-between 0.0 and 1.0 it returns something between the two. This is
	 * meant for things like area of effect abilities that make smaller color changes toward their periphery.
	 * @param color a color that should have its tinting effect potentially weakened
	 * @param fraction how much of {@code color} should be kept, from 0.0 to 1.0
	 * @return an IPT float color between gray and {@code color}
	 */
	public static float lessenChange(final float color, float fraction) {
		final int e = NumberUtils.floatToIntBits(color),
				is = 0x80, ps = 0x80, ts = 0x80, as = 0xFE,
				ie = (e & 0xFF), pe = (e >>> 8) & 0xFF, te = (e >>> 16) & 0xFF, ae = e >>> 24 & 0xFE;
		return NumberUtils.intBitsToFloat(((int) (is + fraction * (ie - is)) & 0xFF)
				| (((int) (ps + fraction * (pe - ps)) & 0xFF) << 8)
				| (((int) (ts + fraction * (te - ts)) & 0xFF) << 16)
				| (((int) (as + fraction * (ae - as)) & 0xFE) << 24));
	}
}
