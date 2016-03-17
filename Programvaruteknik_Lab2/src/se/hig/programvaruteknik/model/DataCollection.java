package se.hig.programvaruteknik.model;

import java.util.Map;

/**
 * A collection of data
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class DataCollection
{
    private Map<String, MatchedDataPair> data;
    private String title;
    private String xUnit;
    private String yUnit;

    private String xSourceName;
    private String xSourceLink;

    private String ySourceLink;
    private String ySourceName;

    /**
     * Set the name of the source of source x
     * 
     * @param xSourceName
     *            The name or null
     */
    public void setXSourceName(String xSourceName)
    {
	this.xSourceName = xSourceName;
    }

    /**
     * Get the name of the source of source x
     * 
     * @return The name or null
     */
    public String getXSourceName()
    {
	return xSourceName;
    }

    /**
     * Set the link of the source of source x
     * 
     * @param xSourceLink
     *            The link or null
     */
    public void setXSourceLink(String xSourceLink)
    {
	this.xSourceLink = xSourceLink;
    }

    /**
     * Get the link of the source of source x
     * 
     * @return The link or null
     */
    public String getXSourceLink()
    {
	return xSourceLink;
    }

    /**
     * Get the name of the source of source y
     * 
     * @return The name or null
     */
    public String getYSourceName()
    {
	return ySourceName;
    }

    /**
     * Get the link of the source of source y
     * 
     * @return The link or null
     */
    public String getYSourceLink()
    {
	return ySourceLink;
    }

    /**
     * Populates a collection of data
     * 
     * @param title
     *            The title for this collection
     * @param sourceX
     *            The data-source for the x values
     * @param sourceY
     *            The data-source for the y values
     * @param data
     *            The data to put in this collection
     */
    public DataCollection(String title, DataSource sourceX, DataSource sourceY, Map<String, MatchedDataPair> data)
    {
	this.data = data;
	this.title = title;

	xUnit = sourceX.getUnit();
	yUnit = sourceY.getUnit();

	xSourceName = sourceX.getSourceName();
	xSourceLink = sourceX.getSourceLink();

	ySourceName = sourceY.getSourceName();
	ySourceLink = sourceY.getSourceLink();
    }

    /**
     * The title
     * 
     * @return The title
     */
    public String getTitle()
    {
	return title;
    }

    /**
     * Get the unit for the x values
     * 
     * @return The unit for the x values
     */
    public String getXUnit()
    {
	return xUnit;
    }

    /**
     * Get the unit for the y values
     * 
     * @return The unit for the y values
     */
    public String getYUnit()
    {
	return yUnit;
    }

    /**
     * Get the data in this collection
     * 
     * @return The data in this collection
     */
    public Map<String, MatchedDataPair> getData()
    {
	return data;
    }

    @Override
    public String toString()
    {
	return "[DataCollection: " + title + "]";
    }
}
