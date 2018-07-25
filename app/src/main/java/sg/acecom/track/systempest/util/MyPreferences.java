package sg.acecom.track.systempest.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MyPreferences {

	private Activity activity;
	
	public MyPreferences(Activity act) {
		this.activity = act;
	}
	
	public void savePreferences(String key, String value) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
    	Editor editor = sharedPreferences.edit();
    	editor.putString(key, value);
    	editor.commit();
    }

	public void savePreferencesBoolean(String key, boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public String getPreferences(String key, String default_value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
		String s = sharedPreferences.getString(key, default_value);
		
		return s;
	}

	public boolean getPreferencesBoolean(String key, boolean default_value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
		boolean s = sharedPreferences.getBoolean(key, default_value);

		return s;
	}
    
    public void removePreferences(String key) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
    	Editor editor = sharedPreferences.edit();
    	editor.remove(key);
    	editor.commit();
    }
}
