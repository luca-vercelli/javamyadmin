package org.javamyadmin.helpers.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Core;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.UserPreferences;
import org.javamyadmin.helpers.config.forms.page.PageFormList;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.javamyadmin.php.Php.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Page-related settings
 *
 * @package PhpMyAdmin
 */
public class PageSettings {


    /**
     * Contains id of the form element
     * @var string
     */
    private String _elemId = "page_settings_modal";

    /**
     * Name of the group to show
     * @var string
     */
    private String _groupName = "";

    /**
     * Contains HTML of errors
     * @var string
     */
    private String _errorHTML = "";

    /**
     * Contains HTML of settings
     * @var string
     */
    private String _HTML = "";

    /**
     * @var UserPreferences
     */
    @Autowired
    private UserPreferences userPreferences;

    @Autowired
    HttpServletRequest httpRequest;
    @Autowired
    HttpServletResponse httpResponse;
    @Autowired
    Response $response;
    @Autowired
    Globals $GLOBALS;
    
    /**
     * Constructor
     *
     * @param string $formGroupName The name of config form group to display
     * @param string $elemId        Id of the div containing settings
     */
    public PageSettings(String $formGroupName, String $elemId /*= null*/)
    {
        this.userPreferences = new UserPreferences();

        String $formClass = PageFormList.get($formGroupName);
        if ($formClass == null) {
            return;
        }

        if ("1".equals(httpRequest.getParameter("printview"))) {
            return;
        }

        if (! empty($elemId)) {
            this._elemId = $elemId;
        }
        this._groupName = $formGroupName;

        ConfigFile $cf = new ConfigFile(Globals.getConfig().base_settings);
        this.userPreferences.pageInit($cf);

        FormDisplay $formDisplay;
	    try {
	        Class<FormDisplay> clazz = (Class<FormDisplay>) Class.forName($formClass);
	        Constructor<FormDisplay> constructor = clazz.getConstructor(ConfigFile.class);
	        $formDisplay = constructor.newInstance($cf);
        } catch(ClassNotFoundException e) {
        	throw new IllegalStateException(e);
        } catch (InstantiationException e) {
        	throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
        	throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
        	throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
        	throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
        	throw new IllegalStateException(e);
		} catch (SecurityException e) {
        	throw new IllegalStateException(e);
		}
	    
        // Process form
        Message $error = null;
        if ((httpRequest.getParameter("submit_save")) != null
            && httpRequest.getParameter("submit_save").equals($formGroupName)
        ) {
            this._processPageSettings($formDisplay, $cf, $error); //FIXME $error by reference...
        }

        // Display forms
        this._HTML = this._getPageSettingsDisplay($formDisplay, $error);
    }
    
    public PageSettings(String $formGroupName) {
    	this($formGroupName, null);
    }

    /**
     * Process response to form
     *
     * @param FormDisplay  $formDisplay Form
     * @param ConfigFile   $cf          Configuration file
     * @param Message|null $error       Error message
     *
     * @return void
     */
    private void _processPageSettings(FormDisplay $formDisplay, ConfigFile $cf, Message $error)
    {
        if ($formDisplay.process(false) && ! $formDisplay.hasErrors()) {
            // save settings
            Message $result = this.userPreferences.save($cf.getConfigArray());
            if ($result == null) {
                // reload page
                Core.sendHeaderLocation(
                    $response.getFooter().getSelfUrl(), httpRequest, httpResponse
                );
                return; // FIXME exit()
            } else {
                $error = $result;
            }
        }
    }

    /**
     * Store errors in _errorHTML
     *
     * @param FormDisplay  $formDisplay Form
     * @param Message|null $error       Error message
     *
     * @return void
     */
    private void _storeError(FormDisplay $formDisplay, Message $error)
    {
        String $retval = "";
        if ($error != null) {
            $retval += $error.getDisplay();
        }
        if ($formDisplay.hasErrors()) {
            // form has errors
            $retval += "<div class='alert alert-danger config-form' role='alert'>"
                + "<b>" + __(
                    "Cannot save settings, submitted configuration form contains "
                    + "errors!"
                ) + "</b>"
                + $formDisplay.displayErrors()
                + "</div>";
        }
        this._errorHTML = $retval;
    }

    /**
     * Display page-related settings
     *
     * @param FormDisplay $formDisplay Form
     * @param Message     $error       Error message
     *
     * @return string
     */
    private String _getPageSettingsDisplay(FormDisplay $formDisplay, Message $error)
    {
        String $retval = "";

        this._storeError($formDisplay, $error);

        Map<String, String> params = new HashMap<>();
        params.put("submit_save", this._groupName);
        $retval += "<div id='" + this._elemId + "'>";
        $retval += "<div class='page_settings'>";
        $retval += $formDisplay.getDisplay(
            true,
            true,
            false,
            $response.getFooter().getSelfUrl(),
            params
        );
        $retval += "</div>";
        $retval += "</div>";

        return $retval;
    }

    /**
     * Get HTML output
     *
     * @return string
     */
    public String getHTML()
    {
        return this._HTML;
    }

    /**
     * Get error HTML output
     *
     * @return string
     */
    public String getErrorHTML()
    {
        return this._errorHTML;
    }

    /**
     * Group to show for Page-related settings
     * @param string $formGroupName The name of config form group to display
     * @return PageSettings
     */
    public static PageSettings showGroup(String $formGroupName, BeanFactory beanFactory)
    {
    	//PageSettings $object = new PageSettings($formGroupName);
    	PageSettings $object = beanFactory.getBean(PageSettings.class, $formGroupName, null);
    	
    	$object.$response.addHTML($object.getErrorHTML());
    	$object.$response.addHTML($object.getHTML());

        return $object;
    }

    /**
     * Get HTML for navigation settings
     * @return string
     */
    public static String getNaviSettings(BeanFactory beanFactory)
    {
    	// PageSettings $object = new PageSettings("Navi", "pma_navigation_settings");
    	PageSettings $object = beanFactory.getBean(PageSettings.class, "Navi", "pma_navigation_settings");

    	$object.$response.addHTML($object.getErrorHTML());
        return $object.getHTML();
    }
}
