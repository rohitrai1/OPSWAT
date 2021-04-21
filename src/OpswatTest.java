import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class OpswatTest {
      @Test
    void checkAuthenticateTest() {
        Opswat op = new Opswat();
        try {
            assertEquals(true, op.checkAuthentication("1.bin"));
        } catch (IOException ie) {

        }
    }

    @Test
    void checkHashFunction() throws IOException, NoSuchAlgorithmException {
        Opswat op = new Opswat();
        assertEquals("93347F7A9E84ED69E86BFABCAC3CA256", op.generateHashMd5("1.bin"));;
        assertEquals("2A9D0D06D292A4CBBE4A95DA4650ED54", op.generateHashMd5("0.exe"));;
      }
}