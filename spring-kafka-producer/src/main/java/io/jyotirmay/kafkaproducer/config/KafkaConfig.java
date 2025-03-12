package io.jyotirmay.kafkaproducer.config;

import java.util.HashMap;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

	@Bean
	DefaultKafkaProducerFactory<String, String> kafkaProducerFactory() {

		var producerConfig = new HashMap<String, Object>();
		producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
		producerConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		producerConfig.put(ProducerConfig.RETRIES_CONFIG, 3);
		producerConfig.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 30000);
		return new DefaultKafkaProducerFactory<>(producerConfig);
	}

	@Bean
	KafkaTemplate<String, String> kafkaTemplate(DefaultKafkaProducerFactory<String, String> kafkaProducerFactory) {

		return new KafkaTemplate<String, String>(kafkaProducerFactory);
	}
}
