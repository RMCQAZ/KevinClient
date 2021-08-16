#version 430 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec4 vertexColor;

out vec2 outTexCoord;
out vec4 outVertexColor;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 playerRotation;

layout(std430, binding = 3) buffer aloc
{
    vec4 locArr[];
};


void main()
{
    gl_Position = projectionMatrix * modelViewMatrix * (locArr[int(position.w)] + (playerRotation * vec4(position.x, position.y, position.z, 1.0)));
    outTexCoord = texCoord;
    outVertexColor = vertexColor;
}

