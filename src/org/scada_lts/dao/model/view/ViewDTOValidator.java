package org.scada_lts.dao.model.view;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ViewDTOValidator implements Validator {

    private static final int VIEW_SIEZE_MIN = 0;
    private static final int VIEW_SIEZE_MAX = 4;
    private static final int NAME_MAX_SIZE = 100;
    private static final int XID_MAX_SIZE = 50;
    private static final int BACKGROUND_PATH_MAX_SIZE = 255;

    @Override
    public boolean supports(Class<?> aClass) {
        return ViewDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ViewDTO view = (ViewDTO) o;

        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty");
        ValidationUtils.rejectIfEmpty(errors, "xid", "xid.empty");
        ValidationUtils.rejectIfEmpty(errors, "imagePath", "imagePath.empty");

        if (view.getSize() < VIEW_SIEZE_MIN || view.getSize() > VIEW_SIEZE_MAX) {
            errors.rejectValue("size", "Values out of range");
        }
        if (view.getName().length() > NAME_MAX_SIZE) {
            errors.rejectValue("name", "Name is too large");
        }
        if (view.getXid().length() > XID_MAX_SIZE) {
            errors.rejectValue("xid", "Xid is too large");
        }
        if (view.getImagePath().length() > BACKGROUND_PATH_MAX_SIZE) {
            errors.rejectValue("background", "Background path is too large");
        }
    }
}
