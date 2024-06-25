package com.profitsoft.notification.microservice.repository;

import com.profitsoft.notification.microservice.entity.MailMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailMessageRepository extends ElasticsearchRepository<MailMessage, String> {

}
