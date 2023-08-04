package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

/**
 * валидатор даты выпуска релиза кина
 */
public class DateNotEarlierValidator implements ConstraintValidator<DateNotEarlier, LocalDate> {
    private static final int YEAR = 1895;
    private static final int DAY_OF_MONTH = 28;
    private static final LocalDate STARTING_DATE = LocalDate.of(YEAR, Month.DECEMBER, DAY_OF_MONTH);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !localDate.isBefore(STARTING_DATE);
    }
}
