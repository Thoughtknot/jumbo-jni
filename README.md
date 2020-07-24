# jumbo-jni
JNI-based project to use an embedded jumbo database.

Example basic usage:
```
int startingSize = 1024;
JumboJniWrapper jumbo = JumboJniWrapper.initialize(startingSize);
int table = 99;
jumbo.put(table, "Hello".getBytes(), "World!".getBytes());
byte[] ret = jumbo.get(table, "Hello".getBytes());
``` 

Using the type registry:
```
int startingSize = 1024;
JumboJniWrapper jumbo = JumboJniWrapper.initialize(startingSize);
int table = 99;
Codec<TradeKey, Trade> codec = getTradeCodec();
CodecRegistry.registerCodec(table, codec);
TradeKey key = getTradeKey();
Trade value = getTrade();
jumbo.put(table, key, value);
Trade ret = (Trade) jumbo.get(table, key);
```
