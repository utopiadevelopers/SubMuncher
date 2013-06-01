package xmlrpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ClientService {
	XmlRpcClientConfigImpl rpcConfig;
	XmlRpcClient rpcClient;
	boolean loggedIn = false;

	public ClientService() throws MalformedURLException {
		rpcConfig = new XmlRpcClientConfigImpl();		
		rpcConfig.setServerURL(new URL(Constants.endPointURL));
		rpcClient = new XmlRpcClient();
		rpcClient.setConfig(rpcConfig);	
	}
	
	public boolean isLoggedIn()
	{
		return loggedIn;
	}
	
	public Object[] search(List<Object> searchParams) throws XmlRpcException
	{
		Map<String,Object> result;
		
		System.out.println(searchParams.toString());
		result = (Map)rpcClient.execute("SearchSubtitles", searchParams);
		//System.out.println(((List<Object>)result.get("data")).toString());
		if(result.get("data").toString().equalsIgnoreCase("false"))
			return null;
		
		Object[] data = (Object[]) result.get("data");
		
	    return data;
	}
	
	public boolean login() throws XmlRpcException
	{
		List<String> params = new ArrayList<String>();
		params.add(Constants.userName);
		params.add(Constants.password);
		params.add(Constants.language);
		params.add(Constants.userAgent);
		
		Map<String,Object> result;
		result = (Map) rpcClient.execute("LogIn", params);
		Constants.token = result.get("token").toString();
		if(Constants.token !=null)
		{
			System.out.println(Constants.token);
			loggedIn = true;
			return loggedIn;
		}			
		else
			return loggedIn;
	}
	
	public void saveUrl(String filename, String urlString) throws MalformedURLException, IOException
    {
    	GZIPInputStream in = null;
    	FileOutputStream fout = null;
    	File file;
    	try
    	{
    		in = new GZIPInputStream(new URL(urlString).openStream());
    		
    		file = new File(filename);
    		// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
    		fout = new FileOutputStream(file);
    		
    		

    		byte data[] = new byte[1024];
    		int count;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
    		}
    	}
    	finally
    	{
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
    }
}
