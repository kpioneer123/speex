/**
 * 
 */
package com.isure.audio.recorder;


import com.isure.audio.encode.SpeexDecoder;

import java.io.File;

/**
 * @author Gauss
 * 
 */
public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;
 

	public SpeexPlayer(String fileName) {

		this.fileName = fileName;
		System.out.println(this.fileName);
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPlay() {
		RecordPlayThread rpt = new RecordPlayThread();

		Thread th = new Thread(rpt);
		th.start();
	}

	boolean isPlay = true;

	class RecordPlayThread extends Thread {

		public void run() {
			try {
				if (speexdec != null)
					speexdec.decode();

			} catch (Exception t) {
				t.printStackTrace();
			}
		}
	};
}
