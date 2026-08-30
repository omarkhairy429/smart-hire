package orange.smart_hire.service;

import orange.smart_hire.dto.ApplicationResponse;
import orange.smart_hire.dto.ApplyRequest;
import orange.smart_hire.dto.PipelineResponse;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.ApplicationStatus;
import orange.smart_hire.model.Application;
import orange.smart_hire.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import orange.smart_hire.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public ApplicationResponse apply(ApplyRequest request, UUID candidateId) {
        if (applicationRepository.existsByPostingIdAndCandidateId(
                request.getPostingId(), candidateId
        )) {
            throw new IllegalStateException(
                    "Candidate has already applied to this posting"
            );
        }
        Application application = new Application();

        application.setPostingId(request.getPostingId());
        application.setCandidateId(candidateId);
        application.setCoverLetter(request.getCoverLetter());
        application.setExperienceSummary(request.getExperienceSummary());
        application.setResumeUrl(request.getResumeUrl());

        application.setStage(ApplicationStage.APPLIED);
        application.setStatus(ApplicationStatus.IN_REVIEW);

        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Application savedApplication =
                applicationRepository.save(application);

        return mapToResponse(savedApplication);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(
            UUID candidateId
    ) {
        return applicationRepository
                .findByCandidateId(candidateId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByPosting(UUID postingId) {
        return applicationRepository.findByPostingId(postingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        return mapToResponse(application);
    }


    private ApplicationResponse mapToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();

        response.setId(application.getId());
        response.setPostingId(application.getPostingId());
        response.setCandidateId(application.getCandidateId());
        response.setCoverLetter(application.getCoverLetter());
        response.setExperienceSummary(application.getExperienceSummary());
        response.setResumeUrl(application.getResumeUrl());
        response.setStage(application.getStage());
        response.setStatus(application.getStatus());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());


        if (application.getCandidateId() != null) {
            userRepository.findById(application.getCandidateId()).ifPresent(user -> {
                response.setCandidateName(user.getFirstName());
                response.setCandidateEmail(user.getEmail());
            });
        }

        return response;
    }

    public List<PipelineResponse> getPipeline(UUID postingId) {

        List<Application> applications =
                applicationRepository.findByPostingId(postingId);

        return Arrays.stream(ApplicationStage.values())
                .map(stage -> {

                    PipelineResponse response =
                            new PipelineResponse();

                    response.setStage(stage);

                    response.setResponses(
                            applications.stream()
                                    .filter(application ->
                                            application.getStage() == stage
                                    )
                                    .map(this::mapToResponse)
                                    .toList()
                    );

                    return response;
                })
                .toList();
    }

    public ApplicationResponse updateStage(UUID applicationId, ApplicationStage newStage) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStage(newStage);
        application.setUpdatedAt(LocalDateTime.now());
        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);

    }
}
