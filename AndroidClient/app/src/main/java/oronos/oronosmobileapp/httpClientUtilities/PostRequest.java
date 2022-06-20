package oronos.oronosmobileapp.httpClientUtilities;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

public class PostRequest extends HttpClient<String> {
    private JSONObject jsonObjectSent = new JSONObject();

    public PostRequest() { super(); }


    @Override
    public String doInBackground(String... params) {
        int result = 0;
        try {
            URL server = new URL(params[0]);
            setConnection(server, "POST");
            buildJsonPost(params);
            postData();
            result = urlConnection.getResponseCode();
        } catch (Exception e) {
            FLog.e(Tag.HTTP, "Failed to send or to retrieve the data with the POST : " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return Integer.toString(result);
    }

    private void postData() {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(String.valueOf(jsonObjectSent));
            writer.flush();
            writer.close();
            out.close();
        } catch (IOException e) {
            FLog.e(Tag.HTTP, "Failed sending data with the POST");
        }

    }

    /*
    *  buildJsonPost : Builds the JSON object that will be sent to the server
    *  params[i] : name of the field ex : username
    *  params[i+1] : data of the field ex : toto
    *  params[i+2] : name of the field ex : password
    *  params[i+3] : data of the field ex : toto987
    *  ETC.
    * */
    private JSONObject buildJsonPost(String... params) {
        //List<String> nameValuePairs;
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 1; i < params.length; i+=2) {
                jsonObjectSent.put(params[i], params[i+1]);
                /*nameValuePairs = new ArrayList<>();// PAS FORCÉMENT UTILE, MAIS POURRAIS L'ÊTRE DANS LE FUTUR
                nameValuePairs.add(jsonObject.toString());// PAS FORCÉMENT UTILE, MAIS POURRAIS L'ÊTRE DANS LE FUTUR*/
            }
        } catch (JSONException e) {
            FLog.e(Tag.HTTP, "Failed building the JSON object to POST");
        }
        return jsonObject;
    }
}

