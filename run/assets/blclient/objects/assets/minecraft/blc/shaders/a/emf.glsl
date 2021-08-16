#version 150

#define MINECRAFT_LIGHT_POWER   (0.6)
#define MINECRAFT_AMBIENT_LIGHT (0.4)

in vec3 outNormal;
in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform sampler2D lightSampler;
uniform vec3 lightDirection0;
uniform vec3 lightDirection1;
uniform ivec2 lightMapCoords;

vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
}

vec4 minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color) {
    lightDir0 = normalize(lightDir0);
    lightDir1 = normalize(lightDir1);
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * MINECRAFT_LIGHT_POWER + MINECRAFT_AMBIENT_LIGHT);
    return vec4(color.rgb * lightAccum, color.a);
}

void main()
{
    vec4 color = texture(textureSampler, outTexCoord).rgba;
    if (color.a == 0.0) {
        discard;
    }

    vec4 lightMapColor = minecraft_sample_lightmap(lightSampler, lightMapCoords);
    fragColor = lightMapColor * minecraft_mix_light(lightDirection0, lightDirection1, outNormal, color);
}