package com.alfredoeka.assignment_five.model;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "training")
@Where(clause = "deleted_date is null")
public class Training extends AbstractDate implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tema", length = 90)
    private String tema;

    @Column(name = "nama_pengajar", length = 45)
    private String namaPengajar;

//    @JsonIgnore
//    @OneToOne(mappedBy = "training")
//    private KaryawanTraining karyawanTraining;

//    @JsonIgnore
//    @OneToMany(mappedBy = "training")
//    List<KaryawanTraining> karyawanTrainings;


}
