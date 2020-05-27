
# generate an RSA private key
openssl genrsa -out private_key.pem

# map the PEM format to DER format, for resource client jjwt input
openssl pkcs8 -in private_key.pem -inform PEM -topk8 -outform DER -nocrypt -out private_key.der

# generate a public key for resource server
openssl rsa -pubout -in private_key.pem -out public_key.pem

