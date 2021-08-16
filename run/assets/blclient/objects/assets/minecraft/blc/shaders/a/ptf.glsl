#version 150

in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    vec4 color = texture(textureSampler, outTexCoord);
    if (color.a == 0.0) {
        discard;
    }

    fragColor = color;
}