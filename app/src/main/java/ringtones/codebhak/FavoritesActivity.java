package ringtones.codebhak;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ringtones.codebhak.ListRingtonesAdapter.OnRingtonePlay;
import ringtones.codebhak.direct.SongInfo;
import ringtones.codebhak.start.Util;
import tayebjaaba.miringtones.R;

public class FavoritesActivity extends Activity {

	private ListView listView;
	private ListRingtonesAdapter adapter;
	private ArrayList<SongInfo> listSong = new ArrayList<SongInfo>();
	private Util util = new Util();
	private IntentFilter intentFilter;
	
	private ProgressBar progressBarParent;
	private LinearLayout linearLayout_contentProgress;
	
	private static final int PREFERENCES = Menu.FIRST;
	private static final int QUIT = Menu.FIRST + 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ringtonelist);
		listView = (ListView) findViewById(R.id.list);
		progressBarParent = (ProgressBar)findViewById(R.id.progressBarParent);
		Resources res = getResources();
		progressBarParent.setProgressDrawable(res.getDrawable(R.drawable.progressbarstyle));		
		linearLayout_contentProgress = (LinearLayout)findViewById(R.id.LL_contentProgressBarParent);
		refreshList();
		
		intentFilter = new IntentFilter();
        intentFilter.addAction("REMOVE_SONG");
        registerReceiver(intentReceiver, intentFilter);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshList();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(intentReceiver);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, QUIT, 0, "Quit").setIcon(R.drawable.icon_delete);
		return true;	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case QUIT:
			finish();
			System.exit(0);
			break;
		}
		return true;
	}
	
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			refreshList();
		}
	};
	
	private void refreshList(){
		listSong = util.getAllSong(this);
		
		for (int i = 0; i < listSong.size(); i++) {
			if (!listSong.get(i).isFavorite()) {
				listSong.remove(i);
				i--;
			}
		}

		adapter = new ListRingtonesAdapter(this, R.layout.listelement, listSong,false);
		
		adapter.setOnRingtonePlay(new OnRingtonePlay() {
			
			@Override
			public void onPlay() {
				// TODO Auto-generated method stub
				VISIBLELAYOUT();
				createProgressParentThread();
			}
		});
		
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	Runnable _progressUpdater;
	private void createProgressParentThread() {

	    _progressUpdater = new Runnable() {
	        @Override
	        public void run() {
	            //Exitting is set on destroy
	                while(Main.mp.isPlaying()) {
	                    try
	                    {
	                        int current = 0;
	                        int total = Main.mp.getDuration();
	                        progressBarParent.setMax(total);
	                        Log.d("ThangTB", "total:"+total);
	                        progressBarParent.setIndeterminate(false);

	                        while(Main.mp!=null && Main.mp.isPlaying() && current<total){
	                            try {
	                                Thread.sleep(200); //Update once per second
	                                current = Main.mp.getCurrentPosition();
	                                 //Removing this line, the track plays normally.
	                                progressBarParent.setProgress(current); 
	                            } catch (InterruptedException e) {

	                            } catch (Exception e){

	                            }            
	                        }
	                    }
	                    catch(Exception e)
	                    {
	                        //Don't want this thread to intefere with the rest of the app.
	                    }
	                }
	                if (!Main.mp.isPlaying()) {
	                	try {
	                		Log.d("ThangTB", "callllllllllllllllll");
	                		GONELAYOUT();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
	        }
	    };
	    Thread thread = new Thread(_progressUpdater);
	    thread.start();
	}
	public void GONELAYOUT(){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressBarParent.setProgress(0);
				linearLayout_contentProgress.setVisibility(View.GONE);
			}
		});
	}
	public void VISIBLELAYOUT(){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressBarParent.setProgress(0);
				linearLayout_contentProgress.setVisibility(View.VISIBLE);
			}
		});
	}
	public static void fixBackgroundRepeat(View view) {
	      Drawable bg = view.getBackground();
	      if(bg != null) {
	           if(bg instanceof BitmapDrawable) {
	                BitmapDrawable bmp = (BitmapDrawable) bg;
	                bmp.mutate(); // make sure that we aren't sharing state anymore
	                bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	           }
	      }
	 }
}
