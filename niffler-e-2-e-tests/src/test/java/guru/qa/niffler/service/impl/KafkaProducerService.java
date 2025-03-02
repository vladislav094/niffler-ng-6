package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.UserJson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class KafkaProducerService {

    private static final Config CFG = Config.getInstance();
    private static final Properties producerProperties = new Properties();
    private final Producer<String, String> producer;

    static {
        // Producer configuration
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public KafkaProducerService() {
        this.producer = new KafkaProducer<>(producerProperties);
    }

    public void sendMessage(String topic, UserJson userJson) {
        final ObjectMapper om = new ObjectMapper();
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, om.writeValueAsString(userJson));
            record.headers().add("__TypeId__", "guru.qa.niffler.model.UserJson".getBytes(StandardCharsets.UTF_8));
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.out.println("Ошибка при отправке сообщения: " + exception.getMessage());
                } else {
                    System.out.println("Сообщение отправлено в топик: " + metadata.topic() + ", партиция: " + metadata.partition());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            producer.flush();
            producer.close();
        }
    }
}
