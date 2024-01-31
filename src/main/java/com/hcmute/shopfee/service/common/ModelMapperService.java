package com.hcmute.shopfee.service.common;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.hcmute.shopfee.constant.BeanConstant.MODEL_MAPPER_NOT_NULL;


@Component
@RequiredArgsConstructor
public class ModelMapperService {
    private final ModelMapper modelMapper;
    @Autowired
    @Qualifier(MODEL_MAPPER_NOT_NULL)
    private ModelMapper modelMapperNotNull;

    public <S, D> List<D> mapListNotNull(List<S> sourceList, Class<D> destinationType) {
        return sourceList
                .stream()
                .map(element -> modelMapperNotNull.map(element, destinationType))
                .collect(Collectors.toList());
    }

    public <S, D> List<D> mapList(List<S> sourceList, Class<D> destinationType) {
        return sourceList
                .stream()
                .map(element -> modelMapper.map(element, destinationType))
                .collect(Collectors.toList());
    }

    public <S, D> D mapNotNullClass(S source, Class<D> destinationType) {
        return modelMapperNotNull.map(source, destinationType);
    }
    public <S, D> D mapNotNull(S source, D destination) {
        modelMapperNotNull.map(source, destination);
        return destination;
    }

    public <S, D> D mapClass(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
    public <S, D> D map(S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
