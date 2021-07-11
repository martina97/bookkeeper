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

@RunWith(value = Parameterized.class)
public class MyLedgerEntriesImplCreateTest {
    private List<LedgerEntry> entryList ;
    private boolean expectedResult;

    public MyLedgerEntriesImplCreateTest(boolean expectedResult, List<LedgerEntry> entryList) {
        this.expectedResult = expectedResult;
        this.entryList = entryList;

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
    public void createTest()  {

        if (entryList == null) {
            try {

                LedgerEntriesImpl.create(null);
                fail("Should not create from null");
            } catch (Exception e) {
                // nulla
            }

        } else {
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

            } catch (IllegalArgumentException e) {
                Assert.assertEquals(e.getMessage(), "entries for create should not be empty.");
            }

        }
    }
}



