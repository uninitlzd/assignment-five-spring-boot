package com.alfredoeka.assignment_five.service;

import com.alfredoeka.assignment_five.model.Training;

import java.util.Map;

public interface TrainingService {

    public Map insert(Training obj);

    public Map update(Training obj);

    public Map delete(Long idTraining);
}
