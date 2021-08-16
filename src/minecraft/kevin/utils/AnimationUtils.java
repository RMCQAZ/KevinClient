package kevin.utils;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class AnimationUtils {

    public static float easeOut(float t, float d) {
        return (t = t / d - 1) * t * t + 1;
    }

    public static float easeOutElastic(float x) {
        double c4 = (2 * Math.PI) / 3.0f;

        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : pow(2, -10 * x) * sin((x * 10 - 0.75) * c4) + 1);

    }
}
