# Demo

## Steps

1 Show the minecraft world
2 Show the item
  a `item/CardItem`
3 Show the blocks
  a `block/DispenserBlock`
  b `block/entity/DispenserEntity`
  c `block/LockBlock`
  d `block/entity/LockEntity`
4 Show the environment
  a show vault cli
  b show vault ui
  c show setup script `main.hcl`
  d show policies `policies/*.hcl`

## POST request

```java
public static final String vaultAddress = System.getenv().getOrDefault("VAULT_ADDR", "http://localhost:8200");
public static final String vaultToken = System.getenv().getOrDefault("VAULT_TOKEN", "root");

String payload = String.format("""
  """)

try {
  HttpClient client = HttpClient.newHttpClient();
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(vaultAddress + "/v1/"))
      .header("Accept", "application/json")
      .header("X-Vault-Token", vaultToken)
      .POST(HttpRequest.BodyPublishers.ofString(payload))
      .build();

  HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
  if (response.statusCode() >= 400) {
    Mod.LOGGER.warn(response.body());
    return false;
  }

  return true;
} catch (Exception e) {
  e.printStackTrace();
  return false;
}
```

## Create user/pass

```java
// Payload
String payload = String.format("""
  {
    "password": "%s",
    "policies": "%s"
  }
  """, password, policies);

// Path
"/v1/auth/userpass/users/" + username
```

## Login

```java
// Get user details
String username = player.getName().getString();
String password = player.getUuidAsString();

// Payload
String payload = String.format("""
  {
    "password": "%s"
  }
  """, password);

// Path
"/v1/auth/userpass/login/" + username

// Build json
GsonBuilder builder = new GsonBuilder();
Gson gson = builder.create();
Login login = gson.fromJson(response.body(), Login.class);
return login;
```

## Encrypt

```java
// Payload
String payload = String.format("""
  {
    "plaintext": "%s"
  }
  """, Base64.getEncoder().encodeToString(input.getBytes()));

// Path
"/v1/transit/encrypt/minecraft"

// Build json
GsonBuilder builder = new GsonBuilder();
Gson gson = builder.create();
Encrypted encrypted = gson.fromJson(response.body(), Encrypted.class);
return encrypted.data.ciphertext;
```

## Sign

```java
// Payload
String payload = String.format("""
  {
    "hash_algorithm":"sha2-256",
    "signature_algorithm":"pkcs1v15",
    "input":"%s"
  }
  """, Base64.getEncoder().encodeToString(input.getBytes()));

// Path
"/v1/transit/sign/minecraft"

// Build json
GsonBuilder builder = new GsonBuilder();
Gson gson = builder.create();
Signature signature = gson.fromJson(response.body(), Signature.class);
return signature.data.signature;
```

=====================================================

## Verify

```java
String payload = String.format("""
{
  "hash_algorithm":"sha2-256",
  "signature_algorithm":"pkcs1v15",
  "input":"%s",
  "signature": "%s"
}
""", Base64.getEncoder().encodeToString(input.getBytes()), signature);

"/v1/transit/verify/minecraft"
```

## Decrypt

```java
// Payload
String payload = String.format("""
  {
    "ciphertext": "%s"
  }
  """, input);

// Path
"/v1/transit/decrypt/minecraft"

// Build json
GsonBuilder builder = new GsonBuilder();
Gson gson = builder.create();
Decrypted decrypted = gson.fromJson(response.body(), Decrypted.class);
return decrypted.data.plaintext;
```

## Access

```java
// Path
"/v1/secret/data/minecraft/" + policy

// Header
.header("X-Vault-Token", token)
```