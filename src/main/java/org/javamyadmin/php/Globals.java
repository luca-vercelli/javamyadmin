package org.javamyadmin.php;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.ListDatabase;
import org.javamyadmin.helpers.Theme;
import org.javamyadmin.helpers.ThemeManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mimic PHP GLOBALS. Intended to be stored in Request (in JavaEE this would be
 * a request-scoped bean).
 * 
 * @author lucav
 *
 */
public class Globals {

	private static final String PMA_PATH_TO_BASEDIR = ""; // Is it needed ?!?
	private static final String PMA_VERSION = "5.1.1";
	private static final String PMA_MAJOR_VERSION = "5.1";
	
	/**
	 * Absolute path of /WEB-INF/..
	 */
	private static String ROOT_PATH;
	private static String THEMES_PATH;
	private static String TEMPLATES_PATH;
	public static String LOCALES_BUNDLE = "org.javamyadmin";
	private static Config PMA_Config = new Config(null);
	private static boolean pma_config_loading = false;
	private static File PMA_PHP_SELF = new File("???"); // TODO
	private ListDatabase dblist;
	
	private Integer server = 0;
	private boolean PMA_MINIMUM_COMMON = false;
	private boolean IS_TRANSFORMATION_WRAPPER = false;
	private Theme PMA_Theme;
	private File pmaThemeFullPath;
	private String pmaThemeUrlPath;
	private String pmaThemeImage;
	private String lang;
	private String db;
	private String table;
	
	@Autowired
	private DatabaseInterface dbi;
	
	private String sql_query;
	private String error_message;
	// private Object error_handler; // Unsupported
	private String text_dir;
	private String active_page;
	private boolean is_upload;
	
	@Deprecated // should always be false
	private boolean PMA_NO_SESSION = false;
	
	private String message;
	private String buffer_message;
	private String route;
	private ThemeManager themeManager;
	
	/* Backward compatibility (see Config.enableBc())
	 * 
	 * cfg -> PMA_Config.settings
	 * default_server -> PMA_Config.default_server
	 * is_upload -> PMA_Config.get("enable_upload")
	 * max_upload_size -> PMA_Config.get("max_upload_size")
	 * is_https -> PMA_Config.get("is_https")
	 * 
	 */

	// FIXME what are these ?!?
	private Object reload;
	private Object focus_querywindow;
	private String submit_mult;
	private String mult_btn;
	private List selected;
	
	private String PMA_USR_BROWSER_AGENT;
	private int PMA_USR_BROWSER_VER;
	private boolean avoid_show_comment = false;
	private String url_query;
	private String err_url;
	private Object is_grantuser;
	private Object is_createuser;
	private List<String> db_to_test;
	private boolean is_create_db_priv;
	private String db_to_create;
	private Map url_parameters = new HashMap();

	public static String getRootPath() {
		return ROOT_PATH;
	}

	public static void setRootPath(String path) {
		ROOT_PATH = path;
	}

	public static String getThemesPath() {
		return THEMES_PATH;
	}

	public static void setThemesPath(String path) {
		THEMES_PATH = path;
	}

	public static String getTemplatesPath() {
		return TEMPLATES_PATH;
	}

	public static void setTemplatesPath(String path) {
		TEMPLATES_PATH = path;
	}

	public static Config getConfig() {
		return PMA_Config;
	}

	public static void setConfig(Config config) {
		PMA_Config = config;
	}

	public static boolean isConfigLoading() {
		return pma_config_loading;
	}

	public static void setConfigLoading(boolean pma_config_loading) {
		Globals.pma_config_loading = pma_config_loading;
	}

	public String getActivePage() {
		return active_page;
	}

	public void setActivePage(String active_page) {
		this.active_page = active_page;
	}

	public boolean isUpload() {
		return is_upload;
	}

	public void setIsUpload(boolean is_upload) {
		this.is_upload = is_upload;
	}

	public static File get_PMA_PHP_SELF() {
		return PMA_PHP_SELF;
	}

	public static void set_PMA_PHP_SELF(File _PMA_PHP_SELF) {
		PMA_PHP_SELF = _PMA_PHP_SELF;
	}

	public Integer getServer() {
		return server;
	}

	public void setServer(Integer server) {
		this.server = server;
	}

	public boolean get_PMA_MINIMUM_COMMON() {
		return PMA_MINIMUM_COMMON;
	}

	public void set_PMA_MINIMUM_COMMON(boolean PMA_MINIMUM_COMMON) {
		this.PMA_MINIMUM_COMMON = PMA_MINIMUM_COMMON;
	}

	public boolean isTransformationWrapper() {
		return IS_TRANSFORMATION_WRAPPER;
	}

	public void setTransformationWrapper(boolean IS_TRANSFORMATION_WRAPPER) {
		this.IS_TRANSFORMATION_WRAPPER = IS_TRANSFORMATION_WRAPPER;
	}

	public Theme getTheme() {
		return PMA_Theme;
	}

	public void setTheme(Theme theme) {
		this.PMA_Theme = theme;
	}

	public File getPmaThemeFullPath() {
		return pmaThemeFullPath;
	}

	public void setPmaThemeFullPath(File pmaThemeFullPath) {
		this.pmaThemeFullPath = pmaThemeFullPath;
	}

	public String getPmaThemeUrlPath() {
		return pmaThemeUrlPath;
	}

	public void setPmaThemeUrlPath(String pmaThemeUrlPath) {
		this.pmaThemeUrlPath = pmaThemeUrlPath;
	}

	public String getPmaThemeImage() {
		return pmaThemeImage;
	}

	public void setPmaThemeImage(String pmaThemeImage) {
		this.pmaThemeImage = pmaThemeImage;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public DatabaseInterface getDbi() {
		return dbi;
	}

	public void setDbi(DatabaseInterface dbi) {
		this.dbi = dbi;
	}

	public String getSqlQuery() {
		return sql_query;
	}

	public void setSqlQuery(String sql_query) {
		this.sql_query = sql_query;
	}

	public String getErrorMessage() {
		return error_message;
	}

	public void setErrorMessage(String error_message) {
		this.error_message = error_message;
	}

	public String getTextDir() {
		return text_dir;
	}

	public void setTextDir(String text_dir) {
		this.text_dir = text_dir;
	}

	@Deprecated
	public boolean get_PMA_NO_SESSION() {
		return PMA_NO_SESSION;
	}

	@Deprecated
	public void set_PMA_NO_SESSION(boolean pMA_NO_SESSION) {
		PMA_NO_SESSION = pMA_NO_SESSION;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getBufferMessage() {
		return buffer_message;
	}

	public void setBufferMessage(String buffer_message) {
		this.buffer_message = buffer_message;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public ThemeManager getThemeManager() {
		return themeManager;
	}

	public void setThemeManager(ThemeManager themeManager) {
		this.themeManager = themeManager;
	}

	public Object getReload() {
		return reload;
	}

	public void setReload(Object reload) {
		this.reload = reload;
	}

	public Object getFocus_querywindow() {
		return focus_querywindow;
	}

	public void setFocus_querywindow(Object focus_querywindow) {
		this.focus_querywindow = focus_querywindow;
	}

	public String getPMA_USR_BROWSER_AGENT() {
		return PMA_USR_BROWSER_AGENT;
	}

	public void setPMA_USR_BROWSER_AGENT(String pMA_USR_BROWSER_AGENT) {
		PMA_USR_BROWSER_AGENT = pMA_USR_BROWSER_AGENT;
	}

	public int getPMA_USR_BROWSER_VER() {
		return PMA_USR_BROWSER_VER;
	}

	public void setPMA_USR_BROWSER_VER(int pMA_USR_BROWSER_VER) {
		PMA_USR_BROWSER_VER = pMA_USR_BROWSER_VER;
	}

	public static String getPmaPathToBasedir() {
		return PMA_PATH_TO_BASEDIR;
	}

	public static String getPmaVersion() {
		return PMA_VERSION;
	}

	public static String getPmaMajorVersion() {
		return PMA_MAJOR_VERSION;
	}

	public boolean getAvoidShowComment() {
		return avoid_show_comment;
	}

	public void setAvoidShowComment(boolean avoid_show_comment) {
		this.avoid_show_comment = avoid_show_comment;
	}

	public String getUrlQuery() {
		return url_query;
	}

	public void setUrlQuery(String url_query) {
		this.url_query = url_query;
	}

	public String getErrUrl() {
		return err_url;
	}

	public void setErrUrl(String err_url) {
		this.err_url = err_url;
	}

	public Object getIsGrantuser() {
		return is_grantuser;
	}

	public void setIsGrantuser(Object is_grantuser) {
		this.is_grantuser = is_grantuser;
	}

	public Object getIsCreateuser() {
		return is_createuser;
	}

	public void setIsCreateuser(Object is_createuser) {
		this.is_createuser = is_createuser;
	}

	public ListDatabase getDblist() {
		return dblist;
	}

	public void setDblist(ListDatabase dblist) {
		this.dblist = dblist;
	}

	public List<String> getDbToTest() {
		return db_to_test;
	}

	public void setDbToTest(List<String> db_to_test) {
		this.db_to_test = db_to_test;
	}

	public boolean getIsCreateDbPriv() {
		return is_create_db_priv;
	}

	public void setIsCreateDbPriv(boolean is_create_db_priv) {
		this.is_create_db_priv = is_create_db_priv;
	}

	public String getDbToCreate() {
		return db_to_create;
	}

	public void setDbToCreate(String db_to_create) {
		this.db_to_create = db_to_create;
	}

	public String getSubmit_mult() {
		return submit_mult;
	}

	public void setSubmitMult(String submit_mult) {
		this.submit_mult = submit_mult;
	}

	public String getMult_btn() {
		return mult_btn;
	}

	public void setMultBtn(String mult_btn) {
		this.mult_btn = mult_btn;
	}

	public List getSelected() {
		return selected;
	}

	public void setSelected(List selected) {
		this.selected = selected;
	}

	public Map getUrlParameters() {
		return url_parameters;
	}

	public void setUrlParameters(Map url_parameters) {
		this.url_parameters = url_parameters;
	}
}
