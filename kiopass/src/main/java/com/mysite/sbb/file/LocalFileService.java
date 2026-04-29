package com.mysite.sbb.file;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileService implements FileService {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Override
	public String upload(MultipartFile file, long userID, String email) {
		
		if(file.isEmpty()) return null;
		
		String originalFilename=file.getOriginalFilename();
		//String savedFilename=UUID.randomUUID().toString()+"_"+originalFilename;
		String savedFilename=Long.toString(userID)+"_"+email+"_"+originalFilename;
		
		try {
			File directory=new File(uploadDir);
			if(!directory.exists()) directory.mkdirs();
			
			File dest=new File(uploadDir+savedFilename);
			file.transferTo(dest);
			
			return dest.getAbsolutePath();
		} catch(IOException e) {
			throw new RuntimeException("파일 저장 중 오류가 발생했습니다.",e);
		}
	}
}
