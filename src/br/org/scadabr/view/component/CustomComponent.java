package br.org.scadabr.view.component;

import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.User;
import com.serotonin.web.i18n.LocalizableMessage;

abstract public class CustomComponent extends ViewComponent {

	abstract public String generateContent();

	abstract public String generateInfoContent();

	public boolean isCustomComponent() {
		return true;
	}

	public int[] getSupportedDataTypes() {
		return definition().getSupportedDataTypes();
	}

	public String getTypeName() {
		return definition().getName();
	}

	public LocalizableMessage getDisplayName() {
		return new LocalizableMessage(definition().getNameKey());
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void validateDataPoint(User user, boolean makeReadOnly) {

	}

}
