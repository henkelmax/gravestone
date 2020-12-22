package de.maxhenkel.gravestone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateChecker {

	private IUpdateCheckResult uCheck;
	private int thisVersion;
	private String url;
	
	public UpdateChecker(IUpdateCheckResult uCheck, int thisVersion, String url){
		this.uCheck=uCheck;
		this.thisVersion=thisVersion;
		this.url=url;
	}
	
	public void start(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean available=isUpdateAvailable(thisVersion, url);
				uCheck.onResult(available);
			}
		}).start();
	}
	
	public int getNewestVersionInt(String url) throws IOException, NumberFormatException{
		URL u= new URL(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(u.openStream()));
		String versionString = reader.readLine();
		reader.close();
		return Integer.parseInt(versionString);
	}
	
	public boolean isUpdateAvailable(int version, String url){
		int newestVersion;
		
		try {
			newestVersion = getNewestVersionInt(url);
		} catch (NumberFormatException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		if(version>=newestVersion){
			return false;
		}else{
			return true;
		}
		
	}
	
	public interface IUpdateCheckResult {

		public void onResult(boolean isAvailable);
		
	}

	
}
