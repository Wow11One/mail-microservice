package com.profitsoft.notification.microservice.repository;

import com.profitsoft.notification.microservice.constants.MailMessageStatus;
import com.profitsoft.notification.microservice.entity.MailMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailMessageRepository extends ElasticsearchRepository<MailMessage, String> {
    Page<MailMessage> findByStatus(MailMessageStatus mailMessageStatus, Pageable pageable);
}
