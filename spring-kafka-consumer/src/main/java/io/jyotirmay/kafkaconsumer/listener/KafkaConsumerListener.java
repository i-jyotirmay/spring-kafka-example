package io.jyotirmay.kafkaconsumer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.jyotirmay.kafkaconsumer.dto.TinyDTO;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class KafkaConsumerListener {

	@KafkaListener(topics = { "tiny-topic" }, groupId = "tiny-group-id", containerFactory = "factory")
	public void listen(@Payload ConsumerRecord<String, TinyDTO> record, Acknowledgment acknowledgment) {
		String topic = record.topic();
		String key = record.key();
		TinyDTO value = record.value();
		log.info("topic: {}, key: {}, value: {}", topic, key, value);
		// acknowledgment.nack(Duration.ofMinutes(1));
		acknowledgment.acknowledge();
	}
}
