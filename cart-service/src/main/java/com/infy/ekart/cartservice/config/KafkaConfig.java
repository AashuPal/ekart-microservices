//package com.infy.ekart.cartservice.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaConfig {
//
//    @Value("${app.kafka.topics.cart-events:cart-events}")
//    private String cartEventsTopic;
//
//    @Value("${app.kafka.topics.cart-abandoned:cart-abandoned}")
//    private String cartAbandonedTopic;
//
//    @Bean
//    public NewTopic cartEventsTopic() {
//        return TopicBuilder.name(cartEventsTopic)
//            .partitions(3)
//            .replicas(1)
//            .build();
//    }
//
//    @Bean
//    public NewTopic cartAbandonedTopic() {
//        return TopicBuilder.name(cartAbandonedTopic)
//            .partitions(3)
//            .replicas(1)
//            .build();
//    }
//}