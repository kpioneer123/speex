package com.isure.audio.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.isure.audio.encode.SpeexEncoder;

public class SpeexRecorder implements Runnable {

//	private Logger log = LoggerFactory.getLogger(SpeexRecorder.class);
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;
	private String fileName = null;
	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}

	public void run() {

		// ���������߳�
		SpeexEncoder encoder = new SpeexEncoder(this.fileName);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);

		short[] tempBuffer = new short[packagesize];

		AudioRecord recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding,
				bufferSize);

		recordInstance.startRecording();

		while (this.isRecording) {
//			log.debug("start to recording.........");
			bufferRead = recordInstance.read(tempBuffer, 0, packagesize);
			// bufferRead = recordInstance.read(tempBuffer, 0, 320);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
//			log.debug("put data into encoder collector....");
			encoder.putData(tempBuffer, bufferRead);

		}
		recordInstance.stop();
		//tell encoder to stop.
		encoder.setRecording(false);
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}
}
