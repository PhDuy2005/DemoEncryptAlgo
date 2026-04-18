# 📚 HƯỚNG DẪN POSTMAN - 3 THUẬT TOÁN MÃ HÓA

## 🎯 Tổng Quan
File `postman_collection.json` chứa 6 request test cho 3 thuật toán:
- **Twofish**: Mã hóa + Giải mã
- **Rijndael**: Mã hóa + Giải mã  
- **RC6**: Mã hóa + Giải mã

---

## 📥 Cách Import Collection vào Postman

1. Mở **Postman**
2. Click **File → Import** (hoặc tổ hợp `Ctrl+O`)
3. Chọn file `postman_collection.json`
4. Nhấp **Import**

---

## 🚀 Các Request Sẵn Sàng

### **1️⃣ TWOFISH**

#### Mã hóa (Encrypt)
```
POST http://localhost:8080/api/twofish/process
```
**Request Body:**
```json
{
  "data": "HelloWorld123456",
  "key": "MySecretKey12345",
  "algoName": "TWOFISH",
  "feature": "ENCRYPT",
  "inputType": "PLAIN_TEXT",
  "keyInputType": "PLAIN_TEXT",
  "outputType": "BASE64"
}
```

**Response Mong Muốn (ví dụ):**
```json
{
  "statusCode": 200,
  "error": null,
  "message": "✅ ENCRYPT (TWOFISH) thành công",
  "data": "abcd1234efgh5678..."  // BASE64 encrypted
}
```

#### Giải mã (Decrypt)
```
POST http://localhost:8080/api/twofish/process
```
**Request Body:**
```json
{
  "data": "abcd1234efgh5678...",  // Copy từ encrypt response
  "key": "MySecretKey12345",
  "algoName": "TWOFISH",
  "feature": "DECRYPT",
  "inputType": "BASE64",
  "keyInputType": "PLAIN_TEXT",
  "outputType": "PLAIN_TEXT"
}
```

---

### **2️⃣ RIJNDAEL**

#### Mã hóa (Encrypt)
```
POST http://localhost:8080/api/rijndael/encrypt
```
**Request Body:**
```json
{
  "data": "HelloWorld123456",
  "key": "MySecretKey12345"
}
```

**Response Mong Muốn:**
```json
{
  "statusCode": 200,
  "message": "Mã hóa Rijndael thành công",
  "data": "xyz9876uvw1234..."  // BASE64 encrypted
}
```

#### Giải mã (Decrypt)
```
POST http://localhost:8080/api/rijndael/decrypt
```
**Request Body:**
```json
{
  "data": "xyz9876uvw1234...",  // Copy từ encrypt response
  "key": "MySecretKey12345"
}
```

---

### **3️⃣ RC6**

#### Mã hóa (Encrypt)
```
POST http://localhost:8080/api/encrypt/process
```
**Request Body:**
```json
{
  "data": "HelloWorld123456",
  "key": "MySecretKey12345",
  "algoName": "RC6",
  "feature": "ENCRYPT",
  "inputType": "PLAIN_TEXT",
  "keyInputType": "PLAIN_TEXT",
  "outputType": "BASE64"
}
```

**Response Mong Muốn:**
```json
{
  "statusCode": 200,
  "error": null,
  "message": "✅ ENCRYPT (RC6) thành công",
  "data": "qwer5678tyui90..."  // BASE64 encrypted
}
```

#### Giải mã (Decrypt)
```
POST http://localhost:8080/api/encrypt/process
```
**Request Body:**
```json
{
  "data": "qwer5678tyui90...",  // Copy từ encrypt response
  "key": "MySecretKey12345",
  "algoName": "RC6",
  "feature": "DECRYPT",
  "inputType": "BASE64",
  "keyInputType": "PLAIN_TEXT",
  "outputType": "PLAIN_TEXT"
}
```

---

## ⚙️ Các Tham Số Chính

| Tham Số        | Mô Tả                      | Giá Trị                      |
| -------------- | -------------------------- | ---------------------------- |
| `data`         | Dữ liệu cần mã hóa/giải mã | String                       |
| `key`          | Khóa bí mật                | String                       |
| `algoName`     | Tên thuật toán             | `TWOFISH`, `RINJADEL`, `RC6` |
| `feature`      | Thao tác                   | `ENCRYPT` hoặc `DECRYPT`     |
| `inputType`    | Định dạng input            | `PLAIN_TEXT` hoặc `BASE64`   |
| `keyInputType` | Định dạng key              | `PLAIN_TEXT` hoặc `BASE64`   |
| `outputType`   | Định dạng output           | `PLAIN_TEXT` hoặc `BASE64`   |

---

## 💡 Mẹo Sử Dụng Postman

### 1️⃣ Copy Dữ Liệu Giữa Các Request
Sau khi mã hóa thành công, copy giá trị `data` từ response:
```
Response data → Paste vào request giải mã `data` field
```

### 2️⃣ Sử Dụng Variables (Optional)
Thay vì hardcode, dùng variables Postman:
- `{{base_url}}` → `http://localhost:8080`
- `{{test_data}}` → `HelloWorld123456`
- `{{test_key}}` → `MySecretKey12345`

### 3️⃣ Kiểm Tra Format
- **Mã hóa**: Input thường là `PLAIN_TEXT` → Output `BASE64`
- **Giải mã**: Input là `BASE64` → Output `PLAIN_TEXT`

---

## ⚠️ Lưu Ý

1. **Port mặc định**: `8080` (nếu khác, chỉnh lại URL)
2. **Khóa phải hợp lệ**: Độ dài khóa tùy thuật toán
3. **Data base64**: Khi giải mã, data phải là base64 hợp lệ
4. **Error Response**: Kiểm tra message khi có lỗi

---

## 🧪 Test Nhanh

**Bước 1:** Chạy ứng dụng
```bash
./gradlew bootRun
```

**Bước 2:** Mở Postman, import collection

**Bước 3:** Test từng request:
- Twofish Encrypt → Copy data → Twofish Decrypt ✓
- Rijndael Encrypt → Copy data → Rijndael Decrypt ✓
- RC6 Encrypt → Copy data → RC6 Decrypt ✓

---

## 📞 Cần Giúp?
Nếu gặp lỗi:
1. Kiểm tra server chạy trên port 8080
2. Kiểm tra khóa không được trống
3. Xem error message trong response
