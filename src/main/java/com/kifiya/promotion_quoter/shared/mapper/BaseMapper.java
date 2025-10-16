package com.kifiya.promotion_quoter.shared.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface BaseMapper<E,T,D> {

    E toEntity(T dto);

    D toBo(E entity);

    E updateEntity(T dto, @MappingTarget E entity);

    List<E> toEntity(List<T> dtoList);

    List<D> toBoList(List<E> entityList);
}
