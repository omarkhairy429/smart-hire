package orange.smart_hire.service;

import orange.smart_hire.dto.CreatePostingRequest;
import orange.smart_hire.model.Posting;

import java.util.UUID;

public interface PostingService {

    Posting createPosting(CreatePostingRequest request, UUID hrManagerId);
}