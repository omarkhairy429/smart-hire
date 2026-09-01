package orange.smart_hire.service;

import orange.smart_hire.dto.DossierResponse;
import orange.smart_hire.dto.InterviewResponse;
import orange.smart_hire.dto.ScheduleInterviewRequest;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Application;
import orange.smart_hire.model.Interview;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.InterviewRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final PostingRepository postingRepository;
    private final UserRepository userRepository;

    public InterviewService(InterviewRepository interviewRepository,
                            ApplicationRepository applicationRepository,
                            PostingRepository postingRepository,
                            UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.postingRepository = postingRepository;
        this.userRepository = userRepository;
    }

    public InterviewResponse schedule(UUID applicationId, ScheduleInterviewRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        User interviewer = userRepository.findById(request.getInterviewerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interviewer not found"));

        if (interviewer.getRole() != UserRole.INTERVIEWER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selected user is not an interviewer");
        }

        Interview interview = new Interview();
        interview.setApplicationId(application.getId());
        interview.setInterviewerId(interviewer.getId());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setCreatedAt(LocalDateTime.now());
        interview.setUpdatedAt(LocalDateTime.now());

        Interview saved = interviewRepository.save(interview);

        // Booking an interview moves the candidate into the INTERVIEW stage, but
        // never drags an already-decided application backwards.
        if (application.getStage() == ApplicationStage.APPLIED
                || application.getStage() == ApplicationStage.SCREENING) {
            application.setStage(ApplicationStage.INTERVIEW);
            application.setUpdatedAt(LocalDateTime.now());
            applicationRepository.save(application);
        }

        return mapToResponse(saved);
    }

    public void cancel(UUID interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interview not found"));

        interviewRepository.delete(interview);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getInterviewers() {
        return userRepository.findByRoleIn(List.of(UserRole.INTERVIEWER))
                .stream()
                .map(StaffResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsByApplication(UUID applicationId) {
        return interviewRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyInterviews(UUID interviewerId) {
        return interviewRepository
                .findByInterviewerIdAndScheduledAtAfterOrderByScheduledAtAsc(
                        interviewerId, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsByCandidate(UUID candidateId) {
        List<Application> applications = applicationRepository.findByCandidateId(candidateId);
        return applications.stream()
                .flatMap(app -> interviewRepository.findByApplicationId(app.getId()).stream())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DossierResponse getDossier(UUID interviewId, UUID interviewerId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interview not found"));

        // US2.7: an interviewer may only open a dossier for their own interview
        if (!interview.getInterviewerId().equals(interviewerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "This interview is not assigned to you");
        }

        Application application = applicationRepository.findById(interview.getApplicationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        DossierResponse response = new DossierResponse();
        response.setInterviewId(interview.getId());
        response.setScheduledAt(interview.getScheduledAt());
        response.setMeetingLink(interview.getMeetingLink());

        response.setCandidateId(application.getCandidateId());
        response.setResumeUrl(application.getResumeUrl());
        response.setCoverLetter(application.getCoverLetter());
        response.setExperienceSummary(application.getExperienceSummary());

        userRepository.findById(application.getCandidateId()).ifPresent(candidate -> {
            response.setCandidateName(fullName(candidate));
            response.setCandidateEmail(candidate.getEmail());
        });

        response.setPostingId(application.getPostingId());
        postingRepository.findById(application.getPostingId()).ifPresent(posting -> {
            response.setPostingTitle(posting.getTitle());
            response.setPostingDescription(posting.getDescription());
            response.setSkillsRequired(posting.getSkillsRequired());
        });

        return response;
    }

    private InterviewResponse mapToResponse(Interview interview) {
        InterviewResponse response = new InterviewResponse();

        response.setId(interview.getId());
        response.setApplicationId(interview.getApplicationId());
        response.setInterviewerId(interview.getInterviewerId());
        response.setScheduledAt(interview.getScheduledAt());
        response.setMeetingLink(interview.getMeetingLink());
        response.setCreatedAt(interview.getCreatedAt());
        response.setUpdatedAt(interview.getUpdatedAt());

        userRepository.findById(interview.getInterviewerId())
                .ifPresent(interviewer -> response.setInterviewerName(fullName(interviewer)));

        applicationRepository.findById(interview.getApplicationId()).ifPresent(application -> {
            userRepository.findById(application.getCandidateId())
                    .ifPresent(candidate -> response.setCandidateName(fullName(candidate)));
            postingRepository.findById(application.getPostingId())
                    .ifPresent(posting -> response.setPostingTitle(posting.getTitle()));
        });

        return response;
    }

    private String fullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
