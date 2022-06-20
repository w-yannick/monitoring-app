package oronos.oronosmobileapp.settings;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

// TODO: Change the serverTest, header1, header2 and header3 when server is working
public class Configuration {
    volatile static public String username = "";
    static public final String urlLogin = "/users/login";
    static public final String urlLogout = "/users/logout";
    static public final String urlConfigBasic = "/config/basic";
    static public final String urlConfigRockets = "/config/rockets";
    static public final String urlConfigMap = "/config/map";
    static public final String urlConfigMiscFiles = "/config/miscFiles";
    static public String ip_addr;
    static public String httpPort = "5001";
    static public final int timeout = 3000; // 3000 milliseconds
    static public final int SOCKET_TIMEOUT = 2000; // 2000 milliseconds
    public static int udpPacketLength = 65508;
    public static int nbLastSavingData = 10;
    public static int nbLogsDisplayLogWidget = 5000;

    public static final String PREFS_NAME = "prefs";
    public static final String PREF_DARK_THEME = "dark_theme";
    public static boolean darkTheme = false;

    volatile static public List<String> pdfFileNames;

    static public String getIpAddr() {
        return "http://" + ip_addr + ":" + httpPort;
    }

    static public class rocketsConfig {
        volatile static public String socketStreamPort;
        volatile static public String rocketDataLayoutName;

        static public class map {
            volatile static public float SERVER_LONG;
            volatile static public float SERVER_LAT;
            volatile static public float DEFAULT_ZOOM_LEVEL = 21;
            volatile static public String MAP_NAME;
            private static final String map_file_location = "Maps/maps.json";
            volatile public static LatLng ServerPosition;

            public static void buildMap(String map, Context context) {
                MAP_NAME = map;
                AssetManager manager = context.getAssets();
                InputStream file;
                try {
                    file = manager.open(map_file_location);
                    byte[] formArray = new byte[file.available()];
                    file.read(formArray);
                    file.close();
                    String jsonString = new String(formArray, "UTF-8");
                    JSONObject jsonFile = new JSONObject(jsonString);
                    JSONObject mapData = jsonFile.getJSONObject(MAP_NAME);
                    SERVER_LAT = (float) mapData.getDouble("LAT");
                    SERVER_LONG = (float) mapData.getDouble("LONG");
                    ServerPosition = new LatLng(Configuration.rocketsConfig.map.SERVER_LAT, Configuration.rocketsConfig.map.SERVER_LONG);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        volatile static public List<String> rocketLayoutFileNames;
        volatile static public String xmlContentLayout;
    }
}
