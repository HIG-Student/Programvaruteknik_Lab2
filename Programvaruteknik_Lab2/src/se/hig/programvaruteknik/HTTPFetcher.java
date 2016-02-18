package se.hig.programvaruteknik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fetches data from a HTTP site
 */
public class HTTPFetcher
{
    /**
     * Fetches data from an URL
     * 
     * @param url
     *            the address to the website
     * @return The data
     */
    public static String fetchData(String url)
    {
	try
	{
	    URL websiteURL = new URL(url);

	    StringBuilder builder = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(websiteURL.openStream())))
	    {
		do
		{
		    builder.append(br.readLine());
		}
		while (br.ready());

		return builder.toString();
	    }
	    catch (IOException ex)
	    {
		throw new RuntimeException(ex);
	    }
	}
	catch (MalformedURLException ex)
	{
	    throw new RuntimeException(ex);
	}
    }
}
