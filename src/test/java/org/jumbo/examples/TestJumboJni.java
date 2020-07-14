package org.jumbo.examples;

import java.util.List;

import org.jumbo.Jumbo;
import org.junit.Assert;
import org.junit.Test;

public class TestJumboJni {

	@Test
	public void test() {
		System.out.println("Starting test");
		Jumbo jumbo = Jumbo.initialize(1024);
		System.out.println("Jumbo initialized!");
		{
			jumbo.put(99, "a".getBytes(), "apa".getBytes());
			String val = new String(jumbo.get(99, "a".getBytes()));
			System.out.println("val" + val);
		}
		jumbo.put(99, "b".getBytes(), "test".getBytes());
		{
			jumbo.put(99, "a".getBytes(), "foo".getBytes());
			String val = new String(jumbo.get(99, "a".getBytes()));
			System.out.println(val);
		}
		List<byte[]> keys = jumbo.keys(99, 5);
		System.out.print("Keys: [");
		for (int i = 0; i < keys.size(); i++) {
			if (i != 0)
				System.out.print(",");
			System.out.print(new String(keys.get(i)));
		}
		System.out.print("]\n");
		
//		for (int i = 0; i < 111; i++) {
//			jumbo.put(99, (String.valueOf(i)).getBytes(), (String.valueOf(i)).getBytes());
//		}
		jumbo.put(99, "2".getBytes(), "c".getBytes());
		jumbo.put(99, "99".getBytes(), "a".getBytes());
		jumbo.put(99, "110".getBytes(), "b".getBytes());
		byte[] val99 = jumbo.get(99, "99".getBytes());
		byte[] val110 = jumbo.get(99, "110".getBytes());
		
		Assert.assertEquals("b", val110);
		Assert.assertEquals("a", val99);
	}
	
	static void foo(Object a) {

	}
}
