package project.richthofen911.callofdroidy.http;

/**
 * Created by admin on 05/06/15.
 */
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.lang.ref.WeakReference;
/*
        HttpParams params = httpclient.getParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setTcpNoDelay(params, true);
        params.setConnectionTimeout(httpParameters, 30000);
        params.setSoTimeout(httpParameters, 30000);
*/

public abstract class HTTPTask extends AsyncTask<HttpUriRequest, Void, Object> {
    private static final String TAG = "RestTask";

    private String taskName = "";
    private String cookie = "";
    private int responseStatusCode = 0;
    private AbstractHttpClient mClient;
    HttpResponse serverResponse;

    public HTTPTask() {
        this(new DefaultHttpClient());
        HttpParams params = mClient.getParams();
        HttpConnectionParams.setTcpNoDelay(params, true);
        mClient.setParams(params);
    }

    public HTTPTask(AbstractHttpClient client) {
        mClient = client;
    }

    public void setTaskName( String name) { taskName = name; }
    public String getTaskName(){ return taskName; }
    public String getCookie(){ return cookie;}
    public int getResponseStatusCode() { return responseStatusCode; }

    @Override
    protected Object doInBackground(HttpUriRequest... params)
    {
        try {
            HttpResponse serverResponse = mClient.execute(params[0]);
            responseStatusCode = serverResponse.getStatusLine().getStatusCode();
            String response = "";
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

    abstract public void actionOnPostExecute();

    @Override
    protected void onPostExecute(Object result) {
        if (getResponseStatusCode() == 200){
            actionOnPostExecute();
        }else {
            Log.e("HTTP request error", serverResponse.toString());
        }
    }
}
