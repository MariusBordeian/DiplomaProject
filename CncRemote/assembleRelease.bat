set JAVA_HOME="C:\Program Files\Java\jdk1.7.0_80"

call gradlew.bat assembleRelease -Pandroid.injected.signing.store.file="mobile\mobile.jks" -Pandroid.injected.signing.store.password=diploma -Pandroid.injected.signing.key.alias=AC -Pandroid.injected.signing.key.password=diploma