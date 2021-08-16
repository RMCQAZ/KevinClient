#version 150

in vec3 position;

uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * vec4(position.x, position.y, position.z, 1.0);
}
