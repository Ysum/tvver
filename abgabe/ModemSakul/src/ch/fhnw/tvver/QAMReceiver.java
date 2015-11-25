package ch.fhnw.tvver;

import ch.fhnw.ether.media.Parameter;

/**
 * Created by lukasmusy
 */
public class QAMReceiver extends AbstractReceiver {
    private static final Parameter START_THRESH = new Parameter("start", "Start Threshold", 0, 1,0.8f);
//    private static final Parameter LOWPASS_THRESH = new Parameter("LP cutoff", "Frequency Cutoff Lowpass Filter", 0, )



    /* Symbol phase of start symbol */
    private int symbolPhase;
    private int sampleIndex;
    /* Idle / data state */
    private boolean idle = true;


    protected void process(float sample) {
        final int symbolSz = (int) (samplingFrequency / QAMSender.FREQ);
        symbolPhase = symbolSz / 4;


        /* Wait for signal to rise above start threshold. */
        if(idle) {
            if (sample > getVal(START_THRESH)) {
                sampleIndex = symbolPhase;
                idle = false;
            }
        } else {

        }
    }

    /**
     * Process samples. Samples are squared (power).
     *
     * @param samples The samples to process.
     */
    @Override
    protected void process(float[] samples) {
        for(int i = 0; i < samples.length; i++)
            process(samples[i]*samples[i]);
    }

    private void decode(float[] symbol){

    }
}
