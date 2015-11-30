package ch.fhnw.tvver;

import ch.fhnw.util.FloatList;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Lukas Musy and Tobias Ernst
 *
 * Adopted from SimpleAMReceiver, courtesy of Simon Schubiger
 *
 */
public class QAMSender extends AbstractSender {
    private static final double PI2  = Math.PI * 2;

    /* Carrier frequency. */
    static final float FREQ = 3000;
    static int SYMBOLSIZE;

    static final float phasePoints[] = {1, 0.3f, -0.3f, -1};
    static final float amplitudePoints[] = {0.8f, 0.3f, -0.3f, -0.8f};

//    static final Map<Integer, float[]> SYMBOLS = new HashMap<>();
    static float[][]  SYMBOLS = new float[16][];


    @Override
    public float[] init(float samplingFrequency) {
        SYMBOLSIZE = (int) (samplingFrequency / FREQ);

        for (int i = 0; i < 16; i++) {
            float[] constPoints = lookUpConstellationPoint(i);
            SYMBOLS[i] = symbol(constPoints[0], constPoints[1]);
        }

        return super.init(samplingFrequency);
    }

    /**
     * Create a wave with given amplitude and phase.
     * @param amp Amplitude for this symbol.
     * @param phase Phase for this symbol.
     * @return Audio data for symbol.
     */
    private float[] symbol(float phase, float amp) {
        final float[] result = new float[SYMBOLSIZE];

        for(int i = 0; i < result.length; i++)
            result[i] = (float) (Math.sin((PI2 * i + phase) / SYMBOLSIZE)) * amp;

        return result;
    }


    @Override
    public float[] synthesize(byte[] data) {
        FloatList result = new FloatList();

		/* Send start bit. */
        result.addAll(symbol(0, 1f));

        for (int d = 0; d < data.length; d++) {
            int highNibble = data[d] >>> 4;
            int lowNibble = data[d] & 0x0f;

            result.addAll(getSymbol(highNibble));
            result.addAll(getSymbol(lowNibble));
        }
        return result._getArray();
    }

    private float[] lookUpConstellationPoint(int nibble) {
        float values[] = new float[2];

        //phase value
        values[0] =  phasePoints[(nibble+1)%4];
        //amp value
        values[1] =  amplitudePoints[nibble%4];

        return values;
    }

    private float[] getSymbol(int nibble) {
//        float[] constPoint = lookUpConstellationPoint(nibble);
//        return symbol(constPoint[0], constPoint[1]);

        return SYMBOLS[nibble];

    }

}
