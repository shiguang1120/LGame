/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.opengl;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTextureBatch.Cache;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.Affine2f;
import loon.utils.GLUtils;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;

public class LSTRFont implements LRelease {

	private char newLineFlag = '\n';

	private boolean useCache;

	private float offsetX = 1, offsetY = 1;

	private ObjectMap<String, Cache> displays;

	private int totalCharSet = 256;

	private ObjectMap<Character, IntObject> customChars = new ObjectMap<Character, IntObject>();

	private IntObject[] charArray = new IntObject[totalCharSet];

	private LColor[] colors = null;

	private LFont font;

	private IntObject intObject;

	private Cache display;

	private float ascent;

	private int charCurrent;

	private int totalWidth = 0, totalHeight = 0;

	private int textureWidth = 512;

	private int textureHeight = 512;

	private int fontSize = 0;

	private int fontHeight = 0;

	private LTextureBatch fontBatch;

	private class IntObject {

		public int width;

		public int height;

		public int storedX;

		public int storedY;

	}

	private boolean initChars = false;

	private char[] additionalChars = null;

	public LSTRFont(LFont font) {
		this(font, (char[]) null);
	}

	public LSTRFont(LFont font, String strings) {
		this(font, strings.toCharArray());
	}

	public LSTRFont(LFont font, String[] strings) {
		this(font, filterStrings(strings).toCharArray());
	}

	public LSTRFont(LFont font, char[] chs) {
		if (displays == null) {
			displays = new ObjectMap<String, Cache>(totalCharSet);
		} else {
			displays.clear();
		}
		this.useCache = true;
		this.font = font;
		this.additionalChars = chs;
		make();
	}

	private LTexture texture;

	private void make() {
		if (initChars) {
			return;
		}
		this.fontSize = (int) font.getSize();
		this.ascent = font.getAscent();
		if (additionalChars != null && additionalChars.length > totalCharSet) {
			textureWidth *= 2;
		}
		Canvas canvas = LSystem.base().graphics()
				.createCanvas(textureWidth, textureHeight);
		canvas.setColor(LColor.white);
		canvas.setFont(font);
		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;
		int customCharsLength = (additionalChars != null) ? additionalChars.length
				: 0;
		this.totalCharSet = customCharsLength == 0 ? totalCharSet : 0;
		StringBuilder sbr = new StringBuilder(totalCharSet);
		for (int i = 0; i < totalCharSet + customCharsLength; i++) {
			char ch = (i < totalCharSet) ? (char) i : additionalChars[i
					- totalCharSet];

			TextLayout layout = font.getLayoutText(String.valueOf(ch));

			int charwidth = layout.charWidth(ch);

			if (charwidth <= 0) {
				charwidth = 1;
			}

			int charheight = (int) layout.getHeight();
			if (charheight <= 0) {
				charheight = fontSize;
			}

			IntObject newIntObject = new IntObject();

			newIntObject.width = charwidth;
			newIntObject.height = charheight;

			if (positionX + newIntObject.width >= textureWidth) {
				layout = font.getLayoutText(sbr.toString());
				canvas.fillText(layout, 0, positionY);
				sbr.delete(0, sbr.length());
				positionX = 0;
				positionY += rowHeight;
				rowHeight = 0;
			}

			newIntObject.storedX = positionX;
			newIntObject.storedY = positionY;

			if (newIntObject.height > fontHeight) {
				fontHeight = newIntObject.height;
			}

			if (newIntObject.height > rowHeight) {
				rowHeight = newIntObject.height;
			}

			sbr.append(ch);

			positionX += newIntObject.width;

			if (i < totalCharSet) {
				charArray[i] = newIntObject;
			} else {
				customChars.put(ch, newIntObject);
			}

		}
		if (sbr.length() > 0) {
			TextLayout layout = font.getLayoutText(sbr.toString());
			canvas.fillText(layout, 0, positionY);
			sbr = null;
		}

		LTextureBatch tmpbatch = fontBatch;
		fontBatch = new LTextureBatch(
				texture = canvas.toTexture(LTexture.Format.LINEAR));
		fontBatch.setBlendState(BlendState.AlphaBlend);
		if (tmpbatch != null) {
			tmpbatch.destoryAll();
		}
		initChars = true;
	}

	public LTexture getTexture() {
		return texture;
	}

	public void drawString(String chars, float x, float y) {
		drawString(x, y, 1f, 1f, 0, 0, 0, chars, LColor.white, 0,
				chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, 0, chars, color, 0, chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float rotation,
			LColor color) {
		drawString(x, y, 1f, 1f, 0, 0, rotation, chars, color, 0,
				chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float rotation) {
		drawString(x, y, 1f, 1f, 0, 0, rotation, chars, LColor.white, 0,
				chars.length() - 1);
	}

	public void drawString(String chars, float x, float y, float sx, float sy,
			float ax, float ay, float rotation, LColor c) {
		drawString(x, y, sx, sy, ax, ay, rotation, chars, c, 0,
				chars.length() - 1);
	}

	private void drawString(float x, float y, float sx, float sy, float ax,
			float ay, float rotation, String chars, LColor c, int startIndex,
			int endIndex) {
		make();
		if (StringUtils.isEmpty(chars)) {
			return;
		}
		if (displays.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			synchronized (displays) {
				for (Cache cache : displays.values()) {
					if (cache != null) {
						cache.close();
						cache = null;
					}
				}
			}
			displays.clear();
		}

		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		if (rotation != 0 && (ax == 0 && ay == 0)) {
			TextLayout layout = font.getLayoutText(chars);
			ax = layout.bounds.width / 2;
			ay = layout.bounds.height;
		}
		if (useCache) {
			display = displays.get(chars);
			if (display == null) {
				fontBatch.begin();
				float old = fontBatch.getFloatColor();
				fontBatch.setColor(c);
				char[] charList = chars.toCharArray();
				for (int i = 0; i < charList.length; i++) {
					charCurrent = charList[i];
					if (charCurrent < totalCharSet) {
						intObject = charArray[charCurrent];
					} else {
						intObject = customChars.get((char) charCurrent);
					}
					if (charCurrent == newLineFlag) {
						totalHeight += fontSize;
						totalWidth = 0;
					}
					if (intObject != null) {
						if ((i >= startIndex) || (i <= endIndex)) {
							fontBatch.drawQuad(totalWidth, totalHeight,
									(totalWidth + intObject.width) - offsetX,
									(totalHeight + intObject.height) - offsetY,
									intObject.storedX, intObject.storedY,
									intObject.storedX + intObject.width
											- offsetX, intObject.storedY
											+ intObject.height - offsetY);
						}
						totalWidth += intObject.width;
					}
				}
				fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
				fontBatch.setColor(old);
				displays.put(chars, display = fontBatch.newCache());
			} else if (display != null && fontBatch != null
					&& fontBatch.toTexture() != null) {
				fontBatch.postCache(display, c, x, y, sx, sy, ax, ay, rotation);
			}
		} else {
			fontBatch.begin();
			float old = fontBatch.getFloatColor();
			fontBatch.setColor(c);
			char[] charList = chars.toCharArray();
			for (int i = 0; i < charList.length; i++) {
				charCurrent = charList[i];
				if (charCurrent < totalCharSet) {
					intObject = charArray[charCurrent];
				} else {
					intObject = customChars.get((char) charCurrent);
				}
				if (charCurrent == newLineFlag) {
					totalHeight += fontSize;
					totalWidth = 0;
				}
				if (intObject != null) {
					if ((i >= startIndex) || (i <= endIndex)) {
						fontBatch.drawQuad(totalWidth, totalHeight,
								(totalWidth + intObject.width) - offsetX,
								(totalHeight + intObject.height) - offsetY,
								intObject.storedX, intObject.storedY,
								intObject.storedX + intObject.width - offsetX,
								intObject.storedY + intObject.height - offsetY);
					}
					totalWidth += intObject.width;
				}
			}
			fontBatch.setColor(old);
			fontBatch.commit(x, y, sx, sy, ax, ay, rotation);
		}
	}

	public void drawString(GLEx gl, String chars, float x, float y) {
		drawString(gl, x, y, 1f, 1f, 0, chars, LColor.white);
	}

	public void drawString(GLEx gl, String chars, float x, float y, LColor color) {
		drawString(gl, x, y, 1f, 1f, 0, chars, color);
	}

	public void drawString(GLEx gl, String chars, float x, float y,
			float rotation, LColor color) {
		drawString(gl, x, y, 1f, 1f, rotation, chars, color);
	}

	public void drawString(GLEx gl, String chars, float x, float y,
			float rotation) {
		drawString(gl, x, y, 1f, 1f, rotation, chars, LColor.white);
	}

	public void drawString(GLEx gl, String chars, float x, float y, float sx,
			float sy, float rotation, LColor c) {
		drawString(gl, x, y, sx, sy, rotation, chars, c);
	}

	public void drawString(GLEx gl, float x, float y, float sx, float sy,
			float rotation, String chars, LColor c) {
		drawString(gl, x, y, sx, sy, 0, 0, rotation, chars, c, 0,
				chars.length() - 1);
	}

	public void drawString(GLEx gl, float x, float y, float sx, float sy,
			float ax, float ay, float rotation, String chars, LColor c) {
		drawString(gl, x, y, sx, sy, ax, ay, rotation, chars, c, 0,
				chars.length() - 1);
	}

	private void drawString(GLEx gl, float x, float y, float sx, float sy,
			float ax, float ay, float rotation, String chars, LColor c,
			int startIndex, int endIndex) {
		make();
		if (StringUtils.isEmpty(chars)) {
			return;
		}
		this.intObject = null;
		this.charCurrent = 0;
		this.totalWidth = 0;
		this.totalHeight = 0;
		final LTexture texture = fontBatch.toTexture();
		int old = gl.color();
		char[] charList = chars.toCharArray();
		final boolean anchor = ax != 0 || ay != 0;
		final boolean scale = sx != 1f || sy != 1f;
		final boolean angle = rotation != 0;
		final boolean update = scale || angle || anchor;
		try {
			gl.setColor(c);
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (anchor) {
					xf.translate(ax, ay);
				}
				if (scale) {
					float centerX = x + this.getWidth(chars) / 2;
					float centerY = y + this.getHeight(chars) / 2;
					xf.translate(centerX, centerY);
					xf.preScale(sx, sy);
					xf.translate(-centerX, -centerY);
				}
				if (angle) {
					float centerX = x + this.getWidth(chars) / 2;
					float centerY = y + this.getHeight(chars) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
			}
			for (int i = 0; i < charList.length; i++) {
				charCurrent = charList[i];
				if (charCurrent < totalCharSet) {
					intObject = charArray[charCurrent];
				} else {
					intObject = customChars.get((char) charCurrent);
				}
				if (charCurrent == newLineFlag) {
					totalHeight += fontSize;
					totalWidth = 0;
				}
				if (intObject != null) {
					if ((i >= startIndex) || (i <= endIndex)) {
						gl.draw(texture, x + totalWidth, y + totalHeight,
								intObject.width * sx, intObject.height * sy,
								intObject.storedX, intObject.storedY,
								intObject.width, intObject.height, c);
					}
					totalWidth += intObject.width;
				}
			}
		} finally {
			gl.setColor(old);
			if (update) {
				gl.restoreTx();
			}
		}
	}

	public void addChar(char c, float x, float y, LColor color) {
		make();
		this.charCurrent = c;
		if (charCurrent < totalCharSet) {
			intObject = charArray[charCurrent];
		} else {
			intObject = customChars.get((char) charCurrent);
		}
		if (intObject != null) {
			if (color != null) {
				setImageColor(color);
			}
			if (c == newLineFlag) {
				fontBatch.draw(colors, x, y + fontSize, intObject.width
						- offsetX, intObject.height - offsetY,
						intObject.storedX, intObject.storedY, intObject.storedX
								+ intObject.width - offsetX, intObject.storedY
								+ intObject.height - offsetY);
			} else {
				fontBatch.draw(colors, x, y, intObject.width - offsetX,
						intObject.height - offsetY, intObject.storedX,
						intObject.storedY, intObject.storedX + intObject.width
								- offsetX, intObject.storedY + intObject.height
								- offsetY);
			}
			if (colors != null) {
				colors = null;
			}
		}
	}

	public void startChar() {
		make();
		fontBatch.begin();
	}

	public void stopChar() {
		make();
		GL20 g = LSystem.base().graphics().gl;
		if (g != null) {
			int old = GLUtils.getBlendMode();
			GLUtils.setBlendMode(g, LSystem.MODE_NORMAL);
			fontBatch.end();
			GLUtils.setBlendMode(g, old);
		}
	}

	public void postCharCache() {
		make();
		GL20 g = LSystem.base().graphics().gl;
		if (g != null) {
			int old = GLUtils.getBlendMode();
			GLUtils.setBlendMode(g, LSystem.MODE_NORMAL);
			fontBatch.postLastCache();
			GLUtils.setBlendMode(g, old);
		}
	}

	public Cache saveCharCache() {
		make();
		fontBatch.disposeLastCache();
		return fontBatch.newCache();
	}

	public LTextureBatch getFontBatch() {
		return fontBatch;
	}

	private void setImageColor(float r, float g, float b) {
		setColor(Painter.TOP_LEFT, r, g, b);
		setColor(Painter.TOP_RIGHT, r, g, b);
		setColor(Painter.BOTTOM_LEFT, r, g, b);
		setColor(Painter.BOTTOM_RIGHT, r, g, b);
	}

	private void setImageColor(LColor c) {
		if (c == null) {
			return;
		}
		setImageColor(c.r, c.g, c.b);
	}

	private void setColor(int corner, float r, float g, float b) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f), new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
	}

	public int charWidth(char c) {
		make();
		return font.charWidth(c);
	}

	public int getWidth(String s) {
		make();
		int totalWidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		char[] charList = s.toCharArray();
		int maxWidth = 0;
		for (int i = 0; i < charList.length; i++) {
			currentChar = charList[i];
			if (currentChar < totalCharSet) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get((char) currentChar);
			}
			if (intObject != null) {
				if (currentChar == newLineFlag) {
					maxWidth = MathUtils.max(maxWidth, totalWidth);
					totalWidth = 0;
				}
				totalWidth += intObject.width;
			}
		}
		return MathUtils.max(maxWidth, totalWidth);
	}

	public int getHeight(String s) {
		make();
		int currentChar = 0;
		char[] charList = s.toCharArray();
		int lines = 0;
		int height = 0;
		int maxHeight = 0;
		for (int i = 0; i < charList.length; i++) {
			currentChar = charList[i];
			if (currentChar < totalCharSet) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get((char) currentChar);
			}
			if (intObject != null) {
				maxHeight = MathUtils.max(maxHeight, intObject.height);
				height = maxHeight;
			}
			if (currentChar == newLineFlag) {
				lines++;
				height = 0;
			}
		}
		return lines * getLineHeight() + height;
	}

	public int getHeight() {
		return fontHeight;
	}

	public int getSize() {
		return fontSize;
	}

	public int getLineHeight() {
		return fontHeight;
	}

	public float getAscent() {
		return ascent;
	}

	public LFont getFont() {
		return font;
	}

	public int getTotalCharSet() {
		return totalCharSet;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public char getNewLineFlag() {
		return newLineFlag;
	}

	public void setNewLineFlag(char newLineFlag) {
		this.newLineFlag = newLineFlag;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public final static String filterStrings(String[] messages) {
		IntArray chars = new IntArray();
		StringBuilder sbr = new StringBuilder();
		for (String text : messages) {
			char[] list = text.toCharArray();
			for (char ch : list) {
				if (!chars.contains(ch)) {
					chars.add(ch);
					sbr.append(ch);
				}
			}
		}
		return sbr.toString();
	}

	@Override
	public void close() {
		if (fontBatch != null) {
			fontBatch.destoryAll();
			fontBatch.close();
		}
		fontBatch = null;
		for (Cache c : displays.values()) {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		displays.clear();
		displays = null;
		initChars = false;
	}

}