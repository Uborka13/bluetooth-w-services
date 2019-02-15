package mobilsoft.icell.hu.seniti2.helpers.sampler;

public class SamplerObject {

    private static final float SMOOTH_CONSTANT = 0.9f;
    private int samplerSize = 10;
    private float[] sampler = new float[samplerSize];
    private int index = 0;
    private boolean isFilled = false;

    public void addSample(float sample) {
        sampler[index] = sample * SMOOTH_CONSTANT;
        index++;
        checkFullness();
    }

    public float getAverage() {
        float average = 0.0f;
        for (float aSampler : sampler) {
            average += aSampler;
        }
        return average;
    }

    private void checkFullness() {
        isFilled = index >= samplerSize;
    }

    public boolean isFilled() {
        return isFilled;
    }
}
