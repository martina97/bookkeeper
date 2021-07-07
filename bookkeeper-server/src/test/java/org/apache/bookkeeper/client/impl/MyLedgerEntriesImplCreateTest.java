package org.apache.bookkeeper.client.impl;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.bookkeeper.client.api.LedgerEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;

/**
 * LedgerEntriesImpl Tester.
 *
 * @author <Authors name>
 * @since <pre>lug 1, 2021</pre>
 * @version 1.0
 */

@RunWith(value = Parameterized.class)
public class MyLedgerEntriesImplCreateTest {
    private final int entryNumber = 1;
    private LedgerEntriesImpl ledgerEntriesImpl;
    private List<LedgerEntry> entryList = Lists.newArrayList();

    // contenuto di ogni entry
    //private final long ledgerId = 1234L;
    //private final long entryId = 5678L;
    //private long entryId;
    private final long length = 9876L;
    private final byte[] dataBytes = "test-ledger-entries-impl".getBytes(UTF_8);
    private final ArrayList<ByteBuf> bufs = Lists.newArrayListWithExpectedSize(entryNumber);
    private boolean expectedResult;


    public MyLedgerEntriesImplCreateTest(boolean expectedResult, List<LedgerEntry> entryList) {
        this.expectedResult = expectedResult;
        this.entryList = entryList;

    }

    //TODO: rimuovere before e after

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    public static List<LedgerEntry> createEntryList(long ledgerId, long entryId) {
        List<LedgerEntry> list = Lists.newArrayList();
        list.add(LedgerEntryImpl.create(ledgerId, entryId));
        return list;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{

                {false, null},
                {false, Lists.newArrayList()},
                {true, createEntryList(0,0)}

        });

    }

    /**
     *
     * Method: create(List<LedgerEntry> entries)
     *
     */
    @Test
    public void testGetEntry() throws Exception {

        if (entryList == null) {
            try {
                System.out.println("qua");

                LedgerEntriesImpl emptyLedgersEntry = LedgerEntriesImpl.create(null);
                fail("Should not create from null");
            } catch (Exception e) {
                //correct behavior
            }

        } else {
            System.out.println("LISTA ===" + entryList);
            boolean result;
            try {
                LedgerEntriesImpl entry = LedgerEntriesImpl.create(entryList);
                int size = 0;
                Iterator<LedgerEntry> iterator = entry.iterator();
                while (iterator.hasNext()) {
                    size++;
                    iterator.next();
                }
                Assert.assertEquals(entryList.size(), size);
                Assert.assertEquals(entryList.get(0), entry.getEntry(0));
                Assert.assertEquals(entryList.get(0).getClass(), entry.getEntry(0).getClass());
                result = entryList.get(0).equals(entry.getEntry(0));
                //Assert.assertEquals(entryList.get(0), entry.getEntry(0));
                //fail("Should not create from null");

            } catch (IllegalArgumentException e) {
                //result = false;
                Assert.assertEquals(e.getMessage(), "entries for create should not be empty.");
            }
            //Assert.assertEquals(expectedResult, result);
        }
    }
}



