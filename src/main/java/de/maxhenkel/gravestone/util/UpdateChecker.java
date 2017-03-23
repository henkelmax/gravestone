package de.maxhenkel.gravestone.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import de.maxhenkel.gravestone.Log;

public class UpdateChecker {

	private IUpdateCheckResult checkResult;
	private int thisVersion;
	private String url;
	
	public UpdateChecker(IUpdateCheckResult uCheck, int thisVersion, String url){
		this.checkResult=uCheck;
		this.thisVersion=thisVersion;
		this.url=url;
	}
	
	public void start(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				UpdateResult result=checkUpdate(url);
				checkResult.onResult(result.isAvailable(), result.getUpdateURL());
			}
		}).start();
	}
	
	public UpdateResult checkUpdate(String url){
		String updateURL=new String();
		try{
			URL u= new URL(url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(u.openStream()));
			String versionString = reader.readLine();
			String urlStr=reader.readLine();
			
			if(urlStr!=null){
				updateURL=urlStr;
			}
			
			try{
				int ver=Integer.parseInt(versionString);
				
				if(ver>thisVersion){
					return new UpdateResult(true, updateURL);
				}else{
					return new UpdateResult(false, updateURL);
				}
			}catch(NumberFormatException e){
				Log.e("Failed to parse version ID: " +e.getMessage());
			}
			
		}catch(Exception e){
			Log.e("Failed to fetch update: " +e.getMessage());
		}
		
		return new UpdateResult(false, updateURL);
	}
	
	public class UpdateResult{
		private boolean available;
		private String updateURL;
		
		public UpdateResult(boolean available, String updateURL) {
			this.available = available;
			this.updateURL = updateURL;
		}
		
		public boolean isAvailable() {
			return available;
		}
		public String getUpdateURL() {
			return updateURL;
		}
	}
	
	public interface IUpdateCheckResult {
		public void onResult(boolean isAvailable, String updateURL);
	}
	
}
