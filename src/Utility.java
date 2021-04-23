import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Scanner;

public class Utility {
    // Utility functions, these functions don't make any API calls

    // reads the API key from key.txt keeps the key change dynamic
    public static String getAPIKey(){
        String data = "";
        try {
            //key file has to be on project root
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

    // generate md5 for the given file
    public static String generateHashMd5(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        md5Digest.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md5Digest.digest();
        String myChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();

        return myChecksum;
    }

    // iterate over the json report and display it
    public static void opswatParserAndPrint (JSONObject json) {
        String fileName = (String) json.getJSONObject("file_info").get("display_name");
        JSONObject scanResults = json.getJSONObject("scan_results");
        String overAllStatus = (String) scanResults.get("scan_all_result_a");
        JSONObject scanDetails = scanResults.getJSONObject("scan_details");

        Iterator itr = scanDetails.keys();
        System.out.println("filename: " + fileName);
        System.out.println("thread_found: " + overAllStatus);
        while (itr.hasNext()) {
            String engine = (String) itr.next();
            JSONObject engineResult = scanDetails.getJSONObject(engine);
            int scanResult = (int) engineResult.get("scan_result_i");
            String threat = (String) engineResult.get("threat_found");
            String dateTime = (String )engineResult.get("def_time");
            System.out.println("engine: " + engine);
            System.out.println("threat_found: " + threat);
            System.out.println("scan_result: " + scanResult);
            System.out.println("def_time: " + dateTime);
            System.out.println("END");
        }
    }
}
