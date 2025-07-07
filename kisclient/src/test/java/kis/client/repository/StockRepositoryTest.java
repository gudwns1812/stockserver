package kis.client.repository;

import kis.client.entity.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockRepositoryTest {

    @Autowired
    StockRepository stockRepository;

    @Test
    public void stockconnectTest() {
        //given

        List<Stock> all = stockRepository.findAll();
        for (Stock stock : all) {
            System.out.println("stock.getName() = " + stock.getName());
        }
        //when

        //then
    }


}