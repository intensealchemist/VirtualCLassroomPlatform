package com.virtualclassroom.service;

import com.virtualclassroom.model.Submission;
import com.virtualclassroom.model.User;

import java.util.List;

public interface SubmissionService {
    // AssignmentController usages
    List<Submission> getSubmissionsByAssignment(Long assignmentId, Long courseId, User user);

    Submission getStudentSubmission(Long assignmentId, User user);

    Submission createSubmission(Long assignmentId, Submission submission, User user);

    Submission createOrUpdateSubmissionWithFile(Long assignmentId, String submissionUrl, User user);

    Submission gradeSubmission(Long submissionId, Long assignmentId, Submission submission, User user);

    // Overload to support SubmissionController which does not pass assignmentId
    Submission gradeSubmission(Long submissionId, Submission submission, User user);

    // SubmissionController usages
    List<Submission> getSubmissionsByStudent(User user);

    Submission getSubmissionById(Long submissionId, User user);

    Submission updateSubmission(Submission submission, User user);

    Submission updateSubmissionFile(Long submissionId, String submissionUrl, User user);

    void deleteSubmission(Long submissionId, User user);

    List<Submission> getSubmissionsForInstructor(User user);

    List<Submission> getUngradedSubmissionsForInstructor(User user);

    Object getCourseSubmissionStatistics(Long courseId, User user);

    Object getStudentSubmissionStatistics(User user);
}
