package com.alfredoeka.assignment_five.repository;


import com.alfredoeka.assignment_five.model.Rekening;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RekeningRepository extends PagingAndSortingRepository<Rekening,Long> {

    @Query("select c from Rekening c WHERE c.id = :id")
    public Rekening getbyID(@Param("id") Long id);
}
