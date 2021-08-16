#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;
uniform vec3 Inventory;

out vec4 FragColor;

void main() {
    if (Inventory.r == 0) {
        FragColor = texture(DiffuseSampler, texCoord);
    }
    else {
        vec4 blurred = vec4(0.0);
        float totalStrength = 0.0;
        float totalAlpha = 0.0;
        float totalSamples = 0.0;
        for (float r = -Radius; r <= Radius; r += 1.0) {
            vec4 samples = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);

            // Accumulate average alpha
            totalAlpha = totalAlpha + samples.a;
            totalSamples = totalSamples + 1.0;

            // Accumulate smoothed blur
            float strength = 1.0 - abs(r / Radius);
            totalStrength = totalStrength + strength;
            blurred = blurred + samples;
        }
        FragColor = vec4(blurred.rgb / (Radius * 2.0 + 1.0), totalAlpha);
    }
}
