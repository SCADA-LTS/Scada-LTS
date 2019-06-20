package br.org.scadabr.api;

import java.util.Iterator;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;

public class AuthenticationHandler extends BasicHandler {
	private static ThreadLocal _username = new ThreadLocal();

	public static String getUsername() {
		return ((String) (_username.get())).toString();
	}

	public static void setUsername(String value) {
		_username.set(value);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean isSecurityEnabled() {
		String sec = Common.getEnvironmentProfile().getString(
				"api.authentication", "enabled");

		if (sec.equals("disabled"))
			return false;

		else
			return true;
	}

	@Override
	public void invoke(MessageContext msgContext) throws AxisFault {
		boolean processedHeader = false;
		if (isSecurityEnabled()) {
			try {
				Message msg = msgContext.getRequestMessage();
				SOAPEnvelope envelope = msg.getSOAPEnvelope();
				SOAPHeader header = envelope.getHeader();
				Iterator it = header.examineAllHeaderElements();
				SOAPHeaderElement hel;

				while (it.hasNext()) {
					hel = (SOAPHeaderElement) it.next();
					String headerName = hel.getElementName().getLocalName();
					if (headerName.toLowerCase().equals("authentication")) {
						checkUsername(hel);
						processedHeader = true;
					}
				}
			} catch (SOAPException e) {
				throw new AxisFault(
						"Failed to retrieve the SOAP Header or it's details properly.",
						e);
			}

			if (!processedHeader)
				throw new AxisFault("No API authentication on header!");
		} else {
			String username = Common.getEnvironmentProfile().getString(
					"api.username", "admin");
			String password = Common.getEnvironmentProfile().getString(
					"api.password", "admin");

			User user = new UserDao().getUser(username);
			if (user == null)
				throw new AxisFault("Invalid Default Username!");

			if (!user.getPassword().equals(Common.encrypt(password)))
				throw new AxisFault("Invalid Default Password!");

			_username.set(username);
		}

	}

	private void checkUsername(SOAPHeaderElement hel) throws AxisFault {
		String username = getUsername(hel);
		String password = getPassword(hel);

		User user = new UserDao().getUser(username);

		if (user == null)
			throw new AxisFault("Invalid Username!");

		if (!user.getPassword().equals(Common.encrypt(password)))
			throw new AxisFault("Invalid Password!");

		_username.set(username);
	}

	private String getPassword(SOAPHeaderElement hel) throws AxisFault {
		for (Iterator iterator = hel.getChildElements(); iterator.hasNext();) {
			org.w3c.dom.Node node = (org.w3c.dom.Node) iterator.next();
			String nodename = node.getLocalName();
			if (nodename.equals("password"))
				return node.getFirstChild().getNodeValue();
		}

		throw new AxisFault("Missing password element in SOAP header!");
	}

	private String getUsername(SOAPHeaderElement hel) throws AxisFault {
		for (Iterator iterator = hel.getChildElements(); iterator.hasNext();) {
			org.w3c.dom.Node node = (org.w3c.dom.Node) iterator.next();
			String nodename = node.getLocalName();
			if (nodename.equals("username"))
				return node.getFirstChild().getNodeValue();

		}

		throw new AxisFault("Missing password element in SOAP header!");
	}

}
