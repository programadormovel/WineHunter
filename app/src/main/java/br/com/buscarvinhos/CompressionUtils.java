package br.com.buscarvinhos;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionUtils {
    //private static final Logger LOG = Logger.getLogger(CompressionUtils.class);
    public static byte[] compress(byte[] data) throws IOException {

        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();
        byte[] output = outputStream.toByteArray();
        Log.i("Original: ", String.valueOf(data.length / 1024 + " Kb"));
        Log.i("Compressed: ", String.valueOf(output.length / 1024 + " Kb"));

        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {

        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();
        byte[] output = outputStream.toByteArray();
        Log.i("Original: ", String.valueOf(data.length));
        Log.i("Compressed: ", String.valueOf(output.length));
        return output;

    }

}