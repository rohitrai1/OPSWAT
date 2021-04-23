import org.json.JSONObject;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Scanner;
import java.net.*;

public class Opswat {
    //Get the API key from the key configuration file
    public boolean checkAuthentication(String fileLocation) throws MalformedURLException, IOException {
        if (fileLocation.isEmpty()) {

        }
        URL url = new URL("https://api.metadefender.com/v4/file");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/octet-stream");
        http.setRequestProperty("apikey", Utility.getAPIKey());
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
        System.out.println(status);
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
        http.setRequestProperty("apikey", Utility.getAPIKey());
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
        String fileHash = Utility.generateHashMd5(fileLocation);
        String getUrl = "https://api.metadefender.com/v4/hash/" + fileHash;
        URL url = new URL(getUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("apikey", Utility.getAPIKey());
        http.setRequestProperty("hash", fileHash);
        int statusCode = http.getResponseCode();
        System.out.println(statusCode);
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
        http.setRequestProperty("apikey", Utility.getAPIKey());
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
        opswatParserAndPrint(jsonReport);
        return jsonReport;
    }

    public void opswatParserAndPrint (JSONObject json) {
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

    public static void main(String [] args) throws InterruptedException, NoSuchAlgorithmException, IOException {
        String fileLocation = args[0];
        System.out.println(fileLocation);
        Opswat obj = new Opswat();
        obj.getReportWrapper(fileLocation);
    }
}
