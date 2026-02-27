# LockFS

**LockFS** is a simple, flexible app for encrypting files individually, keeping your data secure and your vaults manageable

**Note:** LockFS is currently in active development. Core functionality, including encryption, may have bugs. Use at your own risk.

## Requirements

- [Java](https://jdk.java.net/25/) - **The JVM must be installed, at least Java 21**

> LockFS works on Linux, Windows, and MacOS

## Instructions

1. Download latest `lockfs.zip` from these releases:
  - *Current General Release*: [1.0.2](https://github.com/ghost-in-a-jar-00/LockFS/releases/tag/1.0.1)
  - *Current Development Release (Pre-Release)*: [1.0.1-dev.1](https://github.com/ghost-in-a-jar-00/LockFS/releases/tag/1.0.2-dev.1) (**may contain new features and bugs**)
  
2. Unzip the `lockfs.zip` and open the `lockfs` folder
3. Open a terminal or command prompt
4. Run the program using the following command:

**To encrypt files**

```
java -jar lock.jar
```

_Encrypted `.lkx` files can be found in `enc` folder_

**To decrypt files**

```
java -jar unlock.jar
```

_Decrypted files can be found in `unlocked` folder_

## Details
- [Features](#Features)
- [Why LockFS](#Why-LockFS)
- [Main Tools](#Main-Tools)
- [Optional Tools](#Optional-Tools)
- [Security Model](#Security-Model)
  - [Security Goals](#Security-Goals)
  - [Security Assumptions](#Security-Assumptions)
  - [What LockFS Does Not Protect](#What-LockFS-Does-Not-Protect)
- [Release Cycle](#Release-Cycle)
  - [Development Release](#Development-Release-Pre-Release)
  - [General Release](#General-Release-Latest-Release)
- [License](#License)

## Features

- **Granular Encryption**: Encrypt files one by one instead of bundling them in a single vault
- **Expandable Vaults**: Add files to your vault without worrying about it getting too large or fragmented
- **Cross-Storage Friendly**: Ideal for users managing multiple storage devices of different sizes
- **Secure and Reliable**: Uses robust encryption to keep your files safe

## Why LockFS?

Traditional vault systems often require compressing or archiving files before encrypting, which can make growing your vault cumbersome. LockFS lets you:

- Encrypt files individually for flexibility easily
- Avoid creating multiple fragmented vaults
- Easily manage and expand your encrypted storage over time

## Main Tools

**Inside `lockfs.zip`**

- `lock.jar` - Encrypts Files
- `unlock.jar` - Decrypts Files

## Optional Tools

**Inside `optional-tools.zip`**

- `genvault.jar` - Generates a vault
- `reveal.jar` - Displays vault info

## Security Model

LockFS is designed to protect the confidentiality of files by encrypting them before they are stored or transferred outside the host system. The security of LockFS focuses on protecting encrypted files even if they are copied, shared, or stored on untrusted storage systems such as external drives, backups, or cloud storage

LockFS assumes that the host environment where encryption and decryption occur is trusted. Once files are encrypted, their confidentiality relies on the secrecy of the encryption keys and the strength of the cryptographic primitives used. Most users should have secure defaults, which LockFS aims to provide

LockFS does **not** attempt to **defend against a compromised operating system, malicious software running on the host machine, or attackers who can access sensitive data** during runtime

### Security Goals

LockFS aims to provide the following guarantees:

- Files encrypted by LockFS cannot be read without the correct encryption keys
- Encrypted files remain confidential even if they are stored outside the original machine
- Encryption keys are never intentionally exposed by the program
- Plaintext files are removed after encryption to reduce accidental exposure

### Security Assumptions

The security of LockFS relies on the following assumptions:

- The host system (hardware, firmware, and operating system) is trusted and not compromised
- The operating system enforces file access permissions correctly
- Only authorized users have access to encryption keys
- Files created or decrypted by LockFS are only accessible by the owner
- Deleted plaintext files may remain on storage media; the storage environment must prevent recovery of deleted data
- The machine supports and has enabled `fstrim` (or an equivalent mechanism) to allow secure block reuse

### What LockFS Does Not Protect

The following scenarios are outside the protection scope of LockFS:

- Malware or attackers with control of the operating system
- Memory inspection while files are being encrypted or decrypted
- Weak or compromised user passwords or keys
- Data leaks caused by other software on the system

# Release Cycle

LockFS uses a simplified release cycle:

## Development Release (Pre-Release)
- Active development versions
- May include **alpha**, **beta**, or **feature-complete** states
- All testing, feature additions, and bug fixes are done here
- Expect experimental behavior

## General Release (Latest Release)
- Publicly available versions
- Taken from the latest Development Release
- **Typically stable**, but occasionally may include **short-lived unstable releases** for rapid iteration
- Fixes from the Development Phase may be backported here
- Users should report issues if they occur

> This cycle prioritizes fast iteration while maintaining clarity about stability

## License

This project is licensed under the [MIT License](LICENSE)
