//package kis.client.Service.aws;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
//import software.amazon.awssdk.services.cloudwatch.model.Dimension;
//import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
//import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
//import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
//
//import java.time.Instant;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CloudWatchMetricService {
//
//    private final CloudWatchClient cloudWatchClient;
//
//    public void putLatencyMetric(String stockCode, long durationMs) {
//        MetricDatum datum = MetricDatum.builder()
//                .metricName("KisStockApiLatency")
//                .unit(StandardUnit.MILLISECONDS)
//                .value((double) durationMs)
//                .dimensions(Dimension.builder()
//                        .name("StockCode")
//                        .value(stockCode)
//                        .build())
//                .timestamp(Instant.now())
//                .build();
//
//        PutMetricDataRequest request = PutMetricDataRequest.builder()
//                .namespace("Newstoss/StockAPI") // 원하는 네임스페이스
//                .metricData(datum)
//                .build();
//
//        cloudWatchClient.putMetricData(request);
//    }
//
//    public void putStockInfoNullMetric(String stockCode) {
//        MetricDatum datum = MetricDatum.builder()
//                .metricName("StockInfoNullCount")
//                .unit(StandardUnit.COUNT)
//                .value(1.0)
//                .timestamp(Instant.now())
//                .dimensions(Dimension.builder()
//                        .name("StockCode")
//                        .value(stockCode)
//                        .build())
//                .build();
//
//        PutMetricDataRequest request = PutMetricDataRequest.builder()
//                .namespace("Newstoss/StockService")
//                .metricData(datum)
//                .build();
//
//        cloudWatchClient.putMetricData(request);
//    }
//}
