package org.apache.bookkeeper.client;

import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.BookKeeper;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class prova2Test extends BookKeeperClusterTestCase {

    private static LedgerHandle lh;
    //private ZooKeeper zkc;

    //arguments
    private boolean expResult;
    private long lId;
    private AsyncCallback.DeleteCallback cb;
    private CountDownLatch ctx;
    private boolean isClosed;
    //private BookKeeper bkc;


    public prova2Test(boolean expResult, long lId, AsyncCallback.DeleteCallback cb, CountDownLatch ctx, boolean isClosed) {
        //Number of bookies is irrelevant in this test
        super(8);

        this.expResult = expResult;
        this.lId = lId;
        this.cb = cb;
        this.ctx = ctx;
        this.isClosed = isClosed;
    }

    @Before
    public void setup() {
        //Create the ledger we are trying to open
        try {
            //bkc = new BookKeeper(baseClientConf, zkc);
            lh = bkc.createLedger(6, 5, 4, BookKeeper.DigestType.CRC32, "password".getBytes(),null);
        } catch (InterruptedException | BKException  e) {
            e.printStackTrace();
        }
    }


    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        //function signature
        //LedgerHandle openLedger(long lId, DigestType digestType, byte[] passwd)


        CompletableFuture<Void> future = new CompletableFuture<>();
        SyncCallbackUtils.SyncDeleteCallback cb = new SyncCallbackUtils.SyncDeleteCallback(future);

        return Arrays.asList(new Object[][]{

                //true, perche' deleteLedger ritorna true anche se id del ledger non esiste (ossia non esiste un ledger
                //con quell'id)
                /*
                {true, LedgerHandle.INVALID_ENTRY_ID, cb, openLatch, false},
                {true, 0, cb, openLatch, false},
                {true, 0, cb, null, false},

                 */


        });

    }

    @Test
    public void asyncDeleteTest() {
        System.out.println("ID LEDGER == " + lh.getId());

        System.out.println("cb == " + cb);
        try {
            bkc.asyncDeleteLedger(lId, cb,ctx);
        } catch (Exception e ) {
            e.printStackTrace();
        }
        try {
            assertTrue("Delete call should have completed", ctx.await(20, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @After
    public void tearDown() {
        //Delete the ledger that we have created
        try {
            bkc.deleteLedger(lh.getId());
        } catch (InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }

}