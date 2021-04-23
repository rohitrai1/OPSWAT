import org.json.JSONObject;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.net.*;

public class Opswat {
    // post request to upload the file to metadefender, runs if cache miss
    public JSONObject getNonCachedReport(String fileLocation) throws IOException {
        if (fileLocation.isEmpty()) {
            try {
                throw new FileNotFoundException("Please specify a file with location");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // setup header required params for post request
        HttpURLConnection http = null;
        URL url;
        try {
            url = new URL("https://api.metadefender.com/v4/file");
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/octet-stream");
            http.setRequestProperty("apikey", Utility.getAPIKey());
            http.setDoOutput(true);
        } catch (MalformedURLException fe){
            System.out.println("The API end-point is malformed");
        } catch (ProtocolException fe){
            System.out.println("There was a problem with the protocol");
        } catch (IOException ie) {
            System.out.println("Can not open http connection");
        }

        // setup request body: add the uploaded file to body
        InputStream inputStream = null;
        String file = "";

        try {
            inputStream = new FileInputStream(new File(fileLocation));
            file = inputStream.toString();
            OutputStream outputStream = http.getOutputStream();
            byte[] input = file.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        } catch (IOException e) {
            System.out.println("There is a problem uploading this file");
        }

        // validate status code against my status family class
        int statusCode = http.getResponseCode();
        if ("SERVER_ERROR".equals(StatusFamily.checkAPIResponse(statusCode))
                || "CLIENT_ERROR".equals(StatusFamily.checkAPIResponse(statusCode))
                    ||  "REDIRECTED".equals(StatusFamily.checkAPIResponse(statusCode))) {
            try {
                throw new Exception("Metadefender API response code error");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // append the response to string and parse it to json
        String jsonString = "";
        Scanner scanner = new Scanner(http.getInputStream());
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine();
        }
        scanner.close();
        JSONObject jsonObject = new JSONObject(jsonString);

        return jsonObject;
    }

    // get request takes a file, hashes it and checks metadefender hash
    public JSONObject getCachedReport(String fileLocation) throws IOException {
        // generate hash for the file
        String fileHash = null;
        try {
            fileHash = Utility.generateHashMd5(fileLocation);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("There was a problem hashing the file");
        } catch (IOException e) {
            System.out.println("There was a problem hashing the file");
        }

        // setup get requeset body/header required params
        String getUrl = "https://api.metadefender.com/v4/hash/" + fileHash;
        URL url = null;
        HttpURLConnection http = null;
        try {
            url = new URL(getUrl);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("apikey", Utility.getAPIKey());
            http.setRequestProperty("hash", fileHash);
        } catch (MalformedURLException fe){
            System.out.println("The API end-point is malformed");
        } catch (ProtocolException fe){
            System.out.println("There was a problem with the protocol");
        } catch (IOException ie) {
            System.out.println("Can not open http connection");
        }

        // validate status code against my status family class
        int statusCode = http.getResponseCode();
        if ("SERVER_ERROR".equals(StatusFamily.checkAPIResponse(statusCode))
                || "CLIENT_ERROR".equals(StatusFamily.checkAPIResponse(statusCode))
                    ||  "REDIRECTED".equals(StatusFamily.checkAPIResponse(statusCode))) {
            if (statusCode == 404) {
                // cache miss
                return null;
            }
            try {
                throw new Exception("Metadefender returned an error");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // append the response to string and parse it to json
        String jsonString = "";
        Scanner scanner = new Scanner(http.getInputStream());
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine();
        }
        scanner.close();
        JSONObject jsonObject = new JSONObject(jsonString);

        return jsonObject;
    }

    // get request to poll using a dataId - used from within getReportWrapper
    public JSONObject pollReport (String dataId) throws IOException {
        // setup request body/header
        String getUrl = "https://api.metadefender.com/v4/file/" + dataId;
        URL url = new URL(getUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("apikey", Utility.getAPIKey());
        int statusCode = http.getResponseCode();

        // parse the json response
        String jsonString = "";
        Scanner scanner = new Scanner(http.getInputStream());
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine();
        }
        scanner.close();

        // validations against response family
        JSONObject jsonObject = new JSONObject(jsonString);
        if (!"SUCCESS".equals(StatusFamily.checkAPIResponse(statusCode))) {
            return null;
        }
        return jsonObject;
    }

    // wrapper server to wrap all the previous requests
    public JSONObject getReportWrapper(String fileLocation) throws IOException, NoSuchAlgorithmException, InterruptedException {
        // try to get the hashed report first
        JSONObject jsonReport = getCachedReport(fileLocation);
        int pollingPercent = 0;

        // if the there is a cache miss proceed uploading the file
        if (jsonReport == null) {
            jsonReport = getNonCachedReport(fileLocation);

            // Start polling with the data id from previous request
            String dataId = String.valueOf(jsonReport.get("data_id"));
            boolean polling_in_progress = true;
            while (polling_in_progress) {
                JSONObject polledData = pollReport(dataId);
                if (polledData == null) {
                    try {
                        throw new Exception("Error while polling with the data id");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // keep polling util progress percentage in response is 100
                polledData = pollReport(dataId);
                if (polledData.has("scan_results")) {
                    pollingPercent = (int) polledData.getJSONObject("scan_results").get("progress_percentage");
                }
                if (pollingPercent == 100) {
                    polling_in_progress = false;
                    jsonReport = polledData;
                }

                // pool every 3000 ms
                Thread.sleep(3000);
            }
        }

        // call request to parse json response and display
        Utility.opswatParserAndPrint(jsonReport);
        return jsonReport;
    }

    public static void main(String [] args) throws FileNotFoundException {
        // main function takes the arguments from command line
        // and calls wrapper service on it
        try {
            String fileLocation = args[0];
            if (fileLocation == "") {
                throw new FileNotFoundException("No file specified in the arguements");
            }
            System.out.println(fileLocation);
            Opswat obj = new Opswat();
            obj.getReportWrapper(fileLocation);
        } catch (NoSuchAlgorithmException fe) {
            System.out.println("No hashing algorithm exception");
        } catch (InterruptedException fe) {
            System.out.println("Current execution thread was interrupted");
        } catch (IOException fe) {
            System.out.println("Failure in reading the file");
        }
    }
}
