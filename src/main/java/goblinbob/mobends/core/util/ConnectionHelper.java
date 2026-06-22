package goblinbob.mobends.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import goblinbob.mobends.core.asset.AssetLocation;
import goblinbob.mobends.core.supporters.BindPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConnectionHelper
{
    public static ConnectionHelper INSTANCE = new ConnectionHelper();
    private Gson gson;

    /**
     * Makes it so we can't instantiate this class.
     */
    private ConnectionHelper()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(Color.class, new ColorAdapter());
        builder.registerTypeAdapter(BindPoint.class, new BindPoint.Adapter());
        builder.registerTypeAdapter(AssetLocation.class, new AssetLocation.Adapter());
        this.gson = builder.create();
    }

    public Gson getGson()
    {
        return gson;
    }

    public static <T> T sendGetRequest(URL url, Map<String, String> params, Class<T> responseClass) throws IOException, URISyntaxException
    {
        URL requestUrl = buildGetUrl(url, params);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader json = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
        {
            return INSTANCE.gson.fromJson(json, responseClass);
        }
        finally
        {
            connection.disconnect();
        }
    }

    private static URL buildGetUrl(URL url, Map<String, String> params) throws IOException, URISyntaxException
    {
        if (params.isEmpty())
        {
            return url;
        }

        StringBuilder query = new StringBuilder(url.toURI().getRawQuery() == null ? "" : url.toURI().getRawQuery());
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (query.length() > 0)
            {
                query.append('&');
            }
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            query.append('=');
            query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath() + "?" + query);
    }

    public static <T> T sendPostRequest(URL url, JsonObject body, Class<T> responseClass) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        byte[] out = (new Gson()).toJson(body).getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.connect();

        try (OutputStream os = connection.getOutputStream())
        {
            os.write(out);
        }

        // Response
        BufferedReader json = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        T response = INSTANCE.gson.fromJson(json, responseClass);

        return response;
    }
}
