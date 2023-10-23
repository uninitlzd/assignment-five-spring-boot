package com.alfredoeka.assignment_five.service;


import com.alfredoeka.assignment_five.model.Rekening;

import java.util.Map;

public interface RekeningService {

    public Map insert(Rekening obj);

    public Map update(Rekening obj);

    public Map delete(Long obj);

    public Map insert(Rekening rekening, Long idkaryawan);

    public Map update(Rekening rekening, Long idkaryawan);


}
