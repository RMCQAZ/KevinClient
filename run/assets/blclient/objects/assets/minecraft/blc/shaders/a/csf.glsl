#version 150

out vec4 FragColor;

uniform vec2 resolution;
uniform sampler2D image;
uniform float saturation;
uniform bool grayScale;

void main() {
    vec2 uv = vec2(gl_FragCoord.xy/resolution);
    vec4 color = texture(image, uv);
    float lum = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
    if (grayScale) {
        FragColor = vec4(vec3(lum), 1.0);
    } else {
        vec3 diff = color.rgb - vec3(lum);
        FragColor = vec4(vec3(diff) * saturation + lum, 1.0);
    }
}
