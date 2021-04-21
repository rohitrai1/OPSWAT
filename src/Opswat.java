import sun.security.provider.certpath.OCSPResponse;

import javax.xml.ws.Response;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.net.*;

public class Opswat {
    //Get the API key from the key configuration file
    private static String key = "";
    private static String getTagValue(String xml, String tagName){
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
        return data.split("=")[1];
    }

    public boolean checkAuthentication(String fileLocation) throws MalformedURLException, IOException {
        URL url = new URL("https://api.metadefender.com/v4/file");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/octet-stream");
        http.setRequestProperty("apikey", "34790cc8816ec3556cd56fc76dc45546");
        http.setDoOutput(true);

        InputStream inputStream = new FileInputStream(new File("1.bin"));
        String s = inputStream.toString();
        try(OutputStream os = http.getOutputStream()) {
            byte[] input = s.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (Exception e) {
            System.out.println("exception");
        }
        int status = http.getResponseCode();
        if ("SUCCESS".equals(StatusFamily.checkAPIResponse(status)))  {
            return true;
        }
        return false;
    }
    public static void main(String [] args) {
       System.out.println(key);
    }
}
