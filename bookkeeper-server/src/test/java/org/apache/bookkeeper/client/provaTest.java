package org.apache.bookkeeper.client;

import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class provaTest extends BookKeeperClusterTestCase {

    private static LedgerHandle lh;

    //arguments
    private boolean expectedResult;
    private long ledgerID;
    private BookKeeper.DigestType digestType;
    private byte[] password;
    BookKeeper.DigestType digestBad = BookKeeper.DigestType.CRC32 == BookKeeper.DigestType.MAC ? BookKeeper.DigestType.CRC32 : BookKeeper.DigestType.MAC;


    //private boolean closed;

    public provaTest(boolean expResult, long lId, BookKeeper.DigestType digestType, byte[] password) {
        //Number of bookies is irrelevant in this test
        super(8);

        this.expectedResult = expResult;
        this.ledgerID = lId;
        this.digestType = digestType;
        this.password = password;
        //this.closed = closed;
        //        DigestType digestBad = digestType == DigestType.MAC ? DigestType.CRC32 : DigestType.MAC;
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
            BookKeeper bkc = new BookKeeper(conf);
            lh = bkc.createLedger(BookKeeper.DigestType.CRC32, "password".getBytes()); //si crea un LedgerHandle con un ID = 0

        } catch (IOException | InterruptedException | BKException e) {
            e.printStackTrace();
        }
    }



    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {

        BookKeeper.DigestType digestBad = BookKeeper.DigestType.CRC32 == BookKeeper.DigestType.MAC ? BookKeeper.DigestType.CRC32 : BookKeeper.DigestType.MAC;

        //function signature
        //LedgerHandle openLedger(long lId, DigestType digestType, byte[] passwd)

        //this parameters can be tested unidimensionally, they seem to have no connection between each other

        return Arrays.asList(new Object[][]{

                //fail beacuse of negative or wrong id
                {false, LedgerHandle.INVALID_ENTRY_ID, BookKeeper.DigestType.MAC, "password".getBytes()},   // LedgerHandle.INVALID_ENTRY_ID = -1L

                //fail because of wrong password
                {false, 0, BookKeeper.DigestType.MAC, "bad_password".getBytes()},
                {false, 0, BookKeeper.DigestType.MAC, "".getBytes()},

                //fail because of wrong digestType
                {true, 0, digestBad, "password".getBytes()},

                //valid configurations
                {true, 0, BookKeeper.DigestType.MAC, "password".getBytes()},
                {true, 0, BookKeeper.DigestType.CRC32C, "password".getBytes()},
                {true, 0, BookKeeper.DigestType.CRC32, "password".getBytes()},
                {true, 0, BookKeeper.DigestType.DUMMY, "password".getBytes()},

        });

    }

    /*
    Se l'ID e' sbagliato, viene restituito: org.apache.bookkeeper.client.BKException$BKNoSuchLedgerExistsOnMetadataServerException: No such ledger exists on Metadata Server --> exc 1
    Se la password e' sbagliata, viene restituito: org.apache.bookkeeper.client.BKException$BKUnauthorizedAccessException: Attempted to access ledger using the wrong password
    Provided passwd does not match that in metadata --> exc 1
    Quindi in entrambi i casi viene restituita una BKException

     */

/*
    @Test
    public void openLedger2Test() {
        System.out.println("PROVAAAAAAA");
        System.out.println("ledgerID ==" + ledgerID + "\tdigestType == " + digestType + "\tpassword == " + password);   //sono quelli passati per parametri

        System.out.println("LEDGER ID === " + lh.getId());
        if (Arrays.equals(password, "password".getBytes())) {
            System.out.println(("CHE PALLEEEEEE"));
        }
        LedgerHandle res = null;

        try {
            res = bkc.openLedger(ledgerID, digestType, password);

        } catch (BKException e) {
            //ci vado quando fallisce l'operazione openLedger, quindi quando ho o la pw sbagliata o l'id sbagliato
            System.out.println("CACCA1");
            //e.printStackTrace();
            res = null;     // se ho un'eccezione del genere o la pw e' sbagliata o l'id, quindi ci entro
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("CACCA2");
        }
        System.out.println("PASSWORD === " + password + "\tledgerID ===" + ledgerID);
        if(Arrays.equals(password, "password".getBytes()) && ledgerID == 0 ) {
            System.out.println("CACCA");
            Assert.assertEquals(res, lh);
        }
        else {
            Assert.assertNull(res);
            System.out.println("PIPI");
        }

        /*
        try {
            LedgerHandle lha = bkc.openLedger(ledgerID, digestType, password);

            //check if ledger is open for us (which means its closed for others)
            //Assert.assertTrue(lha != null && lha.isClosed());

        } catch (InterruptedException e) {
            //the test failed becuse of a system failure
            e.printStackTrace();
        } catch (BKException e) {
            //we failed to open the ledger - check if the error is correct
            if (!Arrays.equals(password, "password".getBytes()))
                Assert.assertEquals(e.getMessage() ,"Attempted to access ledger using the wrong password");
            else
                Assert.assertEquals(e.getMessage() ,"No such ledger exists on Metadata Server");
        }





    }
 */



    @Test
    public void openLedgerTest() {
        //System.out.println("DIGEST TYPE ==== " + digestType);

        System.out.println("PROVA MARCOOOOO");
        //System.out.println(" PROVA --> " + lh.getDigestManager().getClass().getName());
        System.out.println("digest bad == " + digestBad);
        try {
            bkc.openLedger(0, digestBad, "password".getBytes());

        } catch (BKException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
        try {
            bkc.openLedger(ledgerID, digestType, password);

            //check if ledger is open for us (which means its closed for others)
            //Assert.assertTrue(lha != null && lha.isClosed());

        } catch (InterruptedException e) {
            //the test failed becuse of a system failure
            e.printStackTrace();
        } catch (BKException e) {
            //we failed to open the ledger - check if the error is correct
            if (!Arrays.equals(password, "password".getBytes()))
                Assert.assertEquals(e.getMessage() ,"Attempted to access ledger using the wrong password");
            else    // id e' sbagliato
                Assert.assertEquals(e.getMessage() ,"No such ledger exists on Metadata Server");
            e.printStackTrace();

        }*/
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