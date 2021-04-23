public class StatusFamily {
    // this is a utility class to map response codes
    // java version that I am using doesn't have OOTB class for this
    public static String checkAPIResponse(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "SUCCESS";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "CLIENT_ERROR";
        } else if (statusCode >= 500 && statusCode < 600) {
            return "SERVER_ERROR";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "REDIRECTED";
        }
        return "UNRECOGNIZED";
    }
}
