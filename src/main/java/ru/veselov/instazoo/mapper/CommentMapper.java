package ru.veselov.instazoo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    CommentEntity dtoToEntity(CommentDTO commentDTO);

    Comment entityToComment(CommentEntity commentEntity);

    List<Comment> entitiesToComments(List<CommentEntity> entityList);
}
