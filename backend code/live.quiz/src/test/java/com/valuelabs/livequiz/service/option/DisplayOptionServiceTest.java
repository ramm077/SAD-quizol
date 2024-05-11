package com.valuelabs.livequiz.service.option;
import static org.junit.jupiter.api.Assertions.*;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.DisplayOptionDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.repository.OptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayOptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private DisplayOptionService displayOptionService;

   @Test
   public void testGetOptionById_Success(){
       Option option = new Option(1L,"Option",true,"default");
       when(optionRepository.findByOptionIdAndInActive(1L,false)).thenReturn(Optional.of(option));
       Option result = displayOptionService.getOptionById(1L,false);
       assertEquals(option,result);
   }

   @Test
   public void testGetOptionById_throwInvalidResourceDetailsException(){
       Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> displayOptionService.getOptionById(null,false));
       assertEquals("Option Id must be provided!",exception.getMessage());
   }

    @Test
    public void testGetOptionById_throwResourceNotFoundException(){
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> displayOptionService.getOptionById(1L,false));
        assertEquals("No " + "active" + " option found with optionId " + 1,exception.getMessage());
    }

    @Test
    public void testDisplayOption_Success(){
        Option option = new Option(1L,"Option",true,"default");
        DisplayOptionDTO expectedOption = new DisplayOptionDTO(1L,"Option",true);
        when(optionRepository.findByOptionIdAndInActive(1L,false)).thenReturn(Optional.of(option));
        DisplayOptionDTO result = displayOptionService.displayOption(1L,false);
        assertEquals(expectedOption,result);
    }

    @Test
    public void testDisplayOptionsInQuestion_Success(){
        Option option1 = new Option(1L,"Option 1",true,"default");
        Option option2 = new Option(2L,"Option 2",false,"default");
        when(optionRepository.findByOptionIdAndInActive(1L,false)).thenReturn(Optional.of(option1));
        when(optionRepository.findByOptionIdAndInActive(2L,false)).thenReturn(Optional.of(option2));
        DisplayOptionDTO optionDTO1 = new DisplayOptionDTO(1L,"Option 1",true);
        DisplayOptionDTO optionDTO2 = new DisplayOptionDTO(2L,"Option 2",false);
        List<DisplayOptionDTO> expectedOptions = List.of(optionDTO1,optionDTO2);
        List<DisplayOptionDTO> result = displayOptionService.displayOptionsInQuestion(List.of(option1,option2),false);
        assertEquals(expectedOptions,result);
    }
    @Test
    public void testDisplayOptionsInQuestion_ReturnsNull(){
        assertNull(displayOptionService.displayOptionsInQuestion(null, false));
    }
}
