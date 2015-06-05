package project.richthofen911.callofdroidy;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by admin on 05/06/15.
 */
public class HTTPBuilder {
    public static HttpPost simplePostBuilder(String url, BasicNameValuePair...params){
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> methodParams = new ArrayList<>();
        for(BasicNameValuePair param: params){
            methodParams.add(param);
        }
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(methodParams));
        }catch (Exception e ){
            Log.e("HTTPTask exception", e.toString());
        }
        return httpPost;
    }

    public static HttpGet simpleGetBuilder(String url){
        HttpGet httpGet = new HttpGet(url);
        return httpGet;
    }
}
