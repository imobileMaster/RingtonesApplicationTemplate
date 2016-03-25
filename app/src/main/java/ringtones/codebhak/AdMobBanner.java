package ringtones.codebhak;

import java.lang.ref.WeakReference;

import tayebjaaba.miringtones.R;



import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdMobBanner {
	
	private static Activity activity;
	private static WeakReference<Activity> s_activity;
	private static AdView adView;
	private static InterstitialAd interstitial;
	
	public static void initBridge(Activity activity){
		
		AdMobBanner.s_activity = new WeakReference<Activity>(activity);	
		AdMobBanner.activity = activity;
	
		adView = (AdView) activity.findViewById(R.id.adView);

		AdMobBanner.initBanner();
		AdMobBanner.initInterstitial();	
	}
	
	public static void initBanner(){

		AdMobBanner.s_activity.get().runOnUiThread( new Runnable() {
            public void run() {

            	if(AdMobBanner.adView != null){
            		return;
            	}
            	
            	AdRequest adRequest = new AdRequest.Builder().build();
            	AdMobBanner.adView.loadAd( adRequest );	
            }
		});
	}
	
	public static void initInterstitial(){
	
		AdMobBanner.s_activity.get().runOnUiThread( new Runnable() {
            public void run() {

            	if(AdMobBanner.interstitial != null){
            		return;
            	}
            	
				AdRequest adRequest = new AdRequest.Builder()
									.build();
				
				AdMobBanner.interstitial = new InterstitialAd( AdMobBanner.activity );
				
				String interstitial_ID = activity.getString(R.string.interstitial_ad_unit_id);
				AdMobBanner.interstitial.setAdUnitId( interstitial_ID );
				AdMobBanner.interstitial.setAdListener(new AdListener() {
		            @Override
		            public void onAdLoaded() {
		            //	if(AdMobBanner.isScheduledForShow){
		            		AdMobBanner.showFullScreen();
		            //	}
		            }
		
		            @Override
		            public void onAdClosed() {
					    AdRequest adRequest = new AdRequest.Builder().build();
					    AdMobBanner.interstitial.loadAd(adRequest);
		            }
		            
		            @Override
		            public void onAdFailedToLoad(int errorCode) {
		            	// did fail
		            }
		        });
		
				AdMobBanner.interstitial.loadAd(adRequest);
        		
            }
        });
	}
	
	public static void showFullScreen(){
		
		if(AdMobBanner.interstitial != null){
			AdMobBanner.s_activity.get().runOnUiThread( new Runnable() {
				public void run() {
					if(AdMobBanner.interstitial.isLoaded()){
						AdMobBanner.interstitial.show();
					//	AdMobBanner.isScheduledForShow = false;
					}
					else{
					//	AdMobBanner.isScheduledForShow = true;
					}
				}
			});
 		}
	}
}
