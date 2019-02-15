package mobilsoft.icell.hu.seniti2.helpers;

public class SensorObject {

    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;
    private float maxX = -100000.0f;
    private float maxY = -100000.0f;
    private float maxZ = -100000.0f;
    private float gyroX = 0.0f;
    private float gyroZ = 0.0f;
    private float gyroY = 0.0f;
    private float standardGravity = 0.0f;
    private int samplerSize = 10;
    private float[] sampler = new float[samplerSize];

    private float speed;
    private float diff;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (x > getMaxX()) {
            setMaxX(x);
        }
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (y > getMaxY()) {
            setMaxY(y);
        }
        this.y = y;
    }

    public float getZ() {

        return z-standardGravity;
    }

    public void setZ(float z) {
        if (z > getMaxZ()) {
            setMaxZ(z);
        }
        this.z = z;
    }

    public float getDiff() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void setDiff(float diff) {
        this.diff = diff;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }

    public float getGyroX() {
        return gyroX;
    }

    public void setGyroX(float gyroX) {
        this.gyroX = gyroX;
    }

    public float getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(float gyroZ) {
        this.gyroZ = gyroZ;
    }

    public float getGyroY() {
        return gyroY;
    }

    public void setGyroY(float gyroY) {
        this.gyroY = gyroY;
    }

    public float getStandardGravity() {
        return standardGravity;
    }

    public void setStandardGravity(float standardGravity) {
        this.standardGravity = standardGravity;
    }

    public void resetMax() {
        setMaxX(-100000.0f);
        setMaxY(-100000.0f);
        setMaxZ(-100000.0f);
    }


}
