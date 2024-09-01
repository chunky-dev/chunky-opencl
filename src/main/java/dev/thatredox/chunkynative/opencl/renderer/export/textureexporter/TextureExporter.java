package dev.thatredox.chunkynative.opencl.renderer.export.textureexporter;

import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.resources.Texture;

public interface TextureExporter {
    int getWidth();
    int getHeight();
    byte[] getTexture();
    int textureHashCode();
    boolean equals(TextureExporter other);

    static TextureExporter getExporter(Texture texture) {
        if (texture instanceof SignTexture) {
            return new SignTextureExporter((SignTexture) texture);
        }
        return new DefaultTextureExporter(texture);
    }
    static int hashCode(Texture texture) {
        return getExporter(texture).textureHashCode();
    }
    static boolean equals(Texture a, Texture b) {
        return getExporter(a).equals(getExporter(b));
    }
}
