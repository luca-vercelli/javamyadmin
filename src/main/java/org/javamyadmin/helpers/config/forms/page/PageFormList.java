package org.javamyadmin.helpers.config.forms.page;

import org.javamyadmin.helpers.config.ConfigFile;
import org.javamyadmin.helpers.config.forms.BaseFormList;

/**
 * Class PageFormList
 * @package PhpMyAdmin\Config\Forms\Page
 */
public class PageFormList extends BaseFormList {

    public PageFormList(ConfigFile $cf) {
		super($cf);
	}

	/**
     * @var array
     */
    protected static String[] all = new String[] {
        "Browse",
        "DbStructure",
        "Edit",
        "Export",
        "Import",
        "Navi",
        "Sql",
        "TableStructure",
    };
    
    /**
     * @var string
     */
    protected static String $ns = "org.javamyadmin.helpers.config.forms.page";
}
