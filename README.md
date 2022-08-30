# Providing access and secrets in Minecraft using HashiCorp Vault

## Prerequisites

In order to run this demo locally you will need the following things installed.

- Java 17+ (Minecraft is built in Java, so the JDK is required)
- [Shipyard](https://shipyard.run/docs/install) (The environment is built using Shipyard)
- [Docker](https://docs.docker.com/get-docker/) (The environment runs entirely on top of Docker)

The project is set up using [Visual Studio Code](https://code.visualstudio.com/) and uses [Fabric](https://fabricmc.net/) to extend Minecraft.
To set up your preferred editor to run the project, please check the [Fabric docs](https://fabricmc.net/wiki/tutorial:setup).

## Setup

To start the environment, from the root directory of the project run:

```shell
shipyard run ./shipyard
```

After the environment setup is completed, you can view the Vault UI in your browser at [http://localhost:8200](localhost:8200).

To use the Vault CLI, first run the following command, which will set the required environment variables to point at the just created Vault instance.

```shell
eval $(shipyard env)
```

Check out `shipyard/main.hcl` and `shipyard/policies/*.hcl` for more details on the created paths and policies in the environment.

## Run Minecraft in dev mode

In order to test out the mod in dev mode, you do not need to have Minecraft installed, you simply have to run the `Minecraft Client` in debug mode using your preferred editor.

## Run the mod on your Minecraft client

In order to run the mod on your own Minecraft client, you will need to have the [Fabric launcher](https://fabricmc.net/use/installer/) installed (1.19.2).
You need to build the mod using:

```shell
./gradlew build
```

Then copy the resulting jar file (e.g. `vault-1.0.0.jar` from `build/libs/` to your Minecraft mods folder.

## The code

The main functionality of the mod can be found in the following paths.

### The keycard

The `keycard` is the item that the identity and data is written to, which can then be used on a `lock` to gain access.

```shell
src/main/java/com/hashicorp/vault/item/CardItem.java
```

### The wrench

In order to configure the `dispenser` and `lock` you can use a `wrench` on them, which will display a config screen.

```shell
src/main/java/com/hashicorp/vault/item/WrenchItem.java
```

### The dispenser

The `dispenser` creates the identity and writes the data to the `keycard`.

```shell
src/main/java/com/hashicorp/vault/block/DispenserBlock.java
src/main/java/com/hashicorp/vault/block/entity/DispenserEntity.java
```

### The lock

The `lock` checks the identity and data on the `keycard` and if everything is correct emits a redstone signal.

```shell
src/main/java/com/hashicorp/vault/block/LockBlock.java
src/main/java/com/hashicorp/vault/block/entity/LockEntity.java
```
