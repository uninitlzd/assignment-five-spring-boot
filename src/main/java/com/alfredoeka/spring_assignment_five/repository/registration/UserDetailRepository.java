package com.alfredoeka.spring_assignment_five.repository.registration;

import com.alfredoeka.spring_assignment_five.entity.UserDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDetailRepository extends PagingAndSortingRepository<UserDetail, Long>, CrudRepository<UserDetail, Long> {}
