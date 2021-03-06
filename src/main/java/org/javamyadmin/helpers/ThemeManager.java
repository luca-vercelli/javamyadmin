package org.javamyadmin.helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
	private String _themes_path;

	/**
	 * @var array available themes
	 */
	protected Map<String, Theme> themes = new HashMap<>();

	/**
	 * @var String cookie name
	 */
	protected String cookie_name = "pma_theme";

	/**
	 * @var boolean
	 */
	protected boolean per_server = false;

	/**
	 * @var String name of active theme
	 */
	protected String active_theme = "";

	/**
	 * @var Theme Theme active theme
	 */
	protected Theme theme = null;

	/**
	 * @var String
	 */
	protected String theme_default;

	/**
	 * @final String The name of the fallback theme
	 */
	public final static String FALLBACK_THEME = "pmahomme";

	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private Globals GLOBALS;
	@Autowired
	private SessionMap $_SESSION;
	@Autowired
	private Config cfg;
    @Autowired
    private Url url;
    @Autowired
    private BeanFactory beanFactory;

	/**
	 * Constructor for Theme Manager class
	 * 
	 * @param req
	 * @param req
	 *
	 * @access public
	 */
	public ThemeManager() {
		
	}
	
	@PostConstruct
	protected void init() {

		this.theme_default = FALLBACK_THEME;
		this.active_theme = "";

		setThemesPath(Globals.getThemesPath());

		this.setThemePerServer("true".equals(cfg.get("ThemePerServer")));

		this.loadThemes();

		boolean config_theme_exists = true;

		if (!this.checkTheme((String) cfg.get("ThemeDefault"))) {
			trigger_error(String.format(__("Default theme %s not found!"),
					htmlspecialchars((String) cfg.get("ThemeDefault"))), E_USER_ERROR);
			config_theme_exists = false;
		} else {
			this.theme_default = (String) cfg.get("ThemeDefault");
		}

		// check if user have a theme cookie
		String cookie_theme = this.getThemeCookie(httpRequest);
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
		if (GLOBALS.getServer() != null && this.per_server) {
			return this.cookie_name + "-" + GLOBALS.getServer();
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
		if (cfg.issetCookie(name, req)) {
			return cfg.getCookie(name, req);
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
		cfg.setCookie(this.getThemeCookieName(), this.theme.id, this.theme_default, null, false, req,
				resp);
		// force a change of a dummy session variable to avoid problems
		// with the caching of phpmyadmin.css.php
		cfg.set("theme-update", this.theme.id);
		return true;
	}

	/**
	 * Load all themes into this.themes
	 *
	 * @return boolean true
	 * @access public
	 */
	public boolean loadThemes() {
		this.themes = new HashMap<>();

		File themesDir = new File(Globals.getRootPath() + "/" + this._themes_path);
		if (!themesDir.isDirectory() || !themesDir.canRead()) {
			trigger_error("phpMyAdmin-ERROR: cannot open themes folder: " + this._themes_path, E_USER_WARNING);
			return false;
		}

		// check for themes directory
		File[] directoryListing = themesDir.listFiles();
		for (File PMA_Theme : directoryListing) {
			String name = PMA_Theme.getName();
			// Skip non dirs, . and ..
			if (name.equals(".") || name.equals("..")
					|| !PMA_Theme.isDirectory()) {
				continue;
			}
			if (this.themes.containsKey(name)) {
				continue;
			}
			Theme new_theme = beanFactory.getBean(Theme.class, PMA_Theme.getName());
			if (new_theme.load_ok) {
				new_theme.setId(name);
				this.themes.put(name, new_theme);
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
	{
		String select_box = "";

		if (form) {
			select_box += "<form name='setTheme' method='post'";
			select_box += " action='set-theme' class='disableAjax'>";
			select_box += url.getHiddenInputs(httpRequest, GLOBALS, $_SESSION);
		}

		String theme_preview_href = "<a href='" + url.getFromRoute("/themes", null)
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

	public String getHtmlSelectBox() {
		return getHtmlSelectBox(true);
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
	public void initializeTheme() {

		/**
		 * the theme object
		 *
		 * @global Theme GLOBALS["PMA_Theme"]
		 */
		GLOBALS.setTheme(this.theme);

		// BC
		/**
		 * the theme path
		 * 
		 * @global String GLOBALS["pmaThemePath"]
		 */
		GLOBALS.setPmaThemePath("themes/" + this.theme.getPath());
		
		/**
		 * the theme image path
		 * 
		 * @global String GLOBALS["pmaThemeImage"]
		 */
		GLOBALS.setPmaThemeImage(this.theme.getImgPath(null, null));
	}

	public Map<String, Theme> getThemes() {
		return themes;
	}

	public void setThemes(Map<String, Theme> themes) {
		this.themes = themes;
	}

	public boolean isPer_server() {
		return per_server;
	}

	public void setPer_server(boolean per_server) {
		this.per_server = per_server;
	}

	public String getActive_theme() {
		return active_theme;
	}

	public void setActive_theme(String active_theme) {
		this.active_theme = active_theme;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public String getTheme_default() {
		return theme_default;
	}

	public void setTheme_default(String theme_default) {
		this.theme_default = theme_default;
	}

}
