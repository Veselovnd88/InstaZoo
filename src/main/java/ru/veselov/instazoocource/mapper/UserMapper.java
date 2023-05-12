package ru.veselov.instazoocource.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.model.User;
import ru.veselov.instazoocource.payload.request.SignUpRequest;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserEntity signUpToUser(SignUpRequest sign);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User entityToUser(UserEntity userEntity);

}
