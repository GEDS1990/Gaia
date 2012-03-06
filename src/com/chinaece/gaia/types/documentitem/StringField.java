package com.chinaece.gaia.types.documentitem;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class StringField extends ItemType{

	public StringField(JSONObject obj) throws JSONException {
		super(obj);
		if(obj.getString("type").equals("String"))
			type = "String";
		else
			throw new IllegalStateException("bad init String");
	}

	@Override
	public View getMappingInstance(Context context) throws JSONException {
		TextView text = new TextView(context);
		text.setText(displayValue);
		text.setTextColor(Color.BLACK);
		text.setTextSize(18);
		return text;
	}

	@Override
	public String getInstanceValue() {
		return null;
	}

}
