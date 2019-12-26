package org.javamyadmin.helpers.html;

import static org.javamyadmin.php.Php.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Generator {

    /**
     * Returns an HTML IMG tag for a particular image from a theme
     *
     * The image name should match CSS class defined in icons.css.php
     *
     * @param string image      The name of the file to get
     * @param string alternate  Used to set "alt" and "title" attributes
     *                           of the image
     * @param array  attributes An associative array of other attributes
     *
     * @return string an html IMG tag
     */
    public static String getImage(String image, String alternate /*= ""*/, Map<String, Object> attributes /*= []*/)
    {
    	if (alternate == null) alternate = "";
    	if (attributes == null) attributes = new HashMap<>();
    			
        alternate = htmlspecialchars(alternate);

        if (attributes.containsKey("class")) {
            attributes.put("class", "icon ic_" + image + " " + attributes.get("class"));
        } else {
            attributes.put("class", "icon ic_" + image);
        }

        // set all other attributes
        String attr_str = "";
        for (Entry<String, Object> entry: attributes.entrySet()) {
            if (!entry.getKey().equals("alt") && ! entry.getKey().equals("title")) {
                attr_str += " " + entry.getKey() + "='" + entry.getValue() + "'";
            }
        }

        // override the alt attribute
        String alt;
        if (attributes.containsKey("alt")) {
            alt = (String) attributes.get("alt");
        } else {
            alt = alternate;
        }

        // override the title attribute
        String title;
        if (attributes.containsKey("title")) {
            title = (String) attributes.get("title");
        } else {
            title = alternate;
        }

        // generate the IMG tag
        String template = "<img src='themes/dot.gif' title='%s' alt='%s'%s>";
        return String.format(template, title, alt, attr_str);
    }

	public static String getListNavigator(int totalNumTables, int position, Map $urlParams, String fromRoute,
			String string, Object object) {
		// TODO Auto-generated method stub
		return "";
	}
    
}
