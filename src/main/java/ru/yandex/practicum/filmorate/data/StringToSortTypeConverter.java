package ru.yandex.practicum.filmorate.data;

import org.springframework.core.convert.converter.Converter;

import javax.validation.ValidationException;

public class StringToSortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType convert(String source) {
        try {
            if ("year".equals(source) || "likes".equals(source)) {
                return SortType.valueOf(source.toUpperCase());
            } else {
                throw new ValidationException("Задан не верный тип сортировки");
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Задан не верный тип сортировки");
        }
    }
}
