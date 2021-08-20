package com.marco.javacovidstatus.services.implementations.downloaders;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marco.javacovidstatus.services.interfaces.downloaders.RegionMapDownloader;

/**
 * It retrieves the SVG from the national web site
 * 
 * @author Marco
 * @see <a href=
 *      "http://www.governo.it/it/articolo/domande-frequenti-sulle-misure-adottate-dal-governo/15638">Ministero
 *      della Salute</a>
 */
public class RegionMapDownloaderFromNationalWebSite implements RegionMapDownloader {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegionMapDownloaderFromNationalWebSite.class);

	private static final String URL_MAP = "http://www.governo.it/it/articolo/domande-frequenti-sulle-misure-adottate-dal-governo/15638";

	@Override
	public String createHtmlMap() {
		try {
			/*
			 * Get the SVG map from the government website
			 */
			Document doc = Jsoup.connect(URL_MAP).get();
			Elements contenitoreSvg = doc.select(".contenitore_svg");

			/*
			 * Do some cleaning and make the style like I want it
			 */
			parseChildNodes(contenitoreSvg);
			Element svg = contenitoreSvg.get(0).child(0);
			svg.removeAttr("height");
			svg.attr("width", "100%");

			return contenitoreSvg.html();
		} catch (IOException e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			LOGGER.error(e.getMessage());
		}
		return "";
	}

	/**
	 * Here I do some cleaning as the original HTML contains more info than I need
	 * 
	 * @param elements
	 */
	private void parseChildNodes(Elements elements) {
		elements.forEach(e -> {
			if (e.childrenSize() > 0) {
				parseChildNodes(e.children());
			}

			/*
			 * I don't need their "onclick" event handler, and I want to add a tooltip with
			 * the name of the region
			 */
			e.removeAttr("onclick");
			String id = e.attr("id").trim();
			if (!id.isBlank()) {
				e.attr("data-toggle", "tooltip");
				e.attr("data-placement", "top");
				e.attr("title", id.substring(0, 1).toUpperCase() + id.substring(1).toLowerCase());
				e.addClass("black-tooltip");
			}
		});
	}

}
