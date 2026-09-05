package orange.smart_hire.service;

import orange.smart_hire.dto.ApplicationResponse;
import orange.smart_hire.dto.ApplyRequest;
import orange.smart_hire.dto.PipelineResponse;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.ApplicationStatus;
import orange.smart_hire.enums.NotificationType;
import orange.smart_hire.model.Application;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpStatus;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final PostingRepository postingRepository;
    private final NotificationService notificationService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserRepository userRepository,
                              PostingRepository postingRepository,
                              NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.postingRepository = postingRepository;
        this.notificationService = notificationService;
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

        Posting posting = postingRepository.findById(request.getPostingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Posting not found"
                ));

        UUID hrManagerId = posting.getHrManager().getId();

        notificationService.sendNotification(
                hrManagerId,
                NotificationType.APPLICATION_SUBMITTED,
                "New Application",
                "A new candidate has applied to your job posting.",
                savedApplication.getId()
        );

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

        ApplicationStage oldStage = application.getStage();

        if (oldStage == newStage) {
            return mapToResponse(application);
        }

        application.setStage(newStage);
        application.setUpdatedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);

        notificationService.sendNotification(
                saved.getCandidateId(),
                NotificationType.APPLICATION_STAGE_CHANGED,
                "Application Stage Updated",
                "Your application stage has been changed to " + newStage,
                saved.getId()
        );

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForPosting(UUID postingId, ApplicationStage stage,
                                                               String sort, String dir) {
        Posting posting = assertHrCanAccessPosting(postingId);

        List<ApplicationResponse> responses = applicationRepository
                .findByPostingIdAndOptionalStage(postingId, stage)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return sortApplications(responses, sort, dir);
    }

    @Transactional(readOnly = true)
    public String exportApplicationsAsCsv(UUID postingId, ApplicationStage stage, String sort, String dir) {
        List<ApplicationResponse> responses = getApplicationsForPosting(postingId, stage, sort, dir);

        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                .setHeader("Name", "Email", "Stage", "Status", "AppliedAt", "ResumeUrl")
                .build())) {
            for (ApplicationResponse response : responses) {
                printer.printRecord(
                        response.getCandidateName(),
                        response.getCandidateEmail(),
                        response.getStage(),
                        response.getStatus(),
                        response.getCreatedAt(),
                        response.getResumeUrl()
                );
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate CSV export");
        }

        return writer.toString();
    }

    private Posting assertHrCanAccessPosting(UUID postingId) {
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Posting not found"));

        User currentUser = SecurityUtils.getCurrentUser();
        boolean sameCompany = currentUser.getCompanyName() != null
                && currentUser.getCompanyName().equalsIgnoreCase(posting.getCompany());

        if (!sameCompany) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not authorized to view applications for this posting");
        }

        return posting;
    }

    private List<ApplicationResponse> sortApplications(List<ApplicationResponse> responses, String sort, String dir) {
        Comparator<ApplicationResponse> comparator = "name".equalsIgnoreCase(sort)
                ? Comparator.comparing(ApplicationResponse::getCandidateName,
                Comparator.nullsLast(String::compareToIgnoreCase))
                : Comparator.comparing(ApplicationResponse::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder()));

        if ("desc".equalsIgnoreCase(dir)) {
            comparator = comparator.reversed();
        }

        return responses.stream().sorted(comparator).toList();
    }
}