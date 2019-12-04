package org.javamyadmin.php;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Theme;
import org.javamyadmin.helpers.ThemeManager;

/**
 * Mimic PHP GLOBALS. Intended to be stored in Request (in JavaEE this would be
 * a request-scoped bean).
 * 
 * @author lucav
 *
 */
public class GLOBALS {

	public static final String PMA_PATH_TO_BASEDIR = ""; // Is it needed ?!?
	public static final String PMA_VERSION = "0.1";
	public Integer server = 0;
	public static final String ROOT_PATH = "/"; // Is it needed ?!?
	public static final String LOCALE_PATH = "/"; // Where is it? files *.mo should be there
	public static final String PMA_MINIMUM_COMMON = "";
	public boolean IS_TRANSFORMATION_WRAPPER = false;
	public Theme PMA_Theme;
	public String pmaThemePath;
	public String pmaThemeImage;
	public static Config PMA_Config = new Config(null);
	public static boolean pma_config_loading = false;
	public static String active_page;
	public static boolean is_upload;
	public String lang;
	public String db;
	public String table;
	public DatabaseInterface dbi;
	public String sql_query;
	public String error_message;
	// public Object error_handler; // Unsupported
	public String text_dir;
	public boolean PMA_NO_SESSION = false;
	public String message;
	public String buffer_message;
	public String route;
	
	/* Backward compatibility (see Config.enableBc())
	 * 
	 * cfg -> PMA_Config.settings
	 * 
	 * default_server -> PMA_Config.default_server
	 * 
	 * is_upload -> PMA_Config.get("enable_upload")
	 * 
	 * max_upload_size -> PMA_Config.get("max_upload_size")
	 * 
	 * is_https -> PMA_Config.get("is_https")
	 * 
	 */

	// ?!?
	public Object reload;
	public Object focus_querywindow;
	public ThemeManager themeManager;
	
	public String PMA_USR_BROWSER_AGENT;
	public int PMA_USR_BROWSER_VER;
	

	public GLOBALS() {
	}
}
