#version 150

in vec4 position;
in vec3 normal;
in vec2 texCoord;

out vec3 outNormal;
out vec2 outTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main()
{
    outNormal = normal;
    outTexCoord = texCoord;

    gl_Position = projectionMatrix * modelViewMatrix * position;
}