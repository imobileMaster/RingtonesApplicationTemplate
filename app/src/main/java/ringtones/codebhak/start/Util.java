package ringtones.codebhak.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import tayebjaaba.miringtones.R;
import ringtones.codebhak.RingtonesSharedPreferences;
import ringtones.codebhak.direct.Contact;
import ringtones.codebhak.direct.SongInfo;

public class Util {

	public ArrayList<Contact> getAllContact(Context context) {

		ArrayList<Contact> contacts = new ArrayList<Contact>();
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);

		 if(cursor != null) {
	            while (cursor.moveToNext()) {  
	                // This would allow you get several email addresses  
	                // if the email addresses were stored in an array  
	                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
	                String aaaa = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
	                
	                if (phone!=null && phone.equals("1")) {
	                	 Contact contact = new Contact();
	 	                contact.setId(Integer.parseInt(id));
	 					contact.setName(name);
	 					contacts.add(contact);
					}
	                
	            }
	        }  
		 cursor.close();
		return contacts;

	}

	public ArrayList<SongInfo> getAllSong(Context context) {

		ArrayList<SongInfo> listSong = new ArrayList<SongInfo>();

		RingtonesSharedPreferences pref = new RingtonesSharedPreferences(
				context);

		Field[] fields = R.raw.class.getFields();

		for (int i = 0; i < fields.length - 1; i++) {
			SongInfo info = new SongInfo();
			try {
				String name = fields[i].getName();
				
				if (!name.equals("ringtones")) {
					
					info.setFileName(name + ".mp3");
					info.setFavorite(pref.getString(info.getFileName()));
					int audioResource = R.raw.class.getField(name).getInt(name);
					info.setAudioResource(audioResource);
				}
				// info.setName(name);
			} catch (Exception e) {
				// TODO: handle exception
//				Log.e("LOG", "Error: " + e.getMessage());
			}
			listSong.add(info);
		}

		InputStream inputStream = context.getResources().openRawResource(
				R.raw.zeallist);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));

		try {
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				listSong.get(i).setName(line);
				i++;
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {

			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return listSong;
	}

	
	public void assignRingtoneToContact(Context context, SongInfo info,Contact contact) {
		File dir =null;
		ContentValues values = new ContentValues();
		boolean isRingTone = false;
		
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dir = new File(Environment.getExternalStorageDirectory(),
					"Ringtones");
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
		
			String[] columns = { MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media._ID, 
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DISPLAY_NAME,
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
						Uri fullUri = Uri.withAppendedPath(hasUri, cursor.getString(idColumn));
						isRingTone = true;
						values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, fullUri.toString());
					}
				}
				cursor.close();
				
				 if(!isRingTone){
					 context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

					 Uri oldUri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
					 ContentValues Newvalues = new ContentValues();
					 Uri newUri;
					 String uriString;
						context.getContentResolver().delete(oldUri, MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"", null);
						Newvalues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
						Newvalues.put(MediaStore.MediaColumns.TITLE, info.getName());
						Newvalues.put(MediaStore.MediaColumns.SIZE, file.length());
						Newvalues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
						Newvalues.put(MediaStore.Audio.Media.IS_RINGTONE, true);

						Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
				        newUri = context.getContentResolver().insert(uri, Newvalues);
				        uriString = newUri.toString();
					values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString);
					Log.i("LOG", "uriString: " + uriString);
				 }
				
			}
			
		int count = context.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, values,ContactsContract.Contacts._ID +" = "+contact.getId(), null);
//		Log.i("LOG", "Update: " + count);
		
	}
	
	@SuppressWarnings("deprecation")
	public Uri getContactContentUri() {
		if(Build.VERSION.SDK_INT >= 5){
			return ContactsContract.Contacts.CONTENT_URI;
		}
		else{
			return Contacts.People.CONTENT_URI;
		}
	}

}
