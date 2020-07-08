package org.jumbo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jumbo.codec.Codec;
import org.jumbo.codec.CodecRegistry;

public class Jumbo {
	private final JumboJni jni = new JumboJni();
	private final CodecRegistry registry;
	
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
		registry = CodecRegistry.get();
		jni.init(size);
	}
	
	public static Jumbo initialize(int size) {
		try {
			return new Jumbo(size);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void putObject(int table, Object key, Object value) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		put(table, val.serializeKey(key), val.serializeValue(value));
	}
	
	public Object getObject(int table, Object key) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		byte[] result = get(table, val.serializeKey(key));
		if (result.length == 0)
			return null;
		return val.deserializeValue(result);
	}
	
	public List<Object> getKeysObject(int table, int limit) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		List<byte[]> result = keys(table, limit);
		return result
			.stream()
			.map(val::deserializeKey)
			.collect(Collectors.toList());
	}

	public void deleteObject(int table, Object key) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		del(table, val.serializeKey(key));
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
