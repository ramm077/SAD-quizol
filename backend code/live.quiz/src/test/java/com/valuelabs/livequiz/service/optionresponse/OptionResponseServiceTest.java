package com.valuelabs.livequiz.service.optionresponse;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.OptionResponse;
import com.valuelabs.livequiz.repository.OptionResponseRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OptionResponseServiceTest {
    @Mock
    private OptionResponseRepository optionResponseRepository;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private OptionResponseService optionResponseService;

    @Test
    public void createOptionResponse_throwInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> optionResponseService.createOptionResponse(null));
        assertEquals("No chosenOptions provided!",exception.getMessage());
    }
    @Test
    public void createOptionResponse_Success(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        Option option = new Option(1L,"Option",true,"ChandraMouli");
        OptionResponse optionResponse = new OptionResponse(List.of(option),"ChandraMouli");
        when(optionResponseRepository.save(optionResponse)).thenReturn(optionResponse);
        OptionResponse result = optionResponseService.createOptionResponse(List.of(option));
        assertEquals(result,optionResponse);
    }

    @Test
    public void getOptionResponseById_throwInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> optionResponseService.getOptionResponseById(null));
        assertEquals("optionResponseId must be provided!",exception.getMessage());
    }
    @Test
    public void getOptionResponseById_throwResourceNotFoundException(){
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> optionResponseService.getOptionResponseById(1L));
        assertEquals("No active option responses available for with optionResponseId: " + 1,exception.getMessage());
    }
    @Test
    public void getOptionResponseById_Success(){
        OptionResponse optionResponse = new OptionResponse(); optionResponse.setOptionResponseId(1L); optionResponse.setInActive(false);
        when(optionResponseRepository.findByOptionResponseIdAndInActive(1L,false)).thenReturn(Optional.of(optionResponse));
        OptionResponse result = optionResponseService.getOptionResponseById(1L);
        assertEquals(optionResponse,result);
    }
    @Test
    public void saveOptionResponse_throwInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> optionResponseService.saveOptionResponse(null));
        assertEquals("Option Response must be provided!",exception.getMessage());
    }

    @Test
    public void saveOptionResponse_Success(){
        OptionResponse optionResponse = new OptionResponse(); optionResponse.setOptionResponseId(1L); optionResponse.setInActive(false);
        when(optionResponseRepository.save(optionResponse)).thenReturn(optionResponse);
        OptionResponse result = optionResponseService.saveOptionResponse(optionResponse);
        assertEquals(result,optionResponse);
    }
}
