package com.apexon.auth.util;

import org.modelmapper.ModelMapper;

public class ModelMapperUtil {

    private static final ModelMapper modelMapper = new ModelMapper();

    private ModelMapperUtil() {
        // Prevent instantiation
    }

    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public static ModelMapper getInstance() {
        return modelMapper;
    }
}
