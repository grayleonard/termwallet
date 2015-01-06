mvn package appassembler:assemble
cp -r target/appassembler/ termwallet-repo/
zip -r termwallet.zip termwallet-repo/ termwallet
rm -rf termwallet-repo/
unzip -o termwallet.zip -d /usr/local/bin/
