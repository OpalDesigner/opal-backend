package ai.opal.backend.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.InputStream;
import java.net.URI;

@Service
public class R2DownloadService {
    private final S3Client s3Client;
    private final String bucketName;

    public R2DownloadService(
            @Value("${cloudflare.r2.access-key-id}") String accessKeyId,
            @Value("${cloudflare.r2.secret-access-key}") String secretAccessKey,
            @Value("${cloudflare.r2.account-id}") String accountId,
            @Value("${cloudflare.r2.bucket-name}") String bucketName
    ) {
        this.bucketName = bucketName;

        // Configure S3 client for Cloudflare R2
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                .region(Region.US_EAST_1) // R2 uses US-EAST-1 as default
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
                .build();
    }

    public ResponseEntity<Resource> downloadFile(String fileName) {
        try {
            // Create request to get object from R2
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Get the object from R2
            GetObjectResponse object = s3Client.getObject(getObjectRequest
            ).response();

            // Get input stream of the object
            InputStream inputStream = s3Client.getObject(getObjectRequest);

            // Create resource from input stream
            InputStreamResource resource = new InputStreamResource(inputStream);

            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, object.contentType());
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(object.contentLength()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(object.contentType()))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from R2: " + e.getMessage());
        }
    }
}
