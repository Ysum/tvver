package ch.fhnw.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.Stateless;

/**
 * Created by lukasmusy
 */
public class MyDistortion extends AbstractRenderCommand<IAudioRenderTarget,Stateless<IAudioRenderTarget>> {
    private  static final Parameter DRIVE = new Parameter("drive", "Drive", 0, 2, 1);
    private  static final Parameter TYPE = new Parameter("type", "Type", 0, 2, 1);

    public MyDistortion() {
        super(DRIVE);
    }

    @Override
    protected void run(Stateless<IAudioRenderTarget> state) throws RenderCommandException {
        final  float drive  = 1-  getVal(DRIVE);
        final float[] samples = state.getTarget().getFrame().samples;
        for (int i = 0; i < samples.length; i++) {
            final float current = samples[i];
            samples[i] = current <= 0 ? current : drive;
        }

        state.getTarget().getFrame().modified();

    }
}
