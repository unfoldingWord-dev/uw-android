package com.door43.tools.reporting;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Fechner on 11/23/15.
 */
public class GitHubClient {

    private static final String ENDPOINT_URL = "https://api.github.com/repos/unfoldingWord-dev/translationKeyboard";
    private static final String ISSUE_URL_TAG = "/issues";
    protected static final int TIMEOUT_SECONDS = 10;


    private static String run(String url, String header, IssueBody bodyObject) throws IOException {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(bodyObject, IssueBody.class));
        Request request = new Request.Builder()
                .header("Authorization", header)
                .post(body)
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String submitIssue(String authorization, IssueBody body){
        try {
            return run(ENDPOINT_URL + ISSUE_URL_TAG, authorization, body);
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
//    public interface GHClient {
//
//        @POST(ISSUE_URL_TAG)
//        JsonObject submitIssue(@Header("Authorization") String authorization, @Body IssueBody body);
//    }
//
//    protected static GHClient getRestBuilder(OkHttpClient optionClient){
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(ENDPOINT_URL)
//                .client(optionClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
////                .setLogLevel(RestAdapter.LogLevel.FULL);
//
//        GHClient client = retrofit.create(GHClient.class);
//        return client;
//    }
//
//    public static GHClient createClient(){
//
//        return getRestBuilder(getCustomClient(TIMEOUT_SECONDS));
//    }
//
////    protected static GHClient createClient(RestAdapter.Builder builder){
////        return builder.build().create(GHClient.class);
////    }
//
//    protected static OkHttpClient getCustomClient(int timeoutSeconds){
//
//        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(timeoutSeconds, TimeUnit.SECONDS);
//        client.setReadTimeout(timeoutSeconds, TimeUnit.SECONDS);
//        client.setWriteTimeout(timeoutSeconds, TimeUnit.SECONDS);
//        return client;
//    }
//
    public static class IssueBody implements Serializable{

        public String title;
        public String body;
        public String[] labels;

        public IssueBody(String title, String body, String[] labels) {
            this.title = title;
            this.body = body;
            this.labels = labels;
        }
    }
}
