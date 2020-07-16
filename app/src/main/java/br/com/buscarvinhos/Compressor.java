package br.com.buscarvinhos;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class Compressor {
    public static byte[] compress(byte[] content) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("Compressiono %f\n", (1.0f * content.length / byteArrayOutputStream.size()));
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] contentBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public static boolean notWorthCompressing(String contentType) {
        return contentType.contains("JPEG")
                || contentType.contains("pdf")
                || contentType.contains("zip")
                || contentType.contains("mpeg")
                || contentType.contains("avi");
    }

    public static byte[] compress1(final byte[] data) throws IOException {
        if (data == null || data.length == 0) return new byte[0];

        final ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        final Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        final byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            out.write(buffer, 0, deflater.deflate(buffer));
        }

        return out.toByteArray();
    }

    public static byte[] decompress1(final byte[] data) throws IOException {
        if (data == null || data.length == 0) return new byte[0];

        final Inflater inflater = new Inflater();
        inflater.setInput(data);
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
            final byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                out.write(buffer, 0, inflater.inflate(buffer));
            }

            return out.toByteArray();
        } catch (DataFormatException e) {
            System.err.println("Decompression failed! Returning the compressed data...");
            e.printStackTrace();
            return data;
        }
    }


}
