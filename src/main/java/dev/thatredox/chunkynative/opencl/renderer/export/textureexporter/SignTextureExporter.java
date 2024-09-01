package dev.thatredox.chunkynative.opencl.renderer.export.textureexporter;

import dev.thatredox.chunkynative.util.Reflection;
import se.llbit.chunky.resources.PalettizedBitmapImage;
import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.resources.Texture;

import java.util.Arrays;

public class SignTextureExporter implements TextureExporter {
    protected final SignTexture texture;
    protected final byte[] tex;
    protected final int width;
    protected final int height;

    public SignTextureExporter(SignTexture texture) {
        this.texture = texture;

        Texture signTexture = Reflection.getFieldValue(texture, "signTexture", Texture.class);
        PalettizedBitmapImage textColor = Reflection.getFieldValueNullable(texture, "textColor", PalettizedBitmapImage.class);

        if (textColor != null) {
            this.width = textColor.width;
            this.height = textColor.height;
        } else {
            this.width = signTexture.getWidth();
            this.height = signTexture.getHeight();
        }

        this.tex = new byte[height * width * 4];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0 ; x < width; x++) {
                float[] rgba = texture.getColor((double) x / width, 1 - (double) y / height);
                this.tex[index] = (byte) (rgba[0] * 255.0);
                this.tex[index+1] = (byte) (rgba[1] * 255.0);
                this.tex[index+2] = (byte) (rgba[2] * 255.0);
                this.tex[index+3] = (byte) (rgba[3] * 255.0);
                index += 4;
            }
        }
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public byte[] getTexture() {
        return tex;
    }

    @Override
    public int textureHashCode() {
        return Arrays.hashCode(this.tex);
    }

    @Override
    public boolean equals(TextureExporter other) {
        if (this.getWidth() != other.getWidth()) return false;
        if (this.getHeight() != other.getHeight()) return false;
        if (other instanceof SignTextureExporter) {
            return Arrays.equals(
                this.tex,
                ((SignTextureExporter) other).tex
            );
        }
        return false;
    }
}
