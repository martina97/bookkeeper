package org.apache.bookkeeper.bookie.storage.ldb;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.ReadCache;
import org.apache.bookkeeper.net.BookieSocketAddress;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.UnknownHostException;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class myReadCachePutTest {

    private static final ByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
    private static final int entrySize = 1024;
    private static final int cacheCapability = 10 * 1024;
    private static final int maxSegmentSize = 2*1024;



    private ReadCache cache = null;
    private ByteBuf entry;
    private long ledgerId;
    private long entryId;
    private boolean expected;
    private boolean isAtEnd;


    @Parameterized.Parameters
    public static Collection<?> getParameter() {

        return Arrays.asList(new Object[][] {
                /*
                //grandezza buffer per numero entry
                { -1, -1, Unpooled.wrappedBuffer(new byte[64]), false, false},
                { 0, 1, Unpooled.wrappedBuffer(new byte[64]), true, false},
                { 0, 0, Unpooled.wrappedBuffer(new byte[64]), true, false},
                { 0, 0, Unpooled.wrappedBuffer(new byte[5*1024]), false, true}

                 */

                {0, -1, null, false, false},
                {-1, -1, UnpooledByteBufAllocator.DEFAULT.buffer(1024), false , false},
                {0, 0 , UnpooledByteBufAllocator.DEFAULT.buffer( 2*1024+1), false, true }
        });
    }

    @After
    public void after(){
        cache.close();
    }

    @Before
    public void before(){
        if (!isAtEnd) {
            this.cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
        }
        else {
            this.cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024,  2*1024);
        }
        if (entry != null)  {
            entry.writerIndex(entry.capacity());
        }
    }

    public myReadCachePutTest(long ledgerId, long entryId, ByteBuf entry, boolean expected, boolean isAtEnd){
    //public myReadCachePutTest(long ledgerId, long entryId, ByteBuf entry, boolean expected){

        this.ledgerId = ledgerId;
        this.entryId = entryId;
        this.entry = entry;
        this.expected = expected;
       // this.isAtEnd = isAtEnd;

    }



    @Test
    public void putTest(){
        boolean result;


        try {
            this.cache.put(this.ledgerId, this.entryId, this.entry);
    //            assertEquals(1, this.cache.count());
            result = true;
        }
        catch(Exception e) {
            e.printStackTrace();
            result = false;
        }

        Assert.assertEquals(this.expected, result);


    }

}