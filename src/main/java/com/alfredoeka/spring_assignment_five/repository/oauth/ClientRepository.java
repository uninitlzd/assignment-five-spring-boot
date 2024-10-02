package com.alfredoeka.spring_assignment_five.repository.oauth;

import com.alfredoeka.spring_assignment_five.entity.oauth.Client;
import com.alfredoeka.spring_assignment_five.entity.oauth.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends PagingAndSortingRepository<Client, Long>, CrudRepository<Client, Long> {

    Client findOneByClientId(String clientId);

}
