package org.javamyadmin.helpers.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.annotation.Autowired;

import static org.javamyadmin.php.Php.*;

/**
 * Form management class, displays and processes forms
 *
 * @package PhpMyAdmin
 */
public class FormDisplay {
    /**
     * ConfigFile instance
     * @var ConfigFile
     */
    private ConfigFile _configFile;

    /**
     * Form list
     * @var Form[]
     */
    private Map<String, Form> _forms = new HashMap<>();

    /**
     * Stores validation errors, indexed by paths
     * [ Form_name ] is an array of form errors
     * [path] is a string storing error associated with single field
     * @var array
     */
    private Map<String, List<String>> _errors = new HashMap<>();

    /**
     * Paths changed so that they can be used as HTML ids, indexed by paths
     * @var array
     */
    private Map<String, String> _translatedPaths = new HashMap<>();

    /**
     * Server paths change indexes so we define maps from current server
     * path to the first one, indexed by work path
     * @var array
     */
    private Map<String, String> _systemPaths = new HashMap<>();

    /**
     * Language strings which will be sent to Messages JS variable
     * Will be looked up in $GLOBALS: str{value} or strSetup{value}
     * @var array
     */
    private Map<String, String> _jsLangStrings = new HashMap<>();

    /**
     * Tells whether forms have been validated
     * @var bool
     */
    private boolean _isValidated = true;

    /**
     * Dictionary with user preferences keys
     * @var array|null
     */
    private Map _userprefsKeys = new HashMap<>();

    /**
     * Dictionary with disallowed user preferences keys
     * @var array
     */
    private Map<String, Boolean> _userprefsDisallow = new HashMap<>();

    /**
     * @var FormDisplayTemplate
     */
    @Autowired
    private FormDisplayTemplate formDisplayTemplate;
    @Autowired
    private Globals $GLOBALS;
    @Autowired
    private HttpServletRequest httpRequest;
    
    /**
     * Constructor
     *
     * @param ConfigFile $cf Config file instance
     */
    public FormDisplay(ConfigFile $cf)
    {
        this.formDisplayTemplate = new FormDisplayTemplate($GLOBALS.getConfig());
        this._jsLangStrings = new HashMap<>();
        this._jsLangStrings.put("error_nan_p", __("Not a positive number!"));
        this._jsLangStrings.put("error_nan_nneg", __("Not a non-negative number!"));
        this._jsLangStrings.put("error_incorrect_port", __("Not a valid port number!"));
        this._jsLangStrings.put("error_invalid_value", __("Incorrect value!"));
        this._jsLangStrings.put("error_value_lte", __("Value must be less than or equal to %s!"));
        // initialize validators
        // TODO Validator.getValidators(this._configFile);
    }

    /**
     * Returns {@link ConfigFile} associated with this instance
     *
     * @return ConfigFile
     */
    public ConfigFile getConfigFile()
    {
        return this._configFile;
    }

    /**
     * Registers form in form manager
     *
     * @param string $formName Form name
     * @param array  $form     Form data
     * @param int    $serverId 0 if new server, validation; >= 1 if editing a server
     *
     * @return void
     */
    public void registerForm(String $formName, Map $form, Integer $serverId /*= null*/)
    {
        this._forms.put($formName, new Form(
            $formName,
            $form,
            this._configFile,
            $serverId
        ));
        this._isValidated = false;
        for (String $path : this._forms.get($formName).fields.values()) {
            String $workPath = ($serverId == null)
                ? $path
                : $path.replace("Servers/1/", "Servers/$serverId/");
            this._systemPaths.put($workPath, $path);
            this._translatedPaths.put($workPath, $workPath.replace('/', '-'));
        }
    }

    public void registerForm(String $formName, Map $form) {
    	registerForm($formName, $form, null);
    }
    
    /**
     * Processes forms, returns true on successful save
     *
     * @param bool $allowPartialSave allows for partial form saving
     *                               on failed validation
     * @param bool $checkFormSubmit  whether check for $_POST['submit_save']
     *
     * @return boolean whether processing was successful
     */
    public boolean process(boolean $allowPartialSave /*= true*/, boolean $checkFormSubmit /*= true*/)
    {
        if ($checkFormSubmit && empty(httpRequest.getParameter("submit_save"))) {
            return false;
        }

        // save forms
        if (this._forms.size() > 0) {
            return this.save(this._forms.keySet(), $allowPartialSave);
        }
        return false;
    }

    public boolean process(boolean $allowPartialSave) {
    	return process($allowPartialSave, true);
    }

    public boolean process() {
    	return process(true, true);
    }
    
    /**
     * Runs validation for all registered forms
     *
     * @return void
     */
    private void _validate()
    {
        if (this._isValidated) {
            return;
        }
    	/* TODO 

        List $paths = new ArrayList<>();
        List $values = new ArrayList<>();
        for (Form $form : this._forms.values()) {
            // @var Form $form
            $paths.add($form.name);
            // collect values and paths
            for (String $path : $form.fields.values()) {
                int $workPath = $path.indexOf(this._systemPaths);
                $values.put($path, this._configFile.getValue($workPath));
                $paths.add($path);
            }
        }

        // run validation
        $errors = Validator.validate(
            this._configFile,
            $paths,
            $values,
            false
        );

        // change error keys from canonical paths to work paths
        if ($errors != null && !$errors.isEmpty) {
            this._errors = [];
            for ($errors as $path => $errorList) {
                $workPath = array_search($path, this._systemPaths);
                // field error
                if (! $workPath) {
                    // form error, fix path
                    $workPath = $path;
                }
                this._errors[$workPath] = $errorList;
            }
        }*/
        this._isValidated = true;
    }

    /**
     * Outputs HTML for the forms under the menu tab
     *
     * @param bool  $showRestoreDefault whether to show "restore default"
     *                                  button besides the input field
     * @param array $jsDefault          stores JavaScript code
     *                                  to be displayed
     * @param array $js                 will be updated with javascript code
     * @param bool  $showButtons        whether show submit and reset button
     *
     * @return string
     */
    private String _displayForms(
        boolean $showRestoreDefault,
        List<String> $jsDefault,
        List<String> $js,
        boolean $showButtons
    ) {
        String $htmlOutput = "";
        // TODO $validators = Validator.getValidators(this._configFile);

        for (Form $form : this._forms.values()) {
            /** @var Form $form */
            List<String> $formErrors = this._errors.get($form.name);
            
            Map<String, String> params = new HashMap<>();
            params.put("id", $form.name);
            $htmlOutput += this.formDisplayTemplate.displayFieldsetTop(
                Descriptions.get("Form_" + $form.name),
                Descriptions.get("Form_" + $form.name, "desc"),
                $formErrors,
                params
            );

            for (String $field : $form.fields.keySet()) {
            	String $path = $form.fields.get($field);
                String $workPath = array_search($path, this._systemPaths);
                String $translatedPath = this._translatedPaths.get($workPath);
                // always true/false for user preferences display
                // otherwise null
                Boolean $userPrefsAllow = this._userprefsKeys.containsKey($path)
                    ? ! this._userprefsDisallow.containsKey($path)
                    : null;
                // display input
                $htmlOutput += this._displayFieldInput(
                    $form,
                    $field,
                    $path,
                    $workPath,
                    $translatedPath,
                    $showRestoreDefault,
                    $userPrefsAllow,
                    $jsDefault
                );
                // register JS validators for this field
                /* TODO if (isset($validators[$path])) {
                    this.formDisplayTemplate.addJsValidate($translatedPath, $validators[$path], $js);
                }*/
            }
            $htmlOutput += this.formDisplayTemplate.displayFieldsetBottom($showButtons);
        }
        return $htmlOutput;
    }

    static boolean $jsLangSent = false;
    
    /**
     * Outputs HTML for forms
     *
     * @param bool       $tabbedForm         if true, use a form with tabs
     * @param bool       $showRestoreDefault whether show "restore default" button
     *                                       besides the input field
     * @param bool       $showButtons        whether show submit and reset button
     * @param string     $formAction         action attribute for the form
     * @param array|null $hiddenFields       array of form hidden fields (key: field
     *                                       name)
     *
     * @return string HTML for forms
     */
    public String getDisplay(
        boolean $tabbedForm /*= false*/,
        boolean $showRestoreDefault /*= false*/,
        boolean $showButtons /*= true*/,
        String $formAction /*= null*/,
        Map $hiddenFields /*= null*/
    ) {

        String $htmlOutput = "";

        List<String> $js = new ArrayList<>();
        List<String> $jsDefault = new ArrayList<>();

        $htmlOutput += this.formDisplayTemplate.displayFormTop($formAction, "post", $hiddenFields);

        if ($tabbedForm) {
            Map<String, String> $tabs = new HashMap<>();
            for (Form $form : this._forms.values()) {
                $tabs.put($form.name, Descriptions.get("Form_$form.name"));
            }
            $htmlOutput += this.formDisplayTemplate.displayTabsTop($tabs);
        }

        // validate only when we aren't displaying a "new server" form
        boolean $isNewServer = false;
        for (Form $form : this._forms.values()) {
            /** @var Form $form */
            if ($form.index == 0) {
                $isNewServer = true;
                break;
            }
        }
        if (! $isNewServer) {
            this._validate();
        }

        // user preferences
        this._loadUserprefsInfo();

        // display forms
        $htmlOutput += this._displayForms(
            $showRestoreDefault,
            $jsDefault,
            $js,
            $showButtons
        );

        if ($tabbedForm) {
            $htmlOutput += this.formDisplayTemplate.displayTabsBottom();
        }
        $htmlOutput += this.formDisplayTemplate.displayFormBottom();

        // if not already done, send strings used for validation to JavaScript
        if (! $jsLangSent) {
            $jsLangSent = true;
            List<String> $jsLang = new ArrayList<>();
            for (Entry<String, String> entry : this._jsLangStrings.entrySet()) {
            	String $strName = entry.getKey();
            	String $strValue = entry.getValue();
                $jsLang.add( "'$strName': '" + Sanitize.jsFormat($strValue, false) + '\'');
            }
            $js.add( "$.extend(Messages, {\n\t"
                + String.join(",\n\t", $jsLang) + "})");
        }

        $js.add( "$.extend(defaultValues, {\n\t"
            + String.join(",\n\t", $jsDefault) + "})" );
        $htmlOutput += this.formDisplayTemplate.displayJavascript($js);

        return $htmlOutput;
    }

    /**
     * Prepares data for input field display and outputs HTML code
     *
     * @param Form      $form               Form object
     * @param string    $field              field name as it appears in $form
     * @param string    $systemPath         field path, eg. Servers/1/verbose
     * @param string    $workPath           work path, eg. Servers/4/verbose
     * @param string    $translatedPath     work path changed so that it can be
     *                                      used as XHTML id
     * @param bool      $showRestoreDefault whether show "restore default" button
     *                                      besides the input field
     * @param bool|null $userPrefsAllow     whether user preferences are enabled
     *                                      for this field (null - no support,
     *                                      true/false - enabled/disabled)
     * @param array     $jsDefault          array which stores JavaScript code
     *                                      to be displayed
     *
     * @return string|null HTML for input field
     */
    private String _displayFieldInput(
        Form $form,
        String $field,
        String $systemPath,
        String $workPath,
        String $translatedPath,
        boolean $showRestoreDefault,
        Boolean $userPrefsAllow,
        List<String> $jsDefault
    ) {
        String $name = Descriptions.get($systemPath);
        String $description = Descriptions.get($systemPath, "desc");

        Object $value = this._configFile.get($workPath);
        Object $valueDefault = this._configFile.getDefault($systemPath);
        boolean $valueIsDefault = false;
        if ($value == null || $value == $valueDefault) {
            $value = $valueDefault;
            $valueIsDefault = true;
        }

        Map $opts = new HashMap<>();
        $opts.put("doc", this.getDocLink($systemPath));
        $opts.put("show_restore_default", $showRestoreDefault);
        $opts.put("userprefs_allow", $userPrefsAllow);
        $opts.put("userprefs_comment", Descriptions.get($systemPath, "cmt"));
        if ($form.vDefault.containsKey($systemPath)) {
            $opts.put("setvalue", (String) $form.vDefault.get($systemPath));
        }

        if (this._errors.containsKey($workPath)) {
            $opts.put("errors", this._errors.get($workPath));
        }

        String $type = "";
        switch ($form.getOptionType($field)) {
            case "string":
                $type = "text";
                break;
            case "short_string":
                $type = "short_text";
                break;
            case "double":
            case "integer":
                $type = "number_text";
                break;
            case "boolean":
                $type = "checkbox";
                break;
            case "select":
                $type = "select";
                $opts.put("values", $form.getOptionValueList($form.fields.get($field)));
                break;
            case "array":
                $type = "list";
                break;
            case "group":
                // :group:end is changed to :group:end:{unique id} in Form class
                String $htmlOutput = "";
                if (!$field.substring(7, 4).equals("end:")) {
                    $htmlOutput += this.formDisplayTemplate.displayGroupHeader(
                        $field.substring(7)
                    );
                } else {
                    this.formDisplayTemplate.displayGroupFooter();
                }
                return $htmlOutput;
            case "NULL":
                trigger_error("Field $systemPath has no type", E_USER_WARNING);
                return null;
        }

        // detect password fields
        if ($type.equals("text")
            && ($translatedPath.endsWith("-password")
               || $translatedPath.endsWith("pass")
               || $translatedPath.endsWith("Pass"))
        ) {
            $type = "password";
        }

        // TrustedProxies requires changes before displaying
        if ($systemPath == "TrustedProxies") {
        	Map<String, String> $valueMap = (Map)$value;
            for (Entry<String, String> entry: $valueMap.entrySet()) {
            	String $ip = entry.getKey();
            	String $v = entry.getValue();
                if (! $ip.matches("/^-\\d+$/")) {
                    $v = $ip + ": " + $v;
                }
            }
        }
        this._setComments($systemPath, $opts);

        // send default value to form"s JS
        String $jsLine = "\"" + $translatedPath + "\": ";
        switch ($type) {
            case "text":
            case "short_text":
            case "number_text":
            case "password":
                $jsLine += "\"" + Sanitize.escapeJsString((String) $valueDefault) + "\"";
                break;
            case "checkbox":
                $jsLine += !empty($valueDefault) ? "true" : "false";
                break;
            case "select":
                /*$valueDefaultJs = is_bool($valueDefault)
                ? (int) $valueDefault
                : $valueDefault;*/
                String $valueDefaultJs = (String) $valueDefault; //FIXME
                $jsLine += "[\"" + Sanitize.escapeJsString($valueDefaultJs) + "\"]";
                break;
            case "list":
                Map $val = (Map) $valueDefault;
                $val.remove("wrapper_params");
                $jsLine += "\"" + Sanitize.escapeJsString(String.join("\n", $val.values()))
                + "\"";
                break;
        }
        $jsDefault.add($jsLine);

        return this.formDisplayTemplate.displayInput(
            $translatedPath,
            $name,
            $type,
            $value,
            $description,
            $valueIsDefault,
            $opts
        );
    }

    /**
     * Displays errors
     *
     * @return string|null HTML for errors
     */
    public String displayErrors()
    {
        this._validate();
        if (this._errors.isEmpty()) {
            return null;
        }

        String $htmlOutput = "";

        for (Entry<String, List<String>> entry : this._errors.entrySet()) {
        	String $systemPath = entry.getKey(); 
        	List<String> $errorList = entry.getValue();
        	String $name;
            if (this._systemPaths.containsKey($systemPath)) {
                $name = Descriptions.get(this._systemPaths.get($systemPath));
            } else {
                $name = Descriptions.get("Form_" + $systemPath);
            }
            $htmlOutput += this.formDisplayTemplate.displayErrors($name, $errorList);
        }

        return $htmlOutput;
    }

    /**
     * Reverts erroneous fields to their default values
     *
     * @return void
     */
    public void fixErrors()
    {
        this._validate();
        if (this._errors.isEmpty()) {
            return;
        }

        ConfigFile $cf = this._configFile;
        for (String $workPath : this._errors.keySet()) {
            if (! (this._systemPaths.containsKey($workPath))) {
                continue;
            }
            String $canonicalPath = this._systemPaths.get($workPath);
            $cf.set($workPath, $cf.getDefault($canonicalPath));
        }
    }

    /**
     * Validates select field and casts $value to correct type
     *
     * @param string $value   Current value
     * @param array  $allowed List of allowed values
     *
     * @return bool
     */
    private boolean _validateSelect(String $value, List $allowed)
    {
    	return true; //TODO ?
    	/*
        $valueCmp = is_bool($value)
            ? (int) $value
            : $value;
        for ($allowed as $vk => $v) {
            // equality comparison only if both values are numeric or not numeric
            // (allows to skip 0 == "string" equalling to true)
            // or identity (for string-string)
            if (($vk == $value && ! (is_numeric($valueCmp) xor is_numeric($vk)))
                || $vk === $value
            ) {
                // keep boolean value as boolean
                if (! is_bool($value)) {
                    settype($value, gettype($vk));
                }
                return true;
            }
        }
        return false;*/
    }

    /**
     * Validates and saves form data to session
     *
     * @param array|string $forms            array of form names
     * @param bool         $allowPartialSave allows for partial form saving on
     *                                       failed validation
     *
     * @return boolean true on success (no errors and all saved)
     */
    public boolean save(Collection<String> $forms, boolean $allowPartialSave /*= true*/)
    {
        boolean $result = true;

        Map<String, String> $values = new HashMap<>();
        Map<String, String> $toSave = new HashMap<>();
        boolean $isSetupScript = "true".equals($GLOBALS.getConfig().get("is_setup"));
        if ($isSetupScript) {
            this._loadUserprefsInfo();
        }

        this._errors = new HashMap<>();
        for (String $formName : $forms) {
            Form $form;
            if (this._forms.containsKey($formName)) {
                $form = this._forms.get($formName);
            } else {
                continue;
            }
            // get current server id
            Integer $changeIndex = ($form.index == 0)
                ? this._configFile.getServerCount() + 1
                : null;
            // grab POST values
            for (Entry<String, String> entry : $form.fields.entrySet()) {
            	String $field = entry.getKey();
            	String $systemPath = entry.getValue();
            	String $workPath = array_search($systemPath, this._systemPaths);
            	String $key = this._translatedPaths.get($workPath);
            	String $type = $form.getOptionType($field);

                // skip groups
                if ($type == "group") {
                    continue;
                }

                // ensure the value is set
                if (empty(httpRequest.getParameter($key))) {
                    // checkboxes aren"t set by browsers if they"re off
                    if ($type == "boolean") {
                        // TODO $_POST[$key] = false;
                    } else {
                        this._errors.get($form.name).add(String.format(
                            __("Missing data for %s"),
                            "<i>" + Descriptions.get($systemPath) + "</i>"
                        ));
                        $result = false;
                        continue;
                    }
                }

                // user preferences allow/disallow
                if ($isSetupScript
                    && (this._userprefsKeys.containsKey($systemPath))
                ) {
                    if (!empty(httpRequest.getParameter($key + "-userprefs-allow"))
                    ) {
                        this._userprefsDisallow.remove($systemPath);
                    } else {
                        this._userprefsDisallow.put($systemPath, true);
                    }
                }

                /* FIXME I cannot change POST request in Java !
                // cast variables to correct type
                switch ($type) {
                    case "double":
                        $_POST[$key] = Util.requestString($_POST[$key]);
                        settype($_POST[$key], "float");
                        break;
                    case "boolean":
                    case "integer":
                        if (!empty(httpRequest.getParameter($key))) {
                            $_POST[$key] = Util.requestString(httpRequest.getParameter($key));
                            settype($_POST[$key], $type);
                        }
                        break;
                    case "select":
                        $successfullyValidated = this._validateSelect(
                            $_POST[$key],
                            $form.getOptionValueList($systemPath)
                        );
                        if (! $successfullyValidated) {
                            this._errors[$workPath][] = __("Incorrect value!");
                            $result = false;
                            // "continue" for the $form.fields for-loop
                            continue 2;
                        }
                        break;
                    case "string":
                    case "short_string":
                        $_POST[$key] = Util.requestString($_POST[$key]);
                        break;
                    case "array":
                        // eliminate empty values and ensure we have an array
                        $postValues = is_array($_POST[$key])
                        ? $_POST[$key]
                        : explode("\n", $_POST[$key]);
                        $_POST[$key] = [];
                        this._fillPostArrayParameters($postValues, $key);
                        break;
                }*/

                // now we have value with proper type
                $values.put($systemPath, httpRequest.getParameter($key));
                if ($changeIndex > 0) {
                    $workPath = $workPath.replace(
                        "Servers/$form.index/",
                        "Servers/$changeIndex/"
                    );
                }
                $toSave.put($workPath, $systemPath);
            }
        }

        // save forms
        if (! $allowPartialSave && ! empty(this._errors)) {
            // don"t look for non-critical errors
            this._validate();
            return $result;
        }

        for (Entry<String, String> entry : $toSave.entrySet()) {
        	String $workPath = entry.getKey();
        	String $path = entry.getValue();
            /* TODO TrustedProxies requires changes before saving
            if ($path.equals("TrustedProxies")) {
                Map<String, String> $proxies = new HashMap<>();
                int $i = 0;
                for (String $value : $values.get($path).values()) {
                	Pattern pattern = Pattern.compile("^(.+):(?:[ ]?)(\\w+)$");
                	Matcher matcher = pattern.matcher($value); 
                	 if (matcher.matches()) {
                        // correct "IP: HTTP header" pair
                        String $ip = matcher.group(1);
                        $proxies.put($ip, matcher.group(2).trim());
                    } else {
                        // save also incorrect values
                        $proxies.put("-$i", $value);
                        $i++;
                    }
                  }
                
                $values.put($path, $proxies);
            }*/
            this._configFile.set($workPath, $values.get($path), $path);
        }
        if ($isSetupScript) {
            this._configFile.set(
                "UserprefsDisallow",
                this._userprefsDisallow.keySet()
            );
        }

        // don"t look for non-critical errors
        this._validate();

        return $result;
    }

    /**
     * Tells whether form validation failed
     *
     * @return boolean
     */
    public boolean hasErrors()
    {
        return this._errors.size() > 0;
    }


    /**
     * Returns link to documentation
     *
     * @param string $path Path to documentation
     *
     * @return string
     */
    public String getDocLink(String $path)
    {
        String $test = $path.substring(0, 6);
        if ($test.equals("Import") || $test.equals("Export")) {
            return "";
        }
        return Util.getDocuLink(
            "config",
            "cfg_" + this._getOptName($path)
        );
    }

    /**
     * Changes path so it can be used in URLs
     *
     * @param string $path Path
     *
     * @return string
     */
    private String _getOptName(String $path)
    {
    	return $path.replace("Servers/1/", "/").replace("Servers/", "_");
    }

    /**
     * Fills out {@link userprefs_keys} and {@link userprefs_disallow}
     *
     * @return void
     */
    private void _loadUserprefsInfo()
    {
        if (this._userprefsKeys != null) {
            return;
        }

        // TODO this._userprefsKeys = array_flip(UserFormList.getFields());
        
        // read real config for user preferences display
        Map $userPrefsDisallow = "true".equals($GLOBALS.getConfig().get("is_setup"))
            ? (Map)this._configFile.get("UserprefsDisallow", new HashMap<>())
            : (Map)$GLOBALS.getConfig().get("UserprefsDisallow");
        this._userprefsDisallow = array_flip($userPrefsDisallow);
    }

    /**
     * Sets field comments and warnings based on current environment
     *
     * @param string $systemPath Path to settings
     * @param array  $opts       Chosen options
     *
     * @return void
     */
    private void _setComments(String $systemPath, Map $opts)
    {
    	/* TODO
        // RecodingEngine - mark unavailable types
        if ($systemPath == "RecodingEngine") {
            $comment = "";
            if (! function_exists("iconv")) {
                $opts["values"]["iconv"] += " (" + __("unavailable") + ")";
                $comment = String.format(
                    __("'%s' requires %s extension"),
                    "iconv",
                    "iconv"
                );
            }
            if (! function_exists("recode_string")) {
                $opts["values"]["recode"] += " (" + __("unavailable") + ")";
                $comment += ($comment ? ", " : "") + String.format(
                    __("'%s' requires %s extension"),
                    "recode",
                    "recode"
                );
            }
            // mbstring is always there thanks to polyfill
            $opts["comment"] = $comment;
            $opts["comment_warning"] = true;
        }
        // ZipDump, GZipDump, BZipDump - check function availability
        if ($systemPath == "ZipDump"
            || $systemPath == "GZipDump"
            || $systemPath == "BZipDump"
        ) {
            $comment = "";
            $funcs = [
                "ZipDump"  => [
                    "zip_open",
                    "gzcompress",
                ],
                "GZipDump" => [
                    "gzopen",
                    "gzencode",
                ],
                "BZipDump" => [
                    "bzopen",
                    "bzcompress",
                ],
            ];
            if (! function_exists($funcs[$systemPath][0])) {
                $comment = String.format(
                    __(
                        "Compressed import will not work due to missing function %s."
                    ),
                    $funcs[$systemPath][0]
                );
            }
            if (! function_exists($funcs[$systemPath][1])) {
                $comment += ($comment ? "; " : "") + String.format(
                    __(
                        "Compressed export will not work due to missing function %s."
                    ),
                    $funcs[$systemPath][1]
                );
            }
            $opts["comment"] = $comment;
            $opts["comment_warning"] = true;
        }
        if (! $GLOBALS.getConfig().get("is_setup")) {
            if ($systemPath == "MaxDbList" || $systemPath == "MaxTableList"
                || $systemPath == "QueryHistoryMax"
            ) {
                $opts["comment"] = String.format(
                    __("maximum %s"),
                    $GLOBALS.getConfig()[$systemPath]
                );
            }
        }*/
    }

    /**
     * Copy items of an array to $_POST variable
     *
     * @param array  $postValues List of parameters
     * @param string $key        Array key
     *
     * @return void
     */
    private void _fillPostArrayParameters(List $postValues, String $key)
    {
    	// FIXME unsupported !?!
        /*for (Object $v : $postValues) {
            $v = Util.requestString($v);
            if (!"".equals($v)) {
                $_POST[$key][] = $v;
            }
        }*/
    }

}
