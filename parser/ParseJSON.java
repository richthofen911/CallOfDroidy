package project.richthofen911.callofdroidy.Parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by admin on 05/06/15.
 */
public class ParseJSON {

    public static JSONObject stringToJSONObject(String input){
        input = input.replace("\\", "");
        try{
            return new JSONObject(input);
        }catch (JSONException e){
            Log.e("JSON parser error", e.toString());
            return  null;
        }
    }

    public static ArrayList<JSONObject> splitJSONArray(String input){
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        input = input.replace("\\", "");
        try {
            JSONArray jsonArray = new JSONArray(input);
            for(int i = 0; i < jsonArray.length(); i++){
                jsonObjects.add(jsonArray.getJSONObject(i));
            }
            return jsonObjects;
        }catch (JSONException e ){
            e.printStackTrace();
            return null;
        }
    }
}
