package com.alfredoeka.spring_assignment_five.repository.oauth;

import com.alfredoeka.spring_assignment_five.entity.oauth.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, CrudRepository<Role, Long> {
    Role findOneByName(String name);

    List<Role> findByNameIn(String[] names);
}
