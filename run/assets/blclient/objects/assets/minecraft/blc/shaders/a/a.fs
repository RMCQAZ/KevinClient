#version 330
out vec4 FragColor;

in vec2 outTexCoord;
in vec4 outVertexColor;

uniform sampler2D ourTexture;
uniform float alphaMod;


void main()
{
    if (outTexCoord.x < -0.9 && alphaMod == 1.0) {
        discard;
        return;
    }
    if (outTexCoord.x > -0.6 && outTexCoord.x < -0.4 && alphaMod != 1.0) {
        FragColor = vec4(outVertexColor.rgb, 0.125);
    } else if (outTexCoord.x < 0.0) {
        FragColor = outVertexColor.rgba;
    } else {
        FragColor = texture(ourTexture, outTexCoord) * vec4(outVertexColor.rgb, alphaMod);
    }
}