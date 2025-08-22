package com.virtualclassroom.service.impl;

import com.virtualclassroom.model.Submission;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.SubmissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Override
    public List<Submission> getSubmissionsByAssignment(Long assignmentId, Long courseId, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission getStudentSubmission(Long assignmentId, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission createSubmission(Long assignmentId, Submission submission, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission createOrUpdateSubmissionWithFile(Long assignmentId, String submissionUrl, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission gradeSubmission(Long submissionId, Long assignmentId, Submission submission, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission gradeSubmission(Long submissionId, Submission submission, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Submission> getSubmissionsByStudent(User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission getSubmissionById(Long submissionId, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission updateSubmission(Submission submission, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Submission updateSubmissionFile(Long submissionId, String submissionUrl, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void deleteSubmission(Long submissionId, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Submission> getSubmissionsForInstructor(User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Submission> getUngradedSubmissionsForInstructor(User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Object getCourseSubmissionStatistics(Long courseId, User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Object getStudentSubmissionStatistics(User user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
