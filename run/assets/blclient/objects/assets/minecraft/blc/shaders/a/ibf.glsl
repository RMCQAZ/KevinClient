#version 150

in vec2 blurTextureCoords[21];

out vec4 FragColor;

uniform sampler2D image;

void main() {
    vec4 colorSum = vec4(0.0);

    // Gaussian kernel
    colorSum += texture(image, blurTextureCoords[0]) * 0.003109896279775045;
    colorSum += texture(image, blurTextureCoords[1]) * 0.0060871761607067905;
    colorSum += texture(image, blurTextureCoords[2]) * 0.011101533316773805;
    colorSum += texture(image, blurTextureCoords[3]) * 0.018864523778034565;
    colorSum += texture(image, blurTextureCoords[4]) * 0.02986787077989522;
    colorSum += texture(image, blurTextureCoords[5]) * 0.04406148973828162;
    colorSum += texture(image, blurTextureCoords[6]) * 0.060563697010543836;
    colorSum += texture(image, blurTextureCoords[7]) * 0.077564930099475;
    colorSum += texture(image, blurTextureCoords[8]) * 0.09255884650504277;
    colorSum += texture(image, blurTextureCoords[9]) * 0.10291246587762115;
    colorSum += texture(image, blurTextureCoords[10]) * 0.1066151409077004;
    colorSum += texture(image, blurTextureCoords[11]) * 0.10291246587762115;
    colorSum += texture(image, blurTextureCoords[12]) * 0.09255884650504277;
    colorSum += texture(image, blurTextureCoords[13]) * 0.077564930099475;
    colorSum += texture(image, blurTextureCoords[14]) * 0.060563697010543836;
    colorSum += texture(image, blurTextureCoords[15]) * 0.04406148973828162;
    colorSum += texture(image, blurTextureCoords[16]) * 0.02986787077989522;
    colorSum += texture(image, blurTextureCoords[17]) * 0.018864523778034565;
    colorSum += texture(image, blurTextureCoords[18]) * 0.011101533316773805;
    colorSum += texture(image, blurTextureCoords[19]) * 0.0060871761607067905;
    colorSum += texture(image, blurTextureCoords[20]) * 0.003109896279775045;

    FragColor = vec4(colorSum.rgb, 1.0);
}

