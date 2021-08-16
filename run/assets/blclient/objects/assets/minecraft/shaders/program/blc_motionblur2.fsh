#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform vec3 Phosphor = vec3(0.7, 0.0, 0.0);
uniform float LerpFactor = 1.0;

out vec4 FragColor;

void main() {
    vec4 CurrTexel = texture(DiffuseSampler, texCoord);
    vec4 PrevTexel = texture(PrevSampler, texCoord);
    float factor = Phosphor.r;

    /* if (Phosphor.b == 0) {
        gl_FragColor = CurrTexel;
    }
    else */ if (Phosphor.g == 1) {
        FragColor = vec4(max(PrevTexel.rgb * vec3(factor), CurrTexel.rgb), 1.0);
    }
    else {
        FragColor = vec4(mix(PrevTexel.rgb, CurrTexel.rgb, factor), 1.0);
    }
}
