package ch.fhnw.tvver;

import ch.fhnw.ether.audio.AudioFrame;
import ch.fhnw.ether.audio.Delay;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.*;

/**
 * Created by lukasmusy
 */
public class MyEcho extends AbstractRenderCommand<IAudioRenderTarget, MyEcho.State> {
    private static final Parameter DELAY = new Parameter("delay", "Delay [sec]", 0, 2, 0);
    private static final Parameter GAIN = new Parameter("gain", "Gain", 0, 1, 0);

    @Override
    protected void run(State state) throws RenderCommandException {
         state.process(state.getTarget().getFrame());

    }

    public class State extends PerTargetState<IAudioRenderTarget> {
        private final Delay delay;

        public State(IAudioRenderTarget target) {
            super(target);
            delay = new Delay(target, 0);
        }

        private void process(AudioFrame frame) {
            final float d = getVal(DELAY);
            delay.setLength(d);
            final float gain = getVal(GAIN);

            delay.modifySamples(frame, (float sample, int dx)-> sample + gain * delay.get(d));
        }
    }

    public MyEcho() {
        super(GAIN, DELAY);
    }

    @Override
    protected State createState(IAudioRenderTarget target) throws RenderCommandException {
        return new State(target);
    }


}
