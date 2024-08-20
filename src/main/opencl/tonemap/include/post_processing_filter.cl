#include "../opencl.h"
#include "double.h"
#include "rgba.h"

__kernel void filter(
        const int width,
        const int height,
        const float exposure,
        __global const imposter_double* input,
        __global unsigned int* res,
        const int type
) {
    int gid = get_global_id(0);
    int offset = gid * 3;

    float color_float[3];
    for (int i = 0; i < 3; i++) {
        color_float[i] = idouble_to_float(input[offset + i]);
    }
    float3 color = vload3(0, color_float);
    color *= exposure;

    switch (type) {
        case 0:
            // GAMMA
            color = pow(color, 1.0 / 2.2);
            break;
        case 1:
            // TONEMAP1
            color = fmax((float3)(0), color - 0.004f);
            color = (color * (6.2f * color + 0.5f)) / (color * (6.2f * color + 1.7f) + 0.06f);
            break;
        case 2:
            // ACES
            color = (color * (2.51f * color + 0.03f)) / (color * (2.43f * color + 0.59f) + 0.14f);
            color = clamp(color, (float3)(0), (float3)(1));
            color = pow(color, 1.0 / 2.2);
            break;
        case 3:
            // HABLE
            color *= 16;
            color = ((color * (0.15f * color + 0.10f * 0.50f) + 0.20f * 0.02f) / (color * (0.15f * color + 0.50f) + 0.20f * 0.30f)) - 0.02f / 0.30f;
            color /= (((11.2f * (0.15f * 11.2f + 0.10f * 0.50f) + 0.20f * 0.02f) / (11.2f * (0.15f * 11.2f + 0.50f) + 0.20f * 0.30f)) - 0.02f / 0.30f);
            break;
    }

    float4 pixel;
    pixel.xyz = color;
    pixel.w = 1;
    res[gid] = color_to_argb(pixel);
}


float ue4_filter_process_component(float c, float saturation, float slope, float toe, float shoulder, float blackClip, float whiteClip, float ta, float sa) {
    float logc = log10(c);

    if (logc >= ta && logc <= sa) {
        return saturation * (slope * (logc + 0.733) + 0.18);
    }
    if (logc > sa) {
        return saturation * (1 + whiteClip - (2 * (1 + whiteClip - shoulder)) / (1 + exp(((2 * slope) / (1 + whiteClip - shoulder)) * (logc - sa))));
    }
    return saturation * ((2 * (1 + blackClip - toe)) / (1 + exp(-((2 * slope) / (1 + blackClip - toe)) * (logc - ta))) - blackClip);
}
__kernel void ue4_filter(
        const int width,
        const int height,
        const float exposure,
        __global const imposter_double* input,
        __global unsigned int* res,

        const float saturation,
        const float slope,
        const float toe,
        const float shoulder,
        const float blackClip,
        const float whiteClip,
        const float ta,
        const float sa
) {
    int gid = get_global_id(0);
    int offset = gid * 3;

    float color_float[3];
    for (int i = 0; i < 3; i++) {
        color_float[i] = idouble_to_float(input[offset + i]);
    }
    float3 color = vload3(0, color_float);
    color *= exposure;

    color *= 1.25f;
    color = (float3) (
        ue4_filter_process_component(color.x, saturation, slope, toe, shoulder, blackClip, whiteClip, ta, sa),
        ue4_filter_process_component(color.y, saturation, slope, toe, shoulder, blackClip, whiteClip, ta, sa),
        ue4_filter_process_component(color.z, saturation, slope, toe, shoulder, blackClip, whiteClip, ta, sa)
    );
    color = clamp(color, 0.0f, 1.0f);
    color = pow(color, 1.0 / 2.0);

    float4 pixel;
    pixel.xyz = color;
    pixel.w = 1;
    res[gid] = color_to_argb(pixel);
}
