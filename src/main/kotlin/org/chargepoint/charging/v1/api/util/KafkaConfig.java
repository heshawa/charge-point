package org.chargepoint.charging.v1.api.util;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaConfig {
	
	Logger log = LoggerFactory.getLogger(KafkaConfig.class);

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapConfig;
	@Value("${spring.kafka.producer.key-serializer}")
	private String keySerializerClass;
	@Value("${spring.kafka.producer.value-serializer}")
	private String valueSerializerClass;

//	@Value("${spring.kafka.properties.sasl.jaas.config}")
	@Value("${spring.kafka.properties.sasl.jaas.config}")
	private String  jassConfig;

	@Value("${spring.kafka.producer.acks}")
	private String  producerAckConfig;

	@Value("${spring.kafka.producer.retries}")
	private String  producerRetries;

	@Value("${spring.kafka.producer.batch.size}")
	private String  producerBatchSize;

	@Value("${spring.kafka.producer.linger.ms}")
	private String  producerLingerMs;

	@Value("${spring.kafka.producer.buffer.memory}")
	private String  producerBufferMemory;

	@Value("${spring.kafka.producer.compression.type}")
	private String  producerCompressionType;

	@Value("${spring.kafka.producer.max.request.size}")
	private String  producerMaxRequestSize;

	@Value("${spring.kafka.security.protocol}")
	private String  securityProtocol;

	@Value("${spring.kafka.sasl.mechanism}")
	private String  securitySaslMechanism;

	@Bean
	public ProducerFactory<String, ServiceRequestContext> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapConfig);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		config.put(SaslConfigs.SASL_JAAS_CONFIG,jassConfig);

		config.put(ProducerConfig.ACKS_CONFIG,producerAckConfig);
		config.put(ProducerConfig.RETRIES_CONFIG,producerRetries);
		config.put(ProducerConfig.BATCH_SIZE_CONFIG,producerBatchSize);
		config.put(ProducerConfig.LINGER_MS_CONFIG,producerLingerMs);
		config.put(ProducerConfig.BUFFER_MEMORY_CONFIG,producerBufferMemory);
		config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,producerCompressionType);
		config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,producerMaxRequestSize);

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, ServiceRequestContext> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@PostConstruct
	public void validateBootstrap() {
		log.info("Kafka template created. Kafka bootstrap: " + bootstrapConfig);
	}
}
