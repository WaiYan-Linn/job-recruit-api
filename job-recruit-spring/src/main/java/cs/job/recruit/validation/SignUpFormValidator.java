package cs.job.recruit.validation;

import cs.job.recruit.api.input.SignUpForm;
import cs.job.recruit.domain.entity.Account;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SignUpFormValidator implements ConstraintValidator<ValidSignUpForm, SignUpForm> {

    @Override
    public boolean isValid(SignUpForm form, ConstraintValidatorContext context) {
        boolean valid = true;
        if (form.role() == Account.Role.EMPLOYER) {
            valid = form.companyName() != null && !form.companyName().isBlank();
        } else if (form.role() == Account.Role.JOBSEEKER) {
            valid = form.name() != null && !form.name().isBlank();
        }
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addConstraintViolation();
        }
        return valid;
    }
}
