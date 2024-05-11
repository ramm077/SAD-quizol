
package com.valuelabs.livequiz.service.option;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.model.dto.request.OptionCreationDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.repository.OptionRepository;
import com.valuelabs.livequiz.service.option.OptionService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private OptionService optionService;

    @Test
    public void testCreateOption_Success(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        OptionCreationDTO optionDTO = new OptionCreationDTO("Option",true);
        Option option = new Option(optionDTO.optionText(), optionDTO.isTrue(), "ChandraMouli");
        when(optionRepository.save(option)).thenReturn(option);
        Option result = optionService.createOption(optionDTO);
        assertEquals(option,result);
    }

    @Test
    public void testSetOptionsToQuestion_Success(){
        Option option = new Option(1L,"Option 1",true,"Default");
        Question question = new Question(); question.setQuestionId(1L);
        optionService.setOptionsToQuestion(question,List.of(option));
        assertEquals(option.getQuestion(),question);
    }
}
