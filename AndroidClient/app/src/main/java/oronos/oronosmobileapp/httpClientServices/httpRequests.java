package oronos.oronosmobileapp.httpClientServices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.pdf.PdfDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.httpClientUtilities.GetRequest;
import oronos.oronosmobileapp.login.LoginActivity;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

/**
 * Created by Moussa on 2018-03-20.
 */

public class httpRequests{
    private static Context mContext;
    public httpRequests(Context context){mContext=context;};
    public static class getConfigBasicRequest extends GetRequest<JSONObject> {
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            Integer responseCode = 0;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                try {
                    Configuration.rocketsConfig.socketStreamPort = Integer.toString(s.getInt("otherPort"));
                    Configuration.rocketsConfig.rocketDataLayoutName = s.getString("layout");
                    new getConfigRocketsRequest().execute(Configuration.getIpAddr()+ Configuration.urlConfigRockets);
                    Configuration.rocketsConfig.map.buildMap( s.getString("map"), mContext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                AlertDialog ad = new AlertDialog.Builder(mContext)
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Request Error");
                ad.setMessage("Error " + Integer.toString(responseCode) + "unauthorized request" );
                ad.setButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }

        }
        @Override
        protected JSONObject handleResponse(InputStreamReader rd) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(readBufferToString(rd).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObj;
        }
    }

    public static class getConfigRocketsRequest extends GetRequest<JSONObject>{
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            Integer responseCode = 0;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                FLog.i(Tag.HTTP, "Get rocket config request http succeed");
                Iterator<String> it = s.keys();
                Configuration.rocketsConfig.rocketLayoutFileNames = new ArrayList<>();
                while( it.hasNext()){
                    try {
                        Configuration.rocketsConfig.rocketLayoutFileNames.add(s.getString(it.next()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(Configuration.rocketsConfig.rocketLayoutFileNames.contains(Configuration.rocketsConfig.rocketDataLayoutName)){
                    new getRocketLayoutFileRequest().execute(Configuration.getIpAddr()+ Configuration.urlConfigRockets + "/" + Configuration.rocketsConfig.rocketDataLayoutName);
                    //new getConfigMapRequest().execute(Configuration.getIpAddr() + Configuration.urlConfigMap); //No need to request
                }
                else{
                    AlertDialog ad = new AlertDialog.Builder(mContext)
                            .create();
                    ad.setCancelable(false);
                    ad.setTitle("Request Error");
                    ad.setMessage("Error: Server does not contain " + Configuration.rocketsConfig.rocketDataLayoutName );
                    ad.setButton("ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                }
            }
            else{
                AlertDialog ad = new AlertDialog.Builder(mContext)
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Request Error");
                ad.setMessage("Error " + Integer.toString(responseCode) + "unauthorized request" );
                ad.setButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        }

        @Override
        protected JSONObject handleResponse(InputStreamReader rd) {
            try {
                return new JSONObject(readBufferToString(rd).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class getRocketLayoutFileRequest extends GetRequest<String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Integer responseCode = 0;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                FLog.i(Tag.HTTP, "Get rocket layout request http succeed");
                Configuration.rocketsConfig.xmlContentLayout = s.replaceAll("(\\r|\\n|\\t)", "").replaceAll("\\s+", " ").trim();
                LoginActivity activity = (LoginActivity) mContext;
                activity.navigateToHome();
            }
            else{
                AlertDialog ad = new AlertDialog.Builder(mContext)
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Request Error");
                ad.setMessage("Error " + Integer.toString(responseCode) + "unauthorized request" );
                ad.setButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        }

        @Override
        protected String handleResponse(InputStreamReader rd) {
            return readBufferToString(rd);
        }
    }


    public static class getConfigMapRequest extends GetRequest<JSONObject>{
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            Integer responseCode = 0;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                try {
                    FLog.i(Tag.HTTP, "Get config map request http succeed");
                    Configuration.rocketsConfig.map.MAP_NAME = s.getString("map");
                } catch (JSONException e) {
                    FLog.e(Tag.HTTP, "Get config map request http FAILED");
                }
            }
            else{
                AlertDialog ad = new AlertDialog.Builder(mContext)
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Request Error");
                ad.setMessage("Error " + Integer.toString(responseCode) + "unauthorized request" );
                ad.setButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        }

        @Override
        protected JSONObject handleResponse(InputStreamReader rd) {
            try {
                JSONObject result = new JSONObject(readBufferToString(rd).toString());
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public static class getConfigMiscFilesRequest extends GetRequest<JSONObject>{
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            Integer responseCode = 0;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                try {
                    Configuration.pdfFileNames = new ArrayList<>();
                    int nbFiles = s.getInt("nFiles");
                    for(int i = 1; i <= nbFiles; i++) {
                        Configuration.pdfFileNames.add(s.getString("file" + Integer.toString(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                AlertDialog ad = new AlertDialog.Builder(mContext)
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Request Error");
                ad.setMessage("Error " + Integer.toString(responseCode) + "unauthorized request" );
                ad.setButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        }

        @Override
        protected JSONObject handleResponse(InputStreamReader rd) {
            try {
                JSONObject result = new JSONObject(readBufferToString(rd).toString());
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
