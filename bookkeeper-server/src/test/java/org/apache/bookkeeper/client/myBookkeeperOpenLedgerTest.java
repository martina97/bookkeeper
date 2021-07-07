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
public class myBookkeeperOpenLedgerTest extends BookKeeperClusterTestCase {

    private static LedgerHandle lh;

    //arguments
    private boolean expectedResult;
    private long ledgerID;
    private DigestType digestType;
    private byte[] password;
    private BookKeeper bkc;

    public myBookkeeperOpenLedgerTest(boolean expResult, long lId, BookKeeper.DigestType digestType, byte[] password) {
        //Number of bookies is irrelevant in this test
        super(4);

        this.expectedResult = expResult;
        this.ledgerID = lId;
        this.digestType = digestType;
        this.password = password;
    }

    /*
    @Before
    public void setup() {
        //Create the ledger we are trying to open
        try {
            ClientConfiguration conf = new ClientConfiguration();
            conf.setMetadataServiceUri(zkUtil.getMetadataServiceUri());
            conf.setEnableDigestTypeAutodetection(autodetection);
            BookKeeper bkc = null;
            try {
                bkc = new BookKeeper(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //lh = bkc.createLedger(3,2,BookKeeper.DigestType.CRC32, "password".getBytes()); //si crea un LedgerHandle con un ID = 0
            lh = bkc.createLedger(BookKeeper.DigestType.CRC32, "password".getBytes()); //si crea un LedgerHandle con un ID = 0

            //lh = bkc.createLedger(6, 5, 4, BookKeeper.DigestType.CRC32, "password".getBytes(),null);
        } catch (InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }

     */


    @Before
    public void setup() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setMetadataServiceUri(zkUtil.getMetadataServiceUri());
        conf.setEnableDigestTypeAutodetection(false);
        try {
            bkc = new BookKeeper(conf);
            lh = bkc.createLedger(DigestType.CRC32, "password".getBytes()); //si crea un LedgerHandle con un ID = 0
            //lh.close();

        } catch (IOException | InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }



    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {

        //function signature
        //LedgerHandle openLedger(long lId, DigestType digestType, byte[] passwd)

        //this parameters can be tested unidimensionally, they seem to have no connection between each other

        return Arrays.asList(new Object[][]{

                /*
                METTO LO ZERO PERCHE' SE CI SONO LEDGER CREATI, SICURAMENTE LO ZERO E' PRESENTE (IL PRIMO LEDGER CREATO AVRA' ID = 0)
                 */
                //fail because of wrong id
                {false, LedgerHandle.INVALID_ENTRY_ID, BookKeeper.DigestType.MAC, "password".getBytes()},   // LedgerHandle.INVALID_ENTRY_ID = -1L

                //fail because of wrong password
                {false, 0, BookKeeper.DigestType.MAC, "bad_password".getBytes()},
                {false, 0, BookKeeper.DigestType.MAC, "".getBytes()},
                {false, 0, BookKeeper.DigestType.MAC, null},

                //fail because of wrong digestType
                {false, 0, DigestType.MAC, "password".getBytes()},
                {false, 0, DigestType.CRC32C, "password".getBytes()},
                {false, 0, DigestType.DUMMY, "password".getBytes()},

                {false, 1, BookKeeper.DigestType.CRC32, "password".getBytes()},    //sto tentando di aprire un ledger con un id che non e' mai stato creato

                //valid configurations
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
        /*
        //System.out.println("DIGEST TYPE ==== " + digestType);

        System.out.println("PROVA MARCOOOOO");
        //System.out.println(" PROVA --> " + lh.getDigestManager().getClass().getName());
        System.out.println("digest bad == " + digestBad);
        try {
            bkc.openLedger(0, DigestType.DUMMY, "password".getBytes());
            //fail("Shouldn't be able to open with bad digest");
        } catch (BKException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        */

        try {
            LedgerHandle lh2 = bkc.openLedger(ledgerID, digestType, password);

            //check if ledger is open for us (which means its closed for others)
            //Assert.assertTrue(lha != null && lha.isClosed());

        } catch (InterruptedException e) {
            //the test failed becuse of a system failure
            e.printStackTrace();
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
                    //if (!digestType.equals(DigestType.CRC32)) {
                    Assert.assertEquals(e.getMessage() ,"Entry digest does not match");
                    //}
                }
            }

            e.printStackTrace();

        }
    }


    @After
    public void tearDown() {
        //Delete the ledger created
        try {
            bkc.deleteLedger(lh.getId());
        } catch (InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }

}