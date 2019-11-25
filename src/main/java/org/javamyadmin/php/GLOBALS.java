package org.javamyadmin.php;

/**
 * Mimic PHP GLOBALS
 * 
 * @author lucav
 *
 */
public class GLOBALS {

	public static final String PMA_VERSION = "0.1";
	public static Integer server;
	public static String ROOT_PATH;
	// public static Theme PMA_Theme;
	public static String pmaThemePath;
	public static String pmaThemeImage;
	// public static Config PMA_Config = new Config(ROOT_PATH); /// ?!?
	public static boolean pma_config_loading = false;
	public static String message;
	// public static Config cfg = new Config(null); /// ?!?
	public static String lang;
}
