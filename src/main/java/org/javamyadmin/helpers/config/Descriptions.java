package org.javamyadmin.helpers.config;

import java.util.HashMap;
import java.util.Map;

import org.javamyadmin.helpers.Sanitize;

import static org.javamyadmin.php.Php.*;

/**
 * Base class for forms, loads default configuration options, checks allowed
 * values etc.
 *
 * @package PhpMyAdmin
 */
public class Descriptions {
	/**
	 * Return Return name or description for a configuration path.
	 *
	 * @param string
	 *            $path Path of configuration
	 * @param string
	 *            $type Type of message, either "name", "cmt" or "desc"
	 *
	 * @return string
	 */
	public static String get(String $path, String $type /* = "name" */) {
		String $key = $path.replace("Servers/1/", "/").replace("Servers/", "_");
		String $value = getString($key, $type);

		/* Fallback to path for name and empty string for description and comment */
		if ($value == null) {
			if ("name".equals($type)) {
				$value = $path;
			} else {
				$value = "";
			}
		}

		return Sanitize.sanitizeMessage($value);
	}

	private static Map<String, String> $descriptions = new HashMap<>();
	static {
		$descriptions.put("AllowArbitraryServer_desc",
				__("If enabled, user can enter any MySQL server in login form for cookie auth."));
		$descriptions.put("AllowArbitraryServer_name", __("Allow login to any MySQL server"));
		$descriptions.put("ArbitraryServerRegexp_desc",
				__("Restricts the MySQL servers the user can enter when a login to an arbitrary "
						+ "MySQL server is enabled by matching the IP or hostname of the MySQL server "
						+ "to the given regular expression."));
		$descriptions.put("ArbitraryServerRegexp_name", __("Restrict login to MySQL server"));
		$descriptions.put("AllowThirdPartyFraming_desc",
				__("Enabling this allows a page located on a different domain to call phpMyAdmin "
						+ "inside a frame, and is a potential [strong]security hole[/strong] allowing "
						+ "cross-frame scripting (XSS) attacks."));
		$descriptions.put("AllowThirdPartyFraming_name", __("Allow third party framing"));
		$descriptions.put("AllowUserDropDatabase_name", __("Show 'Drop database' link to normal users"));
		$descriptions.put("blowfish_secret_desc",
				__("Secret passphrase used for encrypting cookies in [kbd]cookie[/kbd] " + "authentication."));
		$descriptions.put("blowfish_secret_name", __("Blowfish secret"));
		$descriptions.put("BrowseMarkerEnable_desc", __("Highlight selected rows."));
		$descriptions.put("BrowseMarkerEnable_name", __("Row marker"));
		$descriptions.put("BrowsePointerEnable_desc", __("Highlight row pointed by the mouse cursor."));
		$descriptions.put("BrowsePointerEnable_name", __("Highlight pointer"));
		$descriptions.put("BZipDump_desc", __("Enable bzip2 compression for" + " import operations."));
		$descriptions.put("BZipDump_name", __("Bzip2"));
		$descriptions.put("CharEditing_desc",
				__("Defines which type of editing controls should be used for CHAR and VARCHAR "
						+ "columns; [kbd]input[/kbd] - allows limiting of input length, "
						+ "[kbd]textarea[/kbd] - allows newlines in columns."));
		$descriptions.put("CharEditing_name", __("CHAR columns editing"));
		$descriptions.put("CodemirrorEnable_desc", __("Use user-friendly editor for editing SQL queries "
				+ "(CodeMirror) with syntax highlighting and " + "line numbers."));
		$descriptions.put("CodemirrorEnable_name", __("Enable CodeMirror"));
		$descriptions.put("LintEnable_desc",
				__("Find any errors in the query before executing it." + " Requires CodeMirror to be enabled."));
		$descriptions.put("LintEnable_name", __("Enable linter"));
		$descriptions.put("MinSizeForInputField_desc",
				__("Defines the minimum size for input fields generated for CHAR and VARCHAR " + "columns."));
		$descriptions.put("MinSizeForInputField_name", __("Minimum size for input field"));
		$descriptions.put("MaxSizeForInputField_desc",
				__("Defines the maximum size for input fields generated for CHAR and VARCHAR " + "columns."));
		$descriptions.put("MaxSizeForInputField_name", __("Maximum size for input field"));
		$descriptions.put("CharTextareaCols_desc", __("Number of columns for CHAR/VARCHAR textareas."));
		$descriptions.put("CharTextareaCols_name", __("CHAR textarea columns"));
		$descriptions.put("CharTextareaRows_desc", __("Number of rows for CHAR/VARCHAR textareas."));
		$descriptions.put("CharTextareaRows_name", __("CHAR textarea rows"));
		$descriptions.put("CheckConfigurationPermissions_name", __("Check config file permissions"));
		$descriptions.put("CompressOnFly_desc",
				__("Compress gzip exports on the fly without the need for much memory; if "
						+ "you encounter problems with created gzip files disable this feature."));
		$descriptions.put("CompressOnFly_name", __("Compress on the fly"));
		$descriptions.put("Confirm_desc", __("Whether a warning ('Are your really sure…') should be displayed "
				+ "when you\"re about to lose data."));
		$descriptions.put("Confirm_name", __("Confirm DROP queries"));
		$descriptions.put("DBG_sql_desc",
				__("Log SQL queries and their execution time, to be displayed in the console"));
		$descriptions.put("DBG_sql_name", __("Debug SQL"));
		$descriptions.put("DefaultTabDatabase_desc", __("Tab that is displayed when entering a database."));
		$descriptions.put("DefaultTabDatabase_name", __("Default database tab"));
		$descriptions.put("DefaultTabServer_desc", __("Tab that is displayed when entering a server."));
		$descriptions.put("DefaultTabServer_name", __("Default server tab"));
		$descriptions.put("DefaultTabTable_desc", __("Tab that is displayed when entering a table."));
		$descriptions.put("DefaultTabTable_name", __("Default table tab"));
		$descriptions.put("EnableAutocompleteForTablesAndColumns_desc",
				__("Autocomplete of the table and column names in the SQL queries."));
		$descriptions.put("EnableAutocompleteForTablesAndColumns_name",
				__("Enable autocomplete for table and column names"));
		$descriptions.put("HideStructureActions_desc", __("Whether the table structure actions should be hidden."));
		$descriptions.put("ShowColumnComments_name", __("Show column comments"));
		$descriptions.put("ShowColumnComments_desc",
				__("Whether column comments should be shown in table structure view"));
		$descriptions.put("HideStructureActions_name", __("Hide table structure actions"));
		$descriptions.put("DefaultTransformations_Hex_name", __("Default transformations for Hex"));
		$descriptions.put("DefaultTransformations_Hex_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_Substring_name", __("Default transformations for Substring"));
		$descriptions.put("DefaultTransformations_Substring_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_Bool2Text_name", __("Default transformations for Bool2Text"));
		$descriptions.put("DefaultTransformations_Bool2Text_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_External_name", __("Default transformations for External"));
		$descriptions.put("DefaultTransformations_External_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_PreApPend_name", __("Default transformations for PreApPend"));
		$descriptions.put("DefaultTransformations_PreApPend_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_DateFormat_name", __("Default transformations for DateFormat"));
		$descriptions.put("DefaultTransformations_DateFormat_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_Inline_name", __("Default transformations for Inline"));
		$descriptions.put("DefaultTransformations_Inline_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_TextImageLink_name", __("Default transformations for TextImageLink"));
		$descriptions.put("DefaultTransformations_TextImageLink_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));
		$descriptions.put("DefaultTransformations_TextLink_name", __("Default transformations for TextLink"));
		$descriptions.put("DefaultTransformations_TextLink_desc", __(
				"Values for options list for default transformations. These will be overwritten if transformation is filled in at table structure page."));

		$descriptions.put("DisplayServersList_desc", __("Show server listing as a list instead of a drop down."));
		$descriptions.put("DisplayServersList_name", __("Display servers as a list"));
		$descriptions.put("DisableMultiTableMaintenance_desc",
				__("Disable the table maintenance mass operations, like optimizing or repairing "
						+ "the selected tables of a database."));
		$descriptions.put("DisableMultiTableMaintenance_name", __("Disable multi table maintenance"));
		$descriptions.put("ExecTimeLimit_desc",
				__("Set the number of seconds a script is allowed to run ([kbd]0[/kbd] for no " + "limit)."));
		$descriptions.put("ExecTimeLimit_name", __("Maximum execution time"));
		$descriptions.put("Export_lock_tables_name",
				String.format(__("Use %s statement"), htmlspecialchars("<code>LOCK TABLES</code>")));
		$descriptions.put("Export_asfile_name", __("Save as file"));
		$descriptions.put("Export_charset_name", __("Character set of the file"));
		$descriptions.put("Export_codegen_format_name", __("Format"));
		$descriptions.put("Export_compression_name", __("Compression"));
		$descriptions.put("Export_csv_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_csv_enclosed_name", __("Columns enclosed with"));
		$descriptions.put("Export_csv_escaped_name", __("Columns escaped with"));
		$descriptions.put("Export_csv_null_name", __("Replace NULL with"));
		$descriptions.put("Export_csv_removeCRLF_name", __("Remove CRLF characters within columns"));
		$descriptions.put("Export_csv_separator_name", __("Columns terminated with"));
		$descriptions.put("Export_csv_terminated_name", __("Lines terminated with"));
		$descriptions.put("Export_excel_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_excel_edition_name", __("Excel edition"));
		$descriptions.put("Export_excel_null_name", __("Replace NULL with"));
		$descriptions.put("Export_excel_removeCRLF_name", __("Remove CRLF characters within columns"));
		$descriptions.put("Export_file_template_database_name", __("Database name template"));
		$descriptions.put("Export_file_template_server_name", __("Server name template"));
		$descriptions.put("Export_file_template_table_name", __("Table name template"));
		$descriptions.put("Export_format_name", __("Format"));
		$descriptions.put("Export_htmlword_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_htmlword_null_name", __("Replace NULL with"));
		$descriptions.put("Export_htmlword_structure_or_data_name", __("Dump table"));
		$descriptions.put("Export_latex_caption_name", __("Include table caption"));
		$descriptions.put("Export_latex_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_latex_comments_name", __("Comments"));
		$descriptions.put("Export_latex_data_caption_name", __("Table caption"));
		$descriptions.put("Export_latex_data_continued_caption_name", __("Continued table caption"));
		$descriptions.put("Export_latex_data_label_name", __("Label key"));
		$descriptions.put("Export_latex_mime_name", __("Media type"));
		$descriptions.put("Export_latex_null_name", __("Replace NULL with"));
		$descriptions.put("Export_latex_relation_name", __("Relationships"));
		$descriptions.put("Export_latex_structure_caption_name", __("Table caption"));
		$descriptions.put("Export_latex_structure_continued_caption_name", __("Continued table caption"));
		$descriptions.put("Export_latex_structure_label_name", __("Label key"));
		$descriptions.put("Export_latex_structure_or_data_name", __("Dump table"));
		$descriptions.put("Export_method_name", __("Export method"));
		$descriptions.put("Export_ods_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_ods_null_name", __("Replace NULL with"));
		$descriptions.put("Export_odt_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_odt_comments_name", __("Comments"));
		$descriptions.put("Export_odt_mime_name", __("Media type"));
		$descriptions.put("Export_odt_null_name", __("Replace NULL with"));
		$descriptions.put("Export_odt_relation_name", __("Relationships"));
		$descriptions.put("Export_odt_structure_or_data_name", __("Dump table"));
		$descriptions.put("Export_onserver_name", __("Save on server"));
		$descriptions.put("Export_onserver_overwrite_name", __("Overwrite existing file(s)"));
		$descriptions.put("Export_as_separate_files_name", __("Export as separate files"));
		$descriptions.put("Export_quick_export_onserver_name", __("Save on server"));
		$descriptions.put("Export_quick_export_onserver_overwrite_name", __("Overwrite existing file(s)"));
		$descriptions.put("Export_remember_file_template_name", __("Remember file name template"));
		$descriptions.put("Export_sql_auto_increment_name", __("Add AUTO_INCREMENT value"));
		$descriptions.put("Export_sql_backquotes_name", __("Enclose table and column names with backquotes"));
		$descriptions.put("Export_sql_compatibility_name", __("SQL compatibility mode"));
		$descriptions.put("Export_sql_dates_name", __("Creation/Update/Check dates"));
		$descriptions.put("Export_sql_delayed_name", __("Use delayed inserts"));
		$descriptions.put("Export_sql_disable_fk_name", __("Disable foreign key checks"));
		$descriptions.put("Export_sql_views_as_tables_name", __("Export views as tables"));
		$descriptions.put("Export_sql_metadata_name",
				__("Export related metadata from phpMyAdmin configuration storage"));
		$descriptions.put("Export_sql_create_database_name", String.format(__("Add %s"), "CREATE DATABASE / USE"));
		$descriptions.put("Export_sql_drop_database_name", String.format(__("Add %s"), "DROP DATABASE"));
		$descriptions.put("Export_sql_drop_table_name",
				String.format(__("Add %s"), "DROP TABLE / VIEW / PROCEDURE / FUNCTION / EVENT / TRIGGER"));
		$descriptions.put("Export_sql_create_table_name", String.format(__("Add %s"), "CREATE TABLE"));
		$descriptions.put("Export_sql_create_view_name", String.format(__("Add %s"), "CREATE VIEW"));
		$descriptions.put("Export_sql_create_trigger_name", String.format(__("Add %s"), "CREATE TRIGGER"));
		$descriptions.put("Export_sql_hex_for_binary_name", __("Use hexadecimal for BINARY & BLOB"));
		$descriptions.put("Export_sql_if_not_exists_name",
				__("Add IF NOT EXISTS (less efficient as indexes will be generated during" + " table creation)"));
		$descriptions.put("Export_sql_view_current_user", __("Exclude definition of current user"));
		$descriptions.put("Export_sql_or_replace_view_name", String.format(__("%s view"), "OR REPLACE"));
		$descriptions.put("Export_sql_ignore_name", __("Use ignore inserts"));
		$descriptions.put("Export_sql_include_comments_name", __("Comments"));
		$descriptions.put("Export_sql_insert_syntax_name", __("Syntax to use when inserting data"));
		$descriptions.put("Export_sql_max_query_size_name", __("Maximal length of created query"));
		$descriptions.put("Export_sql_mime_name", __("Media type"));
		$descriptions.put("Export_sql_procedure_function_name",
				String.format(__("Add %s"), "CREATE PROCEDURE / FUNCTION / EVENT"));
		$descriptions.put("Export_sql_relation_name", __("Relationships"));
		$descriptions.put("Export_sql_structure_or_data_name", __("Dump table"));
		$descriptions.put("Export_sql_type_name", __("Export type"));
		$descriptions.put("Export_sql_use_transaction_name", __("Enclose export in a transaction"));
		$descriptions.put("Export_sql_utc_time_name", __("Export time in UTC"));
		$descriptions.put("Export_texytext_columns_name", __("Put columns names in the first row"));
		$descriptions.put("Export_texytext_null_name", __("Replace NULL with"));
		$descriptions.put("Export_texytext_structure_or_data_name", __("Dump table"));
		$descriptions.put("ForeignKeyDropdownOrder_desc",
				__("Sort order for items in a foreign-key dropdown box; [kbd]content[/kbd] is "
						+ "the referenced data, [kbd]id[/kbd] is the key value."));
		$descriptions.put("ForeignKeyDropdownOrder_name", __("Foreign key dropdown order"));
		$descriptions.put("ForeignKeyMaxLimit_desc", __("A dropdown will be used if fewer items are present."));
		$descriptions.put("ForeignKeyMaxLimit_name", __("Foreign key limit"));
		$descriptions.put("DefaultForeignKeyChecks_desc",
				__("Default value for foreign key checks checkbox for some queries."));
		$descriptions.put("DefaultForeignKeyChecks_name", __("Foreign key checks"));
		$descriptions.put("FirstDayOfCalendar_name", __("First day of calendar"));
		$descriptions.put("Form_Browse_name", __("Browse mode"));
		$descriptions.put("Form_Browse_desc", __("Customize browse mode."));
		$descriptions.put("Form_CodeGen_name", "CodeGen");
		$descriptions.put("Form_CodeGen_desc", __("Customize default options."));
		$descriptions.put("Form_Csv_name", __("CSV"));
		$descriptions.put("Form_Csv_desc", __("Customize default options."));
		$descriptions.put("Form_Developer_name", __("Developer"));
		$descriptions.put("Form_Developer_desc", __("Settings for phpMyAdmin developers."));
		$descriptions.put("Form_Edit_name", __("Edit mode"));
		$descriptions.put("Form_Edit_desc", __("Customize edit mode."));
		$descriptions.put("Form_Export_defaults_name", __("Export defaults"));
		$descriptions.put("Form_Export_defaults_desc", __("Customize default export options."));
		$descriptions.put("Form_General_name", __("General"));
		$descriptions.put("Form_General_desc", __("Set some commonly used options."));
		$descriptions.put("Form_Import_defaults_name", __("Import defaults"));
		$descriptions.put("Form_Import_defaults_desc", __("Customize default common import options."));
		$descriptions.put("Form_Import_export_name", __("Import / export"));
		$descriptions.put("Form_Import_export_desc", __("Set import and export directories and compression options."));
		$descriptions.put("Form_Latex_name", __("LaTeX"));
		$descriptions.put("Form_Latex_desc", __("Customize default options."));
		$descriptions.put("Form_Navi_databases_name", __("Databases"));
		$descriptions.put("Form_Navi_databases_desc", __("Databases display options."));
		$descriptions.put("Form_Navi_panel_name", __("Navigation panel"));
		$descriptions.put("Form_Navi_panel_desc", __("Customize appearance of the navigation panel."));
		$descriptions.put("Form_Navi_tree_name", __("Navigation tree"));
		$descriptions.put("Form_Navi_tree_desc", __("Customize the navigation tree."));
		$descriptions.put("Form_Navi_servers_name", __("Servers"));
		$descriptions.put("Form_Navi_servers_desc", __("Servers display options."));
		$descriptions.put("Form_Navi_tables_name", __("Tables"));
		$descriptions.put("Form_Navi_tables_desc", __("Tables display options."));
		$descriptions.put("Form_Main_panel_name", __("Main panel"));
		$descriptions.put("Form_Microsoft_Office_name", __("Microsoft Office"));
		$descriptions.put("Form_Microsoft_Office_desc", __("Customize default options."));
		$descriptions.put("Form_Open_Document_name", "OpenDocument");
		$descriptions.put("Form_Open_Document_desc", __("Customize default options."));
		$descriptions.put("Form_Other_core_settings_name", __("Other core settings"));
		$descriptions.put("Form_Other_core_settings_desc", __("Settings that didn\"t fit anywhere else."));
		$descriptions.put("Form_Page_titles_name", __("Page titles"));
		$descriptions.put("Form_Page_titles_desc", __("Specify browser\"s title bar text. Refer to "
				+ "[doc@faq6-27]documentation[/doc] for magic strings that can be used " + "to get special values."));
		$descriptions.put("Form_Security_name", __("Security"));
		$descriptions.put("Form_Security_desc",
				__("Please note that phpMyAdmin is just a user interface and its features do not " + "limit MySQL."));
		$descriptions.put("Form_Server_name", __("Basic settings"));
		$descriptions.put("Form_Server_auth_name", __("Authentication"));
		$descriptions.put("Form_Server_auth_desc", __("Authentication settings."));
		$descriptions.put("Form_Server_config_name", __("Server configuration"));
		$descriptions.put("Form_Server_config_desc", __(
				"Advanced server configuration, do not change these options unless you know " + "what they are for."));
		$descriptions.put("Form_Server_desc", __("Enter server connection parameters."));
		$descriptions.put("Form_Server_pmadb_name", __("Configuration storage"));
		$descriptions.put("Form_Server_pmadb_desc",
				__("Configure phpMyAdmin configuration storage to gain access to additional "
						+ "features, see [doc@linked-tables]phpMyAdmin configuration storage[/doc] in "
						+ "documentation."));
		$descriptions.put("Form_Server_tracking_name", __("Changes tracking"));
		$descriptions.put("Form_Server_tracking_desc",
				__("Tracking of changes made in database. Requires the phpMyAdmin configuration " + "storage."));
		$descriptions.put("Form_Sql_name", __("SQL"));
		$descriptions.put("Form_Sql_box_name", __("SQL Query box"));
		$descriptions.put("Form_Sql_box_desc", __("Customize links shown in SQL Query boxes."));
		$descriptions.put("Form_Sql_desc", __("Customize default options."));
		$descriptions.put("Form_Sql_queries_name", __("SQL queries"));
		$descriptions.put("Form_Sql_queries_desc", __("SQL queries settings."));
		$descriptions.put("Form_Startup_name", __("Startup"));
		$descriptions.put("Form_Startup_desc", __("Customize startup page."));
		$descriptions.put("Form_DbStructure_name", __("Database structure"));
		$descriptions.put("Form_DbStructure_desc",
				__("Choose which details to show in the database structure (list of tables)."));
		$descriptions.put("Form_TableStructure_name", __("Table structure"));
		$descriptions.put("Form_TableStructure_desc", __("Settings for the table structure (list of columns)."));
		$descriptions.put("Form_Tabs_name", __("Tabs"));
		$descriptions.put("Form_Tabs_desc", __("Choose how you want tabs to work."));
		$descriptions.put("Form_DisplayRelationalSchema_name", __("Display relational schema"));
		$descriptions.put("Form_DisplayRelationalSchema_desc", "");
		$descriptions.put("PDFDefaultPageSize_name", __("Paper size"));
		$descriptions.put("PDFDefaultPageSize_desc", "");
		$descriptions.put("Form_Databases_name", __("Databases"));
		$descriptions.put("Form_Text_fields_name", __("Text fields"));
		$descriptions.put("Form_Text_fields_desc", __("Customize text input fields."));
		$descriptions.put("Form_Texy_name", __("Texy! text"));
		$descriptions.put("Form_Texy_desc", __("Customize default options"));
		$descriptions.put("Form_Warnings_name", __("Warnings"));
		$descriptions.put("Form_Warnings_desc", __("Disable some of the warnings shown by phpMyAdmin."));
		$descriptions.put("Form_Console_name", __("Console"));
		$descriptions.put("GZipDump_desc", __("Enable gzip compression for import " + "and export operations."));
		$descriptions.put("GZipDump_name", __("GZip"));
		$descriptions.put("IconvExtraParams_name", __("Extra parameters for iconv"));
		$descriptions.put("IgnoreMultiSubmitErrors_desc",
				__("If enabled, phpMyAdmin continues computing multiple-statement queries even if "
						+ "one of the queries failed."));
		$descriptions.put("IgnoreMultiSubmitErrors_name", __("Ignore multiple statement errors"));
		$descriptions.put("Import_allow_interrupt_desc",
				__("Allow interrupt of import in case script detects it is close to time limit. "
						+ "This might be a good way to import large files, however it can break " + "transactions."));
		$descriptions.put("enable_drag_drop_import_name", __("Enable drag and drop import"));
		$descriptions.put("enable_drag_drop_import_desc", __("Uncheck the checkbox to disable drag and drop import"));
		$descriptions.put("Import_allow_interrupt_name", __("Partial import: allow interrupt"));
		$descriptions.put("Import_charset_name", __("Character set of the file"));
		$descriptions.put("Import_csv_col_names_name", __("Lines terminated with"));
		$descriptions.put("Import_csv_enclosed_name", __("Columns enclosed with"));
		$descriptions.put("Import_csv_escaped_name", __("Columns escaped with"));
		$descriptions.put("Import_csv_ignore_name", __("Do not abort on INSERT error"));
		$descriptions.put("Import_csv_replace_name", __("Add ON DUPLICATE KEY UPDATE"));
		$descriptions.put("Import_csv_replace_desc", __("Update data when duplicate keys found on import"));
		$descriptions.put("Import_csv_terminated_name", __("Columns terminated with"));
		$descriptions.put("Import_format_desc",
				__("Default format; be aware that this list depends on location (database, table) "
						+ "and only SQL is always available."));
		$descriptions.put("Import_format_name", __("Format of imported file"));
		$descriptions.put("Import_ldi_enclosed_name", __("Columns enclosed with"));
		$descriptions.put("Import_ldi_escaped_name", __("Columns escaped with"));
		$descriptions.put("Import_ldi_ignore_name", __("Do not abort on INSERT error"));
		$descriptions.put("Import_ldi_local_option_name", __("Use LOCAL keyword"));
		$descriptions.put("Import_ldi_replace_name", __("Add ON DUPLICATE KEY UPDATE"));
		$descriptions.put("Import_ldi_replace_desc", __("Update data when duplicate keys found on import"));
		$descriptions.put("Import_ldi_terminated_name", __("Columns terminated with"));
		$descriptions.put("Import_ods_col_names_name", __("Column names in first row"));
		$descriptions.put("Import_ods_empty_rows_name", __("Do not import empty rows"));
		$descriptions.put("Import_ods_recognize_currency_name", __("Import currencies ($5.00 to 5.00)"));
		$descriptions.put("Import_ods_recognize_percentages_name",
				__("Import percentages as proper decimals (12.00% to .12)"));
		$descriptions.put("Import_skip_queries_desc", __("Number of queries to skip from start."));
		$descriptions.put("Import_skip_queries_name", __("Partial import: skip queries"));
		$descriptions.put("Import_sql_compatibility_name", __("SQL compatibility mode"));
		$descriptions.put("Import_sql_no_auto_value_on_zero_name", __("Do not use AUTO_INCREMENT for zero values"));
		$descriptions.put("Import_sql_read_as_multibytes_name", __("Read as multibytes"));
		$descriptions.put("InitialSlidersState_name", __("Initial state for sliders"));
		$descriptions.put("InsertRows_desc", __("How many rows can be inserted at one time."));
		$descriptions.put("InsertRows_name", __("Number of inserted rows"));
		$descriptions.put("LimitChars_desc",
				__("Maximum number of characters shown in any non-numeric column on browse view."));
		$descriptions.put("LimitChars_name", __("Limit column characters"));
		$descriptions.put("LoginCookieDeleteAll_desc",
				__("If TRUE, logout deletes cookies for all servers; when set to FALSE, logout "
						+ "only occurs for the current server. Setting this to FALSE makes it easy to "
						+ "forget to log out from other servers when connected to multiple servers."));
		$descriptions.put("LoginCookieDeleteAll_name", __("Delete all cookies on logout"));
		$descriptions.put("LoginCookieRecall_desc", __("Define whether the previous login should be recalled or not in "
				+ "[kbd]cookie[/kbd] authentication mode."));
		$descriptions.put("LoginCookieRecall_name", __("Recall user name"));
		$descriptions.put("LoginCookieStore_desc",
				__("Defines how long (in seconds) a login cookie should be stored in browser. "
						+ "The default of 0 means that it will be kept for the existing session only, "
						+ "and will be deleted as soon as you close the browser window. This is "
						+ "recommended for non-trusted environments."));
		$descriptions.put("LoginCookieStore_name", __("Login cookie store"));
		$descriptions.put("LoginCookieValidity_desc", __("Define how long (in seconds) a login cookie is valid."));
		$descriptions.put("LoginCookieValidity_name", __("Login cookie validity"));
		$descriptions.put("LongtextDoubleTextarea_desc", __("Double size of textarea for LONGTEXT columns."));
		$descriptions.put("LongtextDoubleTextarea_name", __("Bigger textarea for LONGTEXT"));
		$descriptions.put("MaxCharactersInDisplayedSQL_desc",
				__("Maximum number of characters used when a SQL query is displayed."));
		$descriptions.put("MaxCharactersInDisplayedSQL_name", __("Maximum displayed SQL length"));
		$descriptions.put("MaxDbList_cmt", __("Users cannot set a higher value"));
		$descriptions.put("MaxDbList_desc", __("Maximum number of databases displayed in database list."));
		$descriptions.put("MaxDbList_name", __("Maximum databases"));
		$descriptions.put("FirstLevelNavigationItems_desc",
				__("The number of items that can be displayed on each page on the first level"
						+ " of the navigation tree."));
		$descriptions.put("FirstLevelNavigationItems_name", __("Maximum items on first level"));
		$descriptions.put("MaxNavigationItems_desc",
				__("The number of items that can be displayed on each page of the navigation tree."));
		$descriptions.put("MaxNavigationItems_name", __("Maximum items in branch"));
		$descriptions.put("MaxRows_desc", __("Number of rows displayed when browsing a result set. If the result set "
				+ "contains more rows, 'Previous' and 'Next' links will be " + "shown."));
		$descriptions.put("MaxRows_name", __("Maximum number of rows to display"));
		$descriptions.put("MaxTableList_cmt", __("Users cannot set a higher value"));
		$descriptions.put("MaxTableList_desc", __("Maximum number of tables displayed in table list."));
		$descriptions.put("MaxTableList_name", __("Maximum tables"));
		$descriptions.put("MemoryLimit_desc",
				__("The number of bytes a script is allowed to allocate, eg. [kbd]32M[/kbd] "
						+ "([kbd]-1[/kbd] for no limit and [kbd]0[/kbd] for no change)."));
		$descriptions.put("MemoryLimit_name", __("Memory limit"));
		$descriptions.put("ShowDatabasesNavigationAsTree_desc",
				__("In the navigation panel, replaces the database tree with a selector"));
		$descriptions.put("ShowDatabasesNavigationAsTree_name", __("Show databases navigation as tree"));
		$descriptions.put("NavigationWidth_name", __("Navigation panel width"));
		$descriptions.put("NavigationWidth_desc", __("Set to 0 to collapse navigation panel."));
		$descriptions.put("NavigationLinkWithMainPanel_desc",
				__("Link with main panel by highlighting the current database or table."));
		$descriptions.put("NavigationLinkWithMainPanel_name", __("Link with main panel"));
		$descriptions.put("NavigationDisplayLogo_desc", __("Show logo in navigation panel."));
		$descriptions.put("NavigationDisplayLogo_name", __("Display logo"));
		$descriptions.put("NavigationLogoLink_desc", __("URL where logo in the navigation panel will point to."));
		$descriptions.put("NavigationLogoLink_name", __("Logo link URL"));
		$descriptions.put("NavigationLogoLinkWindow_desc",
				__("Open the linked page in the main window ([kbd]main[/kbd]) or in a new one " + "([kbd]new[/kbd])."));
		$descriptions.put("NavigationLogoLinkWindow_name", __("Logo link target"));
		$descriptions.put("NavigationDisplayServers_desc",
				__("Display server choice at the top of the navigation panel."));
		$descriptions.put("NavigationDisplayServers_name", __("Display servers selection"));
		$descriptions.put("NavigationTreeDefaultTabTable_name", __("Target for quick access icon"));
		$descriptions.put("NavigationTreeDefaultTabTable2_name", __("Target for second quick access icon"));
		$descriptions.put("NavigationTreeDisplayItemFilterMinimum_desc",
				__("Defines the minimum number of items (tables, views, routines and events) to "
						+ "display a filter box."));
		$descriptions.put("NavigationTreeDisplayItemFilterMinimum_name",
				__("Minimum number of items to display the filter box"));
		$descriptions.put("NavigationTreeDisplayDbFilterMinimum_name",
				__("Minimum number of databases to display the database filter box"));
		$descriptions.put("NavigationTreeEnableGrouping_desc",
				__("Group items in the navigation tree (determined by the separator defined in "
						+ "the Databases and Tables tabs above)."));
		$descriptions.put("NavigationTreeEnableGrouping_name", __("Group items in the tree"));
		$descriptions.put("NavigationTreeDbSeparator_desc",
				__("String that separates databases into different tree levels."));
		$descriptions.put("NavigationTreeDbSeparator_name", __("Database tree separator"));
		$descriptions.put("NavigationTreeTableSeparator_desc",
				__("String that separates tables into different tree levels."));
		$descriptions.put("NavigationTreeTableSeparator_name", __("Table tree separator"));
		$descriptions.put("NavigationTreeTableLevel_name", __("Maximum table tree depth"));
		$descriptions.put("NavigationTreePointerEnable_desc", __("Highlight server under the mouse cursor."));
		$descriptions.put("NavigationTreePointerEnable_name", __("Enable highlighting"));
		$descriptions.put("NavigationTreeEnableExpansion_desc",
				__("Whether to offer the possibility of tree expansion in the navigation panel."));
		$descriptions.put("NavigationTreeEnableExpansion_name", __("Enable navigation tree expansion"));
		$descriptions.put("NavigationTreeShowTables_name", __("Show tables in tree"));
		$descriptions.put("NavigationTreeShowTables_desc",
				__("Whether to show tables under database in the navigation tree"));
		$descriptions.put("NavigationTreeShowViews_name", __("Show views in tree"));
		$descriptions.put("NavigationTreeShowViews_desc",
				__("Whether to show views under database in the navigation tree"));
		$descriptions.put("NavigationTreeShowFunctions_name", __("Show functions in tree"));
		$descriptions.put("NavigationTreeShowFunctions_desc",
				__("Whether to show functions under database in the navigation tree"));
		$descriptions.put("NavigationTreeShowProcedures_name", __("Show procedures in tree"));
		$descriptions.put("NavigationTreeShowProcedures_desc",
				__("Whether to show procedures under database in the navigation tree"));
		$descriptions.put("NavigationTreeShowEvents_name", __("Show events in tree"));
		$descriptions.put("NavigationTreeShowEvents_desc",
				__("Whether to show events under database in the navigation tree"));
		$descriptions.put("NavigationTreeAutoexpandSingleDb_name", __("Expand single database"));
		$descriptions.put("NavigationTreeAutoexpandSingleDb_desc",
				__("Whether to expand single database in the navigation tree automatically."));
		$descriptions.put("NumRecentTables_desc", __("Maximum number of recently used tables; set 0 to disable."));
		$descriptions.put("NumFavoriteTables_desc", __("Maximum number of favorite tables; set 0 to disable."));
		$descriptions.put("NumRecentTables_name", __("Recently used tables"));
		$descriptions.put("NumFavoriteTables_name", __("Favorite tables"));
		$descriptions.put("RowActionLinks_desc", __("These are Edit, Copy and Delete links."));
		$descriptions.put("RowActionLinks_name", __("Where to show the table row links"));
		$descriptions.put("RowActionLinksWithoutUnique_desc",
				__("Whether to show row links even in the absence of a unique key."));
		$descriptions.put("RowActionLinksWithoutUnique_name", __("Show row links anyway"));
		$descriptions.put("DisableShortcutKeys_name", __("Disable shortcut keys"));
		$descriptions.put("DisableShortcutKeys_desc", __("Disable shortcut keys"));
		$descriptions.put("NaturalOrder_desc", __("Use natural order for sorting table and database names."));
		$descriptions.put("NaturalOrder_name", __("Natural order"));
		$descriptions.put("TableNavigationLinksMode_desc", __("Use only icons, only text or both."));
		$descriptions.put("TableNavigationLinksMode_name", __("Table navigation bar"));
		$descriptions.put("OBGzip_desc", __("Use GZip output buffering for increased speed in HTTP transfers."));
		$descriptions.put("OBGzip_name", __("GZip output buffering"));
		$descriptions.put("Order_desc", __("[kbd]SMART[/kbd] - i.e. descending order for columns of type TIME, DATE, "
				+ "DATETIME and TIMESTAMP, ascending order otherwise."));
		$descriptions.put("Order_name", __("Default sorting order"));
		$descriptions.put("PersistentConnections_desc", __("Use persistent connections to MySQL databases."));
		$descriptions.put("PersistentConnections_name", __("Persistent connections"));
		$descriptions.put("PmaNoRelation_DisableWarning_desc",
				__("Disable the default warning that is displayed on the database details "
						+ "Structure page if any of the required tables for the phpMyAdmin "
						+ "configuration storage could not be found."));
		$descriptions.put("PmaNoRelation_DisableWarning_name", __("Missing phpMyAdmin configuration storage tables"));
		$descriptions.put("ReservedWordDisableWarning_desc",
				__("Disable the default warning that is displayed on the Structure page if column "
						+ "names in a table are reserved MySQL words."));
		$descriptions.put("ReservedWordDisableWarning_name", __("MySQL reserved word warning"));
		$descriptions.put("TabsMode_desc", __("Use only icons, only text or both."));
		$descriptions.put("TabsMode_name", __("How to display the menu tabs"));
		$descriptions.put("ActionLinksMode_desc", __("Use only icons, only text or both."));
		$descriptions.put("ActionLinksMode_name", __("How to display various action links"));
		$descriptions.put("ProtectBinary_desc", __("Disallow BLOB and BINARY columns from editing."));
		$descriptions.put("ProtectBinary_name", __("Protect binary columns"));
		$descriptions.put("QueryHistoryDB_desc",
				__("Enable if you want DB-based query history (requires phpMyAdmin configuration "
						+ "storage). If disabled, this utilizes JS-routines to display query history "
						+ "(lost by window close)."));
		$descriptions.put("QueryHistoryDB_name", __("Permanent query history"));
		$descriptions.put("QueryHistoryMax_cmt", __("Users cannot set a higher value"));
		$descriptions.put("QueryHistoryMax_desc", __("How many queries are kept in history."));
		$descriptions.put("QueryHistoryMax_name", __("Query history length"));
		$descriptions.put("RecodingEngine_desc",
				__("Select which functions will be used for character set conversion."));
		$descriptions.put("RecodingEngine_name", __("Recoding engine"));
		$descriptions.put("RememberSorting_desc", __("When browsing tables, the sorting of each table is remembered."));
		$descriptions.put("RememberSorting_name", __("Remember table\"s sorting"));
		$descriptions.put("TablePrimaryKeyOrder_desc", __("Default sort order for tables with a primary key."));
		$descriptions.put("TablePrimaryKeyOrder_name", __("Primary key default sort order"));
		$descriptions.put("RepeatCells_desc",
				__("Repeat the headers every X cells, [kbd]0[/kbd] deactivates this feature."));
		$descriptions.put("RepeatCells_name", __("Repeat headers"));
		$descriptions.put("GridEditing_name", __("Grid editing: trigger action"));
		$descriptions.put("RelationalDisplay_name", __("Relational display"));
		$descriptions.put("RelationalDisplay_desc", __("For display Options"));
		$descriptions.put("SaveCellsAtOnce_name", __("Grid editing: save all edited cells at once"));
		$descriptions.put("SaveDir_desc", __("Directory where exports can be saved on server."));
		$descriptions.put("SaveDir_name", __("Save directory"));
		$descriptions.put("Servers_AllowDeny_order_desc", __("Leave blank if not used."));
		$descriptions.put("Servers_AllowDeny_order_name", __("Host authorization order"));
		$descriptions.put("Servers_AllowDeny_rules_desc", __("Leave blank for defaults."));
		$descriptions.put("Servers_AllowDeny_rules_name", __("Host authorization rules"));
		$descriptions.put("Servers_AllowNoPassword_name", __("Allow logins without a password"));
		$descriptions.put("Servers_AllowRoot_name", __("Allow root login"));
		$descriptions.put("Servers_SessionTimeZone_name", __("Session timezone"));
		$descriptions.put("Servers_SessionTimeZone_desc",
				__("Sets the effective timezone; possibly different than the one from your " + "database server"));
		$descriptions.put("Servers_auth_http_realm_desc",
				__("HTTP Basic Auth Realm name to display when doing HTTP Auth."));
		$descriptions.put("Servers_auth_http_realm_name", __("HTTP Realm"));
		$descriptions.put("Servers_auth_type_desc", __("Authentication method to use."));
		$descriptions.put("Servers_auth_type_name", __("Authentication type"));
		$descriptions.put("Servers_bookmarktable_desc", __(
				"Leave blank for no [doc@bookmarks@]bookmark[/doc] " + "support, suggested: [kbd]pma__bookmark[/kbd]"));
		$descriptions.put("Servers_bookmarktable_name", __("Bookmark table"));
		$descriptions.put("Servers_column_info_desc",
				__("Leave blank for no column comments/media types, suggested: " + "[kbd]pma__column_info[/kbd]."));
		$descriptions.put("Servers_column_info_name", __("Column information table"));
		$descriptions.put("Servers_compress_desc", __("Compress connection to MySQL server."));
		$descriptions.put("Servers_compress_name", __("Compress connection"));
		$descriptions.put("Servers_controlpass_name", __("Control user password"));
		$descriptions.put("Servers_controluser_desc",
				__("A special MySQL user configured with limited permissions, more information "
						+ "available on [doc@linked-tables]documentation[/doc]."));
		$descriptions.put("Servers_controluser_name", __("Control user"));
		$descriptions.put("Servers_controlhost_desc",
				__("An alternate host to hold the configuration storage; leave blank to use the "
						+ "already defined host."));
		$descriptions.put("Servers_controlhost_name", __("Control host"));
		$descriptions.put("Servers_controlport_desc",
				__("An alternate port to connect to the host that holds the configuration storage; "
						+ "leave blank to use the default port, or the already defined port, if the "
						+ "controlhost equals host."));
		$descriptions.put("Servers_controlport_name", __("Control port"));
		$descriptions.put("Servers_hide_db_desc", __("Hide databases matching regular expression (PCRE)."));
		$descriptions.put("Servers_DisableIS_desc",
				__("More information on [a@https://github.com/phpmyadmin/phpmyadmin/issues/8970]phpMyAdmin "
						+ "issue tracker[/a] and [a@https://bugs.mysql.com/19588]MySQL Bugs[/a]"));
		$descriptions.put("Servers_DisableIS_name", __("Disable use of INFORMATION_SCHEMA"));
		$descriptions.put("Servers_hide_db_name", __("Hide databases"));
		$descriptions.put("Servers_history_desc",
				__("Leave blank for no SQL query history support, suggested: " + "[kbd]pma__history[/kbd]."));
		$descriptions.put("Servers_history_name", __("SQL query history table"));
		$descriptions.put("Servers_host_desc", __("Hostname where MySQL server is running."));
		$descriptions.put("Servers_host_name", __("Server hostname"));
		$descriptions.put("Servers_LogoutURL_name", __("Logout URL"));
		$descriptions.put("Servers_MaxTableUiprefs_desc",
				__("Limits number of table preferences which are stored in database, the oldest "
						+ "records are automatically removed."));
		$descriptions.put("Servers_MaxTableUiprefs_name", __("Maximal number of table preferences to store"));
		$descriptions.put("Servers_savedsearches_name", __("QBE saved searches table"));
		$descriptions.put("Servers_savedsearches_desc",
				__("Leave blank for no QBE saved searches support, suggested: " + "[kbd]pma__savedsearches[/kbd]."));
		$descriptions.put("Servers_export_templates_name", __("Export templates table"));
		$descriptions.put("Servers_export_templates_desc",
				__("Leave blank for no export template support, suggested: " + "[kbd]pma__export_templates[/kbd]."));
		$descriptions.put("Servers_central_columns_name", __("Central columns table"));
		$descriptions.put("Servers_central_columns_desc",
				__("Leave blank for no central columns support, suggested: " + "[kbd]pma__central_columns[/kbd]."));
		$descriptions.put("Servers_only_db_desc",
				__("You can use MySQL wildcard characters (% and _), escape them if you want to "
						+ "use their literal instances, i.e. use [kbd]'my_db'[/kbd] and not " + "[kbd]'my_db'[/kbd]."));
		$descriptions.put("Servers_only_db_name", __("Show only listed databases"));
		$descriptions.put("Servers_password_desc", __("Leave empty if not using config auth."));
		$descriptions.put("Servers_password_name", __("Password for config auth"));
		$descriptions.put("Servers_pdf_pages_desc",
				__("Leave blank for no PDF schema support, suggested: [kbd]pma__pdf_pages[/kbd]."));
		$descriptions.put("Servers_pdf_pages_name", __("PDF schema: pages table"));
		$descriptions.put("Servers_pmadb_desc",
				__("Database used for relations, bookmarks, and PDF features. See "
						+ "[doc@linked-tables]pmadb[/doc] for complete information. "
						+ "Leave blank for no support. Suggested: [kbd]phpmyadmin[/kbd]."));
		$descriptions.put("Servers_pmadb_name", __("Database name"));
		$descriptions.put("Servers_port_desc", __("Port on which MySQL server is listening, leave empty for default."));
		$descriptions.put("Servers_port_name", __("Server port"));
		$descriptions.put("Servers_recent_desc",
				__("Leave blank for no 'persistent' recently used tables across sessions, "
						+ "suggested: [kbd]pma__recent[/kbd]."));
		$descriptions.put("Servers_recent_name", __("Recently used table"));
		$descriptions.put("Servers_favorite_desc",
				__("Leave blank for no 'persistent' favorite tables across sessions, "
						+ "suggested: [kbd]pma__favorite[/kbd]."));
		$descriptions.put("Servers_favorite_name", __("Favorites table"));
		$descriptions.put("Servers_relation_desc", __("Leave blank for no "
				+ "[doc@relations@]relation-links[/doc] support, " + "suggested: [kbd]pma__relation[/kbd]."));
		$descriptions.put("Servers_relation_name", __("Relation table"));
		$descriptions.put("Servers_SignonSession_desc",
				__("See [doc@authentication-modes]authentication " + "types[/doc] for an example."));
		$descriptions.put("Servers_SignonSession_name", __("Signon session name"));
		$descriptions.put("Servers_SignonURL_name", __("Signon URL"));
		$descriptions.put("Servers_socket_desc",
				__("Socket on which MySQL server is listening, leave empty for default."));
		$descriptions.put("Servers_socket_name", __("Server socket"));
		$descriptions.put("Servers_ssl_desc", __("Enable SSL for connection to MySQL server."));
		$descriptions.put("Servers_ssl_name", __("Use SSL"));
		$descriptions.put("Servers_table_coords_desc",
				__("Leave blank for no PDF schema support, suggested: [kbd]pma__table_coords[/kbd]."));
		$descriptions.put("Servers_table_coords_name", __("Designer and PDF schema: table coordinates"));
		$descriptions.put("Servers_table_info_desc",
				__("Table to describe the display columns, leave blank for no support; "
						+ "suggested: [kbd]pma__table_info[/kbd]."));
		$descriptions.put("Servers_table_info_name", __("Display columns table"));
		$descriptions.put("Servers_table_uiprefs_desc",
				__("Leave blank for no 'persistent' tables\" UI preferences across sessions, "
						+ "suggested: [kbd]pma__table_uiprefs[/kbd]."));
		$descriptions.put("Servers_table_uiprefs_name", __("UI preferences table"));
		$descriptions.put("Servers_tracking_add_drop_database_desc",
				__("Whether a DROP DATABASE IF EXISTS statement will be added as first line to "
						+ "the log when creating a database."));
		$descriptions.put("Servers_tracking_add_drop_database_name", __("Add DROP DATABASE"));
		$descriptions.put("Servers_tracking_add_drop_table_desc",
				__("Whether a DROP TABLE IF EXISTS statement will be added as first line to the "
						+ "log when creating a table."));
		$descriptions.put("Servers_tracking_add_drop_table_name", __("Add DROP TABLE"));
		$descriptions.put("Servers_tracking_add_drop_view_desc",
				__("Whether a DROP VIEW IF EXISTS statement will be added as first line to the "
						+ "log when creating a view."));
		$descriptions.put("Servers_tracking_add_drop_view_name", __("Add DROP VIEW"));
		$descriptions.put("Servers_tracking_default_statements_desc",
				__("Defines the list of statements the auto-creation uses for new versions."));
		$descriptions.put("Servers_tracking_default_statements_name", __("Statements to track"));
		$descriptions.put("Servers_tracking_desc",
				__("Leave blank for no SQL query tracking support, suggested: " + "[kbd]pma__tracking[/kbd]."));
		$descriptions.put("Servers_tracking_name", __("SQL query tracking table"));
		$descriptions.put("Servers_tracking_version_auto_create_desc",
				__("Whether the tracking mechanism creates versions for tables and views " + "automatically."));
		$descriptions.put("Servers_tracking_version_auto_create_name", __("Automatically create versions"));
		$descriptions.put("Servers_userconfig_desc",
				__("Leave blank for no user preferences storage in database, suggested: "
						+ "[kbd]pma__userconfig[/kbd]."));
		$descriptions.put("Servers_userconfig_name", __("User preferences storage table"));
		$descriptions.put("Servers_users_desc",
				__("Both this table and the user groups table are required to enable the "
						+ "configurable menus feature; leaving either one of them blank will disable "
						+ "this feature, suggested: [kbd]pma__users[/kbd]."));
		$descriptions.put("Servers_users_name", __("Users table"));
		$descriptions.put("Servers_usergroups_desc",
				__("Both this table and the users table are required to enable the configurable "
						+ "menus feature; leaving either one of them blank will disable this feature, "
						+ "suggested: [kbd]pma__usergroups[/kbd]."));
		$descriptions.put("Servers_usergroups_name", __("User groups table"));
		$descriptions.put("Servers_navigationhiding_desc",
				__("Leave blank to disable the feature to hide and show navigation items, "
						+ "suggested: [kbd]pma__navigationhiding[/kbd]."));
		$descriptions.put("Servers_navigationhiding_name", __("Hidden navigation items table"));
		$descriptions.put("Servers_user_desc", __("Leave empty if not using config auth."));
		$descriptions.put("Servers_user_name", __("User for config auth"));
		$descriptions.put("Servers_verbose_desc",
				__("A user-friendly description of this server. Leave blank to display the " + "hostname instead."));
		$descriptions.put("Servers_verbose_name", __("Verbose name of this server"));
		$descriptions.put("ShowAll_desc", __("Whether a user should be displayed a 'show all (rows)' button."));
		$descriptions.put("ShowAll_name", __("Allow to display all the rows"));
		$descriptions.put("ShowChgPassword_desc",
				__("Please note that enabling this has no effect with [kbd]config[/kbd] "
						+ "authentication mode because the password is hard coded in the configuration "
						+ "file; this does not limit the ability to execute the same command directly."));
		$descriptions.put("ShowChgPassword_name", __("Show password change form"));
		$descriptions.put("ShowCreateDb_name", __("Show create database form"));
		$descriptions.put("ShowDbStructureComment_desc",
				__("Show or hide a column displaying the comments for all tables."));
		$descriptions.put("ShowDbStructureComment_name", __("Show table comments"));
		$descriptions.put("ShowDbStructureCreation_desc",
				__("Show or hide a column displaying the Creation timestamp for all tables."));
		$descriptions.put("ShowDbStructureCreation_name", __("Show creation timestamp"));
		$descriptions.put("ShowDbStructureLastUpdate_desc",
				__("Show or hide a column displaying the Last update timestamp for all tables."));
		$descriptions.put("ShowDbStructureLastUpdate_name", __("Show last update timestamp"));
		$descriptions.put("ShowDbStructureLastCheck_desc",
				__("Show or hide a column displaying the Last check timestamp for all tables."));
		$descriptions.put("ShowDbStructureLastCheck_name", __("Show last check timestamp"));
		$descriptions.put("ShowDbStructureCharset_desc",
				__("Show or hide a column displaying the charset for all tables."));
		$descriptions.put("ShowDbStructureCharset_name", __("Show table charset"));
		$descriptions.put("ShowFieldTypesInDataEditView_desc",
				__("Defines whether or not type fields should be initially displayed in " + "edit/insert mode."));
		$descriptions.put("ShowFieldTypesInDataEditView_name", __("Show field types"));
		$descriptions.put("ShowFunctionFields_desc", __("Display the function fields in edit/insert mode."));
		$descriptions.put("ShowFunctionFields_name", __("Show function fields"));
		$descriptions.put("ShowHint_desc", __("Whether to show hint or not."));
		$descriptions.put("ShowHint_name", __("Show hint"));
		$descriptions.put("ShowPhpInfo_desc",
				__("Shows link to [a@https://php.net/manual/function.phpinfo.php]phpinfo()[/a] " + "output."));
		$descriptions.put("ShowPhpInfo_name", __("Show phpinfo() link"));
		$descriptions.put("ShowServerInfo_name", __("Show detailed MySQL server information"));
		$descriptions.put("ShowSQL_desc",
				__("Defines whether SQL queries generated by phpMyAdmin should be displayed."));
		$descriptions.put("ShowSQL_name", __("Show SQL queries"));
		$descriptions.put("RetainQueryBox_desc",
				__("Defines whether the query box should stay on-screen after its submission."));
		$descriptions.put("RetainQueryBox_name", __("Retain query box"));
		$descriptions.put("ShowStats_desc", __("Allow to display database and table statistics (eg. space usage)."));
		$descriptions.put("ShowStats_name", __("Show statistics"));
		$descriptions.put("SkipLockedTables_desc",
				__("Mark used tables and make it possible to show databases with locked tables."));
		$descriptions.put("SkipLockedTables_name", __("Skip locked tables"));
		$descriptions.put("SQLQuery_Edit_name", __("Edit"));
		$descriptions.put("SQLQuery_Explain_name", __("Explain SQL"));
		$descriptions.put("SQLQuery_Refresh_name", __("Refresh"));
		$descriptions.put("SQLQuery_ShowAsPHP_name", __("Create PHP code"));
		$descriptions.put("SuhosinDisableWarning_desc",
				__("Disable the default warning that is displayed on the main page if Suhosin is " + "detected."));
		$descriptions.put("SuhosinDisableWarning_name", __("Suhosin warning"));
		$descriptions.put("LoginCookieValidityDisableWarning_desc",
				__("Disable the default warning that is displayed on the main page if the value "
						+ "of the PHP setting session.gc_maxlifetime is less than the value of "
						+ "`LoginCookieValidity`."));
		$descriptions.put("LoginCookieValidityDisableWarning_name", __("Login cookie validity warning"));
		$descriptions.put("TextareaCols_desc",
				__("Textarea size (columns) in edit mode, this value will be emphasized for SQL "
						+ "query textareas (*2)."));
		$descriptions.put("TextareaCols_name", __("Textarea columns"));
		$descriptions.put("TextareaRows_desc", __(
				"Textarea size (rows) in edit mode, this value will be emphasized for SQL " + "query textareas (*2)."));
		$descriptions.put("TextareaRows_name", __("Textarea rows"));
		$descriptions.put("TitleDatabase_desc", __("Title of browser window when a database is selected."));
		$descriptions.put("TitleDatabase_name", __("Database"));
		$descriptions.put("TitleDefault_desc", __("Title of browser window when nothing is selected."));
		$descriptions.put("TitleDefault_name", __("Default title"));
		$descriptions.put("TitleServer_desc", __("Title of browser window when a server is selected."));
		$descriptions.put("TitleServer_name", __("Server"));
		$descriptions.put("TitleTable_desc", __("Title of browser window when a table is selected."));
		$descriptions.put("TitleTable_name", __("Table"));
		$descriptions.put("TrustedProxies_desc",
				__("Input proxies as [kbd]IP: trusted HTTP header[/kbd]. The following example "
						+ "specifies that phpMyAdmin should trust a HTTP_X_FORWARDED_FOR "
						+ "(X-Forwarded-For) header coming from the proxy 1.2.3.4:[br][kbd]1.2.3.4: "
						+ "HTTP_X_FORWARDED_FOR[/kbd]."));
		$descriptions.put("TrustedProxies_name", __("List of trusted proxies for IP allow/deny"));
		$descriptions.put("UploadDir_desc", __("Directory on server where you can upload files for import."));
		$descriptions.put("UploadDir_name", __("Upload directory"));
		$descriptions.put("UseDbSearch_desc", __("Allow for searching inside the entire database."));
		$descriptions.put("UseDbSearch_name", __("Use database search"));
		$descriptions.put("UserprefsDeveloperTab_desc",
				__("When disabled, users cannot set any of the options below, regardless of the "
						+ "checkbox on the right."));
		$descriptions.put("UserprefsDeveloperTab_name", __("Enable the Developer tab in settings"));
		$descriptions.put("VersionCheck_desc", __("Enables check for latest version on main phpMyAdmin page."));
		$descriptions.put("VersionCheck_name", __("Version check"));
		$descriptions.put("ProxyUrl_desc",
				__("The url of the proxy to be used when retrieving the information about the "
						+ "latest version of phpMyAdmin or when submitting error reports. You need this "
						+ "if the server where phpMyAdmin is installed does not have direct access to "
						+ "the internet. The format is: 'hostname:portnumber'."));
		$descriptions.put("ProxyUrl_name", __("Proxy url"));
		$descriptions.put("ProxyUser_desc", __("The username for authenticating with the proxy. By default, no "
				+ "authentication is performed. If a username is supplied, Basic "
				+ "Authentication will be performed. No other types of authentication are " + "currently supported."));
		$descriptions.put("ProxyUser_name", __("Proxy username"));
		$descriptions.put("ProxyPass_desc", __("The password for authenticating with the proxy."));
		$descriptions.put("ProxyPass_name", __("Proxy password"));

		$descriptions.put("ZipDump_desc", __("Enable ZIP compression for import and export operations."));
		$descriptions.put("ZipDump_name", __("ZIP"));
		$descriptions.put("CaptchaLoginPublicKey_desc", __("Enter your public key for your domain reCaptcha service."));
		$descriptions.put("CaptchaLoginPublicKey_name", __("Public key for reCaptcha"));
		$descriptions.put("CaptchaLoginPrivateKey_desc",
				__("Enter your private key for your domain reCaptcha service."));
		$descriptions.put("CaptchaLoginPrivateKey_name", __("Private key for reCaptcha"));

		$descriptions.put("SendErrorReports_desc", __("Choose the default action when sending error reports."));
		$descriptions.put("SendErrorReports_name", __("Send error reports"));

		$descriptions.put("ConsoleEnterExecutes_desc",
				__("Queries are executed by pressing Enter (instead of Ctrl+Enter). New lines "
						+ "will be inserted with Shift+Enter."));
		$descriptions.put("ConsoleEnterExecutes_name", __("Enter executes queries in console"));

		$descriptions.put("ZeroConf_desc", __("Enable Zero Configuration mode which lets you setup phpMyAdmin "
				+ "configuration storage tables automatically."));
		$descriptions.put("ZeroConf_name", __("Enable Zero Configuration mode"));
		$descriptions.put("Console_StartHistory_name", __("Show query history at start"));
		$descriptions.put("Console_AlwaysExpand_name", __("Always expand query messages"));
		$descriptions.put("Console_CurrentQuery_name", __("Show current browsing query"));
		$descriptions.put("Console_EnterExecutes_name",
				__("Execute queries on Enter and insert new line with Shift + Enter"));
		$descriptions.put("Console_DarkTheme_name", __("Switch to dark theme"));
		$descriptions.put("Console_Height_name", __("Console height"));
		$descriptions.put("Console_Mode_name", __("Console mode"));
		$descriptions.put("Console_GroupQueries_name", __("Group queries"));
		$descriptions.put("Console_Order_name", __("Order"));
		$descriptions.put("Console_OrderBy_name", __("Order by"));
		$descriptions.put("DefaultConnectionCollation_name", __("Server connection collation"));
	}

	/**
	 * Return name or description for a cleaned up configuration path.
	 *
	 * @param string
	 *            $path Path of configuration
	 * @param string
	 *            $type Type of message, either "name", "cmt" or "desc"
	 *
	 * @return string|null Null if not found
	 */
	public static String getString(String $path, String $type /* = "name" */) {
		String $key = $path + "_" + $type;
		return $descriptions.get($key);
	}

}
