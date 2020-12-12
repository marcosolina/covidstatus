package com.marco.javacovidstatus.services.interfaces;

/**
 * The interface provides a way to retrieve the map of Italy with the color of
 * every region
 * 
 * @author Marco
 *
 */
public interface RegionMapDownloader {

	/**
	 * It returns the SVG map
	 * @return
	 */
	public String createHtmlMap();
}
