package oronos.oronosmobileapp.httpClientUtilities;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

public abstract class GetRequest<Z> extends HttpClient<Z> {

    public GetRequest() { super(); }
    protected abstract Z handleResponse(InputStreamReader rd);
    @Override
    public Z doInBackground(String... params) {
        Z result = null;
        try {
            URL server = new URL(params[0]);
            setConnection(server, "GET");
            result = handleResponse(retrieveStreamReader());
        } catch (IOException e) {
            FLog.e(Tag.HTTP, "get request fail : " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

}

