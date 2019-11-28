package org.javamyadmin.php;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Mimic PHP GLOBALS
 * 
 * @author lucav
 *
 */
public class GLOBALS {

	public static final String PMA_VERSION = "0.1";
	public static Integer server = 0;
	public static String ROOT_PATH;
	// public static Theme PMA_Theme;
	public static String pmaThemePath;
	public static String pmaThemeImage;
	// public static Config PMA_Config = ?!?
	public static boolean pma_config_loading = false;
	public static String message;
	public static Properties cfg;
	public static String lang;

	static {
		try {
			cfg = new Properties();
			InputStream is = GLOBALS.class.getClassLoader().getResourceAsStream("/global.properties");
			cfg.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			throw new IllegalStateException("File global.properties not found!", e);
		}
	}
}
