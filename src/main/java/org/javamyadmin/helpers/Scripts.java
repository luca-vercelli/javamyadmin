package org.javamyadmin.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Collects information about which JavaScript
 * files and objects are necessary to render
 * the page and generates the relevant code.
 *
 * @package PhpMyAdmin
 */
public class Scripts {

	/**
	 * POJO with "has_onload", "filename", "params" attributes
	 *
	 */
	public static class FStruct {
		public boolean has_onload;
		public String filename;
		public Map<String, Object> params = new HashMap<>();

		public FStruct(boolean has_onload, String filename, Map<String, Object> params) {
			if (filename == null) {
				throw new IllegalArgumentException("Null filename given");
			}
			this.filename = filename;
			this.has_onload = has_onload;
			this.params = params;
		}
		
		/**
		 * Used by List.contains
		 * @return
		 */
		@Override
		public boolean equals(Object other) {
			return (other instanceof FStruct)&& ((FStruct)other).filename.equals(filename);
		}

	}

	/**
	 * An array of SCRIPT tags
	 *
	 * @access private
	 * @var array of strings
	 */
	private List<FStruct> _files;

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
	@Autowired
	private Template template;

	/**
	 * Generates new Scripts objects
	 *
	 */
	public Scripts() {
		this._files = new ArrayList<>();
		this._code = "";
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
		FStruct struct = new FStruct(this._eventBlacklist(filename), filename, params);
		if (this._files.contains(struct)) {
			return;
		}
		this._files.add(struct);
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

	public void addFiles(String[] filelist) {
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

	/**
	 * POJO with  "fire" and "name" attributes
	 *
	 */
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
		for (FStruct $file : this._files) {
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
		model.put("version", Globals.getPmaVersion());
		model.put("code", this._code);

		return this.template.render("scripts", model);

	}

}
