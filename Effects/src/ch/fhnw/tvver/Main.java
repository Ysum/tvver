/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.tvver;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import ch.fhnw.ether.audio.AudioUtilities.Window;
import ch.fhnw.ether.audio.FFT;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.InvFFT;
import ch.fhnw.ether.audio.JavaSoundTarget;
import ch.fhnw.ether.audio.URLAudioSource;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.IRenderTarget;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.ui.ParameterWindow;
import ch.fhnw.ether.ui.ParameterWindow.Flag;
import ch.fhnw.util.CollectionUtilities;

public class Main {
	public static void main(String[] args) throws IOException, RenderCommandException {

		List<AbstractRenderCommand<IAudioRenderTarget, ?>> audioFx = CollectionUtilities.asList(
				// --- add your effects here ---
				new MyDistortion(),
				new MyEcho()
				);

		AtomicInteger current = new AtomicInteger(0);

		final JPanel panel = new JPanel(new GridLayout(2, 1));
		final JComboBox<String> audioSrcUi = new JComboBox<>();
		audioSrcUi.addItem("startrek.wav");
		audioSrcUi.addItem("avengers.wav");
		audioSrcUi.addItem("eguitar.wav");
		audioSrcUi.addItem("guitar0.wav");
		audioSrcUi.addItem("guitar1.wav");
		final JComboBox<AbstractRenderCommand<?, ?>> audioFxUi = new JComboBox<>();
		for(AbstractRenderCommand<?, ?> fx : audioFx)
			audioFxUi.addItem(fx);
		panel.add(audioSrcUi);
		panel.add(audioFxUi);

		URLAudioSource source = new URLAudioSource(Main.class.getResource(audioSrcUi.getItemAt(0)));
		RenderProgram<IAudioRenderTarget> program = new RenderProgram<>(
				source,
				new URLAudioSource(Main.class.getResource("avengers.wav")),
				new URLAudioSource(Main.class.getResource("eguitar.wav")),
				audioFx.get(0)
				);

		audioSrcUi.addActionListener((ActionEvent e)->{
			try {
				String name = audioSrcUi.getSelectedItem().toString();
				program.replace(new URLAudioSource(Main.class.getResource(name)));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		});
		audioFxUi.addActionListener((ActionEvent e)->{
			int newIdx = audioFxUi.getSelectedIndex();
			program.replace(audioFx.get(current.get()), audioFx.get(newIdx));
			current.set(newIdx);
		});
		new ParameterWindow(panel, program, Flag.EXIT_ON_CLOSE);

		JavaSoundTarget audioOut = new JavaSoundTarget();
		audioOut.useProgram(program);
		audioOut.start();
		audioOut.sleepUntil(IRenderTarget.NOT_RENDERING);
		audioOut.stop();
	}
}
