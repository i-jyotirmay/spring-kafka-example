package io.jyotirmay.kafkaconsumer.config;

import java.util.HashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import io.jyotirmay.kafkaconsumer.dto.TinyDTO;

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

		return new KafkaTemplate<>(kafkaProducerFactory);
	}

	@Bean
	DefaultKafkaConsumerFactory<String, String> consumerFactory() {

		var consumerConfig = new HashMap<String, Object>();
		consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "tiny-group-id");
		consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

		JsonDeserializer<TinyDTO> jsonDeserializer = new JsonDeserializer<>(TinyDTO.class, false);
		jsonDeserializer.setRemoveTypeHeaders(false);
		jsonDeserializer.addTrustedPackages("*");
		jsonDeserializer.setUseTypeMapperForKey(false);

		return new DefaultKafkaConsumerFactory(consumerConfig, new StringDeserializer(), jsonDeserializer);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, TinyDTO> factory(DefaultKafkaConsumerFactory<String, TinyDTO> consumerFactory, KafkaTemplate<String, TinyDTO> kafkaTemplate) {

		ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
				(consumerRecord, e) -> new TopicPartition(consumerRecord.topic() + "-dlq", consumerRecord.partition()));

		DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(5000L, 3));
		defaultErrorHandler.addNotRetryableExceptions(RuntimeException.class);

		ConcurrentKafkaListenerContainerFactory<String, TinyDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
		factory.setCommonErrorHandler(defaultErrorHandler);

		return factory;

	}
}
