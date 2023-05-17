package ru.veselov.instazoo.service;

import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.model.Comment;

import java.security.Principal;

public interface CommentService {

    Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal);

}
