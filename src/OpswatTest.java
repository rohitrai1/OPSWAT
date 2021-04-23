import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class OpswatTest {
//      @Test
    void checkAuthenticateTest() throws IOException {
        Opswat op = new Opswat();
        assertEquals(true, op.checkAuthentication("1.bin"));
        assertEquals(true, op.checkAuthentication("0.exe"));
    }

//    @Test
    void checkHashFunction() throws IOException, NoSuchAlgorithmException {
        assertEquals("93347F7A9E84ED69E86BFABCAC3CA256", Utility.generateHashMd5("1.bin"));
        assertEquals("2A9D0D06D292A4CBBE4A95DA4650ED54", Utility.generateHashMd5("0.exe"));
        assertEquals("3D925E51522D1A7F5DEE216C76A9F23C", Utility.generateHashMd5("ex.txt"));
    }

//     @Test
    void getReportDataHashTest() throws IOException, NoSuchAlgorithmException {
         Opswat op = new Opswat();
         assertEquals(null, op.getCachedReport("ex.txt"));
     }

//     @Test
     void getNonCachedReportTest () throws IOException {
          Opswat op = new Opswat();
          assertNotEquals(null, op.getNonCachedReport("1.bin"));
     }


     @Test
     void getReportWrapperTest () throws IOException, NoSuchAlgorithmException, InterruptedException {
          Opswat op = new Opswat();
          assertNotEquals(null, op.getReportWrapper("1.bin"));;
     }
}