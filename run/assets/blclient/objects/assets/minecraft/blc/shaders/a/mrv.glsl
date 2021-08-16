#version 150

in vec3 position;
in vec4 color;

out vec4 vertexColor;

uniform mat4 projectionMatrix;

void main()
{
    vertexColor = color;

    vec4 glPos = projectionMatrix * vec4(position.x, position.y, position.z, 1.0);
    glPos.y = -glPos.y; // Flipped y coordinates

    gl_Position = glPos;
}
