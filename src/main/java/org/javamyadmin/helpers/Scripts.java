package org.javamyadmin.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.php.GLOBALS;

import static org.javamyadmin.php.Php.*;

/**
 * Collects information about which JavaScript
 * files and objects are necessary to render
 * the page and generates the relevant code.
 *
 * @package PhpMyAdmin
 */
public class Scripts {

	public static class FStruct {
		public boolean has_onload;
		public String filename;
		public Map<String, Object> params = new HashMap<>();

		public FStruct(boolean has_onload, String filename, Map<String, Object> params) {
			this.filename = filename;
			this.has_onload = has_onload;
			this.params = params;
		}

	}

	/**
	 * An array of SCRIPT tags
	 *
	 * @access private
	 * @var array of strings
	 */
	private Map<String, FStruct> _files;

	/**
	 * A String of discrete javascript code snippets
	 *
	 * @access private
	 * @var String
	 */
	private String _code;

	/**
	 * @var Template
	 */
	private Template template;

	private GLOBALS GLOBALS;

	/**
	 * Generates new Scripts objects
	 *
	 */
	public Scripts(GLOBALS GLOBALS) {
		this.template = new Template();
		this._files = new HashMap<>();
		this._code = "";
		this.GLOBALS = GLOBALS;
	}

	/**
	 * Adds a new file to the list of scripts
	 *
	 * @param String
	 *            $filename The name of the file to include
	 * @param array
	 *            $params Additional parameters to pass to the file
	 *
	 * @return void
	 */
	public void addFile(String filename, Map<String, Object> params) {
		String hash = md5(filename);
		if (!empty(this._files.get(hash))) {
			return;
		}

		FStruct struct = new FStruct(this._eventBlacklist(filename), filename, params);
		this._files.put(hash, struct);
	}

	public void addFile(String filename) {
		addFile(filename, new HashMap<>());
	}

	/**
	 * Add new files to the list of scripts
	 *
	 * @param array
	 *            $filelist The array of file names
	 *
	 * @return void
	 */
	public void addFiles(List<String> filelist) {
		for (String $filename : filelist) {
			this.addFile($filename);
		}
	}

	/**
	 * Determines whether to fire up an onload event for a file
	 *
	 * @param String
	 *            $filename The name of the file to be checked against the blacklist
	 *
	 * @return int 1 to fire up the event, 0 not to
	 */
	private boolean _eventBlacklist(String $filename) {
		if ($filename.contains("jquery") || $filename.contains("codemirror") || $filename.contains("messages.php")
				|| $filename.contains("ajax.js") || $filename.contains("cross_framing_protection.js")) {
			return false;
		}

		return true;
	}

	/**
	 * Adds a new code snippet to the code to be executed
	 *
	 * @param String
	 *            $code The JS code to be added
	 *
	 * @return void
	 */
	public void addCode(String $code) {
		this._code += $code + "\n";
	}

	public static class FStruct2 {
		public boolean fire;
		public String name;

		public FStruct2(String name, boolean fire) {
			this.name = name;
			this.fire = fire;
		}
	}

	/**
	 * Returns a list with filenames and a flag to indicate whether to register
	 * onload events for this file
	 *
	 * @return array
	 */
	public List<FStruct2> getFiles() {
		List<FStruct2> retval = new ArrayList<FStruct2>();
		for (FStruct $file : this._files.values()) {
			// If filename contains a "?", continue.
			if ($file.filename.contains("?")) {
				continue;
			}
			retval.add(new FStruct2($file.filename, $file.has_onload));
		}
		return retval;
	}

	/**
	 * Renders all the JavaScript file inclusions, code and events
	 *
	 * @return String
	 */
	public String getDisplay() {
		Map<String, Object> model = new HashMap<>();
		model.put("files", this._files);
		model.put("version", GLOBALS.PMA_VERSION);
		model.put("code", this._code);

		return this.template.render("scripts", model);

	}

}
