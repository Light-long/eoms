package com.tx.eoms.config.tencent.oss;

import cn.hutool.core.util.IdUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CosUtil {

    @Value("${tencent.cloud.appId}")
    private String appId;

    @Value("${tencent.cloud.secretId}")
    private String secretId;

    @Value("${tencent.cloud.secretKey}")
    private String secretKey;

    @Value("${tencent.cloud.region}")
    private String region;

    @Value("${tencent.cloud.bucket}")
    private String bucket;

    private COSClient getCosClient() {
        COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(credentials, clientConfig);
    }

    /**
     * 上传文件
     * @param file 文件
     * @param typeEnum 文件类型
     * @return 存储地址信息
     */
    public Map<String, Object> uploadFile(MultipartFile file, CosTypeEnum typeEnum) throws IOException {
        // 存放图片的相对路径
        String path = null;
        String fileName = file.getOriginalFilename();
        // 根据传入的type判断放入那个文件夹
        if (typeEnum == CosTypeEnum.ARCHIVE) {
            path = "/archive/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        } else if (typeEnum == CosTypeEnum.AVATAR){
            path = "/avatar/" + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        } else if (typeEnum == CosTypeEnum.DOCUMENT) {
            path = "/document/" + fileName.substring(0, fileName.lastIndexOf("."))
                    + "-" +IdUtil.simpleUUID().substring(0,2) + fileName.substring(fileName.lastIndexOf("."));
        }
        // 元数据信息
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentEncoding("UTF-8");
        meta.setContentType(file.getContentType());
        // 创建请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, file.getInputStream(), meta);
        putObjectRequest.setStorageClass(StorageClass.Standard);
        // 获取Client对象
        COSClient client = getCosClient();
        PutObjectResult putObjectResult = client.putObject(putObjectRequest);
        // 上传结束关闭client
        client.shutdown();
        // 存储上传的地址
        Map<String, Object> map = new HashMap<>();
        // 外网访问路径
        map.put("url", "https://" + bucket + ".cos." + region + ".myqcloud.com" + path);
        // 存储的相对路径
        map.put("path", path);
        return map;
    }

    public void deleteFile(String[] paths) {
        COSClient client = getCosClient();
        for (String path : paths) {
            client.deleteObject(bucket, path);
        }
        client.shutdown();
    }
}
