package test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import xmlrpc.ClientService;

public class ServiceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			ClientService test = new ClientService();
			String name = "C:"+File.separator+"ApacheXMLRPC"+File.separator+"test.srt";
			System.out.print(name);
			test.saveUrl(name, "http://dl.opensubtitles.org/en/download/filead/1953240520.gz");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
