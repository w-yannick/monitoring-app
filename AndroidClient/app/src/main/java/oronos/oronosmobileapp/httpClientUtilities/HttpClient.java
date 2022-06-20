package oronos.oronosmobileapp.httpClientUtilities;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

public abstract class HttpClient<Z> extends AsyncTask<String, Integer, Z> {
    public HttpURLConnection urlConnection = null;

    public void setConnection(URL url, String req) {
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Configuration.timeout);
            if (req == "GET") {
                urlConnection.setReadTimeout(Configuration.timeout);//
            } else if(req == "POST") {
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json"); // Setting the `Content-Type` for the data you are sending which is `application/json`
                urlConnection.setDoOutput(true);
            }
            urlConnection.setRequestMethod(req); //
            urlConnection.connect();
        } catch (IOException e) {
            FLog.e(Tag.HTTP, "Fail to connection on http");
        }
    }

    /*
    * JSON object retrieved contained in field "json"
    * */
    public InputStreamReader retrieveStreamReader() {
        try {
            InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
            return streamReader;
        } catch (IOException e){
            FLog.e(Tag.HTTP, "Failed retrieving data response from the POST");
        }
        return null;
    }

    protected String readBufferToString(InputStreamReader streamReader) {
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line+'\n');
            }
            streamReader.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

