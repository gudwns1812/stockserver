package kis.client.repository;

import jakarta.annotation.PostConstruct;
import kis.client.entity.Stock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class StockInit {

    private final StockRepository stockRepository;
    private final List<Stock> stocks = new ArrayList<>();
    private int pageIndex;
    private final int pageSize = 560;

    @PostConstruct
    public void init() {
        String clientId = System.getenv("KIS_CLIENT_ID");
        if (clientId != null) {
             pageIndex = Integer.parseInt(clientId.split("-")[1]);
        }
        log.info("클라이언트 아이디 : {} 클라이언트 번호 : {}" , clientId , pageIndex);
        List<Stock> findStocks = stockRepository.findStockOrderByIdDESC(pageIndex, pageSize);
        stocks.addAll(findStocks);
        log.info("Stocks found: {}" , stocks.size());
//        InputStream is = getClass().getClassLoader().getResourceAsStream("failure_codes.txt");
//        List<String> failedCodes = new BufferedReader(new InputStreamReader(is))
//                .lines()
//                .flatMap(line -> Arrays.stream(line.split(","))) // 각 줄 split
//                .map(String::trim) // 공백 제거
//                .map(s -> s.replaceAll("'", "")) // 작은 따옴표 제거
//                .filter(s -> !s.isEmpty())
//                .toList();
//        int fromIndex = pageIndex * pageSize;
//        int toIndex = Math.min(fromIndex + pageSize, failedCodes.size());
//        if (fromIndex >= failedCodes.size()) {
//            log.warn("해당 클라이언트가 처리할 실패 코드가 없습니다.");
//            return;
//        }
//        List<String> partitionedCodes = failedCodes.subList(fromIndex, toIndex);
//        List<Stock> findStocks = stockRepository.findByStockCodeIn(partitionedCodes);
//        stocks.addAll(findStocks);

        log.info("Stocks found: {}", stocks.size());
    }




}
