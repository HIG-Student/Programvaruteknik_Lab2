package se.hig.programvaruteknik.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

/**
 * Fetches data from source
 */
public class DataFetcher
{
    /**
     * Fetches data from an URL
     * 
     * @param url
     *            the address to the website
     * @return The data
     */
    public static String fetchDataFromURL(String url)
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

    /**
     * Fetches data from an URL
     * 
     * @param path
     *            the path to the file
     * @return The data
     */
    public static String fetchDataFromFile(String path)
    {
	try
	{
	    return String.join("\n\r", Files.readAllLines(new File(path).toPath()));
	}
	catch (IOException ex)
	{
	    throw new RuntimeException(ex);
	}
    }
}
