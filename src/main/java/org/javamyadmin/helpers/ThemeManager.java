package org.javamyadmin.helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.GLOBALS;

import static org.javamyadmin.php.Php.*;

/**
 * phpMyAdmin theme manager
 *
 * @package PhpMyAdmin
 */
public class ThemeManager {

	/**
	 * ThemeManager instance
	 *
	 * @access private
	 * @static
	 * @var ThemeManager
	 */
	// private static ThemeManager _instance = new ThemeManager(); // Not a
	// singleton, in Java
	// Use GLOBALS.themeManager

	/**
	 * @var String path to theme folder
	 * @access protected
	 */
	private String _themes_path = "./themes/";

	/**
	 * @var array available themes
	 */
	public Map<String, Theme> themes = new HashMap<>();

	/**
	 * @var String cookie name
	 */
	public String cookie_name = "pma_theme";

	/**
	 * @var boolean
	 */
	public boolean per_server = false;

	/**
	 * @var String name of active theme
	 */
	public String active_theme = "";

	/**
	 * @var Theme Theme active theme
	 */
	public Theme theme = null;

	/**
	 * @var String
	 */
	public String theme_default;

	/**
	 * @final String The name of the fallback theme
	 */
	public final static String FALLBACK_THEME = "pmahomme";

	private HttpServletRequest request;
	private GLOBALS GLOBALS;

	/**
	 * Constructor for Theme Manager class
	 * 
	 * @param req
	 * @param req
	 *
	 * @access public
	 */
	public ThemeManager(HttpServletRequest req, GLOBALS GLOBALS) {
		this.request = req;
		this.GLOBALS = GLOBALS;

		this.theme_default = FALLBACK_THEME;
		this.active_theme = "";

		if (!setThemesPath("./themes/")) {
			return;
		}

		this.setThemePerServer((Boolean) GLOBALS.PMA_Config.get("ThemePerServer"));

		this.loadThemes();

		this.theme = new Theme();

		boolean config_theme_exists = true;

		if (!this.checkTheme((String) GLOBALS.PMA_Config.get("ThemeDefault"))) {
			trigger_error(String.format(__("Default theme %s not found!"),
					htmlspecialchars((String) GLOBALS.PMA_Config.get("ThemeDefault"))), E_USER_ERROR);
			config_theme_exists = false;
		} else {
			this.theme_default = (String) GLOBALS.PMA_Config.get("ThemeDefault");
		}

		// check if user have a theme cookie
		String cookie_theme = this.getThemeCookie(req);
		if (cookie_theme == null || !this.setActiveTheme(cookie_theme)) {
			if (config_theme_exists) {
				// otherwise use default theme
				this.setActiveTheme(this.theme_default);
			} else {
				// or fallback theme
				this.setActiveTheme(ThemeManager.FALLBACK_THEME);
			}
		}
	}

	/**
	 * Returns the singleton ThemeManager object
	 *
	 * @return ThemeManager The instance
	 */
	/*
	 * public static ThemeManager getInstance() { if (_instance==null) { _instance =
	 * new ThemeManager(); } return _instance; }
	 */

	/**
	 * sets path to folder containing the themes
	 *
	 * @param String
	 *            path path to themes folder
	 *
	 * @access public
	 * @return boolean success
	 */
	public boolean setThemesPath(String path) {
		if (!this._checkThemeFolder(path)) {
			return false;
		}

		this._themes_path = path.trim();
		return true;
	}

	/**
	 * sets if there are different themes per server
	 *
	 * @param boolean
	 *            per_server Whether to enable per server flag
	 *
	 * @access public
	 * @return void
	 */
	public void setThemePerServer(boolean per_server) {
		this.per_server = per_server;
	}

	/**
	 * Sets active theme
	 *
	 * @param String
	 *            theme theme name
	 *
	 * @access public
	 * @return boolean true on success
	 */
	public boolean setActiveTheme(String theme) {
		if (!this.checkTheme(theme)) {
			trigger_error(String.format(__("Theme %s not found!"), htmlspecialchars(theme)), E_USER_ERROR);
			return false;
		}

		this.active_theme = theme;
		this.theme = this.themes.get(theme);

		// need to set later
		// this.setThemeCookie();

		return true;
	}

	/**
	 * Returns name for storing theme
	 *
	 * @return String cookie name
	 * @access public
	 */
	public String getThemeCookieName() {
		// Allow different theme per server
		if (GLOBALS.server != null && this.per_server) {
			return this.cookie_name + "-" + GLOBALS.server;
		}

		return this.cookie_name;
	}

	/**
	 * returns name of theme stored in the cookie
	 * 
	 * @param req
	 *
	 * @return String|null theme name from cookie or false
	 * @access public
	 */
	public String getThemeCookie(HttpServletRequest req) {

		String name = this.getThemeCookieName();
		if (GLOBALS.PMA_Config.issetCookie(name, req)) {
			return GLOBALS.PMA_Config.getCookie(name, req);
		}

		return null;
	}

	/**
	 * save theme in cookie
	 * 
	 * @param req
	 * @param resp
	 *
	 * @return boolean true
	 * @access public
	 */
	public boolean setThemeCookie(HttpServletRequest req, HttpServletResponse resp) {
		GLOBALS.PMA_Config.setCookie(this.getThemeCookieName(), this.theme.id, this.theme_default, null, false, req,
				resp);
		// force a change of a dummy session variable to avoid problems
		// with the caching of phpmyadmin.css.php
		GLOBALS.PMA_Config.set("theme-update", this.theme.id);
		return true;
	}

	/**
	 * Checks whether folder is valid for storing themes
	 *
	 * @param String
	 *            folder Folder name to test
	 *
	 * @return boolean
	 * @access private
	 */
	private boolean _checkThemeFolder(String folder) {
		if (!new File(folder).isDirectory()) {
			trigger_error(String.format(__("Theme path not found for theme %s!"), htmlspecialchars(folder)),
					E_USER_ERROR);
			return false;
		}

		return true;
	}

	/**
	 * read all themes
	 *
	 * @return boolean true
	 * @access public
	 */
	public boolean loadThemes() {
		this.themes = new HashMap<>();

		File themesDir = new File(this._themes_path);
		if (!themesDir.isDirectory() || !themesDir.canRead()) {
			trigger_error("phpMyAdmin-ERROR: cannot open themes folder: " + this._themes_path, E_USER_WARNING);
			return false;
		}

		// check for themes directory
		String[] directoryListing = themesDir.list();
		for (String PMA_Theme : directoryListing) {
			// Skip non dirs, . and ..
			if (PMA_Theme == "." || PMA_Theme == ".."
					|| !new File(GLOBALS.ROOT_PATH + this._themes_path + PMA_Theme).isDirectory()) {
				continue;
			}
			if (this.themes.containsKey(PMA_Theme)) {
				continue;
			}
			Theme new_theme = Theme.load(this._themes_path + PMA_Theme);
			if (new_theme != null) {
				new_theme.setId(PMA_Theme);
				this.themes.put(PMA_Theme, new_theme);
			}
		} // end get themes

		return true;
	}

	/**
	 * checks if given theme name is a known theme
	 *
	 * @param String
	 *            theme name fo theme to check for
	 *
	 * @return boolean
	 * @access public
	 */
	public boolean checkTheme(String theme) {
		return this.themes.containsKey(theme);
	}

	/**
	 * returns HTML selectbox, with or without form enclosed
	 *
	 * @param boolean
	 *            form whether enclosed by from tags or not
	 *
	 * @return String
	 * @access public
	 */
	public String getHtmlSelectBox(boolean form)
	// TODO use twig
	{
		String select_box = "";

		if (form) {
			select_box += "<form name='setTheme' method='post'";
			select_box += " action='index.php?route=/set-theme' class='disableAjax'>";
			select_box += Url.getHiddenInputs(request, GLOBALS);
		}

		String theme_preview_href = "<a href='" + Url.getFromRoute("/themes", null, request, GLOBALS)
				+ "' target='themes' class='themeselect'>";
		select_box += theme_preview_href + __("Theme:") + "</a>" + "\n";

		select_box += "<select name='set_theme' lang='en' dir='ltr'" + " class='autosubmit'>";
		// foreach (this.themes as each_theme_id => each_theme) {
		for (String each_theme_id : this.themes.keySet()) {
			Theme each_theme = this.themes.get(each_theme_id);
			select_box += "<option value='" + each_theme_id + "'";
			if (each_theme_id.equals(this.active_theme)) {
				select_box += " selected='selected'";
			}
			select_box += ">" + htmlspecialchars(each_theme.getName()) + "</option>";
		}
		select_box += "</select>";

		if (form) {
			select_box += "</form>";
		}

		return select_box;
	}

	/**
	 * Renders the previews for all themes
	 *
	 * @return String
	 * @access public
	 */
	public String getPrintPreviews() {
		String retval = "";
		for (Theme each_theme : this.themes.values()) {
			retval += each_theme.getPrintPreview();
		} // end "open themes"
		return retval;
	}

	/**
	 * Theme initialization
	 *
	 * @return void
	 * @access public
	 */
	public static void initializeTheme(HttpServletRequest request, GLOBALS GLOBALS) {
		ThemeManager tmanager = new ThemeManager(request, GLOBALS);

		GLOBALS.themeManager = tmanager;

		/**
		 * the theme object
		 *
		 * @global Theme GLOBALS["PMA_Theme"]
		 */
		GLOBALS.PMA_Theme = tmanager.theme;

		// BC
		/**
		 * the theme path
		 * 
		 * @global String GLOBALS["pmaThemePath"]
		 */
		GLOBALS.pmaThemePath = tmanager.theme.getPath();
		/**
		 * the theme image path
		 * 
		 * @global String GLOBALS["pmaThemeImage"]
		 */
		GLOBALS.pmaThemeImage = tmanager.theme.getImgPath(null, null);
	}

}
