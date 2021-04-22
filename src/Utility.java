import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Utility {
    public static String getAPIKey(){
        String data = "";
        try {
            File keyFile = new File("key.txt");
            Scanner myReader = new Scanner(keyFile);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
        } catch (FileNotFoundException fe) {
            System.out.println("API key is not configured.");
            fe.printStackTrace();
        }
        return (String) data.split("=")[1];
    }

    public static String generateHashMd5(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        md5Digest.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md5Digest.digest();
        String myChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();

        return myChecksum;
    }
}
