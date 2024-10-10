package com.alfredoeka.spring_assignment_five.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private boolean error;
}
