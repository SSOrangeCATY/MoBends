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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConnectionHelper
{
    public static final int CONNECT_TIMEOUT_MS = 3000;
    public static final int READ_TIMEOUT_MS = 5000;

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
        HttpURLConnection connection = (HttpURLConnection) buildUri(url, params).toURL().openConnection();
        configureConnection(connection);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
        {
            return INSTANCE.gson.fromJson(reader, responseClass);
        }
    }

    private static URI buildUri(URL url, Map<String, String> params) throws URISyntaxException
    {
        String query = url.getQuery();
        String extraQuery = buildQuery(params);
        String combinedQuery = query == null || query.isEmpty() ? extraQuery : query + "&" + extraQuery;

        return new URI(
            url.getProtocol(),
            url.getUserInfo(),
            url.getHost(),
            url.getPort(),
            url.getPath(),
            combinedQuery == null || combinedQuery.isEmpty() ? null : combinedQuery,
            url.getRef()
        );
    }

    private static String buildQuery(Map<String, String> params)
    {
        StringBuilder query = new StringBuilder();

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

        return query.toString();
    }

    public static <T> T sendPostRequest(URL url, JsonObject body, Class<T> responseClass) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        configureConnection(connection);
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

        try (BufferedReader json = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
        {
            return INSTANCE.gson.fromJson(json, responseClass);
        }
    }

    public static void configureConnection(URLConnection connection)
    {
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
    }
}
