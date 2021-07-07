package org.apache.bookkeeper.bookie.storage.ldb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.EntryLogger;

public class provaReadCache {


    public static void main(String[] args) {
        ReadCache cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
        System.out.println(cache.count());
        System.out.println(cache.size());

    }
}
