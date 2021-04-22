import com.sun.deploy.net.HttpResponse;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.net.*;
import java.util.*;

public class Opswat {
    //Get the API key from the key configuration file
    private static String key = "";
    private static String getKey(String xml, String tagName){
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

    protected String generateHashMd5(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        md5Digest.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md5Digest.digest();
        String myChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();

        return myChecksum;
    }

    public boolean checkAuthentication(String fileLocation) throws MalformedURLException, IOException {
        if (fileLocation.isEmpty()) {

        }
        URL url = new URL("https://api.metadefender.com/v4/file");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/octet-stream");
        http.setRequestProperty("apikey", "34790cc8816ec3556cd56fc76dc45546");
        http.setDoOutput(true);

        InputStream inputStream = new FileInputStream(new File(fileLocation));
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

    public JSONObject getNonCachedReport(String fileLocation) throws MalformedURLException, IOException {
        if (fileLocation.isEmpty()) {

        }
        URL url = new URL("https://api.metadefender.com/v4/file");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/octet-stream");
        http.setRequestProperty("apikey", "34790cc8816ec3556cd56fc76dc45546");
        http.setDoOutput(true);

        InputStream inputStream = new FileInputStream(new File(fileLocation));
        String s = inputStream.toString();
        try(OutputStream os = http.getOutputStream()) {
            byte[] input = s.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (Exception e) {
            System.out.println("exception");
        }
        int statusCode = http.getResponseCode();
        if (!"SUCCESS".equals(StatusFamily.checkAPIResponse(statusCode))) {
            return null;
        }

        String jsonString = "";
        Scanner scanner = new Scanner(http.getInputStream());
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine();
        }
        scanner.close();
        JSONObject jsonObject = new JSONObject(jsonString);

        return jsonObject;
    }

    public JSONObject getCachedReport(String fileLocation) throws IOException, NoSuchAlgorithmException {
        String fileHash = generateHashMd5(fileLocation);
        String getUrl = "https://api.metadefender.com/v4/hash/" + fileHash;
        URL url = new URL(getUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("apikey", "34790cc8816ec3556cd56fc76dc45546");
        http.setRequestProperty("hash", "34790cc8816ec3556cd56fc76dc45546");
        int statusCode = http.getResponseCode();
        if (!"SUCCESS".equals(StatusFamily.checkAPIResponse(statusCode))) {
            return null;
        }
        String jsonString = "";
        Scanner scanner = new Scanner(http.getInputStream());
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine();
        }
        scanner.close();
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject;
    }

    public JSONObject pollReport (String dataId) throws IOException {
        String getUrl = "https://api.metadefender.com/v4/file/" + dataId;
        URL url = new URL(getUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("apikey", "34790cc8816ec3556cd56fc76dc45546");
        int statusCode = http.getResponseCode();

        String jsonString = "";
        Scanner scanner = new Scanner(http.getInputStream());
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine();
        }
        scanner.close();
        JSONObject jsonObject = new JSONObject(jsonString);
        if (!"SUCCESS".equals(StatusFamily.checkAPIResponse(statusCode))) {
            return null;
        }
        return jsonObject;
    }
    
    public JSONObject getReportWrapper(String fileLocation) throws IOException, NoSuchAlgorithmException, InterruptedException {
        JSONObject jsonReport = getCachedReport(fileLocation);
        int pollingPercent = 0;

        if (jsonReport == null) {
              jsonReport = getNonCachedReport(fileLocation);
        }

        String dataId = String.valueOf(jsonReport.get("data_id"));

        // Polling
        boolean polling_in_progress = true;
        while (polling_in_progress) {
            JSONObject polledData = pollReport(dataId);
            if (polledData == null) {
                return null;
            }

            polledData = pollReport(dataId);
            if (polledData.has("scan_results")) {
                pollingPercent = (int) polledData.getJSONObject("scan_results").get("progress_percentage");
            }
            if (pollingPercent == 100) {
                polling_in_progress = false;
                jsonReport = polledData;
            } else {
                System.out.println(pollingPercent);
            }

            Thread.sleep(5000);
        }
        System.out.println(jsonReport);
        return jsonReport;
    }

    public static void main(String [] args) {
       System.out.println(key);
    }
}
