package ru.veselov.instazoo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    CommentEntity dtoToEntity(CommentDTO commentDTO);

    @Mapping(target = "postId", expression = "java(commentEntity.getPost().getId())")
    Comment entityToComment(CommentEntity commentEntity);

    List<Comment> entitiesToComments(List<CommentEntity> entityList);
}
