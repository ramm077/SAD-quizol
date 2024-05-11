package com.valuelabs.livequiz.service.textresponse;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.OptionResponse;
import com.valuelabs.livequiz.model.entity.TextResponse;
import com.valuelabs.livequiz.repository.TextResponseRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TextResponseServiceTest {
    @Mock
    private TextResponseRepository textResponseRepository;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private TextResponseService textResponseService;

    @Test
    public void createTextResponse_throwInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> textResponseService.createTextResponse(null));
        assertEquals("No answerText provided!",exception.getMessage());
    }
    @Test
    public void createTextResponse_Success(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        TextResponse textResponse = new TextResponse("answer","ChandraMouli");
        when(textResponseRepository.save(textResponse)).thenReturn(textResponse);
        TextResponse result = textResponseService.createTextResponse("answer");
        assertEquals(result,textResponse);
    }

    @Test
    public void getTextResponseById_throwInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> textResponseService.getTextResponseById(null));
        assertEquals("textResponseId must be provided!",exception.getMessage());
    }
    @Test
    public void getTextResponseById_throwResourceNotFoundException(){
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> textResponseService.getTextResponseById(1L));
        assertEquals("No text response available for with textResponseId: " + 1,exception.getMessage());
    }
    @Test
    public void getTextResponseById_Success(){
        TextResponse textResponse = new TextResponse(); textResponse.setTextResponseId(1L); textResponse.setInActive(false);
        when(textResponseRepository.findByTextResponseIdAndInActive(1L,false)).thenReturn(Optional.of(textResponse));
        TextResponse result = textResponseService.getTextResponseById(1L);
        assertEquals(textResponse,result);
    }

    @Test
    public void saveTextResponse_Success(){
        TextResponse textResponse = new TextResponse(); textResponse.setTextResponseId(1L); textResponse.setInActive(false);
        when(textResponseRepository.save(textResponse)).thenReturn(textResponse);
        TextResponse result = textResponseService.saveTextResponse(textResponse);
        assertEquals(result,textResponse);
    }
    @Test
    public void saveTextResponse_ReturnsNull(){
        assertNull(textResponseService.saveTextResponse(null));
    }
}
