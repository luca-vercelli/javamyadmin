package org.javamyadmin.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.javamyadmin.php.Php.*;

import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.php.Globals;

/**
 * Handle error report submission
 * @package PhpMyAdmin\Controllers
 */
@RestController
public class ErrorReportController  extends AbstractController
{
    /** @var ErrorReport */
    private ErrorReport $errorReport;

    /** @var ErrorHandler */
    private ErrorHandler $errorHandler;

    /**
     * @return void
     */
    @RequestMapping(value="/error-report")
    public void index()
    {
        Config $cfg = Globals.getConfig();

        if (empty(httpRequest.getParameter("exception_type"))
            || ! in_array(httpRequest.getParameter("exception_type"), ["js", "php"])
        ) {
            return;
        }

        if ("true".equals(httpRequest.getParameter("send_error_report"))
            || "1".equals(httpRequest.getParameter("send_error_report")))
        ) {
            if (httpRequest.getParameter("exception_type").equals( "php")) {
                /**
                 * Prevent infinite error submission.
                 * Happens in case error submissions fails.
                 * If reporting is done in some time interval,
                 *  just clear them & clear json data too.
                 */
                if (isset($_SESSION.get("prev_error_subm_time"), $_SESSION.get("error_subm_count"])
                    && $_SESSION.get("error_subm_count") >= 3
                    && ($_SESSION.get("prev_error_subm_time") - time()) <= 3000
                ) {
                    $_SESSION.get("error_subm_count") = 0;
                    $_SESSION.get("prev_errors")= "";
                    this.response.addJSON("stopErrorReportLoop", "1");
                } else {
                    $_SESSION.get("prev_error_subm_time") = time();
                    $_SESSION.get("error_subm_count") = (
                    isset($_SESSION.get("error_subm_count"])
                        ? ($_SESSION.get("error_subm_count") + 1)
                        : 0
                    );
                }
            }
            List<String> $reportData = this.errorReport.getData($_POST["exception_type"]);
            // report if and only if there were "actual" errors.
            if ($reportData.size() > 0) {
            	String $msgStr;
            	Message $msg;
            	boolean $success;
                String $server_response = this.errorReport.send($reportData);
                if (!($server_response instanceof String)) {
                    $success = false;
                } else {
                    Map $decoded_response = (Map) json_decode($server_response /*, true*/);
                    $success = ! empty($decoded_response) ?
                        (boolean) $decoded_response.get("success") : false;
                }

                /* Message to show to the user */
                if ($success) {
                    if ("true".equals(httpRequest.getParameter("automatic"))
                        || "always".equals($cfg.get("SendErrorReports"))
                    ) {
                    	$msgStr = __(
                            "An error has been detected and an error report has been "
                            + "automatically submitted based on your settings."
                        );
                    } else {
                    	$msgStr = __("Thank you for submitting this report.");
                    }
                } else {
                	$msgStr = __(
                            "An error has been detected and an error report has been "
                            + "generated but failed to be sent."
                        )
                        + " "
                        + __(
                            "If you experience any "
                            + "problems please submit a bug report manually."
                        );
                }
                $msgStr += " " + __("You may want to refresh the page.");

                /* Create message object */
                if ($success) {
                    $msg = Message.notice($msgStr);
                } else {
                    $msg = Message.error($msgStr);
                }

                /* Add message to response */
                if (this.response.isAjax()) {
                    if ($_POST["exception_type"] == "js") {
                        this.response.addJSON("message", $msg);
                    } else {
                        this.response.addJSON("errSubmitMsg", $msg);
                    }
                } elseif ($_POST["exception_type"] == "php") {
                    $jsCode = "Functions.ajaxShowMessage(\"<div class='alert alert-danger' role='alert'>"
                        + $msg
                        + "</div>\", false);";
                    this.response.getFooter().getScripts().addCode($jsCode);
                }

                if ($_POST["exception_type"] == "php") {
                    // clear previous errors & save new ones.
                    this.errorHandler.savePreviousErrors();
                }

                /* Persist always send settings */
                if (isset($_POST["always_send"])
                    && $_POST["always_send"] == "true"
                ) {
                    $userPreferences = new UserPreferences();
                    $userPreferences.persistOption("SendErrorReports", "always", "ask");
                }
            }
        } elseif (! empty($_POST["get_settings"])) {
            this.response.addJSON("report_setting", $cfg["SendErrorReports"]);
        } elseif ($_POST["exception_type"] == "js") {
            this.response.addHTML(this.errorReport.getForm());
        } else {
            // clear previous errors & save new ones.
            this.errorHandler.savePreviousErrors();
        }
    }
}
