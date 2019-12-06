package org.javamyadmin.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.GLOBALS;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import static org.javamyadmin.php.Php.*;

/**
 * handles theme
 *
 * @todo add the possibility to make a theme depend on another theme
 * and by default on original
 * @todo make all components optional - get missing components from "parent" theme
 *
 * @package PhpMyAdmin
 */
public class Theme {

    /**
     * @var String theme version
     * @access  protected
     */
    public String version = "0.0.0.0";

    /**
     * @var String theme name
     * @access  protected
     */
    public String name = "";

    /**
     * @var String theme id
     * @access  protected
     */
    public String id = "";

    /**
     * @var String theme path
     * @access  protected
     */
    public File path = null;

    /**
     * @var String image path
     * @access  protected
     */
    public String img_path = "";

    /**
     * @var integer last modification time for info file
     * @access  protected
     */
    public long mtime_info = 0;

    /**
     * needed because sometimes, the mtime for different themes
     * is identical
     * @var integer filesize for info file
     * @access  protected
     */
    public long filesize_info = 0;

    /**
     * @var array List of css files to load
     * @access private
     */
    public String[] _cssFiles = new String[] {
        "common",
        "enum_editor",
        "gis",
        "navigation",
        "designer",
        "rte",
        "codemirror",
        "jqplot",
        "resizable-menu",
        "icons",
    };

    /**
     * @var Template
     */
    //public Template template = new Template();

    /**
     * Theme constructor.
     */
    private Theme(File path)
    {
    	this.path = path;
    }

    /**
     * Loads theme information
     *
     * @return boolean whether loading them info was successful or not
     * @access  public
     */
    public boolean loadInfo()
    {
    	File infofile = new File(this.getPath() + "/theme.json");
        if (!infofile.exists()) {
            return false;
        }

        if (this.mtime_info == infofile.lastModified()) {
            return true;
        }

        if (infofile.length() == 0) {
            return false;
        }
        
        Gson gson = new Gson();
        Map<String, Object> data;
		try {
			data = gson.fromJson(new FileReader(infofile), Map.class);
		} catch (JsonSyntaxException e) {
			trigger_error(__("Cannot decode theme.json"), E_USER_ERROR);
			e.printStackTrace();
			return false;
		} catch (JsonIOException e) {
			trigger_error(__("Cannot read theme.json"), E_USER_ERROR);
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			// this should not happen
			e.printStackTrace();
			return false;
		}

        // Did we get expected data?
        if (! is_array(data)) {
            return false;
        }
        // Check that all required data are there
        String[] members = new String[] {
            "name",
            "version",
            "supports",
        };
        for (String member: members) {
            if (! data.containsKey(member)) {
                return false;
            }
        }

        // Version check
        if (! ((List) data.get("supports")).contains(GLOBALS.PMA_MAJOR_VERSION)) {
            return false;
        }

        this.mtime_info = infofile.lastModified();
        this.filesize_info = infofile.length();

        this.setVersion((String)data.get("version"));
        this.setName((String)data.get("name"));

        return true;
    }

    /**
     * returns theme object loaded from given folder
     * or false if theme is invalid
     *
     * @param String folder path to theme
     *
     * @return Theme|false
     * @static
     * @access public
     */
    public static Theme load(File folder)
    {
        Theme theme = new Theme(folder);

        if (! theme.loadInfo()) {
            return null;
        }

        theme.checkImgPath();

        return theme;
    }

    /**
     * checks image path for existence - if not found use img from fallback theme
     *
     * @access public
     * @return boolean
     */
    public boolean checkImgPath()
    {
        // try current theme first
        if (new File(this.getPath() + "/img/").isDirectory()) {
            this.setImgPath(this.getPath() + "/img/");
            return true;
        }

        // try fallback theme
        String fallback = GLOBALS.THEMES_PATH + ThemeManager.FALLBACK_THEME + "/img/";
        if (new File(fallback).isDirectory()) {
            this.setImgPath(fallback);
            return true;
        }

        // we failed
        trigger_error(
        		String.format(
                    __("No valid image path for theme %s found!"),
                    this.getName()
    				),
        		E_FATAL
            );
        return false;
    }

    /**
     * returns path to theme
     *
     * @access public
     * @return String path to theme
     */
    public File getPath()
    {
        return this.path;
    }

    /**
     * sets version
     *
     * @param String version version to set
     *
     * @return void
     * @access public
     */
    public void setVersion(String version)
    {
        this.version = version.trim();
    }

    /**
     * returns version
     *
     * @return String version
     * @access public
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * checks theme version against version
     * returns true if theme version is equal or higher to version
     *
     * @param String version version to compare to
     *
     * @return boolean true if theme version is equal or higher to version
     * @access public
     */
    public int checkVersion(String version)
    {
        return this.getVersion().compareTo(version);
    }

    /**
     * sets name
     *
     * @param String name name to set
     *
     * @return void
     * @access public
     */
    public void setName(String name)
    {
        this.name = name.trim();
    }

    /**
     * returns name
     *
     * @access  public
     * @return String name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * sets id
     *
     * @param String id new id
     *
     * @return void
     * @access public
     */
    public void setId(String id)
    {
        this.id = id.trim();
    }

    /**
     * returns id
     *
     * @return String id
     * @access public
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Sets path to images for the theme
     *
     * @param String path path to images for this theme
     *
     * @return void
     * @access public
     */
    public void setImgPath(String path)
    {
        this.img_path = path;
    }

    /**
     * Returns the path to image for the theme.
     * If filename is given, it possibly fallbacks to fallback
     * theme for it if image does not exist.
     *
     * @param String file     file name for image
     * @param String fallback fallback image
     *
     * @access public
     * @return String image path for this theme
     */
    public String getImgPath(String file , String fallback)
    {
        if (file == null) {
            return this.img_path;
        }

        if (new File(this.img_path + file).canRead()) {
            return this.img_path + file;
        }

        if (fallback != null) {
            return this.getImgPath(fallback, null);
        }

        return "./themes/" + ThemeManager.FALLBACK_THEME + "/img/" + file;
    }

    /**
     * Renders the preview for this theme
     *
     * @return String
     * @access public
     */
    public String getPrintPreview()
    {
    	Map<String,Object> url_params = new HashMap<>();
        url_params.put("set_theme", this.getId());
        String screen = null;
        String path = this.getPath() + "/screen.png";
        if (new File(path).exists()) {
            screen = path;
        }
        
        Map<String,Object> data = new HashMap<>();
        data.put("url_params", url_params);
        data.put("name", this.getName());
        data.put("version", this.getVersion());
        data.put("id", this.getId());
        data.put("screen", screen);
        
        
        return JtwigFactory.render("theme_preview", data);
    }

    @Override
    public String toString() {
    	return "Theme " + name;
    }
}
