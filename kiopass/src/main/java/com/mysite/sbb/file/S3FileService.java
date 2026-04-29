package com.mysite.sbb.file;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Primary // 로컬 저장소 대신 이 클래스를 우선 사용!
@Service
@RequiredArgsConstructor
public class S3FileService implements FileService {
	
	private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, long userID, String email) {
        if (file.isEmpty()) return null;

        String originalFilename=file.getOriginalFilename();
        //String savedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String savedFilename=Long.toString(userID)+"_"+email+"_"+originalFilename;

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드
            var resource = s3Template.upload(bucket, savedFilename, inputStream,
                    ObjectMetadata.builder().contentType(file.getContentType()).build());
            
            // 업로드된 파일의 공개 URL 반환
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류 발생", e);
        }
    }
}
