#version 150

in vec4 vertexColor;
in vec2 outTexCoord;

out vec4 FragColor;

uniform vec2 windowSize;
uniform vec4 rounding;
uniform float blur;

void main()
{
    vec4 c = vertexColor;
    vec2 coords = outTexCoord * windowSize;

    if (rounding != vec4(0, 0, 0, 0)) {
        float cornerBlur = 1.0;

        float topLeftRadius = rounding.x + cornerBlur;
        float bottomLeftRadius = rounding.y + cornerBlur;
        float bottomRightRadius = rounding.z + cornerBlur;
        float topRightRadius = rounding.w + cornerBlur;

        vec2 topLeft = vec2(topLeftRadius, topLeftRadius);
        vec2 bottomLeft = vec2(bottomLeftRadius, windowSize.y - bottomLeftRadius);
        vec2 bottomRight = vec2(windowSize.x - bottomRightRadius, windowSize.y - bottomRightRadius);
        vec2 topRight = vec2(windowSize.x - topRightRadius, topRightRadius);

        bool inTopLeft = coords.x < topLeft.x && coords.y < topLeft.y;
        bool inBottomLeft = coords.x < bottomLeft.x && coords.y > bottomLeft.y;
        bool inBottomRight = coords.x > bottomRight.x && coords.y > bottomRight.y;
        bool inTopRight = coords.x > topRight.x && coords.y < topRight.y;

        float selectedRadius = 0.0;
        float dist = 0.0;
        bool outCorner = false;

        if (inTopLeft) {
            dist = distance(coords, topLeft);
            selectedRadius = topLeftRadius;
            
            if (dist > selectedRadius - cornerBlur) {
                outCorner = true;
            }
        }

        if (!outCorner && inBottomLeft) {
            dist = distance(coords, bottomLeft);
            selectedRadius = bottomLeftRadius;
            
            if (dist > selectedRadius - cornerBlur) {
                outCorner = true;
            }
        }

        if (!outCorner && inBottomRight) {
            dist = distance(coords, bottomRight);
            selectedRadius = bottomRightRadius;
            
            if (dist > selectedRadius - cornerBlur) {
                outCorner = true;
            }
        }

        if (!outCorner && inTopRight) {
            dist = distance(coords, topRight);
            selectedRadius = topRightRadius;
            
            if (dist > selectedRadius - cornerBlur) {
                outCorner = true;
            }
        }

        if (outCorner) {
            float opacity = 1.0 - (dist - selectedRadius) / cornerBlur;
            c.w = c.w * opacity;
        }
    }

    if (blur != 0) {
        vec2 texMinDistance = vec2(coords.x, coords.y);
        vec2 texMaxDistance = vec2(windowSize.x - coords.x, windowSize.y - coords.y);
        vec2 edgeDistance = vec2(min(texMinDistance.x, texMaxDistance.x), min(texMinDistance.y, texMaxDistance.y));

        if (edgeDistance.x < blur) {
            c.w = c.w * smoothstep(0, blur, edgeDistance.x);
        }

        if (edgeDistance.y < blur) {
            c.w = c.w * smoothstep(0, blur, edgeDistance.y);
        }
    }

    FragColor = c;
}