package com.kifiya.promotion_quoter.shared.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EnumMapper {
    default String toString(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    default <E extends Enum<E>> E fromString(String value, Class<E> enumType) {
        return (value != null && !value.isBlank()) ? Enum.valueOf(enumType, value) : null;
    }
}
