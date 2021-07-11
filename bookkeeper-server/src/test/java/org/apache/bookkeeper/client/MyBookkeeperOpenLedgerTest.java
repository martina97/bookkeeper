package org.apache.bookkeeper.client;

import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.apache.bookkeeper.client.BookKeeper.DigestType;
import org.apache.bookkeeper.client.BKException.BKIllegalOpException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(value = Parameterized.class)
public class MyBookkeeperOpenLedgerTest extends BookKeeperClusterTestCase {

    private static LedgerHandle lh;

    private boolean expectedResult;
    private long ledgerID;
    private DigestType digestType;
    private byte[] password;
    private BookKeeper bkc;

    public MyBookkeeperOpenLedgerTest(boolean expResult, long lId, BookKeeper.DigestType digestType, byte[] password) {

        super(4);       //il numero di bookies non e' importante

        this.expectedResult = expResult;
        this.ledgerID = lId;
        this.digestType = digestType;
        this.password = password;
    }


    @Before
    public void setup() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setMetadataServiceUri(zkUtil.getMetadataServiceUri());
        conf.setEnableDigestTypeAutodetection(false);
        try {
            bkc = new BookKeeper(conf);
            lh = bkc.createLedger(DigestType.CRC32, "password".getBytes()); //si crea un LedgerHandle con un ID = 0

        } catch (IOException | InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }



    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {

        return Arrays.asList(new Object[][]{

                /*
                METTO LO ZERO PERCHE' SE CI SONO LEDGER CREATI, SICURAMENTE LO ZERO E' PRESENTE (IL PRIMO LEDGER CREATO AVRA' ID = 0)
                 */
                //id invalido
                {false, LedgerHandle.INVALID_ENTRY_ID, BookKeeper.DigestType.MAC, "password".getBytes()},   // LedgerHandle.INVALID_ENTRY_ID = -1L

                //password sbagliata
                {false, 0, BookKeeper.DigestType.MAC, "bad_password".getBytes()},
                {false, 0, BookKeeper.DigestType.MAC, "".getBytes()},
                {false, 0, BookKeeper.DigestType.MAC, null},

                //digestType diverso da CRC32
                {false, 0, DigestType.MAC, "password".getBytes()},
                {false, 0, DigestType.CRC32C, "password".getBytes()},
                {false, 0, DigestType.DUMMY, "password".getBytes()},

                {false, 1, BookKeeper.DigestType.CRC32, "password".getBytes()},    //sto tentando di aprire un ledger con un id che non e' mai stato creato

                {true, 0, BookKeeper.DigestType.CRC32, "password".getBytes()},

        });

    }

    /*
    Se l'ID e' sbagliato, viene restituito: org.apache.bookkeeper.client.BKException$BKNoSuchLedgerExistsOnMetadataServerException: No such ledger exists on Metadata Server --> exc 1
    Se la password e' sbagliata, viene restituito: org.apache.bookkeeper.client.BKException$BKUnauthorizedAccessException: Attempted to access ledger using the wrong password
    Provided passwd does not match that in metadata --> exc 1
    Se il digest e' sbagliato --> KException$BKDigestMatchException: Entry digest does not match
    Se pw e digest sbagliati --> Provided passwd does not match that in metadata --> quindi non c'e bisogno di fare digest e pw sbagliati, tanto basta che pw sia sbagliata
    Quindi in entrambi i casi viene restituita una BKException

     */


    @Test
    public void openLedgerTest() {

        try {
           bkc.openLedger(ledgerID, digestType, password);

        } catch (InterruptedException e) {
            //e.printStackTrace();
            Assert.fail();

        } catch (BKException e) {   // errore nell'aprire il ledger
            if(ledgerID == LedgerHandle.INVALID_ENTRY_ID) {
                Assert.assertEquals(e.getMessage() ,"No such ledger exists on Metadata Server");
            }
            if(ledgerID != lh.getId()) {
                Assert.assertEquals(e.getMessage() ,"No such ledger exists on Metadata Server");
            }
            else {
                if (!Arrays.equals(password, "password".getBytes())) {
                    Assert.assertEquals(e.getMessage() ,"Attempted to access ledger using the wrong password");
                }
                else {
                    Assert.assertEquals(e.getMessage() ,"Entry digest does not match");
                }
            }

            //e.printStackTrace();

        }
    }


    @After
    public void tearDown() {
        //Cancello il ledger creato
        try {
            bkc.deleteLedger(lh.getId());
        } catch (InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }

}