package org.javamyadmin.controllers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.helpers.Response;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Globals;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DatabasesController extends AbstractController {

	@RequestMapping(value = "/databases")
	public void databases() throws ServletException, IOException, SQLException, NamingException {

		super.prepareResponse();
		
		Map<String, Object> model = new HashMap<>();
//	      'is_create_database_shown' => $cfg['ShowCreateDb'],
//	      'has_create_database_privileges' => $is_create_db_priv,
//	      'has_statistics' => $this->hasStatistics,
//	      'database_to_create' => $db_to_create,
//	      'databases' => $databases['databases'],
//	      'total_statistics' => $databases['total_statistics'],
//	      'header_statistics' => $headerStatistics,
//	      'charsets' => $charsetsList,
//	      'database_count' => $this->databaseCount,
//	      'pos' => $this->position,
//	      'url_params' => $urlParams,
//	      'max_db_list' => $cfg['MaxDbList'],
//	      'has_master_replication' => $replication_info['master']['status'],
//	      'has_slave_replication' => $replication_info['slave']['status'],
//	      'is_drop_allowed' => $this->dbi->isSuperuser() || $cfg['AllowUserDropDatabase'],
//	      'pma_theme_image' => $pmaThemeImage,
//	      'text_dir' => $text_dir,

		String html = JtwigFactory.render("databases/index", model);
		pmaResponse.addHTML(html);
	}
}
