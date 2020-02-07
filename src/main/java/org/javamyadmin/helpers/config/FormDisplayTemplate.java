package org.javamyadmin.helpers.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.helpers.Util;
import org.springframework.beans.factory.annotation.Autowired;

import static org.javamyadmin.php.Php.*;

/**
 * PhpMyAdmin\Config\FormDisplayTemplate class
 *
 * @package PhpMyAdmin
 */
public class FormDisplayTemplate
{
    /**
     * @var int
     */
    public int group;

    /**
     * @var Config
     */
    @Autowired
    protected Config config;

    /**
     * @var Template
     */
    @Autowired
    public Template template;

    @Autowired
    protected HttpServletRequest httpRequest;
    @Autowired
    protected Util util;
    @Autowired
    protected Sanitize sanitize;
    
    /**
     * FormDisplayTemplate constructor.
     *
     * @param Config $config Config instance
     */
    public FormDisplayTemplate()
    {
    }

    static boolean $hasCheckPageRefresh = false;
    
    /**
     * Displays top part of the form
     *
     * @param String     $action       default: $_SERVER['REQUEST_URI']
     * @param String     $method       'post' or 'get'
     * @param array|null $hiddenFields array of form hidden fields (key: field name)
     *
     * @return String
     */
    public String displayFormTop(
        String $action /*= null*/,
        String $method /*= 'post'*/,
        Map $hiddenFields /*= null*/
    ) {
        
        if ($action == null) {
            $action = httpRequest.getRequestURI(); //FIXME it's the same than REQUEST_URI?
        }
        if (!"post".equals($method)) {
            $method = "get";
        }

        /**
         * We do validation on page refresh when browser remembers field values,
         * add a field with known value which will be used for checks.
         */
        if (! $hasCheckPageRefresh) {
            $hasCheckPageRefresh = true;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("method", $method);
        model.put("action", $action);
        model.put("has_check_page_refresh", $hasCheckPageRefresh);
        model.put("hidden_fields", $hiddenFields);
        return this.template.render("config/form_display/form_top", model);
    }

    /**
     * Displays form tabs which are given by an array indexed by fieldset id
     * ({@link self.displayFieldsetTop}), with values being tab titles.
     *
     * @param array $tabs tab names
     *
     * @return String
     */
    public String displayTabsTop(Map<String, String> $tabs)
    {
        List $items = new ArrayList<>();
        for (String $tabId : $tabs.keySet()) {
        	String $tabName = $tabs.get($tabId);
        	Map<String, Object> $tabContent = new HashMap<>();
        	Map<String, Object> $urlContent = new HashMap<>();
            $items.add($tabContent);
            $tabContent.put("content", htmlspecialchars($tabName));
            $tabContent.put("url", $urlContent);
            $urlContent.put("href", "#" + $tabId);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("class", "tabs responsivetable row");
        model.put("items", $items);
        String $htmlOutput = this.template.render("list/unordered", model);
        $htmlOutput += "<div class='tabs_contents row'>";
        return $htmlOutput;
    }

    /**
     * Displays top part of a fieldset
     *
     * @param String     $title       title of fieldset
     * @param String     $description description shown on top of fieldset
     * @param array|null $errors      error messages to display
     * @param array      $attributes  optional extra attributes of fieldset
     *
     * @return String
     */
    public String displayFieldsetTop(
        String $title /*= ""*/,
        String $description /*= ""*/,
        List $errors /*= null*/,
        Map $attributes /*= []*/
    ) {
        this.group = 0;

        if ($attributes == null) {
        	$attributes = new HashMap<>();
        }
        if (!$attributes.containsKey("class")) {
        	$attributes.put("class", "optbox");
        }

        Map<String, Object> model = new HashMap<>();
        model.put("attributes", $attributes);
        model.put("title", $title);
        model.put("description", $description);
        model.put("errors", $errors);
        
        return this.template.render("config/form_display/fieldset_top", model);
    }

    static Map<String, String> $icons;    // An array of IMG tags used further below in the function

    /**
     * Displays input field
     *
     * $opts keys:
     * o doc - (String) documentation link
     * o errors - error array
     * o setvalue - (String) shows button allowing to set predefined value
     * o show_restore_default - (boolean) whether show 'restore default' button
     * o userprefs_allow - whether user preferences are enabled for this field
     *                    (null - no support, true/false - enabled/disabled)
     * o userprefs_comment - (String) field comment
     * o values - key - value pairs for <select> fields
     * o values_escaped - (boolean) tells whether values array is already escaped
     *                    (defaults to false)
     * o values_disabled -  (array)list of disabled values (keys from values)
     * o comment - (String) tooltip comment
     * o comment_warning - (bool) whether this comments warns about something
     *
     * @param String     $path           config option path
     * @param String     $name           config option name
     * @param String     $type           type of config option
     * @param mixed      $value          current value
     * @param String     $description    verbose description
     * @param bool       $valueIsDefault whether value is default
     * @param array|null $opts           see above description
     *
     * @return String
     */
    public String displayInput(
        String $path,
        String $name,
        String $type,
        Object $value,
        String $description /*= ""*/,
        boolean $valueIsDefault /*= true*/,
        Map $opts /*= null*/
    ) {

        boolean $isSetupScript = "true".equals(this.config.get("is_setup"));
        if ($icons == null) { // if the static variables have not been initialised
            $icons = new HashMap<>();
            // Icon definitions:
            // The same indexes will be used in the $icons array.
            // The first element contains the filename and the second
            // element is used for the 'alt' and 'title' attributes.
            Map<String, List<String>> $iconInit = new LinkedHashMap<>();
            List<String> $iconInitEdit = new ArrayList<>();
            List<String> $iconInitHelp = new ArrayList<>();
            List<String> $iconInitReloads = new ArrayList<>();
            List<String> $iconInitTBlops = new ArrayList<>();
            $iconInit.put("edit", $iconInitEdit);
            $iconInit.put("help", $iconInitHelp);
            $iconInit.put("reload", $iconInitReloads);
            $iconInit.put("tblops", $iconInitTBlops);
            $iconInitEdit.add("b_edit");
            $iconInitEdit.add("");
            $iconInitHelp.add("b_help");
            $iconInitHelp.add(__("Documentation"));
            $iconInitReloads.add("s_reload");
            $iconInitReloads.add("");
            $iconInitTBlops.add("b_tblops");
            $iconInitTBlops.add("");
            
            if ($isSetupScript) {
                // When called from the setup script, we don"t have access to the
                // sprite-aware getImage() function because the PMA_theme class
                // has not been loaded, so we generate the img tags manually.
                for (String $k : $iconInit.keySet()) {
                	List<String> $v = $iconInit.get($k);
                    String $title = "";
                    String $first = $v.isEmpty() ? "" : $v.get(0);
                    if (! empty($first)) {
                        $title = " title='" + $first + "'";
                    }
                    $icons.put($k, String.format(
                        "<img alt='%s' src='%s'%s>",
                        $first,
                        "../themes/pmahomme/img/{$v[0]}.png",
                        $title
                    ));
                }
            } else {
                // In this case we just use getImage() because it"s available
            	for (String $k : $iconInit.keySet()) {
                	List<String> $v = $iconInit.get($k);
                    $icons.put($k, util.getImage(
                        $v.get(0),
                        $v.get(1)
                    ));
                }
            }
        }
        boolean $hasErrors = ! empty($opts.get("errors"));
        boolean $optionIsDisabled = ! $isSetupScript && "false".equals($opts.get("userprefs_allow"));
        String $nameId = "name='" + htmlspecialchars($path) + "' id='"
            + htmlspecialchars($path) + "'";
        String $fieldClass = $type == "checkbox" ? "checkbox" : "";
        if (! $valueIsDefault) {
            $fieldClass += ($fieldClass == "" ? "" : " ")
                + ($hasErrors ? "custom field-error" : "custom");
        }
        $fieldClass = !empty($fieldClass) ? " class='" + $fieldClass + "'" : "";
        String $trClass = this.group > 0
            ? "group-field group-field-" + this.group
            : "";
        if (":group".equals($opts.get("setvalue"))) {
            $opts.remove("setvalue");
            this.group++;
            $trClass = "group-header-field group-header-" + this.group;
        }
        if ($optionIsDisabled) {
            $trClass += (!empty($trClass) ? " " : "") + "disabled-field";
        }
        $trClass = !empty($trClass) ? " class='" + $trClass + "'" : "";

        String $htmlOutput = "<tr" + $trClass + ">";
        $htmlOutput += "<th>";
        $htmlOutput += "<label for='" + htmlspecialchars($path) + "'>" + htmlspecialchars_decode($name)
            + "</label>";

        if (! empty($opts.get("doc"))) {
            $htmlOutput += "<span class='doc'>";
            $htmlOutput += "<a href='" + $opts.get("doc")
                + "' target='documentation'>" + $icons.get("help") + "</a>";
            $htmlOutput += '\n';
            $htmlOutput += "</span>";
        }

        if ($optionIsDisabled) {
            $htmlOutput += "<span class='disabled-notice' title='";
            $htmlOutput += __(
                "This setting is disabled, it will not be applied to your configuration."
            );
            $htmlOutput += "'>" + __("Disabled") + "</span>";
        }

        if (! empty($description)) {
            $htmlOutput += "<small>" + $description + "</small>";
        }

        $htmlOutput += "</th>";
        $htmlOutput += "<td>";

        switch ($type) {
            case "text":
                $htmlOutput += "<input type='text' class='w-75' " + $nameId + $fieldClass
                + " value='" + htmlspecialchars((String)$value) + "'>";
                break;
            case "password":
                $htmlOutput += "<input type='password' class='w-75' " + $nameId + $fieldClass
                + " value='" + htmlspecialchars((String)$value) + "'>";
                break;
            case "short_text":
                // As seen in the reporting server (#15042) we sometimes receive
                // an array here. No clue about its origin nor content, so let"s avoid
                // a notice on htmlspecialchars().
                if (! is_array($value)) {
                    $htmlOutput += "<input type='text' size='25' " + $nameId
                    + $fieldClass + " value='" + htmlspecialchars((String)$value)
                    + "'>";
                }
                break;
            case "number_text":
                $htmlOutput += "<input type='number' " + $nameId + $fieldClass
                + " value='" + htmlspecialchars((String) $value) + "'>";
                break;
            case "checkbox":
                $htmlOutput += "<span" + $fieldClass + "><input type='checkbox' " + $nameId
                  + (!empty($value) ? " checked='checked'" : "") + "></span>";
                break;
            case "select":
                $htmlOutput += "<select class='w-75' " + $nameId + $fieldClass + ">";
                boolean $escape = ! ("true".equals( $opts.get("values_escaped")));
                Map $valuesDisabled = !empty($opts.get("values_disabled"))
                		? array_flip((Map)$opts.get("values_disabled")) : new HashMap();
                for (Object $optValueKey: ((Map)$opts.get("values")).keySet() ) {
                	Object $optValue = ((Map)$opts.get("values")).get($optValueKey);
                    // set names for boolean values
                    if ($optValue instanceof Boolean) { // FIXME
                        $optValue = (Boolean)$optValue ? __("Yes") : __("No") ;
                    }
                    // escape if necessary
                    String $display, $displayValue;
                    if ($escape) {
                        $display = htmlspecialchars((String) $optValue);
                        $displayValue = htmlspecialchars((String) $optValueKey);
                    } else {
                        $display = (String) $optValue;
                        $displayValue = (String) $optValueKey;
                    }
                    // compare with selected value
                    // boolean values are cast to integers when used as array keys
                    boolean $selected = $value instanceof Boolean // FIXME ...
                    		? $value.equals( $optValueKey )
                    		: $optValueKey == $value;
                    $htmlOutput += "<option value='" + $displayValue + "'";
                    if ($selected) {
                        $htmlOutput += " selected='selected'";
                    }
                    if (!empty($valuesDisabled.get($optValueKey))) {
                        $htmlOutput += " disabled='disabled'";
                    }
                    $htmlOutput += ">" + $display + "</option>";
                }
                $htmlOutput += "</select>";
                break;
            case "list":
                Map $val = (Map) $value;
                if ($val.containsKey("wrapper_params")) {
                	$val.remove("wrapper_params");
                }
                $htmlOutput += "<textarea cols='35' rows='5' " + $nameId + $fieldClass
                + ">" + htmlspecialchars(String.join("\n", $val.values())) + "</textarea>";
                break;
        }
        if ($isSetupScript
            && "true".equals($opts.get("userprefs_comment"))
        ) {
            $htmlOutput += "<a class='userprefs-comment' title='"
                + htmlspecialchars((String)$opts.get("userprefs_comment")) + "'>"
                + $icons.get("tblops") + "</a>";
        }
        if ("true".equals($opts.get("setvalue"))) {
            $htmlOutput += "<a class='set-value hide' href='#"
                + htmlspecialchars($path + "="  + $opts.get("setvalue")) + "' title='"
                + String.format(__("Set value: %s"), htmlspecialchars((String)$opts.get("setvalue")))
                + "'>" + $icons.get("edit") + "</a>";
        }
        if ("true".equals($opts.get("show_restore_default"))) {
            $htmlOutput += "<a class='restore-default hide' href='#" + $path + "' title='"
                + __("Restore default value") + "'>" + $icons.get("reload") + "</a>";
        }
        // this must match with displayErrors() in scripts/config.js
        if ($hasErrors) {
            $htmlOutput += "\n        <dl class='inline_errors'>";
            for (String $error : (List<String>)$opts.get("errors")) {
                $htmlOutput += "<dd>" + htmlspecialchars($error) + "</dd>";
            }
            $htmlOutput += "</dl>";
        }
        $htmlOutput += "</td>";
        if ($isSetupScript && !empty($opts.get("userprefs_allow"))) {
            $htmlOutput += "<td class='userprefs-allow' title='" +
                __("Allow users to customize this value") + "'>";
            $htmlOutput += "<input type='checkbox' name='" + $path
                + "-userprefs-allow' ";
            if ("true".equals($opts.get("userprefs_allow"))) {
                $htmlOutput += "checked='checked'";
            }
            $htmlOutput += ">";
            $htmlOutput += "</td>";
        } else if ($isSetupScript) {
            $htmlOutput += "<td>&nbsp;</td>";
        }
        $htmlOutput += "</tr>";
        return $htmlOutput;
    }

    /**
     * Display group header
     *
     * @param String $headerText Text of header
     *
     * @return String
     */
    public String displayGroupHeader(String $headerText)
    {
        this.group++;
        if (empty($headerText)) {
            return "";
        }
        int $colspan = "true".equals(this.config.get("is_setup")) ? 3 : 2;

        Map<String, Object> model = new HashMap<>();
        model.put("group", this.group);
        model.put("colspan", $colspan);
        model.put("header_text", $headerText);
        return this.template.render("config/form_display/group_header", model);
    }

    /**
     * Display group footer
     *
     * @return void
     */
    public void displayGroupFooter()
    {
        this.group--;
    }

    /**
     * Displays bottom part of a fieldset
     *
     * @param bool $showButtons Whether show submit and reset button
     *
     * @return String
     */
    public String displayFieldsetBottom(boolean $showButtons /*= true*/)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("show_buttons", $showButtons);
        model.put("is_setup", this.config.get("is_setup"));
        return this.template.render("config/form_display/fieldset_bottom", model);
    }

    /**
     * Closes form tabs
     *
     * @return String
     */
    public String displayTabsBottom()
    {
        return this.template.render("config/form_display/tabs_bottom");
    }

    /**
     * Displays bottom part of the form
     *
     * @return String
     */
    public String displayFormBottom()
    {
        return this.template.render("config/form_display/form_bottom");
    }

    /**
     * Appends JS validation code to $js_array
     *
     * @param String       $fieldId    ID of field to validate
     * @param String|array $validators validators callback
     * @param array        $jsArray    will be updated with javascript code
     *
     * @return void
     */
    public void addJsValidate(String $fieldId, List<List<String>> $validators, List<String> $jsArray)
    {
        for (List<String> $validator : $validators) {
            // ?!? $validator = (array) $validator;
            String $vName = array_shift($validator);
            List<String> $vArgs = new ArrayList<>();
            for (String $arg : $validator) {
                $vArgs.add( sanitize.escapeJsString($arg) );
            }
            String $vArgsStr = $vArgs.size() > 0 ? ", ['" + String.join("', '", $vArgs) + "']" : "";
            $jsArray.add( "registerFieldValidator('" + $fieldId + "', '" + $vName + "', true" + $vArgsStr + ")");
        }
    }

    /**
     * Displays JavaScript code
     *
     * @param array $jsArray lines of javascript code
     *
     * @return String
     */
    public String displayJavascript(List<String> $jsArray)
    {
        if (empty($jsArray)) {
            return "";
        }

        Map<String, Object> model = new HashMap<>();
        model.put("js_array", $jsArray);
        return this.template.render("javascript/display", model);
    }

    /**
     * Displays error list
     *
     * @param String $name      Name of item with errors
     * @param array  $errorList List of errors to show
     *
     * @return String HTML for errors
     */
    public String displayErrors(String $name, List $errorList)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("name", $name);
        model.put("error_list", $errorList);
        return this.template.render("config/form_display/errors", model);
    }
}
