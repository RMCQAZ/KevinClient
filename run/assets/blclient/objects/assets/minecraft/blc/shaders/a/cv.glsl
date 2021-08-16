#version 150

in vec3 position;
in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 projectionMatrix;

void main()
{
    outTexCoord = texCoord;

    gl_Position = projectionMatrix * vec4(position.x, position.y, position.z, 1.0);
}
