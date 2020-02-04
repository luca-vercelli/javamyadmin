package org.javamyadmin.helpers.config.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.config.ConfigFile;
import org.javamyadmin.helpers.config.FormDisplay;

/**
 * Base form for user preferences
 *
 * @package PhpMyAdmin
 */
public abstract class BaseForm extends FormDisplay {
    /**
     * Constructor
     *
     * @param ConfigFile $cf       Config file instance
     * @param int|null   $serverId 0 if new server, validation; >= 1 if editing a server
     */
    public BaseForm(ConfigFile $cf, int $serverId /*= null*/)
    {
        super($cf);
        Map<String, Map> forms = getForms();
        for (String $formName : forms.keySet()) {
        	Map $form = forms.get($formName);
            this.registerForm($formName, $form, $serverId);
        }
    }

    /**
     * List of available forms, each form is described as an array of fields to display.
     * Fields MUST have their counterparts in the $cfg array.
     *
     * To define form field, use the notation below:
     * $forms["Form group"]["Form name"] = array("Option/path");
     *
     * You can assign default values set by special button ("set value: ..."), eg.:
     * "Servers/1/pmadb" => "phpmyadmin"
     *
     * To group options, use:
     * ":group:" . __("group name") // just define a group
     * or
     * "option" => ":group" // group starting from this option
     * End group blocks with:
     * ":group:end"
     *
     * @todo This should be abstract, but that does not work in PHP 5
     *
     * @return array
     */
    public static Map<String, Map> getForms()
    {
        return new HashMap<>();
    }

    /**
     * Returns list of fields used in the form.
     *
     * @return string[]
     */
    public static List<String> getFields()
    {
    	// FIXME ... to be tested...
        List<String> $names = new ArrayList<>();
        for (Map $form : getForms().values()) {
            for (Object $k : $form.keySet()) {
            	Object $v = $form.get($k);
                $names.add( $k instanceof Integer ? (String)$v : (String)$k );
            }
        }
        return $names;
    }

    /**
     * Returns name of the form
     *
     * @todo This should be abstract, but that does not work in PHP 5
     *
     * @return string
     */
    public static String getName()
    {
        return "";
    }
}
