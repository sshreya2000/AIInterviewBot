package com.company.aiinterview.ai.service;

import com.company.aiinterview.ai.dto.request.ResumeAuditRequestDto;
import com.company.aiinterview.ai.dto.response.ResumeAuditResponseDto;

public interface ResumeAuditService {
    ResumeAuditResponseDto audit(ResumeAuditRequestDto request);
}
