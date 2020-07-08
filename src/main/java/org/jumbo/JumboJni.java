package org.jumbo;

class JumboJni {
	private long statePtr = 0;
	native void init(int size);
	native void put(int table, byte[] key, byte[] value);
	native byte[] get(int table, byte[] key);
	native void del(int table, byte[] key);
	native byte[][] keys(int table, int limit);
}
