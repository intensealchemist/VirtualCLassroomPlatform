package com.virtualclassroom.service;

import com.virtualclassroom.model.Assignment;
import com.virtualclassroom.model.User;

import java.util.List;

public interface AssignmentService {
    List<Assignment> getAssignmentsByCourse(Long courseId);

    Assignment getAssignmentById(Long assignmentId, Long courseId);

    Assignment createAssignment(Long courseId, Assignment assignment, User user);

    Assignment updateAssignment(Long courseId, Assignment assignment, User user);

    void deleteAssignment(Long assignmentId, Long courseId, User user);

    void updateAssignmentAttachment(Long assignmentId, Long courseId, String attachmentUrl, User user);

    Assignment publishAssignment(Long assignmentId, Long courseId, boolean publish, User user);
}
