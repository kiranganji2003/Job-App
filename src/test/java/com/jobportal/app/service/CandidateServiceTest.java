package com.jobportal.app.service;

import com.jobportal.app.entity.Candidate;
import com.jobportal.app.exception.AlreadyRegisteredException;
import com.jobportal.app.exception.InvalidJobPostIdException;
import com.jobportal.app.model.CandidateRequestDto;
import com.jobportal.app.repository.CandidateRepository;
import com.jobportal.app.repository.CompanyRepository;
import com.jobportal.app.repository.JobRepository;
import com.jobportal.app.utility.AppMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    void registerCandidate_alreadyRegistered_throwsException() {
        when(candidateRepository.findById("test@gmail.com")).thenReturn(Optional.of(new Candidate()));
        CandidateRequestDto dto = new CandidateRequestDto();
        dto.setEmail("test@gmail.com");

        AlreadyRegisteredException exception = assertThrows(
                AlreadyRegisteredException.class,
                () -> candidateService.registerCandidate(dto)
        );

        assertEquals(String.format(AppMessages.EMAIL_ALREADY_REGISTERED, dto.getEmail()), exception.getMessage());
    }

    @Test
    void registerCandidate_newCandidate_returnsSuccess() {
        when(candidateRepository.findById("test@gmail.com")).thenReturn(Optional.empty());
        CandidateRequestDto dto = new CandidateRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("test@123");

        String result = candidateService.registerCandidate(dto);

        assertEquals(AppMessages.REGISTERED_SUCCESSFULLY, result);
        verify(candidateRepository, times(1)).save(any());
    }

    @Test
    void applyJobPost_InvalidJobPost() {
        when(jobRepository.findById(any())).thenReturn(Optional.empty());

        InvalidJobPostIdException invalidJobPostIdException =
                assertThrows(InvalidJobPostIdException.class,
                        () -> candidateService.applyJobPost(-1));

        assertEquals("No such Job Post Id -1", invalidJobPostIdException.getMessage());
    }
}
