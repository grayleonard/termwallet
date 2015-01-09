mvn package appassembler:assemble
cp -r target/appassembler/ termwallet-repo/
echo "Zipping build folder..."
zip -r -T termwallet.zip termwallet-repo/ termwallet >/dev/null
rm -rf termwallet-repo/
echo "Unzipping into /usr/local/bin"
unzip -o termwallet.zip -d /usr/local/bin/ >/dev/null
