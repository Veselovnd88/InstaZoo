package ru.veselov.instazoo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.model.Post;

import java.util.List;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "likedUsers", ignore = true)
    PostEntity toEntity(PostDTO postDTO);

    Post entityToPost(PostEntity postEntity);

    List<Post> entitiesToPosts(List<PostEntity> entities);

}
