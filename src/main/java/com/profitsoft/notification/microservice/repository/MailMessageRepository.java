package com.profitsoft.notification.microservice.repository;

import com.profitsoft.notification.microservice.entity.MailMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * The Spring data elasticsearch repository interface that simplifies the work with MailMessage entity.
 */
@Repository
public interface MailMessageRepository extends ElasticsearchRepository<MailMessage, String> {

    Page<MailMessage> findByStatus(String status, Pageable pageable);
}
