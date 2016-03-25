package ringtones.codebhak;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import ringtones.codebhak.direct.SongInfo;
import ringtones.codebhak.compute.ActionItem;
import ringtones.codebhak.compute.QuickAction;
import tayebjaaba.miringtones.R;

public class ListRingtonesAdapter extends ArrayAdapter<SongInfo> {

	private ArrayList<SongInfo> items;
	private Context context;
	private ArrayList<ViewHolder> listHolder = new ArrayList<ListRingtonesAdapter.ViewHolder>();
	private int curPosition = 0;
	private RingtonesSharedPreferences pref;
	private boolean inRingtones;
	
	static final String TAG = "LOG";

	private static final int DEFAULT_RINGTONE = 1;
	private static final int ASSIGN_TO_CONTACT = 2;
	private static final int DEFAULT_NOTIFICATION = 3;
	private static final int DEFAULT_ALARM = 4;
	private static final int DELETE_RINGTONE = 5;
	
	
	
	public static final String ALARM_PATH = "/media/audio/alarms/";
	public static final String ALARM_TYPE = "Alarm";
	public static final String NOTIFICATION_PATH = "/media/audio/notifications/";
	public static final String NOTIFICATION_TYPE = "Notification";
	public static final String RINGTONE_PATH = "/media/audio/ringtones/";
	public static final String RINGTONE_TYPE = "Ringtone";
	  

	public ListRingtonesAdapter(Context context, int viewResourceId,
			ArrayList<SongInfo> objects, boolean inRingtones) {
		super(context, viewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.items = objects;
		this.pref = new RingtonesSharedPreferences(context);
		this.inRingtones = inRingtones;
		if(Main.mp.isPlaying()){
			Main.mp.stop();
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ActionItem defRingtone = new ActionItem(DEFAULT_RINGTONE,
				"Default Ringtone", context.getResources().getDrawable(
						R.drawable.icon_ringtone));
		ActionItem assign = new ActionItem(ASSIGN_TO_CONTACT,
				"Contact Ringtone", context.getResources().getDrawable(
						R.drawable.icon_contact));
		ActionItem defNotifi = new ActionItem(DEFAULT_NOTIFICATION,
				"Default Notification", context.getResources().getDrawable(
						R.drawable.icon_notify));
		ActionItem defAlarm = new ActionItem(DEFAULT_ALARM, "Default Alarm",
				context.getResources().getDrawable(R.drawable.icon_alarm));
		ActionItem delRingtone = new ActionItem(DELETE_RINGTONE,
				"Delete Ringtone", context.getResources().getDrawable(
						R.drawable.icon_delete));

		final QuickAction mQuickAction = new QuickAction(context);

		mQuickAction.addActionItem(defRingtone);
		mQuickAction.addActionItem(assign);
		mQuickAction.addActionItem(defNotifi);
		mQuickAction.addActionItem(defAlarm);
		mQuickAction.addActionItem(delRingtone);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						switch (actionId) {
						case DEFAULT_RINGTONE:
							setDefaultRingtone(items.get(curPosition));
							Toast.makeText(context,
									"Ringtone set successfully",
									Toast.LENGTH_LONG).show();
							break;
						case ASSIGN_TO_CONTACT:
							Intent intent = new Intent(context,
									SelectContactActivity.class);
							intent.putExtra("position", curPosition);
							context.startActivity(intent);
							break;
						case DEFAULT_ALARM:
							setDefaultAlarm(items.get(curPosition));
							Toast.makeText(context,
									"Alarm set successfully",
									Toast.LENGTH_LONG).show();
							break;
						case DEFAULT_NOTIFICATION:
							setDefaultNotice(items.get(curPosition));
							Toast.makeText(context,
									"Notification set successfully",
									Toast.LENGTH_LONG).show();
							break;
						case DELETE_RINGTONE:
							deleteRingtone(items.get(curPosition));
							Toast.makeText(context,
									"Ringtone deleted from SD card",
									Toast.LENGTH_LONG).show();
							break;

						default:
							break;
						}
					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});

		View view = null;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = li.inflate(R.layout.listelement, null);
			final ViewHolder holder = new ViewHolder();
			holder.txtName = (TextView) view.findViewById(R.id.txtSongName);
			holder.btnFavorite = (ImageView) view.findViewById(R.id.btnFavorite);
			holder.btnPlayPause = (ImageView) view.findViewById(R.id.btnPlayPause);
			view.setTag(holder);
		} else {
			view = convertView;
			// holder = (ViewHolder) view.getTag();
		}

		final SongInfo item = items.get(position);
		if (item != null) {
			final ViewHolder holder = (ViewHolder) view.getTag();
			holder.txtName.setText(item.getName());
			if (item.isFavorite()) {
				holder.btnFavorite.setBackgroundResource(R.drawable.icon_favorite);
			} else {
				holder.btnFavorite.setBackgroundResource(R.drawable.icon_favorite_off);
			}

			if (!item.isPlaying()) {
				holder.btnPlayPause.setBackgroundResource(R.drawable.icon_play);
			} else {
				holder.btnPlayPause.setBackgroundResource(R.drawable.icon_pause);
			}

			holder.btnPlayPause.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if (Main.mp.isPlaying()) {
						Main.mp.stop();
					}
					
					for(int i = 0; i < items.size(); i++){
						if(items.get(i) != item)
							items.get(i).setPlaying(false);
					}
					for(int i = 0; i < listHolder.size(); i++){
						listHolder.get(i).btnPlayPause.setBackgroundResource(R.drawable.icon_play);
					}

					if (item.isPlaying()) {
						holder.btnPlayPause.setBackgroundResource(R.drawable.icon_play);
						item.setPlaying(false);
						items.get(position).setPlaying(false);
						if (Main.mp.isPlaying()) {
							Main.mp.stop();
						}

					} else {
						curPosition = position;
						playAudio(context, item.getAudioResource());
						
						holder.btnPlayPause.setBackgroundResource(R.drawable.icon_pause);
						item.setPlaying(true);
						items.get(position).setPlaying(true);
					}
					for (ViewHolder object : listHolder) {
						if (object != holder) {
							object.btnPlayPause.setBackgroundResource(R.drawable.icon_play);
						}
					}
				}
			});

			holder.btnFavorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (item.isFavorite()) {
						holder.btnFavorite
								.setBackgroundResource(R.drawable.icon_favorite_off);
						item.setFavorite(false);
						pref.setString(item.getFileName(), false);
						if (!inRingtones) {
							Intent broadcast = new Intent();
							broadcast.setAction("REMOVE_SONG");
							context.sendBroadcast(broadcast);
						}

					} else {
						holder.btnFavorite
								.setBackgroundResource(R.drawable.icon_favorite);
						item.setFavorite(true);
						pref.setString(item.getFileName(), true);
					}
				}
			});
			
			listHolder.add(holder);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mQuickAction.show(v);
					curPosition = position;
				}
			});
		}

		return view;
	}

	static class ViewHolder {
		private TextView txtName;
		private ImageView btnFavorite;
		private ImageView btnPlayPause;
	}

	private void playAudio(Context context, int id) {
		if (Main.mp.isPlaying()) {
			Main.mp.stop();
		}
		Main.mp = MediaPlayer.create(context, id);
		Main.mp.setOnCompletionListener(playCompletionListener);
		Main.mp.start();
		onRingtonePlay.onPlay();
	}

	private OnCompletionListener playCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			for(int i = 0; i < items.size(); i++){
				items.get(i).setPlaying(false);
			}
			for(int i = 0; i < listHolder.size(); i++){
				listHolder.get(i).btnPlayPause
				.setBackgroundResource(R.drawable.icon_play);
			}
		}
	};

	private void setRingtone(SongInfo info, boolean ringtone, boolean alarm,
			boolean music, boolean notification) {

		File dir = null;
		String what = null;
		if (ringtone) {
			what = "Ringtones";
		}else if(alarm){
			what = "alarms";
		}else if(notification){
			what = "notifications";
		}else{
			what = "Ringtones";
		}
		
		
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dir = new File(Environment.getExternalStorageDirectory(),what);
		} else {
			dir = context.getCacheDir();
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, info.getFileName());
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				InputStream inputStream = context.getResources()
						.openRawResource(info.getAudioResource());

				OutputStream outputStream = new FileOutputStream(file);

				byte[] buffer = new byte[1024];
				int length;

				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());

		String[] projection = new String[] { MediaStore.MediaColumns.DATA,
				MediaStore.Audio.Media.IS_RINGTONE,
				MediaStore.Audio.Media.IS_ALARM,
				MediaStore.Audio.Media.IS_MUSIC,
				MediaStore.Audio.Media.IS_NOTIFICATION

		};
		Cursor c = context.getContentResolver().query(uri,projection,MediaStore.MediaColumns.DATA + " = \"" + file.getAbsolutePath()+ "\"", null, null);
		
		String strRingtone = null, strAlarm = null, strNotifi = null, strMusic = null;
		while (c.moveToNext()) {
			strRingtone = c.getString(c
					.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE));
			strAlarm = c.getString(c
					.getColumnIndex(MediaStore.Audio.Media.IS_ALARM));
			strNotifi = c.getString(c
					.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION));
			strMusic = c.getString(c
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
		}

		if (ringtone) {
			if ((strAlarm != null) && (strAlarm.equals("1")))
				alarm = true;
			if ((strNotifi != null) && (strNotifi.equals("1")))
				notification = true;
			if ((strMusic != null) && (strMusic.equals("1")))
				music = true;

		} else if (notification) {
			if ((strAlarm != null) && (strAlarm.equals("1")))
				alarm = true;
			if ((strRingtone != null) && (strRingtone.equals("1")))
				ringtone = true;
			if ((strMusic != null) && (strMusic.equals("1")))
				music = true;

		} else if (alarm) {
			if ((strNotifi != null) && (strNotifi.equals("1")))
				notification = true;
			if ((strRingtone != null) && (strRingtone.equals("1")))
				ringtone = true;
			if ((strMusic != null) && (strMusic.equals("1")))
				music = true;

		} else if (music) {
			if ((strNotifi != null) && (strNotifi.equals("1")))
				notification = true;
			if ((strRingtone != null) && (strRingtone.equals("1")))
				ringtone = true;
			if ((strAlarm != null) && (strAlarm.equals("1")))
				alarm = true;
		}

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, info.getName());
		values.put(MediaStore.MediaColumns.SIZE, file.length());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		if (ringtone) {
			values.put(MediaStore.Audio.Media.IS_RINGTONE, ringtone);
		} else if (notification) {
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, notification);
		} else if (alarm) {
			values.put(MediaStore.Audio.Media.IS_ALARM, alarm);
		} else if (music) {
			values.put(MediaStore.Audio.Media.IS_MUSIC, music);
		}

		context.getContentResolver().delete(uri,MediaStore.MediaColumns.DATA + " = \"" + file.getAbsolutePath()	+ "\"", null);
		Uri newUri = context.getContentResolver().insert(uri, values);

		int type = RingtoneManager.TYPE_ALL;
		if (ringtone)
			type = RingtoneManager.TYPE_RINGTONE;
		if (alarm)
			type = RingtoneManager.TYPE_ALARM;
		if (notification)
			type = RingtoneManager.TYPE_NOTIFICATION;
		
		RingtoneManager.setActualDefaultRingtoneUri(context, type, newUri);
	}

	private void setDefaultRingtone(SongInfo info) {

		File dir = null;
		String what = "Ringtones";
		Uri newUri = null;
		ContentValues values = new ContentValues();
		boolean isRingTone = false;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dir = new File(Environment.getExternalStorageDirectory(),what);
		} else {
			dir = context.getCacheDir();
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, info.getFileName());
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				InputStream inputStream = context.getResources()
						.openRawResource(info.getAudioResource());

				OutputStream outputStream = new FileOutputStream(file);

				byte[] buffer = new byte[1024];
				int length;

				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

		String[] columns = { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media._ID, 
				MediaStore.Audio.Media.IS_RINGTONE
				};

		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Audio.Media.DATA+" = '"+file.getAbsolutePath()+"'",null, null);
		if (cursor!=null) {
			int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

			int ringtoneColumn = cursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE);
			while (cursor.moveToNext()) {
				String audioFilePath = cursor.getString(fileColumn);
				if (cursor.getString(ringtoneColumn)!=null && cursor.getString(ringtoneColumn).equals("1")) {
					Uri hasUri = MediaStore.Audio.Media.getContentUriForPath(audioFilePath);
					newUri = Uri.withAppendedPath(hasUri, cursor.getString(idColumn));
					isRingTone = true;
				}
			}
			cursor.close();
		}
		if (isRingTone) {
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
		}else{
			values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, info.getName());
			values.put(MediaStore.MediaColumns.SIZE, file.length());
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			newUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
		}
	}
	
	private void setDefaultAlarm(SongInfo info) {

		File dir = null;
		String what = "alarms";
		Uri newUri = null;
		ContentValues values = new ContentValues();
		boolean isRingTone = false;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dir = new File(Environment.getExternalStorageDirectory(),what);
		} else {
			dir = context.getCacheDir();
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, info.getFileName());
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				InputStream inputStream = context.getResources()
						.openRawResource(info.getAudioResource());

				OutputStream outputStream = new FileOutputStream(file);

				byte[] buffer = new byte[1024];
				int length;

				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

		String[] columns = { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media._ID, 
				MediaStore.Audio.Media.IS_ALARM
				};

		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Audio.Media.DATA+" = '"+file.getAbsolutePath()+"'",null, null);
		if (cursor!=null) {
			int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

			int ringtoneColumn = cursor.getColumnIndex(MediaStore.Audio.Media.IS_ALARM);
			while (cursor.moveToNext()) {
				String audioFilePath = cursor.getString(fileColumn);
				if (cursor.getString(ringtoneColumn)!=null && cursor.getString(ringtoneColumn).equals("1")) {
					Uri hasUri = MediaStore.Audio.Media.getContentUriForPath(audioFilePath);
					newUri = Uri.withAppendedPath(hasUri, cursor.getString(idColumn));
					isRingTone = true;
				}
			}
			cursor.close();
		}
		if (isRingTone) {
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);
		}else{
			values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, info.getName());
			values.put(MediaStore.MediaColumns.SIZE, file.length());
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.Audio.Media.IS_ALARM, true);
			newUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);
		}
	}
	
	private void setDefaultNotice(SongInfo info) {

		File dir = null;
		String what = "notifications";
		Uri newUri = null;
		ContentValues values = new ContentValues();
		boolean isRingTone = false;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dir = new File(Environment.getExternalStorageDirectory(),what);
		} else {
			dir = context.getCacheDir();
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, info.getFileName());
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				InputStream inputStream = context.getResources()
						.openRawResource(info.getAudioResource());

				OutputStream outputStream = new FileOutputStream(file);

				byte[] buffer = new byte[1024];
				int length;

				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

		String[] columns = { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media._ID, 
				MediaStore.Audio.Media.IS_NOTIFICATION
				};

		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Audio.Media.DATA+" = '"+file.getAbsolutePath()+"'",null, null);
		if (cursor!=null) {
			int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

			int ringtoneColumn = cursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION);
			while (cursor.moveToNext()) {
				String audioFilePath = cursor.getString(fileColumn);
				if (cursor.getString(ringtoneColumn)!=null && cursor.getString(ringtoneColumn).equals("1")) {
					Uri hasUri = MediaStore.Audio.Media.getContentUriForPath(audioFilePath);
					newUri = Uri.withAppendedPath(hasUri, cursor.getString(idColumn));
					isRingTone = true;
				}
			}
			cursor.close();
		}
		if (isRingTone) {
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);
		}else{
			values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, info.getName());
			values.put(MediaStore.MediaColumns.SIZE, file.length());
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
			newUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);
		}
	}
	
	private void deleteRingtone(SongInfo info) {
		File dir = null;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			dir = new File(Environment.getExternalStorageDirectory(),
					"Ringtones");
		} else {
			dir = context.getCacheDir();
		}

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		Log.d(TAG, "dir:"+dir.getPath());

		File file = new File(dir, info.getFileName());
		Log.d(TAG, "file name:"+info.getFileName());

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(file
				.getAbsolutePath());

		context.getContentResolver().delete(
				uri,
				MediaStore.MediaColumns.DATA + " = \"" + file.getAbsolutePath()
						+ "\"", null);
		if (file.exists()) {
			file.delete();
		}
	}
	
	OnRingtonePlay onRingtonePlay;
	/**
	 * @param onRingtonePlay the onRingtonePlay to set
	 */
	public void setOnRingtonePlay(OnRingtonePlay onRingtonePlay) {
		this.onRingtonePlay = onRingtonePlay;
	}
	interface OnRingtonePlay{
		public void onPlay();
	}
}

