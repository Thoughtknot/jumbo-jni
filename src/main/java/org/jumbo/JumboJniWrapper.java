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

public class JumboJniWrapper implements Jumbo {
	public final JumboJni jni = new JumboJni();
	private final CodecRegistry registry;
	
	private JumboJniWrapper(int size) throws IOException {
		String os = System.getProperty("os.name");
		String libName;
		if (os.contains("Windows")) {
			libName = "jumbo_jni.dll";
		}
		else {
			libName = "libjumbo_jni.so"; 
		}
		URL url = getClass().getResource("/" + libName);
		File tmpDir = Files.createTempDirectory("jumbo_jni").toFile();
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
	
	public static JumboJniWrapper initialize(int size) {
		try {
			return new JumboJniWrapper(size);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void putObject(int table, Object key, Object value) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		put(table, val.serializeKey(key), val.serializeValue(value));
	}

	@Override
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

	@Override
	public List<Object> getKeysObject(int table, int limit) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		List<byte[]> result = getKeys(table, limit);
		return result
			.stream()
			.map(val::deserializeKey)
			.collect(Collectors.toList());
	}

	@Override
	public void deleteObject(int table, Object key) throws IOException {
		Codec<Object, Object> val = registry.getCodec(table);
		if (val == null) {
			throw new RuntimeException("Could not find a codec registered for table " + table);
		}
		delete(table, val.serializeKey(key));
	}

	@Override
	public void put(int table, byte[] key, byte[] value) {
		jni.put(table, key, value);
	}

	@Override
	public byte[] get(int table, byte[] key) {
		return jni.get(table, key);
	}

	@Override
	public void delete(int table, byte[] key) {
		jni.del(table, key);
	}
	
	@Override
	public List<byte[]> getKeys(int table, int limit) {
		byte[][] keyBytes = jni.keys(table, limit);
		List<byte[]> keys = new ArrayList<>(keyBytes.length);
		for (int i = 0; i < keyBytes.length; i++) {
			keys.add(keyBytes[i]);
		}
		return keys;
	}
}
