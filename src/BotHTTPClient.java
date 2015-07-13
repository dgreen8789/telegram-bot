
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David
 */
class BotHTTPClient {

    private final String AUTH_TOKEN = "[REDACTED]";
    private static int OFFSET = 1;

    public BotHTTPClient() {
    }

    /**
     *
     * @param method the name of the method in the Telegram API
     * @param params the parameters to pass to the TelegramAPI, in the form
     * "<paramName>=<paramValue>"
     * @return the output of the TelegramAPI
     * @throws MalformedURLException
     * @throws IOException
     */
    private String sendPost(String method, String[] params) throws MalformedURLException, IOException {

        String url = "https://api.telegram.org/bot" + AUTH_TOKEN + "/" + method;
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        String urlParameters = "";
        if (params != null) {
            for (int i = 0; i < params.length - 1; i++) {
                urlParameters += params[i] + "&";

            }
            urlParameters += params[params.length - 1];
        }
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'POST' request to URL : " + url);
        //System.out.println("Post parameters : " + urlParameters);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }

    public String getUpdates(int limit, int timeout) {
        String[] params = new String[]{"offset=" + OFFSET, "limit=" + limit, "timeout=" + timeout};
        try {
            return sendPost("getUpdates", params);
        } catch (IOException ex) {
            return null;
        }
    }

    public String getUpdates(int limit) {
        return getUpdates(limit, 0);
    }


    public String getNextUpdate() {
        return getUpdates(1);
    }

    public String getAllUpdates() throws Exception {
        return sendPost("getUpdates", null);
    }

    public String sendMessage(int id, String text) {
        String[] params = new String[]{"chat_id=" + id, "text=" + text};
        try {
            return sendPost("sendMessage", params);
        } catch (IOException ex) {
            return null;
        }
    }



    public int getOFFSET() {
        return OFFSET;
    }
    public void setOFFSET(int offset){
        OFFSET = offset;
        System.out.println("offset set to  " + OFFSET);
    }

}
