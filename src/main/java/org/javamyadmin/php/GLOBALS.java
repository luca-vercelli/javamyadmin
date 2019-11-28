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

	public final String PMA_VERSION = "0.1";
	public Integer server = 0;
	public String ROOT_PATH;
	public Theme PMA_Theme;
	public String pmaThemePath;
	public String pmaThemeImage;
	public Config PMA_Config;
	public boolean pma_config_loading = false;
	public String message;
	public Properties cfg = new Properties();
	public String lang;
	public String db;
	public String table;
	public Object dbi;
	public String sql_query;
	public String error_message;

	public GLOBALS() {
		try {
			
			InputStream is = GLOBALS.class.getClassLoader().getResourceAsStream("/global.properties");
			cfg.load(is);
		} catch (NullPointerException e) {
			throw new IllegalStateException("File global.properties not found!", e);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading global.properties!", e);
		}
		this.PMA_Config = new Config(null, this);
	}
}
