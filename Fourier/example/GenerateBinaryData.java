package Fourier.example;

import java.util.Random;

public class GenerateBinaryData {

    public static double[] CreateBinaryData() {
        int N = 16384;
        double[] binaryData = new double[N];
        Random rand = new Random();

        for (int i = 0; i < N; i++) {
            binaryData[i] = rand.nextBoolean() ? 1 : 0;
        }
        
        return binaryData;
    }
}