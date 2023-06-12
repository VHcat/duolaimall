package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by 北海 on 2023-06-06 11:07
 */
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    // 获取文件上传对应的地址
    @Value("${minio.endpointUrl}")
    public String endpointUrl;
    @Value("${minio.accessKey}")
    public String accessKey;
    @Value("${minio.secreKey}")
    public String secreKey;
    @Value("${minio.bucketName}")
    public String bucketName;

    /*
         返回的字符串，代表的是上传图片，对应的访问url
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {

        // 创建一个MinioClient对象
        MinioClient client = MinioClient.builder()
                .endpoint(endpointUrl)
                .credentials(accessKey, secreKey)
                .build();


         // 判断桶是否存在，如果不存在则创建
        boolean exists = client.bucketExists(BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .build());
        if (!exists) {
            System.out.println("桶" + bucketName + "不存在，重新创建！");
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

//        // 检查存储桶是否已经存在
//        boolean isExist = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//        if(isExist) {
//            System.out.println("Bucket already exists.");
//        } else {
//            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
//            client.makeBucket(MakeBucketArgs.builder()
//                    .bucket(bucketName)
//                    .build());
//        }

        // 为了让文件名不重复根据时间和uuid，计算文件名
        String fileName = System.currentTimeMillis() + UUID.randomUUID().toString();

        // 向桶中上传文件数据
        client.putObject(PutObjectArgs
                .builder()
                // 指定上传文件的桶
                .bucket(bucketName)
                // 指定以流的形式，上传文件内容, -1表示minio自动每次上传数据的大小
                .stream(file.getInputStream(), file.getSize(), -1)
                // 指定流数据的数据类型
                .contentType(file.getContentType())
                // 上传的数据在桶中名称
                .object(fileName)
                .build());


        // 上传成功，返回访问图片的url
        String url = endpointUrl + "/" + bucketName + "/" +fileName;
        
  
        return Result.ok(url);

    }
}
