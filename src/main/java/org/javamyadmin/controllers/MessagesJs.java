package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

import static org.javamyadmin.php.Php.*;

/**
 * Exporting of translated messages from PHP to Javascript
 *
 * @package PhpMyAdmin
 * @see messages.php
 *
 */
@WebServlet(urlPatterns = "/js/messages.php", name = "MessagesJs")
public class MessagesJs extends AbstractController {

	private static final long serialVersionUID = -5104865747886536865L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response, Response pmaResponse,
			SessionMap $_SESSION, Globals GLOBALS) throws ServletException, IOException {

		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		// TODO header('Expires: ' . gmdate('D, d M Y H:i:s', time() + 3600) + " GMT"));

		Map<String, String> $js_messages = new HashMap<>();

		/* For confirmations */
		$js_messages.put("strConfirm", __("Confirm"));
		$js_messages.put("strDoYouReally", __("Do you really want to execute '%s'?"));
		$js_messages.put("strDropDatabaseStrongWarning", "You are about to DESTROY a complete database!");
		$js_messages.put("strDatabaseRenameToSameName",
				"Cannot rename database to the same name. Change the name and try again");
		$js_messages.put("strDropTableStrongWarning", "You are about to DESTROY a complete table!");
		$js_messages.put("strTruncateTableStrongWarning", "You are about to TRUNCATE a complete table!");
		$js_messages.put("strDeleteTrackingData", __("Delete tracking data for this table?"));
		$js_messages.put("strDeleteTrackingDataMultiple", "Delete tracking data for these tables?");
		$js_messages.put("strDeleteTrackingVersion", "Delete tracking data for this version?");
		$js_messages.put("strDeleteTrackingVersionMultiple", "Delete tracking data for these versions?");
		$js_messages.put("strDeletingTrackingEntry", __("Delete entry from tracking report?"));
		$js_messages.put("strDeletingTrackingData", __("Deleting tracking data"));
		$js_messages.put("strDroppingPrimaryKeyIndex", __("Dropping Primary Key/Index"));
		$js_messages.put("strDroppingForeignKey", __("Dropping Foreign key."));
		$js_messages.put("strOperationTakesLongTime", "This operation could take a long time. Proceed anyway?");
		$js_messages.put("strDropUserGroupWarning", "Do you really want to delete user group '%s'?");
		$js_messages.put("strConfirmDeleteQBESearch", "Do you really want to delete the search '%s'?");
		$js_messages.put("strConfirmNavigation", "You have unsaved changes; are you sure you want to leave this page?");
		$js_messages.put("strConfirmRowChange",
				"You are trying to reduce the number of rows, but have already entered data in those rows which will be lost. Do you wish to continue?");
		$js_messages.put("strDropUserWarning", "Do you really want to revoke the selected user(s) ?");
		$js_messages.put("strDeleteCentralColumnWarning", "Do you really want to delete this central column?");
		$js_messages.put("strDropRTEitems", "Do you really want to delete the selected items?");
		$js_messages.put("strDropPartitionWarning",
				__("Do you really want to DROP the selected partition(s)? This will also DELETE "
						+ "the data related to the selected partition(s)!"));
		$js_messages.put("strTruncatePartitionWarning", "Do you really want to TRUNCATE the selected partition(s)?");
		$js_messages.put("strRemovePartitioningWarning", "Do you really want to remove partitioning?");
		$js_messages.put("strResetSlaveWarning", __("Do you really want to RESET SLAVE?"));
		$js_messages.put("strChangeColumnCollation",
				__("This operation will attempt to convert your data to the new collation. In "
						+ "rare cases, especially where a character doesn\'t exist in the new "
						+ "collation, this process could cause the data to appear incorrectly under "
						+ "the new collation; in this case we suggest you revert to the original "
						+ "collation and refer to the tips at ") + "<a href='%s' target='garbled_data_wiki'>"
						+ __("Garbled Data") + "</a>." + "<br><br>"
						+ __("Are you sure you wish to change the collation and convert the data?"));
		$js_messages.put("strChangeAllColumnCollationsWarning",
				__("Through this operation, MySQL attempts to map the data values between "
						+ "collations. If the character sets are incompatible, there may be data loss "
						+ "and this lost data may <b>NOT</b> be recoverable simply by changing back the "
						+ "column collation(s). <b>To convert existing data, it is suggested to use the "
						+ "column(s) editing feature (the 'Change' Link) on the table structure page. " + "</b>")
						+ "<br><br>"
						+ __("Are you sure you wish to change all the column collations and convert the data?"));

		/* For modal dialog buttons */
		$js_messages.put("strSaveAndClose", __("Save & close"));
		$js_messages.put("strReset", __("Reset"));
		$js_messages.put("strResetAll", __("Reset all"));

		/* For indexes */
		$js_messages.put("strFormEmpty", __("Missing value in the form!"));
		$js_messages.put("strRadioUnchecked", __("Select at least one of the options!"));
		$js_messages.put("strEnterValidNumber", __("Please enter a valid number!"));
		$js_messages.put("strEnterValidLength", __("Please enter a valid length!"));
		$js_messages.put("strAddIndex", __("Add index"));
		$js_messages.put("strEditIndex", __("Edit index"));
		$js_messages.put("strAddToIndex", __("Add %s column(s) to index"));
		$js_messages.put("strCreateSingleColumnIndex", __("Create single-column index"));
		$js_messages.put("strCreateCompositeIndex", __("Create composite index"));
		$js_messages.put("strCompositeWith", __("Composite with:"));
		$js_messages.put("strMissingColumn", __("Please select column(s) for the index."));

		/* For Preview SQL */
		$js_messages.put("strPreviewSQL", __("Preview SQL"));

		/* For Simulate DML */
		$js_messages.put("strSimulateDML", __("Simulate query"));
		$js_messages.put("strMatchedRows", __("Matched rows:"));
		$js_messages.put("strSQLQuery", __("SQL query:"));

		/* Charts */
		/* l10n: Default label for the y-Axis of Charts */
		$js_messages.put("strYValues", __("Y values"));

		/* Database multi-table query */
		$js_messages.put("strEmptyQuery", __("Please enter the SQL query first."));

		/* For server/privileges.js */
		$js_messages.put("strHostEmpty", __("The host name is empty!"));
		$js_messages.put("strUserEmpty", __("The user name is empty!"));
		$js_messages.put("strPasswordEmpty", __("The password is empty!"));
		$js_messages.put("strPasswordNotSame", __("The passwords aren\'t the same!"));
		$js_messages.put("strRemovingSelectedUsers", __("Removing Selected Users"));
		$js_messages.put("strClose", __("Close"));

		/* For export.js */
		$js_messages.put("strTemplateCreated", __("Template was created."));
		$js_messages.put("strTemplateLoaded", __("Template was loaded."));
		$js_messages.put("strTemplateUpdated", __("Template was updated."));
		$js_messages.put("strTemplateDeleted", __("Template was deleted."));

		/* l10n: Other, small valued, queries */
		$js_messages.put("strOther", __("Other"));
		/* l10n: Thousands separator */
		$js_messages.put("strThousandsSeparator", __(","));
		/* l10n: Decimal separator */
		$js_messages.put("strDecimalSeparator", __("."));

		$js_messages.put("strChartConnectionsTitle", __("Connections / Processes"));

		/* server status monitor */
		$js_messages.put("strIncompatibleMonitorConfig", "Local monitor configuration incompatible!");
		$js_messages.put("strIncompatibleMonitorConfigDescription",
				__("The chart arrangement configuration in your browsers local storage is not "
						+ "compatible anymore to the newer version of the monitor dialog. It is very "
						+ "likely that your current configuration will not work anymore. Please reset "
						+ "your configuration to default in the <i>Settings</i> menu."));

		$js_messages.put("strQueryCacheEfficiency", __("Query cache efficiency"));
		$js_messages.put("strQueryCacheUsage", __("Query cache usage"));
		$js_messages.put("strQueryCacheUsed", __("Query cache used"));

		$js_messages.put("strSystemCPUUsage", __("System CPU usage"));
		$js_messages.put("strSystemMemory", __("System memory"));
		$js_messages.put("strSystemSwap", __("System swap"));

		$js_messages.put("strAverageLoad", __("Average load"));
		$js_messages.put("strTotalMemory", __("Total memory"));
		$js_messages.put("strCachedMemory", __("Cached memory"));
		$js_messages.put("strBufferedMemory", __("Buffered memory"));
		$js_messages.put("strFreeMemory", __("Free memory"));
		$js_messages.put("strUsedMemory", __("Used memory"));

		$js_messages.put("strTotalSwap", __("Total swap"));
		$js_messages.put("strCachedSwap", __("Cached swap"));
		$js_messages.put("strUsedSwap", __("Used swap"));
		$js_messages.put("strFreeSwap", __("Free swap"));

		$js_messages.put("strBytesSent", __("Bytes sent"));
		$js_messages.put("strBytesReceived", __("Bytes received"));
		$js_messages.put("strConnections", __("Connections"));
		$js_messages.put("strProcesses", __("Processes"));

		/* summary row */
		$js_messages.put("strB", __("B"));
		$js_messages.put("strKiB", __("KiB"));
		$js_messages.put("strMiB", __("MiB"));
		$js_messages.put("strGiB", __("GiB"));
		$js_messages.put("strTiB", __("TiB"));
		$js_messages.put("strPiB", __("PiB"));
		$js_messages.put("strEiB", __("EiB"));
		$js_messages.put("strNTables", __("%d table(s)"));

		/* l10n: Questions is the name of a MySQL Status variable */
		$js_messages.put("strQuestions", __("Questions"));
		$js_messages.put("strTraffic", __("Traffic"));
		$js_messages.put("strSettings", __("Settings"));
		$js_messages.put("strAddChart", __("Add chart to grid"));
		$js_messages.put("strClose", __("Close"));
		$js_messages.put("strAddOneSeriesWarning", "Please add at least one variable to the series!");
		$js_messages.put("strNone", __("None"));
		$js_messages.put("strResumeMonitor", __("Resume monitor"));
		$js_messages.put("strPauseMonitor", __("Pause monitor"));
		$js_messages.put("strStartRefresh", __("Start auto refresh"));
		$js_messages.put("strStopRefresh", __("Stop auto refresh"));
		/* Monitor: Instructions Dialog */
		$js_messages.put("strBothLogOn", __("general_log and slow_query_log are enabled."));
		$js_messages.put("strGenLogOn", __("general_log is enabled."));
		$js_messages.put("strSlowLogOn", __("slow_query_log is enabled."));
		$js_messages.put("strBothLogOff", __("slow_query_log and general_log are disabled."));
		$js_messages.put("strLogOutNotTable", __("log_output is not set to TABLE."));
		$js_messages.put("strLogOutIsTable", __("log_output is set to TABLE."));
		$js_messages.put("strSmallerLongQueryTimeAdvice",
				__("slow_query_log is enabled, but the server logs only queries that take longer "
						+ "than %d seconds. It is advisable to set this long_query_time 0-2 seconds, "
						+ "depending on your system."));
		$js_messages.put("strLongQueryTimeSet", __("long_query_time is set to %d second(s)."));
		$js_messages.put("strSettingsAppliedGlobal",
				__("Following settings will be applied globally and reset to default on server " + "restart:"));
		/* l10n: %s is FILE or TABLE */
		$js_messages.put("strSetLogOutput", __("Set log_output to %s"));
		/* l10n: Enable in this context means setting a status variable to ON */
		$js_messages.put("strEnableVar", __("Enable %s"));
		/* l10n: Disable in this context means setting a status variable to OFF */
		$js_messages.put("strDisableVar", __("Disable %s"));
		/* l10n: %d seconds */
		$js_messages.put("setSetLongQueryTime", __("Set long_query_time to %d seconds."));
		$js_messages.put("strNoSuperUser", __("You can't change these variables. Please log in as root or contact"
				+ " your database administrator."));
		$js_messages.put("strChangeSettings", __("Change settings"));
		$js_messages.put("strCurrentSettings", __("Current settings"));

		$js_messages.put("strChartTitle", __("Chart title"));
		/* l10n: As in differential values */
		$js_messages.put("strDifferential", __("Differential"));
		$js_messages.put("strDividedBy", __("Divided by %s"));
		$js_messages.put("strUnit", __("Unit"));

		$js_messages.put("strFromSlowLog", __("From slow log"));
		$js_messages.put("strFromGeneralLog", __("From general log"));
		$js_messages.put("strServerLogError",
				__("The database name is not known for this query in the server's logs."));
		$js_messages.put("strAnalysingLogsTitle", __("Analysing logs"));
		$js_messages.put("strAnalysingLogs", "Analysing & loading logs. This may take a while.");
		$js_messages.put("strCancelRequest", __("Cancel request"));
		$js_messages.put("strCountColumnExplanation",
				__("This column shows the amount of identical queries that are grouped together. "
						+ "However only the SQL query itself has been used as a grouping criteria, so "
						+ "the other attributes of queries, such as start time, may differ."));
		$js_messages.put("strMoreCountColumnExplanation",
				__("Since grouping of INSERTs queries has been selected, INSERT queries into the "
						+ "same table are also being grouped together, disregarding of the inserted " + "data."));
		$js_messages.put("strLogDataLoaded", "Log data loaded. Queries executed in this time span:");

		$js_messages.put("strJumpToTable", __("Jump to Log table"));
		$js_messages.put("strNoDataFoundTitle", __("No data found"));
		$js_messages.put("strNoDataFound", "Log analysed, but no data found in this time span.");

		$js_messages.put("strAnalyzing", __("Analyzing…"));
		$js_messages.put("strExplainOutput", __("Explain output"));
		$js_messages.put("strStatus", __("Status"));
		$js_messages.put("strTime", __("Time"));
		$js_messages.put("strTotalTime", __("Total time:"));
		$js_messages.put("strProfilingResults", __("Profiling results"));
		$js_messages.put("strTable", _pgettext("Display format", "Table"));
		$js_messages.put("strChart", __("Chart"));

		$js_messages.put("strAliasDatabase", _pgettext("Alias", "Database"));
		$js_messages.put("strAliasTable", _pgettext("Alias", "Table"));
		$js_messages.put("strAliasColumn", _pgettext("Alias", "Column"));

		/* l10n: A collection of available filters */
		$js_messages.put("strFiltersForLogTable", __("Log table filter options"));
		/* l10n: Filter as in "Start Filtering" */
		$js_messages.put("strFilter", __("Filter"));
		$js_messages.put("strFilterByWordRegexp", __("Filter queries by word/regexp:"));
		$js_messages.put("strIgnoreWhereAndGroup", "Group queries, ignoring variable data in WHERE clauses");
		$js_messages.put("strSumRows", __("Sum of grouped rows:"));
		$js_messages.put("strTotal", __("Total:"));

		$js_messages.put("strLoadingLogs", __("Loading logs"));
		$js_messages.put("strRefreshFailed", __("Monitor refresh failed"));
		$js_messages.put("strInvalidResponseExplanation",
				__("While requesting new chart data the server returned an invalid response. This "
						+ "is most likely because your session expired. Reloading the page and "
						+ "reentering your credentials should help."));
		$js_messages.put("strReloadPage", __("Reload page"));

		$js_messages.put("strAffectedRows", __("Affected rows:"));

		$js_messages.put("strFailedParsingConfig",
				__("Failed parsing config file. It doesn\'t seem to be valid JSON code."));
		$js_messages.put("strFailedBuildingGrid",
				__("Failed building chart grid with imported config. Resetting to default config…"));
		$js_messages.put("strImport", __("Import"));
		$js_messages.put("strImportDialogTitle", __("Import monitor configuration"));
		$js_messages.put("strImportDialogMessage", "Please select the file you want to import.");
		$js_messages.put("strTableNameDialogMessage", "Please enter a valid table name.");
		$js_messages.put("strDBNameDialogMessage", "Please enter a valid database name.");
		$js_messages.put("strNoImportFile", __("No files available on server for import!"));

		$js_messages.put("strAnalyzeQuery", __("Analyse query"));

		/* Server status advisor */

		$js_messages.put("strAdvisorSystem", __("Advisor system"));
		$js_messages.put("strPerformanceIssues", __("Possible performance issues"));
		$js_messages.put("strIssuse", __("Issue"));
		$js_messages.put("strRecommendation", __("Recommendation"));
		$js_messages.put("strRuleDetails", __("Rule details"));
		$js_messages.put("strJustification", __("Justification"));
		$js_messages.put("strFormula", __("Used variable / formula"));
		$js_messages.put("strTest", __("Test"));

		/* For query editor */
		$js_messages.put("strFormatting", __("Formatting SQL…"));
		$js_messages.put("strNoParam", __("No parameters found!"));

		/* For inline query editing */
		$js_messages.put("strGo", __("Go"));
		$js_messages.put("strCancel", __("Cancel"));

		/* For page-related settings */
		$js_messages.put("strPageSettings", __("Page-related settings"));
		$js_messages.put("strApply", __("Apply"));

		/* For Ajax Notifications */
		$js_messages.put("strLoading", __("Loading…"));
		$js_messages.put("strAbortedRequest", __("Request aborted!!"));
		$js_messages.put("strProcessingRequest", __("Processing request"));
		$js_messages.put("strRequestFailed", __("Request failed!!"));
		$js_messages.put("strErrorProcessingRequest", __("Error in processing request"));
		$js_messages.put("strErrorCode", __("Error code: %s"));
		$js_messages.put("strErrorText", __("Error text: %s"));
		$js_messages.put("strErrorConnection",
				__("It seems that the connection to server has been lost. Please check your "
						+ "network connectivity and server status."));
		$js_messages.put("strNoDatabasesSelected", __("No databases selected."));
		$js_messages.put("strNoAccountSelected", __("No accounts selected."));
		$js_messages.put("strDroppingColumn", __("Dropping column"));
		$js_messages.put("strAddingPrimaryKey", __("Adding primary key"));
		$js_messages.put("strOK", __("OK"));
		$js_messages.put("strDismiss", __("Click to dismiss this notification"));

		/* For database/operations.js */
		$js_messages.put("strRenamingDatabases", __("Renaming databases"));
		$js_messages.put("strCopyingDatabase", __("Copying database"));
		$js_messages.put("strChangingCharset", __("Changing charset"));
		$js_messages.put("strNo", __("No"));

		/* For Foreign key checks */
		$js_messages.put("strForeignKeyCheck", __("Enable foreign key checks"));

		/* For db_stucture.js */
		$js_messages.put("strErrorRealRowCount", __("Failed to get real row count."));

		/* For database/search.js */
		$js_messages.put("strSearching", __("Searching"));
		$js_messages.put("strHideSearchResults", __("Hide search results"));
		$js_messages.put("strShowSearchResults", __("Show search results"));
		$js_messages.put("strBrowsing", __("Browsing"));
		$js_messages.put("strDeleting", __("Deleting"));
		$js_messages.put("strConfirmDeleteResults", __("Delete the matches for the %s table?"));

		/* For db_routines.js */
		$js_messages.put("MissingReturn", "The definition of a stored function must contain a RETURN statement!");
		$js_messages.put("strExport", __("Export"));
		$js_messages.put("NoExportable", "No routine is exportable. Required privileges may be lacking.");

		/* For ENUM/SET editor */
		$js_messages.put("enum_editor", __("ENUM/SET editor"));
		$js_messages.put("enum_columnVals", __("Values for column %s"));
		$js_messages.put("enum_newColumnVals", __("Values for a new column"));
		$js_messages.put("enum_hint", __("Enter each value in a separate field."));
		$js_messages.put("enum_addValue", __("Add %d value(s)"));

		/* For import.js */
		$js_messages.put("strImportCSV",
				__("Note: If the file contains multiple tables, they will be combined into one."));

		/* For sql.js */
		$js_messages.put("strHideQueryBox", __("Hide query box"));
		$js_messages.put("strShowQueryBox", __("Show query box"));
		$js_messages.put("strEdit", __("Edit"));
		$js_messages.put("strDelete", __("Delete"));
		$js_messages.put("strNotValidRowNumber", __("%d is not valid row number."));
		$js_messages.put("strBrowseForeignValues", __("Browse foreign values"));
		$js_messages.put("strNoAutoSavedQuery",
				__("No previously auto-saved query is available. Loading default query."));
		$js_messages.put("strPreviousSaveQuery",
				__("You have a previously saved query. Click Get auto-saved query to load the query."));
		$js_messages.put("strBookmarkVariable", __("Variable %d:"));

		/* For Central list of columns */
		$js_messages.put("pickColumn", __("Pick"));
		$js_messages.put("pickColumnTitle", __("Column selector"));
		$js_messages.put("searchList", __("Search this list"));
		$js_messages.put("strEmptyCentralList",
				__("No columns in the central list. Make sure the Central columns list for "
						+ "database %s has columns that are not present in the current table."));
		$js_messages.put("seeMore", __("See more"));
		$js_messages.put("confirmTitle", __("Are you sure?"));
		$js_messages.put("makeConsistentMessage", __(
				"This action may change some of the columns definition.<br>Are you sure you " + "want to continue?"));
		$js_messages.put("strContinue", __("Continue"));

		/** For normalization */
		$js_messages.put("strAddPrimaryKey", __("Add primary key"));
		$js_messages.put("strPrimaryKeyAdded", __("Primary key added."));
		$js_messages.put("strToNextStep", __("Taking you to next step…"));
		$js_messages.put("strFinishMsg", __("The first step of normalization is complete for table '%s'."));
		$js_messages.put("strEndStep", __("End of step"));
		$js_messages.put("str2NFNormalization", __("Second step of normalization (2NF)"));
		$js_messages.put("strDone", __("Done"));
		$js_messages.put("strConfirmPd", __("Confirm partial dependencies"));
		$js_messages.put("strSelectedPd", __("Selected partial dependencies are as follows:"));
		$js_messages.put("strPdHintNote", __("Note: a, b . d,f implies values of columns a and b combined together can "
				+ "determine values of column d and column f."));
		$js_messages.put("strNoPdSelected", __("No partial dependencies selected!"));
		$js_messages.put("strBack", __("Back"));
		$js_messages.put("strShowPossiblePd", "Show me the possible partial dependencies based on data in the table");
		$js_messages.put("strHidePd", __("Hide partial dependencies list"));
		$js_messages.put("strWaitForPd",
				__("Sit tight! It may take few seconds depending on data size and column count of " + "the table."));
		$js_messages.put("strStep", __("Step"));
		$js_messages.put("strMoveRepeatingGroup",
				"<ol><b>" + __("The following actions will be performed:") + "</b>" + "<li>"
						+ __("DROP columns %s from the table %s") + "</li>" + "<li>" + __("Create the following table")
						+ "</li>");
		$js_messages.put("strNewTablePlaceholder", "Enter new table name");
		$js_messages.put("strNewColumnPlaceholder", "Enter column name");
		$js_messages.put("str3NFNormalization", __("Third step of normalization (3NF)"));
		$js_messages.put("strConfirmTd", __("Confirm transitive dependencies"));
		$js_messages.put("strSelectedTd", __("Selected dependencies are as follows:"));
		$js_messages.put("strNoTdSelected", __("No dependencies selected!"));

		/* For server/variables.js */
		$js_messages.put("strSave", __("Save"));

		/* For table/select.js */
		$js_messages.put("strHideSearchCriteria", __("Hide search criteria"));
		$js_messages.put("strShowSearchCriteria", __("Show search criteria"));
		$js_messages.put("strRangeSearch", __("Range search"));
		$js_messages.put("strColumnMax", __("Column maximum:"));
		$js_messages.put("strColumnMin", __("Column minimum:"));
		$js_messages.put("strMinValue", __("Minimum value:"));
		$js_messages.put("strMaxValue", __("Maximum value:"));

		/* For table/find_replace.js */
		$js_messages.put("strHideFindNReplaceCriteria", __("Hide find and replace criteria"));
		$js_messages.put("strShowFindNReplaceCriteria", __("Show find and replace criteria"));

		/* For table/zoom_plot_jqplot.js */
		$js_messages.put("strDisplayHelp",
				"<ul><li>" + __("Each point represents a data row.") + "</li><li>"
						+ __("Hovering over a point will show its label.") + "</li><li>"
						+ __("To zoom in, select a section of the plot with the mouse.") + "</li><li>"
						+ __("Click reset zoom button to come back to original state.") + "</li><li>"
						+ __("Click a data point to view and possibly edit the data row.") + "</li><li>"
						+ __("The plot can be resized by dragging it along the bottom right corner.") + "</li></ul>");
		$js_messages.put("strHelpTitle", "Zoom search instructions");
		$js_messages.put("strInputNull", "<strong>" + __("Select two columns") + "</strong>");
		$js_messages.put("strSameInputs", "<strong>" + __("Select two different columns") + "</strong>");
		$js_messages.put("strDataPointContent", __("Data point content"));

		/* For table/change.js */
		$js_messages.put("strIgnore", __("Ignore"));
		$js_messages.put("strCopy", __("Copy"));
		$js_messages.put("strX", __("X"));
		$js_messages.put("strY", __("Y"));
		$js_messages.put("strPoint", __("Point"));
		$js_messages.put("strPointN", __("Point %d"));
		$js_messages.put("strLineString", __("Linestring"));
		$js_messages.put("strPolygon", __("Polygon"));
		$js_messages.put("strGeometry", __("Geometry"));
		$js_messages.put("strInnerRing", __("Inner ring"));
		$js_messages.put("strOuterRing", __("Outer ring"));
		$js_messages.put("strAddPoint", __("Add a point"));
		$js_messages.put("strAddInnerRing", __("Add an inner ring"));
		$js_messages.put("strYes", __("Yes"));
		$js_messages.put("strCopyEncryptionKey", __("Do you want to copy encryption key?"));
		$js_messages.put("strEncryptionKey", __("Encryption key"));

		/* For Tip to be shown on Time field */
		$js_messages.put("strMysqlAllowedValuesTipTime",
				__("MySQL accepts additional values not selectable by the slider;"
						+ " key in those values directly if desired"));

		/* For Tip to be shown on Date field */
		$js_messages.put("strMysqlAllowedValuesTipDate",
				__("MySQL accepts additional values not selectable by the datepicker;"
						+ " key in those values directly if desired"));

		/* For Lock symbol Tooltip */
		$js_messages.put("strLockToolTip", __("Indicates that you have made changes to this page;"
				+ " you will be prompted for confirmation before abandoning changes"));

		/* Designer (js/designer/move.js) */
		$js_messages.put("strSelectReferencedKey", __("Select referenced key"));
		$js_messages.put("strSelectForeignKey", __("Select Foreign Key"));
		$js_messages.put("strPleaseSelectPrimaryOrUniqueKey", "Please select the primary key or a unique key!");
		$js_messages.put("strChangeDisplay", __("Choose column to display"));
		$js_messages.put("strLeavingDesigner",
				__("You haven\"t saved the changes in the layout. They will be lost if you"
						+ " don\"t save them. Do you want to continue?"));
		$js_messages.put("strQueryEmpty", __("value/subQuery is empty"));
		$js_messages.put("strAddTables", __("Add tables from other databases"));
		$js_messages.put("strPageName", __("Page name"));
		$js_messages.put("strSavePage", __("Save page"));
		$js_messages.put("strSavePageAs", __("Save page as"));
		$js_messages.put("strOpenPage", __("Open page"));
		$js_messages.put("strDeletePage", __("Delete page"));
		$js_messages.put("strUntitled", __("Untitled"));
		$js_messages.put("strSelectPage", __("Please select a page to continue"));
		$js_messages.put("strEnterValidPageName", __("Please enter a valid page name"));
		$js_messages.put("strLeavingPage", "Do you want to save the changes to the current page?");
		$js_messages.put("strSuccessfulPageDelete", __("Successfully deleted the page"));
		$js_messages.put("strExportRelationalSchema", __("Export relational schema"));
		$js_messages.put("strModificationSaved", __("Modifications have been saved"));

		/* Visual query builder (js/designer/move.js) */
		$js_messages.put("strAddOption", __("Add an option for column '%s'."));
		$js_messages.put("strObjectsCreated", __("%d object(s) created."));
		$js_messages.put("strSubmit", __("Submit"));

		/* For makegrid.js (column reordering, show/hide column, grid editing) */
		$js_messages.put("strCellEditHint", __("Press escape to cancel editing."));
		$js_messages.put("strSaveCellWarning",
				__("You have edited some data and they have not been saved. Are you sure you want "
						+ "to leave this page before saving the data?"));
		$js_messages.put("strColOrderHint", __("Drag to reorder."));
		$js_messages.put("strSortHint", __("Click to sort results by this column."));
		$js_messages.put("strMultiSortHint",
				__("Shift+Click to add this column to ORDER BY clause or to toggle ASC/DESC."
						+ "<br>- Ctrl+Click or Alt+Click (Mac: Shift+Option+Click) to remove column "
						+ "from ORDER BY clause"));
		$js_messages.put("strColMarkHint", __("Click to mark/unmark."));
		$js_messages.put("strColNameCopyHint", __("Double-click to copy column name."));
		$js_messages.put("strColVisibHint", __("Click the drop-down arrow<br>to toggle column\"s visibility."));
		$js_messages.put("strShowAllCol", __("Show all"));
		$js_messages.put("strAlertNonUnique",
				__("This table does not contain a unique column. Features related to the grid "
						+ "edit, checkbox, Edit, Copy and Delete links may not work after saving."));
		$js_messages.put("strEnterValidHex", "Please enter a valid hexadecimal string. Valid characters are 0-9, A-F.");
		$js_messages.put("strShowAllRowsWarning",
				__("Do you really want to see all of the rows? For a big table this could crash " + "the browser."));
		$js_messages.put("strOriginalLength", __("Original length"));

		/** Drag & Drop sql import messages */
		$js_messages.put("dropImportMessageCancel", __("cancel"));
		$js_messages.put("dropImportMessageAborted", __("Aborted"));
		$js_messages.put("dropImportMessageFailed", __("Failed"));
		$js_messages.put("dropImportMessageSuccess", __("Success"));
		$js_messages.put("dropImportImportResultHeader", __("Import status"));
		$js_messages.put("dropImportDropFiles", __("Drop files here"));
		$js_messages.put("dropImportSelectDB", __("Select database first"));

		/* For Print view */
		$js_messages.put("print", __("Print"));
		$js_messages.put("back", __("Back"));

		// this approach does not work when the parameter is changed via user prefs
		switch ((String) GLOBALS.PMA_Config.get("GridEditing")) {
		case "double-click":
			$js_messages.put("strGridEditFeatureHint",
					__("You can also edit most values<br>by double-clicking directly on them."));
			break;
		case "click":
			$js_messages.put("strGridEditFeatureHint",
					__("You can also edit most values<br>by clicking directly on them."));
			break;
		default:
			break;
		}
		$js_messages.put("strGoToLink", __("Go to link:"));
		$js_messages.put("strColNameCopyTitle", __("Copy column name."));
		$js_messages.put("strColNameCopyText", "Right-click the column name to copy it to your clipboard.");

		/* password generation */
		$js_messages.put("strGeneratePassword", __("Generate password"));
		$js_messages.put("strGenerate", __("Generate"));
		$js_messages.put("strChangePassword", __("Change password"));

		/* navigation tabs */
		$js_messages.put("strMore", __("More"));

		/* navigation panel */
		$js_messages.put("strShowPanel", __("Show panel"));
		$js_messages.put("strHidePanel", __("Hide panel"));
		$js_messages.put("strUnhideNavItem", __("Show hidden navigation tree items."));
		$js_messages.put("linkWithMain", __("Link with main panel"));
		$js_messages.put("unlinkWithMain", __("Unlink from main panel"));

		/* microhistory */
		$js_messages.put("strInvalidPage", "The requested page was not found in the history, it may have expired.");

		/* update */
		$js_messages.put("strNewerVersion",
				__("A newer version of phpMyAdmin is available and you should consider upgrading. "
						+ "The newest version is %s, released on %s."));
		/* l10n: Latest available phpMyAdmin version */
		$js_messages.put("strLatestAvailable", __(", latest stable version:"));
		$js_messages.put("strUpToDate", __("up to date"));

		$js_messages.put("strCreateView", __("Create view"));

		/* Error Reporting */
		$js_messages.put("strSendErrorReport", __("Send error report"));
		$js_messages.put("strSubmitErrorReport", __("Submit error report"));
		$js_messages.put("strErrorOccurred",
				__("A fatal JavaScript error has occurred. Would you like to send an error report?"));
		$js_messages.put("strChangeReportSettings", __("Change report settings"));
		$js_messages.put("strShowReportDetails", __("Show report details"));
		$js_messages.put("strIgnore", __("Ignore"));
		$js_messages.put("strTimeOutError",
				__("Your export is incomplete, due to a low execution time limit at the PHP level!"));

		$js_messages.put("strTooManyInputs", __("Warning: a form on this page has more than %d fields. On submission, "
				+ "some of the fields might be ignored, due to PHP's " + "max_input_vars configuration."));

		$js_messages.put("phpErrorsFound",
				"<div class='alert alert-danger' role='alert'>" + __("Some errors have been detected on the server!")
						+ "<br>" + __("Please look at the bottom of this window.") + "<div>"
						+ "<input id='pma_ignore_errors_popup' type='submit' value='" + __("Ignore")
						+ "' class='btn btn-secondary floatright message_errors_found'>"
						+ "<input id='pma_ignore_all_errors_popup' type='submit' value='" + __("Ignore All")
						+ "' class='btn btn-secondary floatright message_errors_found'>" + "</div></div>");

		$js_messages.put("phpErrorsBeingSubmitted",
				"<div class='alert alert-danger' role='alert'>" + __("Some errors have been detected on the server!")
						+ "<br>"
						+ __("As per your settings, they are being submitted currently, please be " + "patient.")
						+ "<br>" + "<img src='" + GLOBALS.PMA_Theme.getImgPath("ajax_clock_small.gif", null)
						+ "' width='16' height='16' alt='ajax clock'>" + "</div>");
		$js_messages.put("strCopyQueryButtonSuccess", __("Successfully copied!"));
		$js_messages.put("strCopyQueryButtonFailure", __("Copying failed!"));

		// For console
		$js_messages.put("strConsoleRequeryConfirm", __("Execute this query again?"));
		$js_messages.put("strConsoleDeleteBookmarkConfirm", "Do you really want to delete this bookmark?");
		$js_messages.put("strConsoleDebugError", "Some error occurred while getting SQL debug info.");
		$js_messages.put("strConsoleDebugSummary", "%s queries executed %s times in %s seconds.");
		$js_messages.put("strConsoleDebugArgsSummary", __("%s argument(s) passed"));
		$js_messages.put("strConsoleDebugShowArgs", __("Show arguments"));
		$js_messages.put("strConsoleDebugHideArgs", __("Hide arguments"));
		$js_messages.put("strConsoleDebugTimeTaken", __("Time taken:"));
		$js_messages.put("strNoLocalStorage", __(
				"There was a problem accessing your browser storage, some features may not work properly for you. It is likely that the browser doesn\"t support storage or the quota limit has been reached. In Firefox, corrupted storage can also cause such a problem, clearing your 'Offline Website Data' might help. In Safari, such problem is commonly caused by 'Private Mode Browsing'."));
		// For modals in /database/structure
		$js_messages.put("strCopyTablesTo", __("Copy tables to"));
		$js_messages.put("strAddPrefix", __("Add table prefix"));
		$js_messages.put("strReplacePrefix", __("Replace table with prefix"));
		$js_messages.put("strCopyPrefix", __("Copy table with prefix"));

		/* For password strength simulation */
		$js_messages.put("strExtrWeak", __("Extremely weak"));
		$js_messages.put("strVeryWeak", __("Very weak"));
		$js_messages.put("strWeak", __("Weak"));
		$js_messages.put("strGood", __("Good"));
		$js_messages.put("strStrong", __("Strong"));

		/* U2F errors */
		$js_messages.put("strU2FTimeout", __("Timed out waiting for security key activation."));
		$js_messages.put("strU2FError", __("Failed security key activation (%s)."));

		/* Designer */
		$js_messages.put("strTableAlreadyExists",
				_pgettext("The table already exists in the designer and can not be added once more.",
						"Table %s already exists!"));
		$js_messages.put("strHide", __("Hide"));
		$js_messages.put("strStructure", __("Structure"));

		response.getWriter().write("var Messages = [];\n");
		for (Entry<String, String> entry : $js_messages.entrySet()) {
			String $name = entry.getKey();
			String $js_message = entry.getValue();
			Sanitize.printJsValue("Messages." + $name + "", $js_message, response);
		}

		/* Calendar */
		response.getWriter().write("var themeCalendarImage = '" + GLOBALS.pmaThemeImage + "b_calendar.png" + "';\n");

		/* Calendar First Day */
		response.getWriter()
				.write("var firstDayOfCalendar = '" + GLOBALS.PMA_Config.get("FirstDayOfCalendar") + "';\n");

		/* Image path */
		response.getWriter().write("var pmaThemeImage = '" + GLOBALS.pmaThemeImage + "';\n");

		response.getWriter().write("var mysqlDocTemplate = '';\n"); // Unsupported

		// Max input vars allowed by PHP.
		response.getWriter().write("var maxInputVars = false;\n"); // Unsupported

		response.getWriter().write("if ($.datepicker) {\n");
		/* l10n: Display text for calendar close link */
		Sanitize.printJsValue("$.datepicker.regional['']['closeText']", __("Done"), response);
		/* l10n: Display text for previous month link in calendar */
		Sanitize.printJsValue("$.datepicker.regional['']['prevText']", _pgettext("Previous month", "Prev"), response);
		/* l10n: Display text for next month link in calendar */
		Sanitize.printJsValue("$.datepicker.regional['']['nextText']", _pgettext("Next month", "Next"), response);
		/* l10n: Display text for current month link in calendar */
		Sanitize.printJsValue("$.datepicker.regional['']['currentText']", __("Today"), response);
		Sanitize.printJsValue("$.datepicker.regional['']['monthNames']",
				new String[] { __("January"), __("February"), __("March"), __("April"), __("May"), __("June"),
						__("July"), __("August"), __("September"), __("October"), __("November"), __("December"), },
				response);
		Sanitize.printJsValue("$.datepicker.regional['']['monthNamesShort']", new String[] {
				/* l10n: Short month name */
				__("Jan"),
				/* l10n: Short month name */
				__("Feb"),
				/* l10n: Short month name */
				__("Mar"),
				/* l10n: Short month name */
				__("Apr"),
				/* l10n: Short month name */
				_pgettext("Short month name", "May"),
				/* l10n: Short month name */
				__("Jun"),
				/* l10n: Short month name */
				__("Jul"),
				/* l10n: Short month name */
				__("Aug"),
				/* l10n: Short month name */
				__("Sep"),
				/* l10n: Short month name */
				__("Oct"),
				/* l10n: Short month name */
				__("Nov"),
				/* l10n: Short month name */
				__("Dec"), }, response);
		Sanitize.printJsValue("$.datepicker.regional['']['dayNames']", new String[] { __("Sunday"), __("Monday"),
				__("Tuesday"), __("Wednesday"), __("Thursday"), __("Friday"), __("Saturday"), }, response);
		Sanitize.printJsValue("$.datepicker.regional['']['dayNamesShort']", new String[] {
				/* l10n: Short week day name for Sunday */
				__("Sun"),
				/* l10n: Short week day name for Monday */
				__("Mon"),
				/* l10n: Short week day name for Tuesday */
				__("Tue"),
				/* l10n: Short week day name for Wednesday */
				__("Wed"),
				/* l10n: Short week day name for Thursday */
				__("Thu"),
				/* l10n: Short week day name for Friday */
				__("Fri"),
				/* l10n: Short week day name for Saturday */
				__("Sat"), }, response);
		Sanitize.printJsValue("$.datepicker.regional['']['dayNamesMin']", new String[] {
				/* l10n: Minimal week day name for Sunday */
				__("Su"),
				/* l10n: Minimal week day name for Monday */
				__("Mo"),
				/* l10n: Minimal week day name for Tuesday */
				__("Tu"),
				/* l10n: Minimal week day name for Wednesday */
				__("We"),
				/* l10n: Minimal week day name for Thursday */
				__("Th"),
				/* l10n: Minimal week day name for Friday */
				__("Fr"),
				/* l10n: Minimal week day name for Saturday */
				__("Sa"), }, response);
		/* l10n: Column header for week of the year in calendar */
		Sanitize.printJsValue("$.datepicker.regional['']['weekHeader']", __("Wk"), response);

		Sanitize.printJsValue("$.datepicker.regional['']['showMonthAfterYear']",
				/*
				 * l10n: Month-year order for calendar, use either "calendar-month-year" or
				 * "calendar-year-month".
				 */
				__("calendar-month-year") == "calendar-year-month", response);
		/* l10n: Year suffix for calendar, "none" is empty. */
		String $year_suffix = _pgettext("Year suffix", "none");
		Sanitize.printJsValue("$.datepicker.regional['']['yearSuffix']",
				("none".equals($year_suffix) ? "" : $year_suffix), response);

		response.getWriter().write("$.extend($.datepicker._defaults, $.datepicker.regional['']);\n");
		response.getWriter().write("} /* if ($.datepicker) */\n");

		response.getWriter().write("if ($.timepicker) {\n");
		Sanitize.printJsValue("$.timepicker.regional['']['timeText']", __("Time"), response);
		Sanitize.printJsValue("$.timepicker.regional['']['hourText']", __("Hour"), response);
		Sanitize.printJsValue("$.timepicker.regional['']['minuteText']", __("Minute"), response);
		Sanitize.printJsValue("$.timepicker.regional['']['secondText']", __("Second"), response);

		response.getWriter().write("$.extend($.timepicker._defaults, $.timepicker.regional['']);\n");
		response.getWriter().write("} /* if ($.timepicker) */\n");

		/* Form validation */

		response.getWriter().write("function extendingValidatorMessages() {\n");
		response.getWriter().write("$.extend($.validator.messages, {\n");

		/* Default validation functions */
		Sanitize.printJsValueForFormValidation("required", __("This field is required"), response);
		Sanitize.printJsValueForFormValidation("remote", __("Please fix this field"), response);
		Sanitize.printJsValueForFormValidation("email", __("Please enter a valid email address"), response);
		Sanitize.printJsValueForFormValidation("url", __("Please enter a valid URL"), response);
		Sanitize.printJsValueForFormValidation("date", __("Please enter a valid date"), response);
		Sanitize.printJsValueForFormValidation("dateISO", __("Please enter a valid date ( ISO )"), response);
		Sanitize.printJsValueForFormValidation("number", __("Please enter a valid number"), response);
		Sanitize.printJsValueForFormValidation("creditcard", __("Please enter a valid credit card number"), response);
		Sanitize.printJsValueForFormValidation("digits", __("Please enter only digits"), response);
		Sanitize.printJsValueForFormValidation("equalTo", __("Please enter the same value again"), response);
		Sanitize.printJsValueForFormValidation("maxlength", __("Please enter no more than {0} characters"), true,
				response);
		Sanitize.printJsValueForFormValidation("minlength", __("Please enter at least {0} characters"), true, response);
		Sanitize.printJsValueForFormValidation("rangelength",
				__("Please enter a value between {0} and {1} characters long"), true, response);
		Sanitize.printJsValueForFormValidation("range", __("Please enter a value between {0} and {1}"), true, response);
		Sanitize.printJsValueForFormValidation("max", __("Please enter a value less than or equal to {0}"), true,
				response);
		Sanitize.printJsValueForFormValidation("min", __("Please enter a value greater than or equal to {0}"), true,
				response);
		/* customed functions */
		Sanitize.printJsValueForFormValidation("validationFunctionForDateTime", __("Please enter a valid date or time"),
				true, response);
		Sanitize.printJsValueForFormValidation("validationFunctionForHex", __("Please enter a valid HEX input"), true,
				response);
		Sanitize.printJsValueForFormValidation("validationFunctionForFuns", __("Error"), true, false, response);
		response.getWriter().write("\n});");
		response.getWriter().write("\n} /* if ($.validator) */");
	}
}