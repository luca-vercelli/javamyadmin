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

	public final String PMA_VERSION = "0.1"; // TODO should be static
	public Integer server = 0;
	public static String ROOT_PATH = "/"; // TODO should be static. Is it needed ?!?
	public Theme PMA_Theme;
	public String pmaThemePath;
	public String pmaThemeImage;
	public Config PMA_Config;
	public static boolean pma_config_loading = false;
	public String message;
	public static Properties cfg;
	public String lang;
	public String db;
	public String table;
	public Object dbi;
	public String sql_query;
	public String error_message;
	public Object error_handler;
	public String text_dir;
	public boolean PMA_NO_SESSION = false;

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
