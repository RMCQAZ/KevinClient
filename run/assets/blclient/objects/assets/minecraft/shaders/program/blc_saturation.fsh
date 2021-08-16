#version 120

uniform sampler2D DiffuseSampler;
uniform float Saturation;

varying vec2 texCoord;

const vec3 luminance = vec3(0.2126, 0.7152, 0.0722);

void main() {
    vec4 texel = texture2D(DiffuseSampler, texCoord);
    vec3 gray = vec3(dot(texel.rgb, luminance));
    gl_FragColor = vec4(mix(gray, texel.rgb, Saturation), texel.a);
}
