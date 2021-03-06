package org.javamyadmin.controllers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.helpers.LanguageManager;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.ThemeManager;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.helpers.server.Select;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @see https://github.com/phpmyadmin/phpmyadmin/blob/master/index.php and
 * https://github.com/phpmyadmin/phpmyadmin/blob/master/libraries/routes.php for
 * original dispatcher
 *
 */
@RestController
public class HomeController extends AbstractController {
	
	@Autowired
	private LanguageManager $languageManager;
	@Autowired
	private ThemeManager themeManager;
	@Autowired
	private Select select;
	@Autowired
	private Util util;
	
	@RequestMapping(value = {"/", "/index.html", "/index.jsp"})
	public void root() throws IOException {
		httpResponse.sendRedirect("Home");
	}
	
    @RequestMapping(value = "/Home")
	public void index()
			throws ServletException, IOException, SQLException, NamingException {
		
    	commons.execute();
		
		if (response.isAjax() && !empty(httpRequest.getParameter("access_time"))) {
			response.response();
			return;
		}

		// @see https://docs.phpmyadmin.net/en/latest/faq.html#faq1-34
		if (!empty(httpRequest.getParameter("db"))) {
			if (!empty(httpRequest.getParameter("table"))) {
				httpResponse.sendRedirect("/sql");
			} else {
				httpResponse.sendRedirect("/database/structure?db=" + httpRequest.getParameter("db"));
			}
			return;
		}

		if (GLOBALS.getServer() > 0) {
			// @see libraries/server_common.inc.php
			GLOBALS.setUrlQuery(url.getCommon(null));
			GLOBALS.setErrUrl(url.getFromRoute("/"));
			GLOBALS.setIsGrantuser(dbi.isUserType("grant"));
			GLOBALS.setIsCreateuser(dbi.isUserType("create"));
		}

		String $displayMessage = "";
		if (!empty(GLOBALS.getMessage())) {
			$displayMessage = GLOBALS.getMessage(); // Util.getMessage(message);
			GLOBALS.setMessage(null);
		}

		String $partialLogout = "";
		if (httpRequest.getSession().getAttribute("partial_logout") != null) {
			$partialLogout = Message.success(__("You were logged out from one server, to logout completely "
					+ "from phpMyAdmin, you need to logout from all servers.")).getDisplay();
			httpRequest.getSession().removeAttribute("partial_logout");
		}

		String $syncFavoriteTables = "";
		// TODO syncFavoriteTables = RecentFavoriteTable.getInstance("favorite")
		// .getHtmlSyncFavoriteTables();

		boolean $hasServerSelection = false;
		String $serverSelection = "";
		String $changePassword = "";
		List<String> $charsetsList = new ArrayList<>();
		String $userPreferences = "";
		
		boolean $hasServer = GLOBALS.getServer() > 0 || !empty(config.get("Servers")) && ((Map)config.get("Servers")).size() > 1;
        if ($hasServer) {
            $hasServerSelection = "0".equals(config.get("ServerDefault"))
                || ("false".equals(config.get("NavigationDisplayServers"))
                		&& ((Map)config.get("Servers")).size() > 1
                		|| (GLOBALS.getServer() == 0 && ((Map)config.get("Servers")).size() == 1));
            if ($hasServerSelection) {
                $serverSelection = select.render(true, true, GLOBALS, $_SESSION, httpRequest);
            }

            if (GLOBALS.getServer() > 0) {
                /* TODO
                $checkUserPrivileges = new CheckUserPrivileges(this.dbi);
                $checkUserPrivileges.getPrivileges();

                if ((config.get("Server"]["auth_type"] != "config") && config.get("ShowChgPassword"]) {
                    $changePassword = this.template.render("list/item", [
                        "content" => Util.getImage("s_passwd") . " " . __(
                            "Change password"
                        ),
                        "id" => "li_change_password",
                        "class" => "no_bullets",
                        "url" => [
                            "href" => url.getFromRoute("/user_password"),
                            "target" => null,
                            "id" => "change_password_anchor",
                            "class" => "ajax",
                        ],
                        "mysql_help_page" => null,
                    ]);
                }

                $charsets = Charsets.getCharsets(this.dbi, config.get("Server"]["DisableIS"]);
                $collations = Charsets.getCollations(this.dbi, config.get("Server"]["DisableIS"]);
                $charsetsList = [];
                // @var Charset $charset
                foreach ($charsets as $charset) {
                    $collationsList = [];
                    // @var Collation $collation
                    foreach ($collations[$charset.getName()] as $collation) {
                        $collationsList[] = [
                            "name" => $collation.getName(),
                            "description" => $collation.getDescription(),
                            "is_selected" => $collation_connection === $collation.getName(),
                        ];
                    }
                    $charsetsList[] = [
                        "name" => $charset.getName(),
                        "description" => $charset.getDescription(),
                        "collations" => $collationsList,
                    ];
                }
				*/
            
	            Map<String,Object> model0 = new HashMap<>();
	        	model0.put("content", util.getImage("b_tblops") + " " + __(
                        "More settings"
                    ));
            	model0.put("id", "li_user_preferences");
            	model0.put("class", "no_bullets");
	            Map<String,Object> modelUrl = new HashMap<>();
            	modelUrl.put("href", url.getFromRoute("/preferences/manage"));
            	model0.put("url", modelUrl);
                $userPreferences = template.render("list/item", model0);
            }
        }

		String $languageSelector = "";
        
		if (empty(config.get("Lang")) && $languageManager.hasChoice()) {
            $languageSelector = $languageManager.getSelectorDisplay(GLOBALS);
        }

        String $themeSelection = "";
        if (!empty(config.get("ThemeManager"))) {
            $themeSelection = themeManager.getHtmlSelectBox();
        }

        Map<String, Object> $databaseServer = new HashMap<>();
        if (GLOBALS.getServer() > 0 && "true".equals(config.get("ShowServerInfo"))) {
            String $hostInfo = "";
            if (! empty(((Map) config.get("Server")).get("verbose"))) {
                $hostInfo += ((Map) config.get("Server")).get("verbose");
                if ("true".equals(config.get("ShowServerInfo"))) {
                    $hostInfo += " (";
                }
            }
            if ("true".equals(config.get("ShowServerInfo")) || empty(((Map) config.get("Server")).get("verbose"))) {
                // TODO $hostInfo += this.dbi.getHostInfo();
            }
            if (! empty(((Map) config.get("Server")).get("verbose")) && "true".equals(config.get("ShowServerInfo"))) {
                $hostInfo += ")";
            }

            /*
            String $serverCharset = Charsets.getServerCharset($this.dbi, config.get("Server").get("DisableIS"));
            */
            $databaseServer.put("host", $hostInfo);
            // More properties not supported
            // $databaseServer.put("type", Util.getServerType());
            // $databaseServer.put("connection", Util.getServerSSL());
            // $databaseServer.put("version", this.dbi.getVersionString() . " - " . $this.dbi.getVersionComment());
            // $databaseServer.put("protocol", this.dbi.getProtoInfo());
            // $databaseServer.put("user", this.dbi.fetchValue("SELECT USER();"));
            // $databaseServer.put("charset", serverCharset.getDescription() + " (" + $serverCharset.getName() + ")");
        }

		WebServer $webServer = new WebServer();
        if ("true".equals(config.get("ShowServerInfo"))) {
            $webServer.setSoftware(InetAddress.getLocalHost().getHostName());
            // More properties not supported
        }
        
		Map<String, Object> model = new HashMap<>();
		model.put("message", $displayMessage);
		model.put("partial_logout", $partialLogout);
		model.put("is_git_revision", false);
		model.put("server", GLOBALS.getServer());
		model.put("sync_favorite_tables", $syncFavoriteTables);
		model.put("has_server", $hasServer);
		model.put("is_demo", config.get("DBG.demo"));
		model.put("has_server_selection", $hasServerSelection);
		model.put("server_selection", $serverSelection != null ? $serverSelection : "");
		model.put("change_password", $changePassword);
		model.put("charsets", $charsetsList);
		model.put("language_selector", $languageSelector);
		model.put("theme_selection", $themeSelection);
		model.put("user_preferences", $userPreferences);
		model.put("database_server", $databaseServer);
		model.put("web_server", $webServer);
		model.put("php_info", null);
		model.put("is_version_checked", config.get("VersionCheck"));
		model.put("phpmyadmin_version", Globals.getPmaVersion());
		model.put("config_storage_message", null);

		String html = template.render("home/index", model);
		response.addHTML(html);
		response.response();
	}
	
	public final static class WebServer {

		private String software;

		public String getSoftware() {
			return software;
		}

		public void setSoftware(String software) {
			this.software = software;
		}
		
	}
}