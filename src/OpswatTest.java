import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class OpswatTest {
    @Test
    void checkHashFunction() throws IOException, NoSuchAlgorithmException {
        assertEquals("93347F7A9E84ED69E86BFABCAC3CA256", Utility.generateHashMd5("./test_malware_samples/1.bin"));
        assertEquals("2A9D0D06D292A4CBBE4A95DA4650ED54", Utility.generateHashMd5("./test_malware_samples/0.exe"));
        assertEquals("3D925E51522D1A7F5DEE216C76A9F23C", Utility.generateHashMd5("./test_malware_samples/ex.txt"));
    }

     @Test
    void getReportDataHashTest() throws IOException, NoSuchAlgorithmException {
         Opswat op = new Opswat();
         assertEquals(null, op.getCachedReport("./test_malware_samples/ex.txt"));
     }

     @Test
     void testAPIKey () throws IOException {
           System.out.println(Utility.getAPIKey().toString());
          assertNotEquals("34790cc8816ec3556cd56fc76dc45546", Utility.getAPIKey().toString());
     }


     @Test
     void getNonCachedReportTest () throws IOException {
          Opswat op = new Opswat();
          assertNotEquals(null, op.getNonCachedReport("./test_malware_samples/1.bin"));
     }


     @Test
     void getReportWrapperTest () throws IOException, NoSuchAlgorithmException, InterruptedException {
          Opswat op = new Opswat();
          assertNotEquals(null, op.getReportWrapper("./test_malware_samples/1.bin"));;
     }
}