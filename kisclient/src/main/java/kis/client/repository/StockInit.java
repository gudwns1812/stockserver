package kis.client.repository;

import jakarta.annotation.PostConstruct;
import kis.client.entity.Stock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class StockInit {

    private final StockRepository stockRepository;
    private final List<Stock> stocks = new ArrayList<>();
    private int pageIndex;

    @PostConstruct
    public void init() {
        String clientId = System.getenv("KIS_CLIENT_ID");
        if (clientId != null) {
             pageIndex = Integer.parseInt(clientId.split("-")[1]);
        }
        log.info("클라이언트 아이디 : {} 클라이언트 번호 : {}" , clientId , pageIndex);
        List<Stock> findStocks = stockRepository.findAll();
        stocks.addAll(findStocks);

        log.info("Stocks found: {}", stocks.size());


//        log.info("Stocks found: {}" , stocks.size());
//        InputStream is = getClass().getClassLoader().getResourceAsStream("failure_codes.txt");
//        List<String> failedCodes = new BufferedReader(new InputStreamReader(is))
//                .lines()
//                .flatMap(line -> Arrays.stream(line.split(","))) // 각 줄 split
//                .map(String::trim) // 공백 제거
//                .map(s -> s.replaceAll("'", "")) // 작은 따옴표 제거
//                .filter(s -> !s.isEmpty())
//                .toList();
//        List<Stock> allFailedStocks = stockRepository.findByStockCodeIn(failedCodes);
//        log.info("DB에서 조회된 실패 종목 수: {}", allFailedStocks.size());
//
//        List<Stock> assignedStocks = allFailedStocks.stream()
//                .filter(stock -> stock.getId() % 10 == pageIndex)
//                .toList();
//        stocks.addAll(assignedStocks);

    }




}
