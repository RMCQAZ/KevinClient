#version 150

out vec4 FragColor;

uniform vec2 resolution;
uniform sampler2D framebufferTexture;
uniform float blur;

void main() {
    vec2 uv = vec2(gl_FragCoord.xy / resolution);
    vec4 framebufferColor = texture(framebufferTexture, uv);

    framebufferColor.a = max(0, min(framebufferColor.a - 0.075, framebufferColor.a * blur * 0.95));

    FragColor = framebufferColor;
}