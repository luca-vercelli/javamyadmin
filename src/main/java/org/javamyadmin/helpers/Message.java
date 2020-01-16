package org.javamyadmin.helpers;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.html.Generator;
import org.javamyadmin.jtwig.JtwigFactory;

import static org.javamyadmin.php.Php.*;

/**
 * a single message
 *
 * simple usage examples:
 * <code>
 * // display simple error message "Error"
 * Message.error().display();
 *
 * // get simple success message "Success"
 * message = Message.success();
 *
 * // get special notice
 * message = Message.notice(__("This is a localized notice"));
 * </code>
 *
 * more advanced usage example:
 * <code>
 * // create another message, a hint, with a localized String which expects
 * hint = Message.notice("Read the %smanual%s");
 * // replace placeholders with the following params
 * hint.addParam("[doc@cfg_Example]");
 * hint.addParam("[/doc]");
 * // add this hint as a tooltip
 * hint = showHint(hint);
 *
 * // add the retrieved tooltip reference to the original message
 * message.addMessage(hint);
 * </code>
 *
 * @package PhpMyAdmin
 */
public class Message {


    public final static int SUCCESS = 1; // 0001
    public final static int NOTICE  = 2; // 0010
    public final static int ERROR   = 8; // 1000

    public final static int SANITIZE_NONE   = 0;  // 0000 0000
    public final static int SANITIZE_STRING = 16; // 0001 0000
    public final static int SANITIZE_PARAMS = 32; // 0010 0000
    public final static int SANITIZE_BOOTH  = 48; // 0011 0000

    /**
     * message levels
     *
     * @var array
     */
    public static Map<Integer, String> level =  new HashMap<>();
    static {
    	level.put(Message.SUCCESS, "success");
    	level.put(Message.NOTICE, "notice");
    	level.put(Message.ERROR, "error");
    }

    /**
     * The message number
     *
     * @access  protected
     * @var     integer
     */
    protected int number = Message.NOTICE;

    /**
     * The locale String identifier
     *
     * @access  protected
     * @var     String
     */
    protected String string = "";

    /**
     * The formatted message
     *
     * @access  protected
     * @var     String
     */
    protected String message = "";

    /**
     * Whether the message was already displayed
     *
     * @access  protected
     * @var     boolean
     */
    protected boolean isDisplayed = false;

    /**
     * Whether to use BB code when displaying.
     *
     * @access  protected
     * @var     boolean
     */
    protected boolean useBBCode = true;

    /**
     * Unique id
     *
     * @access  protected
     * @var String
     */
    protected int hash = 0;

    /**
     * holds parameters
     *
     * @access  protected
     * @var     array
     */
    protected List<Object> params = new ArrayList<>();

    /**
     * holds additional messages
     *
     * @access  protected
     * @var     array
     */
    protected List<Object> addedMessages = new ArrayList<>();
    
    /**
     * Constructor
     *
     * @param String  String   The message to be displayed
     * @param integer number   A numeric representation of the type of message
     * @param array   params   An array of parameters to use in the message
     * @param integer sanitize A flag to indicate what to sanitize, see
     *                          constant definitions above
     */
    public Message(
        String String /*= ""*/,
        int number /*= Message.NOTICE*/,
        List<Object> params /*= []*/,
        int sanitize /*= Message.SANITIZE_NONE*/
    ) {
        this.setString(String, (sanitize & Message.SANITIZE_STRING) != 0);
        this.setNumber(number);
        this.setParams(params, (sanitize & Message.SANITIZE_PARAMS) != 0);
    }

    public Message(String string, int success) {
		this(string,success, new ArrayList<>(), SANITIZE_NONE);
	}

	/**
     * magic method: return String representation for this object
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return this.getMessage();
    }

    /**
     * get Message of type success
     *
     * shorthand for getting a simple success message
     *
     * @param String String A localized String
     *                       e.g. __("Your SQL query has been
     *                       executed successfully")
     * @return 
     *
     * @return Message
     * @static
     */
    public static Message success(String string)
    {
        if (empty(string)) {
            string = __("Your SQL query has been executed successfully.");
        }

        return new Message(string, SUCCESS);
    }

    /**
     * get Message of type error
     *
     * shorthand for getting a simple error message
     *
     * @param String String A localized String e.g. __("Error")
     * @return 
     *
     * @return Message
     * @static
     */
    public static Message error(String string)
    {
        if (empty(string)) {
            string = __("Error");
        }

        return new Message(string, ERROR);
    }

    /**
     * get Message of type notice
     *
     * shorthand for getting a simple notice message
     *
     * @param String String A localized String
     *                       e.g. __("The additional features for working with
     *                       linked tables have been deactivated. To find out
     *                       why click %shere%s.")
     *
     * @return Message
     * @static
     */
    public static Message notice(String String)
    {
        return new Message(String, NOTICE);
    }

    /**
     * get Message with customized content
     *
     * shorthand for getting a customized message
     *
     * @param String  message A localized String
     * @param integer type    A numeric representation of the type of message
     *
     * @return Message
     * @static
     */
    public static Message raw(String message, int type /*= Message.NOTICE*/)
    {
    	Message r = new Message("", type);
        r.setMessage(message, false);
        r.setBBCode(false);
        return r;
    }

    /**
     * get Message for number of affected rows
     *
     * shorthand for getting a customized message
     *
     * @param integer rows Number of rows
     *
     * @return Message
     * @static
     */
    public static Message getMessageForAffectedRows(int rows)
    {
        Message message = success(
            _ngettext("%1d row affected.", "%1d rows affected.", rows)
        );
        message.addParam(rows);
        return message;
    }

    /**
     * get Message for number of deleted rows
     *
     * shorthand for getting a customized message
     *
     * @param integer rows Number of rows
     *
     * @return Message
     * @static
     */
    public static Message getMessageForDeletedRows(int rows)
    {
    	Message message = success(
            _ngettext("%1d row deleted.", "%1d rows deleted.", rows)
        );
        message.addParam(rows);
        return message;
    }

	/**
     * get Message for number of inserted rows
     *
     * shorthand for getting a customized message
     *
     * @param integer rows Number of rows
     *
     * @return Message
     * @static
     */
    public static Message getMessageForInsertedRows(int rows)
    {
    	Message message = success(
            _ngettext("%1d row inserted.", "%1d rows inserted.", rows)
        );
        message.addParam(rows);
        return message;
    }

    /**
     * get Message of type error with custom content
     *
     * shorthand for getting a customized error message
     *
     * @param String message A localized String
     *
     * @return Message
     * @static
     */
    public static Message rawError(String message)
    {
        return raw(message, ERROR);
    }

    /**
     * get Message of type notice with custom content
     *
     * shorthand for getting a customized notice message
     *
     * @param String message A localized String
     *
     * @return Message
     * @static
     */
    public static Message rawNotice(String message)
    {
        return raw(message, NOTICE);
    }

    /**
     * get Message of type success with custom content
     *
     * shorthand for getting a customized success message
     *
     * @param String message A localized String
     *
     * @return Message
     * @static
     */
    public static Message rawSuccess(String message)
    {
        return raw(message, SUCCESS);
    }

    /**
     * returns whether this message is a success message or not
     * and optionally makes this message a success message
     *
     * @param boolean set Whether to make this message of SUCCESS type
     *
     * @return boolean whether this is a success message or not
     */
    public boolean isSuccess(boolean set /* = false*/)
    {
        if (set) {
            this.setNumber(SUCCESS);
        }

        return this.getNumber() == SUCCESS;
    }
    
    public boolean isSuccess() {
    	return isSuccess(false);
    }

    /**
     * returns whether this message is a notice message or not
     * and optionally makes this message a notice message
     *
     * @param boolean set Whether to make this message of NOTICE type
     *
     * @return boolean whether this is a notice message or not
     */
    public boolean isNotice(boolean set )
    {
        if (set) {
            this.setNumber(NOTICE);
        }

        return this.getNumber() == NOTICE;
    }

    /**
     * returns whether this message is an error message or not
     * and optionally makes this message an error message
     *
     * @param boolean set Whether to make this message of ERROR type
     *
     * @return boolean Whether this is an error message or not
     */
    public boolean isError(boolean set )
    {
        if (set) {
            this.setNumber(ERROR);
        }

        return this.getNumber() == ERROR;
    }

    /**
     * Set whether we should use BB Code when rendering.
     *
     * @param boolean useBBCode Use BB Code?
     *
     * @return void
     */
    public void setBBCode(boolean useBBCode)
    {
        this.useBBCode = useBBCode;
    }

    /**
     * set raw message (overrides String)
     *
     * @param String  message  A localized String
     * @param boolean sanitize Whether to sanitize message or not
     *
     * @return void
     */
    public void setMessage(String message, boolean sanitize /*= false*/)
    {
        if (sanitize) {
            message = (String) sanitize(message);
        }
        this.message = message;
    }

    /**
     * set String (does not take effect if raw message is set)
     *
     * @param String      String   String to set
     * @param boolean|int sanitize whether to sanitize String or not
     *
     * @return void
     */
    public void setString(String string, boolean sanitize)
    {
        if (sanitize) {
        	string = (String) sanitize(string);
        }
        this.string = string;
    }

    /**
     * set message type number
     *
     * @param integer number message type number to set
     *
     * @return void
     */
    public void setNumber(int number)
    {
        this.number = number;
    }

    /**
     * add String or Message parameter
     *
     * usage
     * <code>
     * message.addParam("[em]some String[/em]");
     * </code>
     *
     * @param mixed param parameter to add
     *
     * @return void
     */
    public void addParam(Object param)
    {
        if (param instanceof Message || param instanceof Number) {
            this.params.add(param);
        } else {
            this.params.add(htmlspecialchars(param.toString()));
        }
    }

    /**
     * add parameter as raw HTML, usually in conjunction with strings
     *
     * usage
     * <code>
     * message.addParamHtml("<img src="img">");
     * </code>
     *
     * @param String param parameter to add
     *
     * @return void
     */
    public void addParamHtml(String param)
    {
        this.params.add(notice(param));
    }

    /**
     * add a bunch of messages at once
     *
     * @param Message[] messages  to be added
     * @param String    separator to use between this and previous String/message
     *
     * @return void
     */
    public void addMessages(List<Message> messages, String separator /*= " "*/)
    {
        for (Message message: messages) {
            this.addMessage(message, separator);
        }
    }

    /**
     * add a bunch of messages at once
     *
     * @param String[] messages  to be added
     * @param String   separator to use between this and previous String/message
     *
     * @return void
     */
    public void addMessagesString(List<String> messages, String separator /*= " "*/)
    {
    	for (String message: messages) {
            this.addText(message, separator);
        }
    }

    /**
     * Real implementation of adding message
     *
     * @param Message message   to be added
     * @param String  separator to use between this and previous String/message
     *
     * @return void
     */
    private void addMessageToList(Message message, String separator)
    {
        if (! empty(separator)) {
            this.addedMessages.add(separator);
        }
        this.addedMessages.add(message);
    }

    /**
     * add another raw message to be concatenated on displaying
     *
     * @param self   message   to be added
     * @param String separator to use between this and previous String/message
     *
     * @return void
     */
    public void addMessage(Message message, String separator /*= " "*/)
    {
        this.addMessageToList(message, separator);
    }

    /**
     * add another raw message to be concatenated on displaying
     *
     * @param String message   to be added
     * @param String separator to use between this and previous String/message
     *
     * @return void
     */
    public void addText(String message, String separator /*= " "*/)
    {
        this.addMessageToList(notice(htmlspecialchars(message)), separator);
    }

    /**
     * add another html message to be concatenated on displaying
     *
     * @param String message   to be added
     * @param String separator to use between this and previous String/message
     *
     * @return void
     */
    public void addHtml(String message, String separator)
    {
        this.addMessageToList(rawNotice(message), separator);
    }

    /**
     * set all params at once, usually used in conjunction with String
     *
     * @param array|String params   parameters to set
     * @param boolean|int     sanitize whether to sanitize params
     *
     * @return void
     */
    public void setParams(List<Object> params, boolean sanitize)
    {
        if (sanitize) {
            params = (List<Object>) sanitize(params);
        }
        this.params = params;
    }

    /**
     * return all parameters
     * @return 
     *
     * @return array|String
     */
    public List<Object> getParams()
    {
        return this.params;
    }

    /**
     * return all added messages
     * @return 
     *
     * @return array
     */
    public List<Object> getAddedMessages()
    {
        return this.addedMessages;
    }

    /**
     * Sanitizes message
     *
     * @param mixed message the message(s)
     *
     * @return mixed  the sanitized message(s) (String or Map or List)
     * @access  public
     * @static
     */
    public static Object sanitize(Object messages)
    {
        if (messages instanceof Map) {
        	Map<String, Object> map = (Map<String, Object>) messages;
            for (String key : map.keySet()) {
                map.put(key, sanitize(map.get(key)));
            }

            return map;
        } else if (messages instanceof List) {
        	List<Object> list = (List<Object>) messages;
        	for(int i = 0; i < list.size(); ++i) {
        		list.set(i, sanitize(list.get(i)));
        	}
        }

        return htmlspecialchars(messages.toString());
    }

    /**
     * decode message, taking into account our special codes
     * for formatting
     * @param request 
     * @param GLOBALS 
     *
     * @param String message the message
     *
     * @return String  the decoded message
     * @access  public
     * @static
     */
    public static String decodeBB(String message)
    {
        return Sanitize.sanitizeMessage(message, false, true);
    }

    /**
     * returns unique Message.hash, if not exists it will be created
     *
     * @return String Message.hash
     */
    public int getHash()
    {
        return this.hashCode();
    }
    
    @Override
    public int hashCode() {
    	if (this.hash == 0) {
            this.hash = (
                this.getNumber() +
                this.string +
                this.message
            ).hashCode();
        }
    	return this.hash;
    }

    /**
     * returns compiled message
     * @param request 
     * @param GLOBALS 
     *
     * @return String complete message
     */
    public String getMessage()
    {
        String message = this.message;

        if (empty(message)) {
            String string = this.getString();
            if (empty (string)) {
                message = "";
            } else {
                message = string;
            }
        }

        if (this.isDisplayed(false)) {
            message = this.getMessageWithIcon(message);
        }
        if (this.getParams().size() > 0) {
            message = String.format(message, this.getParams());
        }

        if (this.useBBCode) {
            message = decodeBB(message);
        }

        for (Object add_message: this.getAddedMessages()) {
            message += add_message;
        }

        return message;
    }

    /**
     * Returns only message String without image & other HTML.
     *
     * @return String
     */
    public String getOnlyMessage()
    {
        return this.message;
    }


    /**
     * returns Message.String
     *
     * @return String Message.String
     */
    public String getString()
    {
        return this.string;
    }

    /**
     * returns Message.number
     *
     * @return integer Message.number
     */
    public int getNumber()
    {
        return this.number;
    }

    /**
     * returns level of message
     *
     * @return String level of message
     */
    public String getLevel()
    {
        return Message.level.get(this.getNumber());
    }

    /**
     * Displays the message in HTML
     *
     * @return void
     * @throws IOException 
     */
    public void display(HttpServletResponse resp) throws IOException
    {
        resp.getWriter().write(this.getDisplay());
    }

    /**
     * returns HTML code for displaying this message
     *
     * @return String whole message box
     */
    public String getDisplay()
    {
    	this.isDisplayed(true);

        String context = "primary";
        String level = this.getLevel();
        if (level.equals("error")) {
            context = "danger";
        } else if (level.equals("success")) {
            context = "success";
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("context", context);
        model.put("message", this.getMessage());
        return JtwigFactory.render("message", model);
    }

    /**
     * sets and returns whether the message was displayed or not
     *
     * @param boolean isDisplayed whether to set displayed flag
     *
     * @return boolean Message.isDisplayed
     */
    public boolean isDisplayed(boolean isDisplayed /* = false */)
    {
        if (isDisplayed) {
            this.isDisplayed = true;
        }

        return this.isDisplayed;
    }

    /**
     * Returns the message with corresponding image icon
     *
     * @param String message the message(s)
     *
     * @return String message with icon
     */
    public String getMessageWithIcon(String message)
    {
    	String image;
        if ("error".equals(this.getLevel())) {
            image = "s_error";
        } else if ("success".equals(this.getLevel())) {
            image = "s_success";
        } else {
            image = "s_notice";
        }
        message = Message.notice(Generator.getImage(image, null, null)) + " " + message;
        return message;
    }

    
}
