package org.javamyadmin.php;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.Theme;

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
	public boolean IS_TRANSFORMATION_WRAPPER = false;
	public Theme PMA_Theme;
	public String pmaThemePath;
	public String pmaThemeImage;
	public static Config PMA_Config;
	public static boolean pma_config_loading = false;
	public static Properties cfg;
	public String lang;
	public String db;
	public String table;
	public Object dbi;
	public String sql_query;
	public String error_message;
	// public Object error_handler; // Unsupported
	public String text_dir;
	public boolean PMA_NO_SESSION = false;
	public String message;
	public String buffer_message;

	// ?!?
	public Object reload;
	public Object focus_querywindow;

	static {
		cfg = new Properties();
		try {
			InputStream is = GLOBALS.class.getClassLoader().getResourceAsStream("/global.properties");
			cfg.load(is);
		} catch (NullPointerException e) {
			throw new IllegalStateException("File global.properties not found!", e);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading global.properties!", e);
		}
	}

	public GLOBALS() {
	}
}
