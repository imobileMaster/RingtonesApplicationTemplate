package ringtones.codebhak;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import tayebjaaba.miringtones.R;

public class SplashActivity extends Activity{
	
	protected int seconds = 3;
	Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashpage);
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 1000);
	}
	
	private Runnable runnable = new Runnable() {
		public void run() {
			long currentMilliseconds = System.currentTimeMillis();
			seconds--;
			if (seconds > 0) {
				handler.postAtTime(this, currentMilliseconds);
				handler.postDelayed(runnable, 1000);
			} else {
				Intent it = new Intent(SplashActivity.this,
						Main.class);
				startActivity(it);
				handler.removeCallbacks(runnable);
				finish();
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			handler.removeCallbacks(runnable);
			Intent it = new Intent(SplashActivity.this,
					Main.class);
			startActivity(it);
			finish();
		}
		return true;
	}
}
