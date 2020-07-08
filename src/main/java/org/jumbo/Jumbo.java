package org.jumbo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Jumbo {
	private final JumboJni jni = new JumboJni();
	
	private Jumbo(int size) throws IOException {
		String libName = "libjumbo_jni.so"; // The name of the file in resources/ dir
		URL url = getClass().getResource("/" + libName);
		File tmpDir = Files.createTempDirectory("libjumbo_jni").toFile();
		tmpDir.deleteOnExit();
		File nativeLibTmpFile = new File(tmpDir, libName);
		nativeLibTmpFile.deleteOnExit();
		try (InputStream in = url.openStream()) {
		    Files.copy(in, nativeLibTmpFile.toPath());
		}
		System.load(nativeLibTmpFile.getAbsolutePath());
		jni.init(size);
	}
	
	public static Jumbo initialize(int size) {
		try {
			return new Jumbo(size);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void put(int table, byte[] key, byte[] value) {
		jni.put(table, key, value);
	}
	
	public byte[] get(int table, byte[] key) {
		return jni.get(table, key);
	}
	
	public void del(int table, byte[] key) {
		jni.del(table, key);
	}
	public List<byte[]> keys(int table, int limit) {
		byte[][] keyBytes = jni.keys(table, limit);
		List<byte[]> keys = new ArrayList<>(keyBytes.length);
		for (int i = 0; i < keyBytes.length; i++) {
			keys.add(keyBytes[i]);
		}
		return keys;
	}
}
