package org.javamyadmin.helpers.config.forms;

import java.util.ArrayList;
import java.util.List;

import org.javamyadmin.helpers.config.ConfigFile;

/**
 * Class BaseFormList
 * @package PhpMyAdmin\Config\Forms
 */
public class BaseFormList {

    /**
     * List of all forms
     */
    protected static List<String> all = new ArrayList<>();

    /**
     * @var string
     */
    protected static String ns = "org.javamyadmin.helpers.config.forms";

    /**
     * @var array
     */
    private List<BaseForm> _forms;

    /**
     * @return array
     */
    public static List getAll()
    {
        return all;
    }

    /**
     * @param string $name Name
     * @return bool
     */
    public static boolean isValid(String $name)
    {
        return all.contains($name);
    }

    /**
     * @param string $name Name
     * @return null|string
     */
    public static String get(String $name)
    {
        if (isValid($name)) {
            return ns + "." + $name + "Form";
        }
        return null;
    }

    /**
     * Constructor
     *
     * @param ConfigFile $cf Config file instance
     */
    public BaseFormList(ConfigFile $cf)
    {
        this._forms = new ArrayList<>();
        for (String $form : all) {
            String $class = get($form);
            Class<BaseForm> clazz = Class.forName($class);
            Constructor<>
            this._forms.add( new $class($cf));
        }
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
        boolean $ret = true;
        for (Object $form : this._forms) {
            $ret = $ret && $form.process($allowPartialSave, $checkFormSubmit);
        }
        return $ret;
    }

    /**
     * Displays errors
     *
     * @return string HTML for errors
     */
    public String displayErrors()
    {
        String $ret = "";
        for (Object $form : this._forms) {
            $ret += $form.displayErrors();
        }
        return $ret;
    }

    /**
     * Reverts erroneous fields to their default values
     *
     * @return void
     */
    public void fixErrors()
    {
        for (Object $form : this._forms) {
            $form.fixErrors();
        }
    }

    /**
     * Tells whether form validation failed
     *
     * @return boolean
     */
    public boolean hasErrors()
    {
    	boolean $ret = false;
        for (Object $form : this._forms) {
            $ret = $ret || $form.hasErrors();
        }
        return $ret;
    }

    /**
     * Returns list of fields used in the form.
     *
     * @return string[]
     */
    public static List<String> getFields()
    {
        $names = [];
        for (Object $form : $all) {
            $class = get($form);
            $names = array_merge($names, $class.getFields());
        }
        return $names;
    }
}
