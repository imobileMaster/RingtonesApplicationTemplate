package ringtones.codebhak;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RingtonesSharedPreferences {
	
	private SharedPreferences prefs;
	
	private static final String NAME = "ringtones_pref";

	public RingtonesSharedPreferences(Context context) {
		prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
	}
	
	public boolean getString(String key) {
		return prefs.getBoolean(key, false);
	}
	
	public void setString(String key, boolean value) {
		Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
