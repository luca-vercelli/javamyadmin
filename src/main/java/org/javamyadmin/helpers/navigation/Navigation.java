package org.javamyadmin.helpers.navigation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Relation;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.server.Select;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.javamyadmin.php.Php.UrlComponents;
import org.springframework.beans.factory.annotation.Autowired;

import static org.javamyadmin.php.Php.*;

/**
 * The navigation panel - displays server, db and table selection tree
 *
 * @package PhpMyAdmin-Navigation
 */
public class Navigation {
	/**
	 * @var Template
	 */
	@Autowired
	private Template template;

	/**
	 * @var Relation
	 */
	// TODO? private Relation relation;

	/**
	 * @var DatabaseInterface
	 */
	@Autowired
	private DatabaseInterface dbi;

	@Autowired
	private Response $response;

	/**
	 * @var NavigationTree
	 */
	@Autowired
	private NavigationTree tree;

	/**
	 * Navigation constructor.
	 * 
	 * @param Template $template Template instance
	 * @param Relation $relation Relation instance
	 * @param DatabaseInterface $dbi DatabaseInterface instance
	 */
	public Navigation() {
	}

	/**
	 * Renders the navigation tree, or part of it
	 * 
	 * @param GLOBALS
	 * @param httpRequest
	 * @param sessionMap
	 *
	 * @return String The navigation tree
	 * @throws SQLException
	 */
	public String getDisplay(HttpServletRequest httpRequest, SessionMap sessionMap, Globals GLOBALS)
			throws SQLException {
		Config $cfg = Globals.getConfig();

		Map<String, Object> $logo = new HashMap<>();
		$logo.put("is_displayed", $cfg.get("NavigationDisplayLogo"));
		$logo.put("has_link", false);
		$logo.put("link", "#");
		$logo.put("attributes", " target='_blank' rel='noopener noreferrer'");
		$logo.put("source", "");

		String $serverSelect = "";
		String $navigationSettings = "";

		if (!$response.isAjax()) {
			$logo.put("source", this.getLogoSource(GLOBALS));
			$logo.put("has_link", !empty($cfg.get("NavigationLogoLink")));
			$logo.put("link", ((String) $cfg.get("NavigationLogoLink")).trim());
			if (!Sanitize.checkLink((String) $logo.get("link"), true)) {
				$logo.put("link", "index.php");
			}
			if ("main".equals($cfg.get("NavigationLogoLinkWindow"))) {
				try {
					UrlComponents components = parse_url((String) $logo.get("link"));
					if (empty(components.host)) {
						boolean $hasStartChar = ((String) $logo.get("link")).contains("?");
						$logo.put("link", $logo.get("link") + Url.getCommon(new HashMap<>(),
								$hasStartChar ? "?" : Url.getArgSeparator(), httpRequest, GLOBALS));
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				$logo.put("attributes", "");
			}

			if ("true".equals($cfg.get("NavigationDisplayServers")) && ((Map) $cfg.get("Servers")).size() > 1) {
				$serverSelect = Select.render(true, true, GLOBALS, sessionMap, httpRequest);
			}

			if (!GLOBALS.getPMA_DISABLE_NAVI_SETTINGS()) {
				// TODO $navigationSettings = PageSettings.getNaviSettings();
			}
		}
		String $navRender = "";
		if (!$response.isAjax() || !empty(httpRequest.getParameter("full"))
				|| !empty(httpRequest.getParameter("reload"))) {
			if ("true".equals($cfg.get("ShowDatabasesNavigationAsTree"))) {
				// provide database tree in navigation
				$navRender = this.tree.renderState();
			} else {
				// provide legacy pre-4.0 navigation
				$navRender = this.tree.renderDbSelect();
			}
		} else {
			$navRender = this.tree.renderPath();
		}

		Map<String, Object> model = new HashMap<>();
		model.put("is_ajax", $response.isAjax());
		model.put("logo", $logo);
		model.put("is_synced", $cfg.get("NavigationLinkWithMainPanel"));
		model.put("is_highlighted", $cfg.get("NavigationTreePointerEnable"));
		model.put("is_autoexpanded", $cfg.get("NavigationTreeAutoexpandSingleDb"));
		model.put("server", GLOBALS.getServer());
		model.put("auth_type", multiget($cfg.settings, "Server", "auth_type"));
		model.put("is_servers_displayed", $cfg.get("NavigationDisplayServers"));
		model.put("servers", $cfg.get("Servers"));
		model.put("server_select", $serverSelect);
		model.put("navigation_tree", $navRender);
		model.put("is_navigation_settings_enabled", !GLOBALS.getPMA_DISABLE_NAVI_SETTINGS());
		model.put("navigation_settings", $navigationSettings);
		model.put("is_drag_drop_import_enabled", "true".equals($cfg.get("enable_drag_drop_import")));

		return this.template.render("navigation/main", model);
	}

	/**
	 * Add an item of navigation tree to the hidden items list in PMA database.
	 *
	 * @param String $itemName name of the navigation tree item
	 * @param String $itemType type of the navigation tree item
	 * @param String $dbName database name
	 * @param String $tableName table name if applicable
	 *
	 * @return void
	 */
	public void hideNavigationItem(String $itemName, String $itemType, String $dbName, String $tableName /* = null */,
			Globals GLOBALS) {
		/*
		 * TODO $navTable = Util.backquote(GLOBALS["cfgRelation"]["db"]) + '.' +
		 * Util.backquote(GLOBALS["cfgRelation"]["navigationhiding"]); $sqlQuery =
		 * 'INSERT INTO ' + $navTable + '(`username`, `item_name`, `item_type`,
		 * `db_name`, `table_name`)' + ' VALUES (' +
		 * '"' + this.dbi.escapeString($GLOBALS["cfg"]["Server"]["user"]) + '",' +
		 * '"' + this.dbi.escapeString($itemName) + '",' +
		 * '"' + this.dbi.escapeString($itemType) + '",' +
		 * '"' + this.dbi.escapeString($dbName) + '",' + '"' + (! empty($tableName) ?
		 * this.dbi.escapeString($tableName) : '' ) + '")';
		 * this.relation.queryAsControlUser($sqlQuery, false);
		 */
	}

	/**
	 * Remove a hidden item of navigation tree from the list of hidden items in PMA
	 * database.
	 *
	 * @param String $itemName name of the navigation tree item
	 * @param String $itemType type of the navigation tree item
	 * @param String $dbName database name
	 * @param String $tableName table name if applicable
	 *
	 * @return void
	 */
	public void unhideNavigationItem(String $itemName, String $itemType, String $dbName, String $tableName /* = null */
	) {
		/*
		 * TODO $navTable = Util.backquote($GLOBALS["cfgRelation"]["db"]) + '.' +
		 * Util.backquote($GLOBALS["cfgRelation"]["navigationhiding"]); $sqlQuery =
		 * 'DELETE FROM ' + $navTable + ' WHERE' + ' `username`="' +
		 * this.dbi.escapeString($GLOBALS["cfg"]["Server"]["user"]) + '"' + ' AND
		 * `item_name`="' + this.dbi.escapeString($itemName) + '"' + ' AND
		 * `item_type`="' + this.dbi.escapeString($itemType) + '"' + ' AND
		 * `db_name`="' + this.dbi.escapeString($dbName) + '"' + (! empty($tableName) ?
		 * ' AND `table_name`="' + this.dbi.escapeString($tableName) + '"' : '' );
		 * this.relation.queryAsControlUser($sqlQuery, false);
		 */
	}

	/**
	 * Returns HTML for the dialog to show hidden navigation items.
	 *
	 * @param String $database database name
	 * @param String $itemType type of the items to include
	 * @param String $table table name
	 *
	 * @return String HTML for the dialog to show hidden navigation items
	 */
	public String getItemUnhideDialog(String $database, String $itemType /* = null */, String $table /* = null */) {
		Map $hidden = this.getHiddenItems($database, $table);

		Map<String, Object> $typeMap = new HashMap<>();
		$typeMap.put("group", __("Groups:"));
		$typeMap.put("event", __("Events:"));
		$typeMap.put("function", __("Functions:"));
		$typeMap.put("procedure", __("Procedures:"));
		$typeMap.put("table", __("Tables:"));
		$typeMap.put("view", __("Views:"));

		Map<String, Object> model = new HashMap<>();
		model.put("database", $database);
		model.put("table", $table);
		model.put("hidden", $hidden);
		model.put("types", $typeMap);
		model.put("item_type", $itemType);

		return this.template.render("navigation/item_unhide_dialog", model);
	}

	/**
	 * @param String $database Database name
	 * @param String|null $table Table name
	 * @return array
	 */
	private Map getHiddenItems(String $database, String $table) {
		return new HashMap(); /*
								 * TODO $navTable = Util.backquote($GLOBALS["cfgRelation"]["db"]) + '.' +
								 * Util.backquote($GLOBALS["cfgRelation"]["navigationhiding"]); $sqlQuery =
								 * 'SELECT `item_name`, `item_type` FROM ' + $navTable + ' WHERE `username`="' +
								 * this.dbi.escapeString($GLOBALS["cfg"]["Server"]["user"]) + '"' + ' AND
								 * `db_name`="' + this.dbi.escapeString($database) + '"' + ' AND `table_name`="'
								 * + (! empty($table) ? this.dbi.escapeString($table) : "") + '"'; $result =
								 * this.relation.queryAsControlUser($sqlQuery, false);
								 * 
								 * $hidden = []; if ($result) { while ($row = this.dbi.fetchArray($result)) {
								 * $type = $row["item_type"]; if (! isset($hidden[$type])) { $hidden[$type] =
								 * []; } $hidden[$type][] = $row["item_name"]; } } this.dbi.freeResult($result);
								 * return $hidden;
								 */
	}

	/**
	 * @return String Logo source
	 */
	private String getLogoSource(Globals GLOBALS) {
		String $pmaThemeImage = GLOBALS.getPmaThemeImage();

		if (!empty($pmaThemeImage) && new File($pmaThemeImage + "logo_left.png").exists()) {
			return $pmaThemeImage + "logo_left.png";
		} else if (!empty($pmaThemeImage) && new File($pmaThemeImage + "pma_logo2.png").exists()) {
			return $pmaThemeImage + "pma_logo2.png";
		}
		return "";
	}
}
