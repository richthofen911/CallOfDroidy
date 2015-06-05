package project.richthofen911.callofdroidy;

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

public class HTTPTask extends AsyncTask<HttpUriRequest, Void, Object> {
    private static final String TAG = "RestTask";

    private String taskName = "";
    private String cookie = "";
    private int responseStatusCode = 0;
    private AbstractHttpClient mClient;
    private WeakReference<ResponseCallback> mCallback;

    public interface ResponseCallback {
        void onRequestSuccess(String response);
        void onRequestError(Exception error);
    }

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


    public void setResponseCallback(ResponseCallback callback) {
        mCallback = new WeakReference<>(callback);
    }

    @Override
    protected Object doInBackground(HttpUriRequest... params)
    {
        try {
            HttpUriRequest request = params[0];
            HttpResponse serverResponse = mClient.execute(request);
            BasicResponseHandler handler = new BasicResponseHandler();

            responseStatusCode = serverResponse.getStatusLine().getStatusCode();

            if ( responseStatusCode == 200 ) {
                Log.e(TAG, "doInBackground: code 200 OK");
            }else{
                Log.e(TAG, "doInBackground: HTTP response code: " + responseStatusCode);
                Log.e(TAG, "doInBackground: HTTP response: " + serverResponse);
            }

            String response = "";
            if ( taskName.equals("")) {
                response = handler.handleResponse(serverResponse);
            }

            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if ( mCallback != null && mCallback.get() != null ) {
            final ResponseCallback callback = mCallback.get();
            if (result instanceof String) {
                callback.onRequestSuccess((String) result);
            }
            else if (result instanceof Exception) {
                callback.onRequestError((Exception) result);
            }
            else {
                callback.onRequestError(new IOException(
                        "Unknown Error Contacting Host") );
            }
        }
    }
}
