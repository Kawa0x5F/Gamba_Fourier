package Fourier.view;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavReader {
    public static double[] readWavFile(String filePath) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
            AudioFormat format = ais.getFormat();

            // PCM符号化のみ対応
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                throw new UnsupportedAudioFileException("Only PCM_SIGNED is supported.");
            }

            byte[] byteBuffer = ais.readAllBytes();
            int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
            int numSamples = byteBuffer.length / sampleSizeInBytes;
            double[] samples = new double[numSamples];

            boolean bigEndian = format.isBigEndian();
            for (int i = 0; i < numSamples; i++) {
                int value = 0;

                // 16bit PCM を仮定（モノラル or 左チャンネルのみ抽出）
                int lower = byteBuffer[i * 2] & 0xFF;
                int upper = byteBuffer[i * 2 + 1];

                if (bigEndian) {
                    value = (upper << 8) | lower;
                } else {
                    value = (lower) | (upper << 8);
                }

                // -32768 ~ 32767 を -1.0 ~ 1.0 に正規化
                samples[i] = value / 32768.0;
            }

            return samples;
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return new double[0];
        }
    }
}
