package ru.yandex.practicum.filmorate.data;

import org.springframework.core.convert.converter.Converter;

import javax.validation.ValidationException;

public class StringToSearchTypeConverter implements Converter<String, SearchType> {
    @Override
    public SearchType convert(String source) {
        try {
            if ("director".equals(source) || "title".equals(source)) {
                return SearchType.valueOf(source.toUpperCase());
            } else {
                throw new ValidationException("Задан не верный тип поиска");
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Задан не верный тип поиска");
        }
    }
}
