package org.webhis.mvc;

import java.io.InputStream;

/**
 * 存储文件上传时传递的文件参数
 */
public class MvcFile {
    private String key ;
    private String fileName ;
    private String contentType ;
    private Long fileSize ;
    private InputStream inputStream  ;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public MvcFile(String key, String fileName, String contentType, Long fileSize, InputStream inputStream) {
        this.key = key;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.inputStream = inputStream;
    }

    public MvcFile() {
    }
}
