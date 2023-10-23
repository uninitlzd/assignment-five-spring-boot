package com.alfredoeka.assignment_five.dao;

import lombok.Data;

import java.util.Date;

@Data
public class KaryawanTrainingRequest {

    public Long id;
    public Long idTraining;
    public Long idKaryawan;
    public Date tanggalTraining;
}
