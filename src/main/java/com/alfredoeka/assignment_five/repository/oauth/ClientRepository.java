package com.alfredoeka.assignment_five.repository.oauth;

import com.alfredoeka.assignment_five.model.oauth.Client;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {

    Client findOneByClientId(String clientId);

}
