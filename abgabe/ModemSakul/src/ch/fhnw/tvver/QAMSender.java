package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

/**
 * Created by lukasmusy
 */
public class QAMSender extends AbstractSender {
    private static final double PI2  = Math.PI * 2;
    /* Carrier frequency. */
    static final float FREQ = 3000;

    static final float phasePoints[] = {1, 0.3f, -0.3f, -1};
    static final float amplitudePoints[] = {0.8f, 0.3f, -0.3f, -0.8f};

    /**
     * Create a wave with given amplitude and phase.
     * @param amp Amplitude for this symbol.
     * @param phase Phase for this symbol.
     * @return Audio data for symbol.
     */
    private float[] symbol(float phase, float amp) {
        final int symbolSz = (int) (samplingFrequency / FREQ);
        final float[] result = new float[symbolSz];

        for(int i = 0; i < result.length; i++)
            result[i] = (float) (Math.sin((PI2 * i + phase) / symbolSz)) * amp;

        return result;
    }


    @Override
    public float[] synthesize(byte[] data) {
        FloatList result = new FloatList();

		/* Send start bit. */
        result.addAll(symbol(0, 1f));

        for (int d = 0; d < data.length; d++) {
            int highNibble = data[d] >>> 4;
            int lowNibble = data[d]& 0x0f;

            result.addAll(getSymbol(highNibble));
            result.addAll(getSymbol(lowNibble));
        }
        return result._getArray();
    }

    private float[] lookUp(int nibble) {
        float values[] = new float[2];

        //phase value
        values[0] =  phasePoints[(nibble+1)%4];
        //amp value
        values[1] =  amplitudePoints[nibble%4];

        return values;
    }

    private float[] getSymbol(int nibble) {
        float[] constPoint = lookUp(nibble);
        return symbol(constPoint[0], constPoint[1]);

    }

}
