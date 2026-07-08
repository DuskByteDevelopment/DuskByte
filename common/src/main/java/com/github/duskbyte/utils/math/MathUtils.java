package com.github.duskbyte.utils.math;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    public static int getRandom(int min, int max) {
        if (min == max) return min;
        int actualMin = Math.min(min, max);
        int actualMax = Math.max(min, max);
        return ThreadLocalRandom.current().nextInt(actualMin, actualMax);
    }

    public static double getRandom(double min, double max) {
        if (min == max) return min;
        double actualMin = Math.min(min, max);
        double actualMax = Math.max(min, max);
        return ThreadLocalRandom.current().nextDouble(actualMin, actualMax);
    }

    public static float getRandom(float min, float max) {
        if (min == max) return min;
        float actualMin = Math.min(min, max);
        float actualMax = Math.max(min, max);
        return (float) ThreadLocalRandom.current().nextDouble(actualMin, actualMax);
    }

    public static int getRandomLogNormal(int min, int max) {
        if (min == max) return min;
        double value = getRandomLogNormal((double) min, (double) max);
        return (int) Math.round(value);
    }

    public static double getRandomLogNormal(double min, double max) {
        if (min == max) return min;
        double actualMin = Math.min(min, max);
        double actualMax = Math.max(min, max);

        double mean = (actualMin + actualMax) / 2.0;
        double sigma = 0.3;

        double mu = Math.log(mean) - sigma * sigma / 2.0;

        double u1 = ThreadLocalRandom.current().nextDouble();
        double u2 = ThreadLocalRandom.current().nextDouble();

        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
        double value = Math.exp(mu + sigma * z);

        value = Math.max(actualMin, Math.min(actualMax, value));

        return value;
    }

    public static float getRandomLogNormal(float min, float max) {
        if (min == max) return min;
        double value = getRandomLogNormal((double) min, (double) max);
        return (float) value;
    }

    /** @deprecated Delegates to {@link #getRandomLogNormal(int, int)}. */
    @Deprecated
    public static int getRandomLongTail(int min, int max) {
        return getRandomLogNormal(min, max);
    }

    /** @deprecated Delegates to {@link #getRandomLogNormal(double, double)}. */
    @Deprecated
    public static double getRandomLongTail(double min, double max) {
        return getRandomLogNormal(min, max);
    }

    /** @deprecated Delegates to {@link #getRandomLogNormal(float, float)}. */
    @Deprecated
    public static float getRandomLongTail(float min, float max) {
        return getRandomLogNormal(min, max);
    }

}
