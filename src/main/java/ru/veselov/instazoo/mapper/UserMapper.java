package ru.veselov.instazoo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.request.SignUpRequest;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "posts", ignore = true)
    UserEntity signUpToUser(SignUpRequest sign);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User entityToUser(UserEntity userEntity);

    UserDTO modelToDTO(User user);

}
