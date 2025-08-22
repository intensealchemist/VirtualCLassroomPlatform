package com.virtualclassroom.service.impl;

import com.virtualclassroom.model.Assignment;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Override
    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Assignment getAssignmentById(Long assignmentId, Long courseId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Assignment createAssignment(Long courseId, Assignment assignment, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Assignment updateAssignment(Long courseId, Assignment assignment, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void deleteAssignment(Long assignmentId, Long courseId, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void updateAssignmentAttachment(Long assignmentId, Long courseId, String attachmentUrl, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Assignment publishAssignment(Long assignmentId, Long courseId, boolean publish, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
