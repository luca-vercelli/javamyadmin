package org.javamyadmin.helpers;

import org.javamyadmin.php.Globals;
import static org.javamyadmin.php.Php.*;

import java.util.Arrays;
import java.util.List;

/**
 * Language object
 *
 * @package PhpMyAdmin
 */
public class Language implements Comparable<Language> {
    protected String code;
    protected String name;
    protected String _native;
    protected String regex;
    protected String mysql;

    /**
     * Constructs the Language object
     *
     * @param string $code   Language code
     * @param string $name   English name
     * @param string $native Native name
     * @param string $regex  Match regullar expression
     * @param string $mysql  MySQL locale code
     *
     */
    public Language(String $code, String $name, String $native, String $regex, String $mysql)
    {
        this.code = $code;
        this.name = $name;
        this._native = $native;
        if (!($regex.contains( "[-_]") )) {
            $regex = $regex.replace("|", "([-_][[:alpha:]]{2,3})?|");
        }
        this.regex = $regex;
        this.mysql = $mysql;
    }

    /**
     * Returns native name for language
     *
     * @return string
     */
    public String getNativeName()
    {
        return this._native;
    }

    /**
     * Returns English name for language
     *
     * @return string
     */
    public String getEnglishName()
    {
        return this.name;
    }

    /**
     * Returns verbose name for language
     *
     * @return string
     */
    public String getName()
    {
        if (! empty(this._native)) {
            return this._native + " - " + this.name;
        }
        return this.name;
    }

    /**
     * Returns language code
     *
     * @return string
     */
    public String getCode()
    {
        return this.code;
    }

    /**
     * Returns MySQL locale code, can be empty
     *
     * @return string
     */
    public String getMySQLLocale()
    {
        return this.mysql;
    }

    /**
     * Compare function used for sorting
     *
     * @param Language $other Other object to compare
     *
     * @return int same as strcmp
     */
	@Override
	public int compareTo(Language $other) {
		return this.name.compareTo($other.name);
	}

    /**
     * Checks whether language is currently active.
     *
     * @return bool
     */
    public boolean isActive(Globals GLOBALS)
    {
        return GLOBALS.getLang().equals(this.code);  //FIXME wtf?!?
    }

    /**
     * Checks whether language matches HTTP header Accept-Language.
     *
     * @param string $header Header content
     *
     * @return bool
     */
    public boolean matchesAcceptLanguage(String $header)
    {
        String $pattern = "/^("
            + addcslashes(this.regex, "/")
            + ")(;q=[0-9]\\.[0-9])?$/i";
        return $header.matches($pattern);
    }

    /**
     * Checks whether language matches HTTP header User-Agent
     *
     * @param string $header Header content
     *
     * @return bool
     */
    public boolean matchesUserAgent(String $header)
    {
        String $pattern = "/(\\(|\\[|;[[:space:]])("
            + addcslashes(this.regex, "/")
            + ")(;|\\]|\\))/i";
        return $header.matches($pattern);
    }

    private static List<String> rtl_lang = Arrays.asList(new String[] {"ar", "fa", "he", "ur"});
    /**
     * Checks whether language is RTL
     *
     * @return bool
     */
    public boolean isRTL()
    {
        return rtl_lang.contains(this.code);
    }

    /**
     * Activates given translation
     *
     * @return void
     */
    public void activate(Globals GLOBALS)
    {
        GLOBALS.setLang(this.code);

      //FIXME
        
        /*
        // Set locale
        _setlocale(0, this.code);
        _bindtextdomain("phpmyadmin", LOCALE_PATH);
        _textdomain("phpmyadmin");
        // Set PHP locale as well
        if (function_exists("setlocale")) {
            setlocale(0, this.code);
        }

        // Text direction for language 
        if (this.isRTL()) {
            GLOBALS.getTextDir() = "rtl";
        } else {
            GLOBALS.getTextDir() = "ltr";
        }

        // TCPDF 
        //GLOBALS["l"] = [];

        // TCPDF settings 
        //GLOBALS["l"]["a_meta_charset"] = "UTF-8";
        //GLOBALS["l"]["a_meta_dir"] = GLOBALS["text_dir"];
        //GLOBALS["l"]["a_meta_language"] = this.code;

        // TCPDF translations 
        //GLOBALS["l"]["w_page"] = __("Page number:");

        // Show possible warnings from langauge selection 
        LanguageManager.getInstance().showWarnings();
        */
    }
}
