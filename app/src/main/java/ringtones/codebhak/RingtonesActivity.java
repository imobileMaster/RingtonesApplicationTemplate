package ringtones.codebhak;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import ringtones.codebhak.ListRingtonesAdapter.OnRingtonePlay;
import ringtones.codebhak.direct.SongInfo;
import ringtones.codebhak.start.Util;
import tayebjaaba.miringtones.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class RingtonesActivity extends Activity {

	private ListView listView;
	private ListRingtonesAdapter adapter;
	private ArrayList<SongInfo> listSong = new ArrayList<SongInfo>();
	private Util util = new Util();
	private ProgressBar progressBarParent;
	private LinearLayout linearLayout_contentProgress;
	
	private static final int PREFERENCES = Menu.FIRST;
	private static final int QUIT = Menu.FIRST + 1;
	
	private static int numberOfRingPlayed = 0;
	
	static InterstitialAd mInterstitialAd;

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
	    
	    /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5586353482678665/4380869430");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            //    beginPlayingGame();
            }
        });

        requestNewInterstitial();*/
	    
	    AdMobBanner.initBridge(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshList();
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
	
	private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice("3CCA38430872559BCB7448C35C6D5718").build();

        mInterstitialAd.loadAd(adRequest);
    }

	public void showFullScreen(){
	
		if(mInterstitialAd != null){
			runOnUiThread( new Runnable() {
				public void run() {
					if(mInterstitialAd.isLoaded()){
						mInterstitialAd.show();
					//	PTAdAdMobBridge.isScheduledForShow = false;
					}
					else{
					//	PTAdAdMobBridge.isScheduledForShow = true;
					}
				}
			});
 		}
	}
	
	private void showBanner() {
		showFullScreen();
		
		runOnUiThread( new Runnable() {
            public void run() {
            	final AdView mAdView = (AdView) findViewById(R.id.adView);
    	        AdRequest adRequest = new AdRequest
    	        						.Builder()
    	        						.addTestDevice("3CCA38430872559BCB7448C35C6D5718").build();
    	        mAdView.loadAd(adRequest);
    	        mAdView.setVisibility( View.VISIBLE );
    	        mAdView.setAdListener(new AdListener()
    	        {
    	            public void onAdLoaded()
    	            {
    	                Log.i("Ads", "onAdLoaded");
    	                mAdView.bringToFront();
    	            }
    	        });
            }
          });
		 
	}
	
	private void refreshList(){
		
		listSong = util.getAllSong(this);

		adapter = new ListRingtonesAdapter(this, R.layout.listelement, listSong,false);
		adapter.setOnRingtonePlay(new OnRingtonePlay() {
			
			@Override
			public void onPlay() {
				AdMobBanner.showFullScreen();
			//	showBanner();
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
}