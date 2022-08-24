network "main" {
  subnet = "10.5.0.0/16"
}

variable "vault_bootstrap_script" {
  default = <<-EOF
  #/bin/sh -e
  vault status

  vault secrets enable kv-v2
  vault kv put secret/minecraft/404 access=true
  vault kv put secret/minecraft/418 access=true
  vault kv put secret/minecraft/vip access=true

  vault auth enable userpass

  vault policy write 404 /data/policies/room-404.hcl
  vault policy write 418 /data/policies/room-418.hcl
  vault policy write vip /data/policies/vip.hcl

  vault secrets enable transit
  vault write -f transit/keys/minecraft type=rsa-4096

  vault policy write minecraft /data/policies/transit.hcl
  EOF
}

copy "vault_policies" {
  source      = "${file_dir()}/policies"
  destination = "${data("vault_data")}/policies"
}

variable "vault_network" {
  default = "main"
}

module "vault" {
  depends_on = ["copy.vault_policies"]
  source     = "github.com/shipyard-run/blueprints?ref=f235847a73c5bb81943aaed8f0c526edee693d75/modules//vault-dev"
}

output "VAULT_ADDR" {
  value = "http://localhost:8200"
}

output "VAULT_TOKEN" {
  value = "root"
}