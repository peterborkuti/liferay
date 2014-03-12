# LPE adder

## Description
When we create a hotfix first for a customer who has many public-patches, the work for searching the necessary LPEs is very boring. I wrote a some-liner script which do it for us.

## Prerequisites
Git bash on Windows or Linux or Mac (it uses POSIX commands like grep/sed/etc.)

## Usage
* download the customer's patches with internal patching-tool:
**  install a 6130 bundle
**  upgrade pathcing tool to internal-13
**  issue a command similar to this:
**  patching-tool.bat download administration-1-6130, collaboration-5-6130, development-2-6130, document-management-7-6130, dynamic-data-lists-4-6130
* Now run my script from the patches directory. You should get an LPE.txt file.

Just tell me bugs, improvements, send pull requests.