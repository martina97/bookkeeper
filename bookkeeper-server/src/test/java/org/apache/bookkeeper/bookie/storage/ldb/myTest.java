package org.apache.bookkeeper.bookie.storage.ldb;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class myTest {

    private boolean expectedResult;
    private long ledgerId;
    private long entryId;
    private ByteBuf entry;
    private ReadCache cache = null;

    public myTest(boolean expectedResult, long ledgerId, long entryId, ByteBuf entry) {
        this.expectedResult = expectedResult;
        this.ledgerId = ledgerId;
        this.entryId = entryId;
        this.entry = entry;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{

                {false, -1, 0, Unpooled.wrappedBuffer(new byte[1024])}, //false perche' la put di -1 fallisce
                {false, 1, 0, null },
                {true, 1, 0,Unpooled.wrappedBuffer(new byte[1024]) },
                {true, Long.MAX_VALUE, 1,Unpooled.wrappedBuffer(new byte[1024]) },
                {true, 1, 10,Unpooled.wrappedBuffer(new byte[1024]) },

        });
    }

    @Before
    public void setup() {
        cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
    }

    @Test
    public void readCacheGetTest() {
        boolean result;
        try {
            cache.put(ledgerId, entryId, entry);
            result = true;
        } catch(Exception e ) {
            result = false;
            e.printStackTrace();
        }
        Assert.assertEquals(result, expectedResult);
    }


    @After
    public void tearDown() {
        cache.close();
    }
}
