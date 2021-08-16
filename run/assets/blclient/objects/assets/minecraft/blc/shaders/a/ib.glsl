#version 150

in vec3 position;

out vec2 blurTextureCoords[21];

uniform vec2 resolution;
uniform vec2 direction;
uniform float radius;

void main()
{
    gl_Position = vec4(position, 1.0);
    vec2 centerTextureCoords = position.xy * 0.5 + 0.5;

    float pixelSizeX = (1.0 / resolution.x) * direction.x;
    float pixelSizeY = (1.0 / resolution.y) * direction.y;

    for (int i=-10; i<=10; i++){
        vec2 texCoord = centerTextureCoords + vec2(pixelSizeX * i * radius, pixelSizeY * i * radius);
        blurTextureCoords[i+10] = texCoord;
    }
}