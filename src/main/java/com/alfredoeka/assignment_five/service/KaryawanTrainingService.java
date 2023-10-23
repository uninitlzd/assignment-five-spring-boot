package com.alfredoeka.assignment_five.service;


import com.alfredoeka.assignment_five.dao.KaryawanTrainingRequest;

import java.util.Map;

public interface KaryawanTrainingService {

    public Map insert(KaryawanTrainingRequest obj);

    public Map update(KaryawanTrainingRequest obj);

    public Map delete(Long obj);
}
