//package kis.client.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
//
//@Configuration
//public class AWSConfig {
//
//    @Bean
//    public CloudWatchClient cloudWatchClient() {
//        return CloudWatchClient.builder()
//                .region(Region.AP_NORTHEAST_2) // 서울 리전
//                .build();
//    }
//}
