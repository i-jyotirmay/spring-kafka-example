package io.jyotirmay.kafkaproducer.controller;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ProducerController {

	private KafkaTemplate<String, String> kafkaTemplate;

	@PostMapping(value = "/v1/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void publish(@RequestBody final String string) throws JsonProcessingException {
		ProducerRecord<String, String> producerRecord = new ProducerRecord<>("tiny-topic", "Jerry", string);

		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(producerRecord);
		SendResult<String, String> join = future.join();
		String value = join.getProducerRecord().value();
	}
}
