package ru.veselov.instazoocource.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoocource.dto.PostDTO;
import ru.veselov.instazoocource.entity.PostEntity;
import ru.veselov.instazoocource.model.Post;

import java.util.List;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "user", ignore = true)
    PostEntity toEntity(PostDTO postDTO);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "user", ignore = true)
    Post entityToPost(PostEntity postEntity);

    List<Post> entitiesToPosts(List<PostEntity> entities);

}
