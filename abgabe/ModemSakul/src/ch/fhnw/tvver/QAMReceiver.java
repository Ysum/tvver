package ch.fhnw.tvver;

import ch.fhnw.ether.media.Parameter;

/**
 * Created by lukasmusy
 */
public class QAMReceiver extends AbstractReceiver {
    private static final Parameter START_THRESHOLD = new Parameter("start", "Start Threshold", 0, 1,0.8f);
//    private static final Parameter LOWPASS_THRESH = new Parameter("LP cutoff", "Frequency Cutoff Lowpass Filter", 0, )

    /* Symbol phase of start symbol */
    private int symbolPhase;
    private int sampleIndex;
    /* Idle / data state */
    private boolean idle = true;

    final static  int DIFF_THRESHOLD = 100;



    public QAMReceiver() {
        super(START_THRESHOLD);

    }

    /**
     * Process samples. Samples are squared (power).
     *
     * @param samples The samples to process.
     */
    @Override
    protected void process(float[] samples) {
        int start = getStart(samples);
        if (start > 0) {
            float[] currentSymbol = new float[QAMSender.SYMBOLSIZE];
            boolean secondNibble = false;
            byte databyte = 0;

            for (int i = 0; i < samples.length; i += QAMSender.SYMBOLSIZE) {


                for (int j = 0; j < QAMSender.SYMBOLSIZE; j++)
                    currentSymbol[j] = samples[i * QAMSender.SYMBOLSIZE + j];

                int currentbits = processSymbol(currentSymbol);
                if (currentbits > 0) {
                    if (!secondNibble) {
                        databyte = (byte) (currentbits << 4);
                        secondNibble = true;
                    } else {
                        databyte = (byte) (databyte & currentbits);
                        addData(databyte);
                        databyte = 0;
                        secondNibble = false;
                    }
                }
            }

        }

    }

    private int processSymbol(float[] symbol) {

        for (int i = 0; i < 16; i++) {
            int matches = 0;
            for (int j = 0; j < symbol.length; j++) {
               if (symbol == QAMSender.SYMBOLS[j])
                   matches++;
            }
            if (QAMSender.SYMBOLSIZE - matches < DIFF_THRESHOLD)
                return i;
        }

        return -1;


    }

    private int getStart(float[] samples) {
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] >= getVal(START_THRESHOLD)) {
                return i + (int)(0.75 * QAMSender.SYMBOLSIZE);
            }
        }
        return -1;
    }

}
