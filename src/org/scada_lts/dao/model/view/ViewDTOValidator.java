package org.scada_lts.dao.model.view;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ViewDTOValidator implements Validator {
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

        if (view.getSize() < 0 || view.getSize() > 4){
            errors.rejectValue("size", "Values out of range");
        }
        if (view.getName().length() > 100){
            errors.rejectValue("name", "Name is too large");
        }



    }
}
