package dev.thatredox.chunkynative.opencl.renderer.export.textureexporter;

import se.llbit.chunky.resources.Texture;

import java.util.Arrays;

public class DefaultTextureExporter implements TextureExporter {
    protected final Texture texture;

    public DefaultTextureExporter(Texture texture) {
        this.texture = texture;
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public byte[] getTexture() {
        byte[] out = new byte[getHeight() * getWidth() * 4];
        int index = 0;
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                float[] rgba = texture.getColor(x, y);
                out[index] = (byte) (rgba[0] * 255.0);
                out[index+1] = (byte) (rgba[1] * 255.0);
                out[index+2] = (byte) (rgba[2] * 255.0);
                out[index+3] = (byte) (rgba[3] * 255.0);
                index += 4;
            }
        }
        return out;
    }

    @Override
    public int textureHashCode() {
        return Arrays.hashCode(texture.getData());
    }

    @Override
    public boolean equals(TextureExporter other) {
        if (this.getWidth() != other.getWidth()) return false;
        if (this.getHeight() != other.getHeight()) return false;
        if (other instanceof DefaultTextureExporter) {
            return Arrays.equals(
                    this.texture.getData(),
                    ((DefaultTextureExporter) other).texture.getData()
            );
        }
        return false;
    }
}
