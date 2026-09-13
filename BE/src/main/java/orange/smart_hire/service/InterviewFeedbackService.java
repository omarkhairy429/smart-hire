package orange.smart_hire.service;

import orange.smart_hire.dto.FeedbackResponse;
import orange.smart_hire.dto.SubmitFeedbackRequest;
import orange.smart_hire.enums.NotificationType;
import orange.smart_hire.exception.ForbiddenException;
import orange.smart_hire.exception.ResourceNotFoundException;
import orange.smart_hire.model.Application;
import orange.smart_hire.model.Interview;
import orange.smart_hire.model.InterviewFeedback;
import orange.smart_hire.model.Posting;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.InterviewFeedbackRepository;
import orange.smart_hire.repository.InterviewRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InterviewFeedbackService {

    private final InterviewFeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final PostingRepository postingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InterviewFeedbackService(InterviewFeedbackRepository feedbackRepository,
                                    InterviewRepository interviewRepository,
                                    ApplicationRepository applicationRepository,
                                    PostingRepository postingRepository,
                                    UserRepository userRepository,
                                    NotificationService notificationService) {
        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.postingRepository = postingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public FeedbackResponse submit(UUID interviewId, UUID interviewerId, SubmitFeedbackRequest request) {
        requireOwnInterview(interviewId, interviewerId);

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

        InterviewFeedback saved = feedbackRepository.save(feedback);
        notifyHiringManager(interviewId, interviewerId, saved.getId());

        return mapToResponse(saved);
    }

    private void notifyHiringManager(UUID interviewId, UUID interviewerId, UUID feedbackId) {
        interviewRepository.findById(interviewId)
                .flatMap(interview -> applicationRepository.findById(interview.getApplicationId()))
                .map(Application::getPostingId)
                .flatMap(postingRepository::findById)
                .map(Posting::getHrManager)
                .ifPresent(hrManager -> {
                    String interviewerName = userRepository.findById(interviewerId)
                            .map(u -> u.getFirstName() + " " + u.getLastName())
                            .orElse("An interviewer");

                    notificationService.sendNotification(
                            hrManager.getId(),
                            NotificationType.FEEDBACK_SUBMITTED,
                            "Interview Feedback Submitted",
                            interviewerName + " submitted feedback for an interview.",
                            feedbackId
                    );
                });
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getMyFeedback(UUID interviewId, UUID interviewerId) {
        requireOwnInterview(interviewId, interviewerId);

        return feedbackRepository.findByInterviewIdAndInterviewerId(interviewId, interviewerId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No feedback submitted for this interview yet"));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackForInterview(UUID interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new ResourceNotFoundException("Interview not found");
        }

        return feedbackRepository.findByInterviewId(interviewId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void requireOwnInterview(UUID interviewId, UUID interviewerId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getInterviewerId().equals(interviewerId)) {
            throw new ForbiddenException("This interview is not assigned to you");
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
