# 📋 Hướng dẫn Tích hợp Thuật toán Mã hóa - API Controller Chung

## 📌 Tổng quan

Hệ thống hỗ trợ tích hợp **4 thuật toán mã hóa**: TWOFISH, RINJADEL, SERPENT, RC6.

Hiện tại, mỗi thuật toán có **một service riêng**. Mục tiêu là **tạo một controller chung** sử dụng switch-case để dispatch request tới service phù hợp.

---

## 📥 INPUT - EncryptionRequest

### Cấu trúc Request

```java
{
  "data": String,              // Dữ liệu input cần mã hóa/giải mã
  "key": String,               // Khóa bí mật
  "algoName": AlgoName,        // Thuật toán: TWOFISH, RINJADEL, SERPENT, RC6
  "feature": FeatureOption,    // Hành động: ENCRYPT hoặc DECRYPT
  "inputType": InputType,      // Format input: BASE64 hoặc PLAIN_TEXT (mặc định: BASE64)
  "keyInputType": InputType,   // Format key: BASE64 hoặc PLAIN_TEXT (mặc định: PLAIN_TEXT)
  "outputType": InputType      // Format output: BASE64 hoặc PLAIN_TEXT (mặc định: BASE64)
}
```

### Chi tiết các field

| Field          | Type   | Bắt buộc | Mặc định   | Ghi chú                                       |
| -------------- | ------ | -------- | ---------- | --------------------------------------------- |
| `data`         | String | ✅        | -          | Dữ liệu cần xử lý                             |
| `key`          | String | ✅        | -          | Khóa bí mật (8/16/24/32 bytes tùy thuật toán) |
| `algoName`     | Enum   | ✅        | -          | TWOFISH \| RINJADEL \| SERPENT \| RC6         |
| `feature`      | Enum   | ✅        | -          | ENCRYPT \| DECRYPT                            |
| `inputType`    | Enum   | ❌        | BASE64     | BASE64 \| PLAIN_TEXT                          |
| `keyInputType` | Enum   | ❌        | PLAIN_TEXT | BASE64 \| PLAIN_TEXT                          |
| `outputType`   | Enum   | ❌        | BASE64     | BASE64 \| PLAIN_TEXT                          |

### Ví dụ Request

**Mã hóa Plain Text:**
```json
{
  "data": "Hello World",
  "key": "1234567890123456",
  "algoName": "TWOFISH",
  "feature": "ENCRYPT",
  "inputType": "PLAIN_TEXT"
}
```

**Giải mã thành Plain Text:**
```json
{
  "data": "cWr/I5uiAwCav1iqQkqJ/g==",
  "key": "1234567890123456",
  "algoName": "TWOFISH",
  "feature": "DECRYPT",
  "outputType": "PLAIN_TEXT"
}
```

---

## 📤 OUTPUT - RestResponse

### Cấu trúc Response

```java
{
  "statusCode": int,           // 200, 400, 500...
  "error": String,             // Pesan lỗi (null nếu thành công)
  "message": String,           // Thông báo kết quả
  "data": T                     // Dữ liệu kết quả (generic type)
}
```

### Ví dụ Response thành công (Encrypt)

**Status: 200**
```json
{
  "statusCode": 200,
  "error": null,
  "message": "Mã hóa thành công",
  "data": "cWr/I5uiAwCav1iqQkqJ/g=="
}
```

### Ví dụ Response thất bại

**Status: 400**
```json
{
  "statusCode": 400,
  "error": "Thuật toán không hỗ trợ",
  "message": "Chỉ hỗ trợ thuật toán TWOFISH tại endpoint này",
  "data": null
}
```

**Status: 500**
```json
{
  "statusCode": 500,
  "error": "Lỗi xử lý",
  "message": "Lỗi: Incorrect key length",
  "data": null
}
```

---

## 🔧 CÁCH TÍCH HỢP

### 1️⃣ Tạo Service Factory (Optional nhưng recommended)

```java
@Component
public class EncryptionServiceFactory {
    
    @Autowired
    private TwofishEncryptionService twofishService;
    
    @Autowired
    private RinjadelEncryptionService rinjadelService;
    
    @Autowired
    private SerpentEncryptionService serpentService;
    
    @Autowired
    private RC6EncryptionService rc6Service;
    
    public IEncryptionService getService(AlgoName algoName) {
        switch(algoName) {
            case TWOFISH:
                return twofishService;
            case RINJADEL:
                return rinjadelService;
            case SERPENT:
                return serpentService;
            case RC6:
                return rc6Service;
            default:
                throw new IllegalArgumentException("Thuật toán không hỗ trợ: " + algoName);
        }
    }
}
```

### 2️⃣ Tạo Interface chung (Optional)

Nếu các service có signature khác nhau, tạo interface wrapper:

```java
public interface IEncryptionService {
    byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException;
    byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException;
}
```

### 3️⃣ Tạo Controller chung

```java
@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {

    @Autowired
    private EncryptionServiceFactory factory;

    @PostMapping("/process")
    public ResponseEntity<RestResponse<String>> process(@RequestBody EncryptionRequest request) {
        try {
            // Validate input
            if (request.getAlgoName() == null || request.getFeature() == null) {
                return buildErrorResponse(400, "Thiếu thông tin", "Vui lòng cung cấp algoName và feature");
            }

            // Lấy service phù hợp dựa trên thuật toán
            IEncryptionService service = factory.getService(request.getAlgoName());
            
            // Decode input
            InputType inputType = request.getInputType() != null ? request.getInputType() : InputType.BASE64;
            InputType keyInputType = request.getKeyInputType() != null ? request.getKeyInputType() : InputType.PLAIN_TEXT;
            
            byte[] data = decodeInput(request.getData(), inputType);
            byte[] key = decodeInput(request.getKey(), keyInputType);
            
            // Xử lý
            byte[] result;
            String message;
            
            if (request.getFeature() == FeatureOption.ENCRYPT) {
                result = service.encrypt(data, key);
                message = "Mã hóa thành công";
            } else if (request.getFeature() == FeatureOption.DECRYPT) {
                result = service.decrypt(data, key);
                message = "Giải mã thành công";
            } else {
                return buildErrorResponse(400, "Feature không hợp lệ", "Sử dụng ENCRYPT hoặc DECRYPT");
            }
            
            // Encode output
            InputType outputType = request.getOutputType() != null ? request.getOutputType() : InputType.BASE64;
            String encodedResult = encodeOutput(result, outputType);
            
            return buildSuccessResponse(200, message, encodedResult);
            
        } catch (Exception e) {
            return buildErrorResponse(500, "Lỗi xử lý", "Lỗi: " + e.getMessage());
        }
    }
    
    private byte[] decodeInput(String input, InputType inputType) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input không được trống");
        }
        return inputType == InputType.BASE64 
            ? Base64.getDecoder().decode(input) 
            : input.getBytes();
    }
    
    private String encodeOutput(byte[] output, InputType outputType) {
        return outputType == InputType.BASE64 
            ? Base64.getEncoder().encodeToString(output) 
            : new String(output);
    }
    
    private ResponseEntity<RestResponse<String>> buildSuccessResponse(int code, String message, String data) {
        RestResponse<String> response = RestResponse.<String>builder()
                .statusCode(code)
                .error(null)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<RestResponse<String>> buildErrorResponse(int code, String error, String message) {
        RestResponse<String> response = RestResponse.<String>builder()
                .statusCode(code)
                .error(error)
                .message(message)
                .data(null)
                .build();
        return code == 400 ? ResponseEntity.badRequest().body(response) : ResponseEntity.status(code).body(response);
    }
}
```

---

## 🔑 YÊU CẦU MỗI THUẬT TOÁN

### TWOFISH
- **Key length**: 8, 16, 24, 32 bytes
- **Block size**: 16 bytes (tự động padding)
- **Service method**:
  ```java
  byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException
  byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException
  ```

### RINJADEL / SERPENT / RC6
*(Cần xác nhận từ các service tương ứng)*
- **Key length**: ?
- **Block size**: ?
- **Service method**: Tương tự

---

## 📞 Cách gọi hàm từ Service

### Twofish

```java
// Inject service
@Autowired
private TwofishEncryptionService twofishService;

// Gọi
byte[] encryptedData = twofishService.encrypt(data, key); // trả về byte[]
byte[] decryptedData = twofishService.decrypt(encryptedData, key); // trả về byte[]
```

### Các service khác
*(Cần implement tương tự)*

```java
// Kết cấu có thể khác nhau, cần kiểm tra từng service
byte[] result = someService.process(data, key, operation);
```

---

## ✅ Checklist khi thêm thương toán mới

- [ ] Service class đã implement `byte[] encrypt()` và `byte[] decrypt()`
- [ ] Service class được annotate `@Service`
- [ ] Thêm vào `AlgoName` enum nếu chưa có
- [ ] Update `EncryptionServiceFactory` với case mới
- [ ] Test with Postman

---

## 🧪 Test mẫu

**Encrypt:**
```
POST http://localhost:8080/api/encryption/process
{
  "data": "Test Data",
  "key": "1234567890123456",
  "algoName": "TWOFISH",
  "feature": "ENCRYPT",
  "inputType": "PLAIN_TEXT"
}
```

**Decrypt:**
```
POST http://localhost:8080/api/encryption/process
{
  "data": "<encrypted_data_from_above>",
  "key": "1234567890123456",
  "algoName": "TWOFISH",
  "feature": "DECRYPT",
  "outputType": "PLAIN_TEXT"
}
```

---

## 📞 Liên hệ

Nếu có câu hỏi hoặc cần support, vui lòng liên hệ team development.
