#version 150

in vec2 outTexCoord;

out vec4 FragColor;

uniform vec2 windowSize;
uniform vec4 rounding;
uniform float thickness;
uniform vec4 topColor;
uniform vec4 bottomColor;
uniform vec4 leftColor;
uniform vec4 rightColor;

void main()
{
    float opacity = 1.0;
    vec2 coords = outTexCoord * windowSize;
    vec4 color = topColor;

    float cornerBlur = 1.0;

    float topLeftRadius = rounding.x + cornerBlur;
    float bottomLeftRadius = rounding.y + cornerBlur;
    float bottomRightRadius = rounding.z + cornerBlur;
    float topRightRadius = rounding.w + cornerBlur;

    if (coords.y < thickness) {
        color = topColor;
    }

    if (coords.y > windowSize.y - thickness) {
        color = bottomColor;
    }

    if (coords.x < thickness) {
        color = leftColor;
    }

    if (coords.x > windowSize.x - thickness) {
        color = rightColor;
    }

    float checkValue = max(thickness, topLeftRadius);
    if (coords.x < checkValue && coords.y < checkValue) {
        float x = coords.x;
        float y = coords.y;

        if (x > y) {
            color = topColor;
        } else {
            color = leftColor;
        }
    }

    checkValue = max(thickness, bottomRightRadius);
    if (coords.x > windowSize.x - checkValue && coords.y > windowSize.y - checkValue) {
        float x = coords.x - (windowSize.x - checkValue);
        float y = coords.y - (windowSize.y - checkValue);

        if (x > y) {
            color = rightColor;
        } else {
            color = bottomColor;
        }
    }

    checkValue = max(thickness, bottomLeftRadius);
    if (coords.x < checkValue && coords.y > windowSize.y - checkValue) {
        float x = checkValue - coords.x;
        float y = coords.y - (windowSize.y - checkValue);

        if (x > y) {
            color = leftColor;
        } else {
            color = bottomColor;
        }
    }

    checkValue = max(thickness, topRightRadius);
    if (coords.x > windowSize.x - checkValue && coords.y < checkValue) {
        float x = coords.x - (windowSize.x - checkValue);
        float y = checkValue - coords.y;

        if (x > y) {
            color = rightColor;
        } else {
            color = topColor;
        }
    }

    vec2 topLeft = vec2(topLeftRadius, topLeftRadius);
    vec2 bottomLeft = vec2(bottomLeftRadius, windowSize.y - bottomLeftRadius);
    vec2 bottomRight = vec2(windowSize.x - bottomRightRadius, windowSize.y - bottomRightRadius);
    vec2 topRight = vec2(windowSize.x - topRightRadius, topRightRadius);

    bool inTopLeft = coords.x < topLeft.x && coords.y < topLeft.y;
    bool inBottomLeft = coords.x < bottomLeft.x && coords.y > bottomLeft.y;
    bool inBottomRight = coords.x > bottomRight.x && coords.y > bottomRight.y;
    bool inTopRight = coords.x > topRight.x && coords.y < topRight.y;

    float selectedRadius = 0.0;
    float thicknessRadius = 0.0;
    float dist = 0.0;
    bool outCorner = false;

    if (inTopLeft) {
        dist = distance(coords, topLeft);
        selectedRadius = topLeftRadius;

        thicknessRadius = selectedRadius - thickness;
        if (dist < thicknessRadius) {
            outCorner = true;
        }

        if (dist > selectedRadius) {
            outCorner = true;
        }
    }

    if (!outCorner && inBottomLeft) {
        dist = distance(coords, bottomLeft);
        selectedRadius = bottomLeftRadius;

        thicknessRadius = selectedRadius - thickness;
        if (dist < thicknessRadius) {
            outCorner = true;
        }

        if (dist > selectedRadius) {
            outCorner = true;
        }
    }

    if (!outCorner && inBottomRight) {
        dist = distance(coords, bottomRight);
        selectedRadius = bottomRightRadius;

        thicknessRadius = selectedRadius - thickness;
        if (dist < thicknessRadius) {
            outCorner = true;
        }

        if (dist > selectedRadius) {
            outCorner = true;
        }
    }

    if (!outCorner && inTopRight) {
        dist = distance(coords, topRight);
        selectedRadius = topRightRadius;

        thicknessRadius = selectedRadius - thickness;
        if (dist < thicknessRadius) {
            outCorner = true;
        }

        if (dist > selectedRadius) {
            outCorner = true;
        }
    }

    if (coords.x > thickness && coords.x < windowSize.x - thickness && coords.y > thickness && coords.y < windowSize.y - thickness) {
        bool inCorner = inBottomLeft || inBottomRight || inTopLeft || inTopRight;
        if (!inCorner) {
            discard;
        }
    }

    if (outCorner) {
        discard;
    }

    FragColor = color * vec4(1, 1, 1, opacity);
}