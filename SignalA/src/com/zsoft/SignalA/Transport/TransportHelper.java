package com.zsoft.SignalA.Transport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zsoft.SignalA.Connection;

public class TransportHelper {
    private static final String TAG = "TransportHelper";

    public static ProcessResult ProcessResponse(Connection connection, JSONObject response)
    {
    	ProcessResult result = new ProcessResult();
    	
        if (connection == null)
        {
            //throw new Exception("connection");
        }

        if (response == null)
        {
            return result;
        }

    	String newMessageId = null;
    	JSONArray messagesArray = null;
    	String groupsToken = null;
    	JSONObject transportData = null;
    	JSONObject info = null;

		result.timedOut = response.optInt("T") == 1;	
		result.disconnected = response.optInt("D") == 1;
		newMessageId = response.optString("C");
		messagesArray = response.optJSONArray("M");
		groupsToken = response.optString("G");
		info = response.optJSONObject("I");

		if(info != null)
		{
			
			// ToDo
			//connection.OnReceive(response);
			return result;
		}
		
		if(result.disconnected)
		{
			return result;
		}			
		
		if(groupsToken!=null)
		{
			connection.setGroupsToken(groupsToken);
		}

        if (messagesArray != null)
        {
			for (int i = 0; i < messagesArray.length(); i++) {
				//JSONObject m = null;
				try {
					String m = messagesArray.getString(i); //.getJSONObject(i);
					connection.OnMessage(m.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

            connection.setMessageId(newMessageId);
        }
        
        return result;
    }

    
    
	public static String GetReceiveQueryString(Connection connection, String data, String transport)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("connection");
        }
        if (transport == null)
        {
            throw new IllegalArgumentException("transport");
        }

    	
        // ?transport={0}&connectionToken={1}&messageId={2}&groupsToken={3}&connectionData={4}{5}
		String qs = "?transport=";
		qs += transport;
		try {
			qs += "&connectionToken=" + URLEncoder.encode(connection.getConnectionToken(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Unsupported message encoding error, when encoding connectionToken.");
		}
		if(connection.getMessageId()!=null)
		{
			try {
				qs += "&messageId=" + URLEncoder.encode(connection.getMessageId(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Unsupported message encoding error, when encoding messageid.");
			}
		}

        if (connection.getGroupsToken() != null)
        {
            try {
				qs += "&groupsToken=" + URLEncoder.encode(connection.getGroupsToken(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Unsupported message encoding error, when encoding groupsToken.");
			}
        }

        if (data != null)
        {
            qs += "&connectionData=" + data;
        }

        return qs;
    }

	
	public static ArrayList<String> ToArrayList(JSONArray jsonArray)
	{
		ArrayList<String> list = null;
		if (jsonArray != null) { 
			int len = jsonArray.length();
			list = new ArrayList<String>(len);     
			for (int i=0;i<len;i++){ 
				Object o = jsonArray.opt(i);
				if(o!=null)
					list.add(o.toString());
			} 
		}
		
		return list;
	}

}
