#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;
uniform vec3 Inventory;

void main() {
    if (Inventory.r == 0) {
        gl_FragColor = texture2D(DiffuseSampler, texCoord);
    }
    else {
        vec4 blurred = vec4(0.0);
        float totalStrength = 0.0;
        float totalAlpha = 0.0;
        float totalSamples = 0.0;
        for (float r = -Radius; r <= Radius; r += 1.0) {
            vec4 samples = texture2D(DiffuseSampler, texCoord + oneTexel * r * BlurDir);

            // Accumulate average alpha
            totalAlpha = totalAlpha + samples.a;
            totalSamples = totalSamples + 1.0;

            // Accumulate smoothed blur
            float strength = 1.0 - abs(r / Radius);
            totalStrength = totalStrength + strength;
            blurred = blurred + samples;
        }
        gl_FragColor = vec4(blurred.rgb / (Radius * 2.0 + 1.0), totalAlpha);
    }
}
