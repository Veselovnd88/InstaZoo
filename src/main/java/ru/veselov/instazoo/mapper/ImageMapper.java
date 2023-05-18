package ru.veselov.instazoo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoo.entity.ImageEntity;
import ru.veselov.instazoo.model.ImageModel;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

    ImageModel entityToModel(ImageEntity entity);

}
