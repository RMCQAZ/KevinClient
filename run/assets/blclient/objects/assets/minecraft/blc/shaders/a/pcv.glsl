#version 150

in vec3 position;
in vec4 color;

out vec4 vertexColor;

uniform mat4 projectionMatrix;

void main()
{
    vertexColor = color;

    gl_Position = projectionMatrix * vec4(position.x, position.y, position.z, 1.0);
}
