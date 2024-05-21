# Cipher Shell

Cipher Shell is a CLI application for encrypting and decrypting files using AES encryption. It uses a user-provided passphrase to generate a secure key to encrypt/decrypt any given file.

I have an interest in cybersecurity so I thought this would be a fun project to tackle and better understand cryptography using Java's Cipher class.

### Features
- AES/CBC/PKCS5Padding encryption and decryption.
- Secure passphrase handling.
- Salts and initial vectors for added security.
- Magic number to validate the encrypted file.
- File extension preservation.
- File I/O streams.
- JUnit & Mockito tests.

## How it works
The application can run in encrypt or decrypt mode and processes a given file. The user is prompted for a passphrase which, along with a randomly generated salt and initial vector, is used to to generate a secret key using an AES algorithm. 

When encrypting a file, a header is created containing a magic number, the original file extension and the salt/IV used to generate the secret key. Upon decrypting a file, the application first reads and checks the encrypted file's header's magic number to validate that the file was indeed encrypted using this application. The unpacked salt/IV is then used along with the user's passphrase to recreate the secret key in order to decrypt the file.

## Requirements
Java 11 or higher.
## Installation
Clone the repository and navigate to folder.

```bash
git clone https://github.com/leithatia/cipher-shell.git
cd cipher-shell
```

#### Building the Project (Optional)
If you want to build the project from source, you need to have Maven installed. Follow the instructions on the [Maven](https://maven.apache.org/install.html) website to install Maven on your system. Then, build the project using the following command:

```bash
mvn clean package
```
## Usage

You can use the newly created jar file from Maven or alternatively, if you trust me *insert evil laugh*, you can download the pre-built JAR file from the [releases](https://github.com/leithatia/cipher-shell/releases/tag/v1.0) page. Either way, navigate to the folder containing the jar file (target folder if using Maven), rename jar file to `ciphershell.jar` if needed and run the application with:

```bash
java -jar ciphershell.jar <encrypt|decrypt> <filename>
```

#### Example
To encrypt a file named "secrets.txt":

```bash
java -jar ciphershell.jar encrypt secrets.txt
```

To decrypt a file named "secrets.txt":

```bash
java -jar ciphershell.jar decrypt secrets.enc
```

Note that the decrypted file will use the same file name as the encrypted file with the original file's extension. Any existing file of the same name will be overwritten.

You can use the `-e` or `-d` flags instead of `<encrypt|decrypt>` if you prefer:
```bash
java -jar ciphershell.jar -e secrets.txt
java -jar ciphershell.jar -d secrets.enc
```

 

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Suggested future improvements

- Add option to use different cryptographic algorithms other than AES

## License

[MIT](https://choosealicense.com/licenses/mit/)
