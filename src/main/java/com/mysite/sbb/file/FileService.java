package com.mysite.sbb.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String upload(MultipartFile file, long userID, String email);
}
