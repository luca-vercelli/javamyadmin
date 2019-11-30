package org.javamyadmin.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Language selection manager
 *
 * @package PhpMyAdmin
 */
public class LanguageManager {
    /**
     * @var array Definition data for languages
     *
     * Each member contains:
     * - Language code
     * - English language name
     * - Native language name
     * - Match regular expression
     * - MySQL locale
     */
    private static Map<String, String[]> $_language_data;
    static {
    	$_language_data = new HashMap<>();
    	$_language_data.put("af", new String[] {
                "af",
                "Afrikaans",
                "",
                "af|afrikaans",
                "",
            });
    
    
    	$_language_data.put("af", new String[] {
            "af",
            "Afrikaans",
            "",
            "af|afrikaans",
            "",
        });$_language_data.put(
        "ar", new String[] {
            "ar",
            "Arabic",
            "&#1575;&#1604;&#1593;&#1585;&#1576;&#1610;&#1577;",
            "ar|arabic",
            "ar_AE",
});$_language_data.put(
        "az", new String[] {
            "az",
            "Azerbaijani",
            "Az&#601;rbaycanca",
            "az|azerbaijani",
            "",
});$_language_data.put(
        "bn", new String[] {
            "bn",
            "Bangla",
            "বাংলা",
            "bn|bangla",
            "",
});$_language_data.put(
        "be", new String[] {
            "be",
            "Belarusian",
            "&#1041;&#1077;&#1083;&#1072;&#1088;&#1091;&#1089;&#1082;&#1072;&#1103;",
            "be|belarusian",
            "be_BY",
});$_language_data.put(
        "be@latin", new String[] {
            "be@latin",
            "Belarusian (latin)",
            "Bie&#0322;aruskaja",
            "be[-_]lat|be@latin|belarusian latin",
            "",
});$_language_data.put(
        "bg", new String[] {
            "bg",
            "Bulgarian",
            "&#1041;&#1098;&#1083;&#1075;&#1072;&#1088;&#1089;&#1082;&#1080;",
            "bg|bulgarian",
            "bg_BG",
});$_language_data.put(
        "bs", new String[] {
            "bs",
            "Bosnian",
            "Bosanski",
            "bs|bosnian",
            "",
});$_language_data.put(
        "br", new String[] {
            "br",
            "Breton",
            "Brezhoneg",
            "br|breton",
            "",
});$_language_data.put(
        "brx", new String[] {
            "brx",
            "Bodo",
            "बड़ो",
            "brx|bodo",
            "",
});$_language_data.put(
        "ca", new String[] {
            "ca",
            "Catalan",
            "Catal&agrave;",
            "ca|catalan",
            "ca_ES",
});$_language_data.put(
        "ckb", new String[] {
            "ckb",
            "Sorani",
            "سۆرانی",
            "ckb|sorani",
            "",
});$_language_data.put(
        "cs", new String[] {
            "cs",
            "Czech",
            "Čeština",
            "cs|czech",
            "cs_CZ",
});$_language_data.put(
        "cy", new String[] {
            "cy",
            "Welsh",
            "Cymraeg",
            "cy|welsh",
            "",
});$_language_data.put(
        "da", new String[] {
            "da",
            "Danish",
            "Dansk",
            "da|danish",
            "da_DK",
});$_language_data.put(
        "de", new String[] {
            "de",
            "German",
            "Deutsch",
            "de|german",
            "de_DE",
});$_language_data.put(
        "el", new String[] {
            "el",
            "Greek",
            "&Epsilon;&lambda;&lambda;&eta;&nu;&iota;&kappa;&#940;",
            "el|greek",
            "",
});$_language_data.put(
        "en", new String[] {
            "en",
            "English",
            "",
            "en|english",
            "en_US",
});$_language_data.put(
        "en_gb", new String[] {
            "en_GB",
            "English (United Kingdom)",
            "",
            "en[_-]gb|english (United Kingdom)",
            "en_GB",
});$_language_data.put(
        "eo", new String[] {
            "eo",
            "Esperanto",
            "Esperanto",
            "eo|esperanto",
            "",
});$_language_data.put(
        "es", new String[] {
            "es",
            "Spanish",
            "Espa&ntilde;ol",
            "es|spanish",
            "es_ES",
});$_language_data.put(
        "et", new String[] {
            "et",
            "Estonian",
            "Eesti",
            "et|estonian",
            "et_EE",
});$_language_data.put(
        "eu", new String[] {
            "eu",
            "Basque",
            "Euskara",
            "eu|basque",
            "eu_ES",
});$_language_data.put(
        "fa", new String[] {
            "fa",
            "Persian",
            "&#1601;&#1575;&#1585;&#1587;&#1740;",
            "fa|persian",
            "",
});$_language_data.put(
        "fi", new String[] {
            "fi",
            "Finnish",
            "Suomi",
            "fi|finnish",
            "fi_FI",
});$_language_data.put(
        "fil", new String[] {
            "fil",
            "Filipino",
            "Pilipino",
            "fil|filipino",
            "",
});$_language_data.put(
        "fr", new String[] {
            "fr",
            "French",
            "Fran&ccedil;ais",
            "fr|french",
            "fr_FR",
});$_language_data.put(
        "fy", new String[] {
            "fy",
            "Frisian",
            "Frysk",
            "fy|frisian",
            "",
});$_language_data.put(
        "gl", new String[] {
            "gl",
            "Galician",
            "Galego",
            "gl|galician",
            "gl_ES",
});$_language_data.put(
        "gu", new String[] {
            "gu",
            "Gujarati",
            "ગુજરાતી",
            "gu|gujarati",
            "gu_IN",
});$_language_data.put(
        "he", new String[] {
            "he",
            "Hebrew",
            "&#1506;&#1489;&#1512;&#1497;&#1514;",
            "he|hebrew",
            "he_IL",
});$_language_data.put(
        "hi", new String[] {
            "hi",
            "Hindi",
            "&#2361;&#2367;&#2344;&#2381;&#2342;&#2368;",
            "hi|hindi",
            "hi_IN",
});$_language_data.put(
        "hr", new String[] {
            "hr",
            "Croatian",
            "Hrvatski",
            "hr|croatian",
            "hr_HR",
});$_language_data.put(
        "hu", new String[] {
            "hu",
            "Hungarian",
            "Magyar",
            "hu|hungarian",
            "hu_HU",
});$_language_data.put(
        "hy", new String[] {
            "hy",
            "Armenian",
            "Հայերէն",
            "hy|armenian",
            "",
});$_language_data.put(
        "ia", new String[] {
            "ia",
            "Interlingua",
            "",
            "ia|interlingua",
            "",
});$_language_data.put(
        "id", new String[] {
            "id",
            "Indonesian",
            "Bahasa Indonesia",
            "id|indonesian",
            "id_ID",
});$_language_data.put(
        "ig", new String[] {
            "ig",
            "Igbo",
            "Asụsụ Igbo",
            "ig|igbo",
            "",
});$_language_data.put(
        "it", new String[] {
            "it",
            "Italian",
            "Italiano",
            "it|italian",
            "it_IT",
});$_language_data.put(
        "ja", new String[] {
            "ja",
            "Japanese",
            "&#26085;&#26412;&#35486;",
            "ja|japanese",
            "ja_JP",
});$_language_data.put(
        "ko", new String[] {
            "ko",
            "Korean",
            "&#54620;&#44397;&#50612;",
            "ko|korean",
            "ko_KR",
});$_language_data.put(
        "ka", new String[] {
            "ka",
            "Georgian",
            "&#4325;&#4304;&#4320;&#4311;&#4323;&#4314;&#4312;",
            "ka|georgian",
            "",
});$_language_data.put(
        "kab", new String[] {
            "kab",
            "Kabylian",
            "Taqbaylit",
            "kab|kabylian",
            "",
});$_language_data.put(
        "kk", new String[] {
            "kk",
            "Kazakh",
            "Қазақ",
            "kk|kazakh",
            "",
});$_language_data.put(
        "km", new String[] {
            "km",
            "Khmer",
            "ខ្មែរ",
            "km|khmer",
            "",
});$_language_data.put(
        "kn", new String[] {
            "kn",
            "Kannada",
            "ಕನ್ನಡ",
            "kn|kannada",
            "",
});$_language_data.put(
        "ksh", new String[] {
            "ksh",
            "Colognian",
            "Kölsch",
            "ksh|colognian",
            "",
});$_language_data.put(
        "ku", new String[] {
            "ku",
            "Kurdish",
            "کوردی",
            "ku|kurdish",
            "",
});$_language_data.put(
        "ky", new String[] {
            "ky",
            "Kyrgyz",
            "Кыргызча",
            "ky|kyrgyz",
            "",
});$_language_data.put(
        "li", new String[] {
            "li",
            "Limburgish",
            "Lèmbörgs",
            "li|limburgish",
            "",
});$_language_data.put(
        "lt", new String[] {
            "lt",
            "Lithuanian",
            "Lietuvi&#371;",
            "lt|lithuanian",
            "lt_LT",
});$_language_data.put(
        "lv", new String[] {
            "lv",
            "Latvian",
            "Latvie&scaron;u",
            "lv|latvian",
            "lv_LV",
});$_language_data.put(
        "mk", new String[] {
            "mk",
            "Macedonian",
            "Macedonian",
            "mk|macedonian",
            "mk_MK",
});$_language_data.put(
        "ml", new String[] {
            "ml",
            "Malayalam",
            "Malayalam",
            "ml|malayalam",
            "",
});$_language_data.put(
        "mn", new String[] {
            "mn",
            "Mongolian",
            "&#1052;&#1086;&#1085;&#1075;&#1086;&#1083;",
            "mn|mongolian",
            "mn_MN",
});$_language_data.put(
        "ms", new String[] {
            "ms",
            "Malay",
            "Bahasa Melayu",
            "ms|malay",
            "ms_MY",
});$_language_data.put(
        "my", new String[] {
            "my",
            "Burmese",
            "မြန်မာ",
            "my|burmese",
            "",
});$_language_data.put(
        "ne", new String[] {
            "ne",
            "Nepali",
            "नेपाली",
            "ne|nepali",
            "",
});$_language_data.put(
        "nb", new String[] {
            "nb",
            "Norwegian",
            "Norsk",
            "nb|norwegian",
            "nb_NO",
});$_language_data.put(
        "nn", new String[] {
            "nn",
            "Norwegian Nynorsk",
            "Nynorsk",
            "nn|nynorsk",
            "nn_NO",
});$_language_data.put(
        "nl", new String[] {
            "nl",
            "Dutch",
            "Nederlands",
            "nl|dutch",
            "nl_NL",
});$_language_data.put(
        "pa", new String[] {
            "pa",
            "Punjabi",
            "ਪੰਜਾਬੀ",
            "pa|punjabi",
            "",
});$_language_data.put(
        "pl", new String[] {
            "pl",
            "Polish",
            "Polski",
            "pl|polish",
            "pl_PL",
});$_language_data.put(
        "pt", new String[] {
            "pt",
            "Portuguese",
            "Portugu&ecirc;s",
            "pt|portuguese",
            "pt_PT",
});$_language_data.put(
        "pt_br", new String[] {
            "pt_BR",
            "Portuguese (Brazil)",
            "Portugu&ecirc;s (Brasil)",
            "pt[-_]br|portuguese (brazil)",
            "pt_BR",
});$_language_data.put(
        "ro", new String[] {
            "ro",
            "Romanian",
            "Rom&acirc;n&#259;",
            "ro|romanian",
            "ro_RO",
});$_language_data.put(
        "ru", new String[] {
            "ru",
            "Russian",
            "&#1056;&#1091;&#1089;&#1089;&#1082;&#1080;&#1081;",
            "ru|russian",
            "ru_RU",
});$_language_data.put(
        "si", new String[] {
            "si",
            "Sinhala",
            "&#3523;&#3538;&#3458;&#3524;&#3517;",
            "si|sinhala",
            "",
});$_language_data.put(
        "sk", new String[] {
            "sk",
            "Slovak",
            "Sloven&#269;ina",
            "sk|slovak",
            "sk_SK",
});$_language_data.put(
        "sl", new String[] {
            "sl",
            "Slovenian",
            "Sloven&scaron;&#269;ina",
            "sl|slovenian",
            "sl_SI",
});$_language_data.put(
        "sq", new String[] {
            "sq",
            "Albanian",
            "Shqip",
            "sq|albanian",
            "sq_AL",
});$_language_data.put(
        "sr@latin", new String[] {
            "sr@latin",
            "Serbian (latin)",
            "Srpski",
            "sr[-_]lat|sr@latin|serbian latin",
            "sr_YU",
});$_language_data.put(
        "sr", new String[] {
            "sr",
            "Serbian",
            "&#1057;&#1088;&#1087;&#1089;&#1082;&#1080;",
            "sr|serbian",
            "sr_YU",
});$_language_data.put(
        "sv", new String[] {
            "sv",
            "Swedish",
            "Svenska",
            "sv|swedish",
            "sv_SE",
});$_language_data.put(
        "ta", new String[] {
            "ta",
            "Tamil",
            "தமிழ்",
            "ta|tamil",
            "ta_IN",
});$_language_data.put(
        "te", new String[] {
            "te",
            "Telugu",
            "తెలుగు",
            "te|telugu",
            "te_IN",
});$_language_data.put(
        "th", new String[] {
            "th",
            "Thai",
            "&#3616;&#3634;&#3625;&#3634;&#3652;&#3607;&#3618;",
            "th|thai",
            "th_TH",
});$_language_data.put(
        "tk", new String[] {
            "tk",
            "Turkmen",
            "Türkmençe",
            "tk|turkmen",
            "",
});$_language_data.put(
        "tr", new String[] {
            "tr",
            "Turkish",
            "T&uuml;rk&ccedil;e",
            "tr|turkish",
            "tr_TR",
});$_language_data.put(
        "tt", new String[] {
            "tt",
            "Tatarish",
            "Tatar&ccedil;a",
            "tt|tatarish",
            "",
});$_language_data.put(
        "ug", new String[] {
            "ug",
            "Uyghur",
            "ئۇيغۇرچە",
            "ug|uyghur",
            "",
});$_language_data.put(
        "uk", new String[] {
            "uk",
            "Ukrainian",
            "&#1059;&#1082;&#1088;&#1072;&#1111;&#1085;&#1089;&#1100;&#1082;&#1072;",
            "uk|ukrainian",
            "uk_UA",
});$_language_data.put(
        "ur", new String[] {
            "ur",
            "Urdu",
            "اُردوُ",
            "ur|urdu",
            "ur_PK",
});$_language_data.put(
        "uz@latin", new String[] {
            "uz@latin",
            "Uzbek (latin)",
            "O&lsquo;zbekcha",
            "uz[-_]lat|uz@latin|uzbek-latin",
            "",
});$_language_data.put(
        "uz", new String[] {
            "uz",
            "Uzbek (cyrillic)",
            "&#1038;&#1079;&#1073;&#1077;&#1082;&#1095;&#1072;",
            "uz[-_]cyr|uz@cyrillic|uzbek-cyrillic",
            "",
});$_language_data.put(
        "vi", new String[] {
            "vi",
            "Vietnamese",
            "Tiếng Việt",
            "vi|vietnamese",
            "vi_VN",
});$_language_data.put(
        "vls", new String[] {
            "vls",
            "Flemish",
            "West-Vlams",
            "vls|flemish",
            "",
});$_language_data.put(
        "zh_tw", new String[] {
            "zh_TW",
            "Chinese traditional",
            "&#20013;&#25991;",
            "zh[-_](tw|hk)|chinese traditional",
            "zh_TW",
});$_language_data.put(
        // only TW and HK use traditional Chinese while others (CN, SG, MY)
        // use simplified Chinese
        "zh_cn", new String[] {
            "zh_CN",
            "Chinese simplified",
            "&#20013;&#25991;",
            "zh(?![-_](tw|hk))([-_][[:alpha:]]{2,3})?|chinese simplified",
            "zh_CN",
});

    private $_available_locales;
    private $_available_languages;
    private $_lang_failed_cfg;
    private $_lang_failed_cookie;
    private $_lang_failed_request;

    /**
     * @var LanguageManager
     */
    private static LanguageManager $instance;

    /**
     * Returns LanguageManager singleton
     *
     * @return LanguageManager
     */
    public static LanguageManager getInstance()
    {
        if ($instance === null) {
            $instance = new LanguageManager();
        }
        return $instance;
    }

    /**
     * Returns list of available locales
     *
     * @return array
     */
    public function listLocaleDir()
    {
        $result = ["en"];

        /* Check for existing directory */
        if (! is_dir(LOCALE_PATH)) {
            return $result;
        }

        /* Open the directory */
        $handle = @opendir(LOCALE_PATH);
        /* This can happen if the kit is English-only */
        if ($handle === false) {
            return $result;
        }

        /* Process all files */
        while (false !== ($file = readdir($handle))) {
            $path = LOCALE_PATH
                . "/" . $file
                . "/LC_MESSAGES/phpmyadmin.mo";
            if ($file != "."
                && $file != ".."
                && @file_exists($path)
            ) {
                $result[] = $file;
            }
        }
        /* Close the handle */
        closedir($handle);

        return $result;
    }

    /**
     * Returns (cached) list of all available locales
     *
     * @return array of strings
     */
    public function availableLocales()
    {
        if (! this._available_locales) {
            if (GLOBALS.PMA_Config == null || empty(GLOBALS.PMA_Config.get("FilterLanguages"))) {
                this._available_locales = this.listLocaleDir();
            } else {
                this._available_locales = preg_grep(
                    "@" . GLOBALS["PMA_Config"].get("FilterLanguages") . "@",
                    this.listLocaleDir()
                );
            }
        }
        return this._available_locales;
    }

    /**
     * Checks whether there are some languages available
     *
     * @return boolean
     */
    public function hasChoice()
    {
        return count(this.availableLanguages()) > 1;
    }

    /**
     * Returns (cached) list of all available languages
     *
     * @return Language[] array of Language objects
     */
    public function availableLanguages()
    {
        if (! this._available_languages) {
            this._available_languages = [];

            foreach (this.availableLocales() as $lang) {
                $lang = strtolower($lang);
                if (isset(static.$_language_data[$lang])) {
                    $data = static.$_language_data[$lang];
                    this._available_languages[$lang] = new Language(
                        $data[0],
                        $data[1],
                        $data[2],
                        $data[3],
                        $data[4]
                    );
                } else {
                    this._available_languages[$lang] = new Language(
                        $lang,
                        ucfirst($lang),
                        ucfirst($lang),
                        $lang,
                        ""
                    );
                }
            }
        }
        return this._available_languages;
    }

    /**
     * Returns (cached) list of all available languages sorted
     * by name
     *
     * @return Language[] array of Language objects
     */
    public function sortedLanguages()
    {
        this.availableLanguages();
        uasort(this._available_languages, function ($a, $b) {
            return $a.cmp($b);
        });
        return this._available_languages;
    }

    /**
     * Return Language object for given code
     *
     * @param string $code Language code
     *
     * @return Language|false Language object or false on failure
     */
    public function getLanguage($code)
    {
        $code = strtolower($code);
        $langs = this.availableLanguages();
        if (isset($langs[$code])) {
            return $langs[$code];
        }
        return false;
    }

    /**
     * Return currently active Language object
     *
     * @return Language Language object
     */
    public function getCurrentLanguage()
    {
        return this._available_languages[strtolower(GLOBALS["lang"])];
    }

    /**
     * Activates language based on configuration, user preferences or
     * browser
     *
     * @return Language
     */
    public function selectLanguage()
    {
        // check forced language
        if (! empty(GLOBALS["PMA_Config"].get("Lang"))) {
            $lang = this.getLanguage(GLOBALS["PMA_Config"].get("Lang"));
            if ($lang !== false) {
                return $lang;
            }
            this._lang_failed_cfg = true;
        }

        // Don"t use REQUEST in following code as it might be confused by cookies
        // with same name. Check user requested language (POST)
        if (! empty($_POST["lang"])) {
            $lang = this.getLanguage($_POST["lang"]);
            if ($lang !== false) {
                return $lang;
            }
            this._lang_failed_request = true;
        }

        // check user requested language (GET)
        if (! empty($_GET["lang"])) {
            $lang = this.getLanguage($_GET["lang"]);
            if ($lang !== false) {
                return $lang;
            }
            this._lang_failed_request = true;
        }

        // check previous set language
        if (! empty(GLOBALS["PMA_Config"].getCookie("pma_lang"))) {
            $lang = this.getLanguage(GLOBALS["PMA_Config"].getCookie("pma_lang"));
            if ($lang !== false) {
                return $lang;
            }
            this._lang_failed_cookie = true;
        }

        $langs = this.availableLanguages();

        // try to find out user"s language by checking its HTTP_ACCEPT_LANGUAGE variable;
        $accepted_languages = Core.getenv("HTTP_ACCEPT_LANGUAGE");
        if ($accepted_languages) {
            foreach (explode(",", $accepted_languages) as $header) {
                foreach ($langs as $language) {
                    if ($language.matchesAcceptLanguage($header)) {
                        return $language;
                    }
                }
            }
        }

        // try to find out user"s language by checking its HTTP_USER_AGENT variable
        $user_agent = Core.getenv("HTTP_USER_AGENT");
        if (! empty($user_agent)) {
            foreach ($langs as $language) {
                if ($language.matchesUserAgent($user_agent)) {
                    return $language;
                }
            }
        }

        // Didn"t catch any valid lang : we use the default settings
        if (isset($langs[GLOBALS["PMA_Config"].get("DefaultLang")])) {
            return $langs[GLOBALS["PMA_Config"].get("DefaultLang")];
        }

        // Fallback to English
        return $langs["en"];
    }

    /**
     * Displays warnings about invalid languages. This needs to be postponed
     * to show messages at time when language is initialized.
     *
     * @return void
     */
    public function showWarnings()
    {
        // now, that we have loaded the language strings we can send the errors
        if (this._lang_failed_cfg
            || this._lang_failed_cookie
            || this._lang_failed_request
        ) {
            trigger_error(
                __("Ignoring unsupported language code."),
                E_USER_ERROR
            );
        }
    }


    /**
     * Returns HTML code for the language selector
     *
     * @param Template $template     Template instance
     * @param boolean  $use_fieldset whether to use fieldset for selection
     * @param boolean  $show_doc     whether to show documentation links
     *
     * @return string
     *
     * @access  public
     */
    public function getSelectorDisplay(Template $template, $use_fieldset = false, $show_doc = true)
    {
        $_form_params = [
            "db" => GLOBALS["db"],
            "table" => GLOBALS["table"],
        ];

        // For non-English, display "Language" with emphasis because it"s
        // not a proper word in the current language; we show it to help
        // people recognize the dialog
        $language_title = __("Language")
            . (__("Language") != "Language" ? " - <em>Language</em>" : "");
        if ($show_doc) {
            $language_title .= Util.showDocu("faq", "faq7-2");
        }

        $available_languages = this.sortedLanguages();

        return $template.render("select_lang", [
            "language_title" => $language_title,
            "use_fieldset" => $use_fieldset,
            "available_languages" => $available_languages,
            "_form_params" => $_form_params,
        ]);
    }
}
