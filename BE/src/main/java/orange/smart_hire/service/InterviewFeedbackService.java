package orange.smart_hire.service;

import orange.smart_hire.dto.FeedbackResponse;
import orange.smart_hire.dto.SubmitFeedbackRequest;
import orange.smart_hire.model.Interview;
import orange.smart_hire.model.InterviewFeedback;
import orange.smart_hire.repository.InterviewFeedbackRepository;
import orange.smart_hire.repository.InterviewRepository;
import orange.smart_hire.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InterviewFeedbackService {

    private final InterviewFeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    public InterviewFeedbackService(InterviewFeedbackRepository feedbackRepository,
                                    InterviewRepository interviewRepository,
                                    UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
    }

    public FeedbackResponse submit(UUID interviewId, UUID interviewerId, SubmitFeedbackRequest request) {
        requireOwnInterview(interviewId, interviewerId);

        // One feedback per interviewer per interview: a second submit edits the first
        InterviewFeedback feedback = feedbackRepository
                .findByInterviewIdAndInterviewerId(interviewId, interviewerId)
                .orElseGet(() -> {
                    InterviewFeedback created = new InterviewFeedback();
                    created.setInterviewId(interviewId);
                    created.setInterviewerId(interviewerId);
                    return created;
                });

        feedback.setRating(request.getRating());
        feedback.setTechnicalScore(request.getTechnicalScore());
        feedback.setCommunicationScore(request.getCommunicationScore());
        feedback.setRecommendation(request.getRecommendation());
        feedback.setComments(request.getComments());

        return mapToResponse(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getMyFeedback(UUID interviewId, UUID interviewerId) {
        requireOwnInterview(interviewId, interviewerId);

        return feedbackRepository.findByInterviewIdAndInterviewerId(interviewId, interviewerId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No feedback submitted for this interview yet"));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackForInterview(UUID interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found");
        }

        return feedbackRepository.findByInterviewId(interviewId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /** An interviewer may only touch feedback for an interview assigned to them. */
    private void requireOwnInterview(UUID interviewId, UUID interviewerId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interview not found"));

        if (!interview.getInterviewerId().equals(interviewerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "This interview is not assigned to you");
        }
    }

    private FeedbackResponse mapToResponse(InterviewFeedback feedback) {
        FeedbackResponse response = new FeedbackResponse();

        response.setId(feedback.getId());
        response.setInterviewId(feedback.getInterviewId());
        response.setInterviewerId(feedback.getInterviewerId());
        response.setRating(feedback.getRating());
        response.setTechnicalScore(feedback.getTechnicalScore());
        response.setCommunicationScore(feedback.getCommunicationScore());
        response.setRecommendation(feedback.getRecommendation());
        response.setComments(feedback.getComments());
        response.setCreatedAt(feedback.getCreatedAt());
        response.setUpdatedAt(feedback.getUpdatedAt());

        userRepository.findById(feedback.getInterviewerId()).ifPresent(user ->
                response.setInterviewerName(user.getFirstName() + " " + user.getLastName()));

        return response;
    }
}
