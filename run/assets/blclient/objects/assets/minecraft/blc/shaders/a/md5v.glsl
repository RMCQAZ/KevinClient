#version 150

in vec3 position;
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

    gl_Position = projectionMatrix * modelViewMatrix * vec4(position.x, position.y, position.z, 1.0);
}
