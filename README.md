# LockFS

**LockFS** is a simple, flexible app for encrypting files individually, keeping your data secure and your vaults manageable

## Requirements

- [Java](https://jdk.java.net/25/) - **The JVM must be installed, at least Java 21**

> LockFS works on Linux, Windows, and MacOS

## Instructions

1. Download latest `lockfs.zip` from these releases:
  - *Current General Release*: [2.0.1](https://github.com/ghost-in-a-jar-00/LockFS/releases/tag/2.0.1)
  - *Current Development Release (Pre-Release)*: [2.0.1-dev.2](https://github.com/ghost-in-a-jar-00/LockFS/releases/tag/2.0.1-dev.2) (**may contain new features and bugs**)
  
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
- [PGP Key Fingerprints](#PGP-Key-Fingerprints)
  - [Instructions To Verify Releases](#Instructions-To-Verify-Releases)
- [Release Cycle](#Release-Cycle)
  - [Development Release](#Development-Release-Pre-Release)
  - [General Release](#General-Release-Latest-Release)
- [Developers](#Developers)
  - [Tools And Dependencies](#Tools-And-Dependencies)
- [File Version Tracker](#File-Version-Tracker)
  - [LockFS1](#LockFS1)
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

- `lock.jar` - Encrypts files
- `unlock.jar` - Decrypts files

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

# PGP Key Fingerprints

These are the PGP keys for verifying that the releases are authentic. You can get a copy of the keys [here](pub-keys.asc)

**0xGhostInAJar:** `6C31 6AD1 B314 BA1B BB36  0D23 70EC 4ECD 1B3B 0D16`

## Instructions To Verify Releases

You will need the following items to verify your release

- GnuPG or [Gpg4win (Windows users)](https://www.gpg4win.org/)
- [Public key](pub-key.asc)
- The hash from the release `lockfs.sha256`
- The signature file `lockfs.sha256.asc`

The hash file is signed instead of the package itself to make signing and verification faster. Since hash collisions are extremely unlikely, verifying the signed hash ensures the package comes from the owner of the private key.

These are the commands to use in your terminal/command prompt

- `gpg --import pub-key.asc`
- `gpg --verify lockfs.sha256.asc`

If it passes, it should show something like this:

```
gpg: assuming signed data in 'lockfs.sha256'
gpg: Signature made Wed 03 Mar 2026 10:00:00 AM UTC
gpg:                using EDDSA key 6C316AD1B314BA1BBB360D2370EC4ECD1B3B0D16
gpg: Good signature from "0xGhostInAJar (LockFS)" [ultimate]
```

The last 2 lines are important as it tells you if the signature is correct and if it has been signed by the correct key

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
- Fixes from the Development Release may be backported here
- Users should report issues if they occur

> This cycle prioritizes fast iteration while maintaining clarity about stability

# Developers

This section is geared towards contributors or those who want to replicate the build environment used for LockFS.

## Tools And Dependencies

LockFS is tested using the following:

- Gradle `9.1.3`
  - Groovy `5.0.4`
  - _PLUGIN_: com.gradleup.shadow `9.3.0` 
- OpenJDK `25`
  - BouncyCastle `1.83`
  
# File Version Tracker

This section tracks the file versions. Each file version has specific encryption parameters required to decrypt your files

**Note:** If you are using an older file version, you will need to use the software version released **before** the introduced version to decrypt it.

## LockFS V1 (LockFS1)
- Encryption Algo: **AES**
- Encryption Mode: **GCM**
- Encryption Padding: **No Paddding**
- KDF: **Argon2id**
- Introduced: `2.0.0-dev.1`

## License

This project is licensed under the [MIT License](LICENSE)
