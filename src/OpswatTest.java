import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class OpswatTest {
      @Test
      void checkAuthenticateTest() {
            Opswat op = new Opswat();
            try {
                op.checkAuthentication();
            } catch (IOException ie) {

            }
    }
}