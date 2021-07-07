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
public class myTest2 {

    private boolean expectedResult;
    private long ledgerId;
    private long entryId;
    private ByteBuf entry;
    private ReadCache cache = null;
    private int numPuts;

    public myTest2(boolean expectedResult, long ledgerId, long entryId, ByteBuf entry, int numPuts) {
        this.expectedResult = expectedResult;
        this.ledgerId = ledgerId;
        this.entryId = entryId;
        this.entry = entry;
        this.numPuts = numPuts;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{

                {false, -1, 0, Unpooled.wrappedBuffer(new byte[1024]),1}, //false perche' la put di -1 fallisce
                {false, 1, 0, null, 1},
                {true, 1, 0,Unpooled.wrappedBuffer(new byte[1024]),1 },
                {true, 1, 0,Unpooled.wrappedBuffer(new byte[1024]),10},
               // {true, 1, 0,Unpooled.wrappedBuffer(new byte[32]),1},

        });
    }

    @Before
    public void setup() {
        cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
    }

    @Test
    public void readCacheGetTest() {
        boolean result;
        if(numPuts == 1) {
        System.out.println((" num puts == 1"));
            try {
                cache.put(ledgerId, entryId, entry);
                result = true;
            } catch(Exception e ) {
                result = false;
                e.printStackTrace();
            }
            Assert.assertEquals(result, expectedResult);
        }
        else {
            System.out.println((" num puts != 1"));
            try {
                for (int i = 1; i < numPuts; i++) {
                    cache.put(ledgerId, i, entry);
                }
                result = true;
            }catch(Exception e ) {
                    result = false;
                    e.printStackTrace();
                }
                Assert.assertEquals(result, expectedResult);
        }
    }


    @After
    public void tearDown() {
        cache.close();
    }
}
