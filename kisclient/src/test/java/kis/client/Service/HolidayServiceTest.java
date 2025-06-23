package kis.client.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HolidayServiceTest {

    @Autowired
    HolidayService holidayService;

    @Test
    public void holiday() {
        //given
        List<LocalDate> holidays = holidayService.getHolidays();
        //when
        for (LocalDate holiday : holidays) {
            System.out.println("holiday = " + holiday);
        }
        //then
    }

}