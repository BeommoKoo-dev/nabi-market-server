package org.prgrms.nabimarketbe.domain.completeRequest.dto.response;

import lombok.Builder;
import org.prgrms.nabimarketbe.domain.completeRequest.entity.CompleteRequest;
import org.prgrms.nabimarketbe.domain.completeRequest.entity.CompleteRequestStatus;

import java.time.LocalDateTime;

@Builder
public record CompleteRequestResponseDTO(
    Long completeRequestId,
    Long fromCardId,
    Long toCardId,
    CompleteRequestStatus completeRequestStatus,
    LocalDateTime createdAt
) {
    public static CompleteRequestResponseDTO of(CompleteRequest completeRequest) {
        return CompleteRequestResponseDTO.builder()
            .completeRequestId(completeRequest.getCompleteRequestId())
            .fromCardId(completeRequest.getFromCard().getCardId())
            .toCardId(completeRequest.getToCard().getCardId())
            .completeRequestStatus(completeRequest.getCompleteRequestStatus())
            .createdAt(completeRequest.getCreatedDate())
            .build();
    }
}
